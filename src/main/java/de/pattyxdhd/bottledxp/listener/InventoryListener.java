package de.pattyxdhd.bottledxp.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.pattyxdhd.bottledxp.BottledXP;
import de.pattyxdhd.bottledxp.inventory.BottleInventory;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    private static final Cache<String, Integer> amountHandler = CacheBuilder.newBuilder().build();

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){

        Player player = (Player) event.getWhoClicked();

        if (!amountHandler.asMap().containsKey(player.getUniqueId().toString())){
            amountHandler.put(player.getUniqueId().toString(), 1);
        }

        int amount = amountHandler.asMap().get(player.getUniqueId().toString());

        if (event.getView().getTitle().equals(BottledXP.getInstance().getLanguageManager().getMessage("inventory.inventoryName"))){

            if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory())){
                event.setCancelled(true);
            }

            if (event.getCurrentItem() == null){
                return;
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(BottleInventory.getMinusItemName())) {
                if (amount >= 2){
                    if (event.isShiftClick()){
                        amount=amount-BottledXP.getInstance().getIntFromConfig("inventory.minusAmountShift");
                        if (amount < 1){
                            amount = 1;
                            BottledXP.getInstance().playConfigSound(player, "sounds.inventoryClickSound");
                        }
                    } else {
                        amount--;
                        BottledXP.getInstance().playConfigSound(player, "sounds.inventoryClickSound");
                    }
                } else {
                    BottledXP.getInstance().playConfigSound(player, "sounds.failChangeGuiNumber");
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(BottleInventory.getPlusItemName())) {
                if (amount <= XPUtils.getBottles(player)-1){
                    if (event.isShiftClick()){
                        amount=amount+BottledXP.getInstance().getIntFromConfig("inventory.plusAmountShift");
                        if (amount >= XPUtils.getBottles(player)-1){
                            amount = XPUtils.getBottles(player);
                            BottledXP.getInstance().playConfigSound(player, "sounds.inventoryClickSound");
                        }
                    }else {
                        amount++;
                        BottledXP.getInstance().playConfigSound(player, "sounds.inventoryClickSound");
                    }
                } else {
                    BottledXP.getInstance().playConfigSound(player, "sounds.failChangeGuiNumber");
                }
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(BottledXP.getInstance().getLanguageManager().getMessage("inventory.applyItem"))){

                int maxAllowed = 10000;
                if (amount > maxAllowed) {
                    amount = maxAllowed;
                }

                if (XPUtils.getBottles(player) <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.notEnoughXP"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                    player.closeInventory();
                    return;
                }

                int possible = Math.min(XPUtils.getBottles(player), XPUtils.getAvailableBottleSpace(player.getInventory()));
                if (possible <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.notEnoughSpace"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                    return;
                }

                XPUtils.fillExactAmount(player, amount);
                player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.filledAmount").replace("%filled%", String.valueOf(amount)));
                BottledXP.getInstance().playConfigSound(player, "sounds.successfulSound");

                player.closeInventory();
                amountHandler.asMap().remove(player.getUniqueId().toString());
                return;
            }

            amountHandler.asMap().put(player.getUniqueId().toString(), amount);
            BottleInventory.updateMathButtons(player, amount);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if (event.getView().getTitle().equals(BottledXP.getInstance().getLanguageManager().getMessage("inventory.inventoryName"))){
            amountHandler.asMap().remove(event.getPlayer().getUniqueId().toString());
        }
    }

}
