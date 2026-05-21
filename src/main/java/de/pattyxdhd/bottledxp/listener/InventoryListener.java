package de.pattyxdhd.bottledxp.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.pattyxdhd.bottledxp.BottledXP;
import de.pattyxdhd.bottledxp.inventory.BottleInventory;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private static final Cache<String, Integer> amountHandler = CacheBuilder.newBuilder().build();

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (!event.getView().getTitle().equals(BottledXP.getInstance().getLanguageManager().getMessage("inventory.inventoryName")))
            return;

        if (event.getClickedInventory() == null) return;

        Player player = (Player) event.getWhoClicked();

        Inventory topInventory = event.getView().getTopInventory();

        boolean top = event.getRawSlot() < topInventory.getSize();

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        // SHIFT KLICKS BLOCKIEREN
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }

        // HOTBAR SWAP BLOCKIEREN
        if (event.getClick() == ClickType.NUMBER_KEY) {
            event.setCancelled(true);
            return;
        }

        // ITEMS INS GUI LEGEN BLOCKIEREN
        if (top) {

            // Wenn Cursor ein Item hält -> blockieren
            if (cursor != null && cursor.getType() != Material.AIR) {
                event.setCancelled(true);
                return;
            }

            // GUI grundsätzlich readonly
            event.setCancelled(true);

            // AB HIER DEINE GUI BUTTONS
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                return;
            }

            if (!currentItem.hasItemMeta()) {
                return;
            }

            if (!currentItem.getItemMeta().hasDisplayName()) {
                return;
            }

            int bottles = Math.min(XPUtils.getBottles(player), 1);

            if (!amountHandler.asMap().containsKey(player.getUniqueId().toString())) {
                amountHandler.put(player.getUniqueId().toString(), bottles);
            }

            int amount = amountHandler.asMap().get(player.getUniqueId().toString());

            String name = currentItem.getItemMeta().getDisplayName();

            // PLUS BUTTON
            if (name.equals(BottleInventory.getPlusItemName())) {
                if (amount <= XPUtils.getBottles(player) - 1) {
                    if (event.isShiftClick()) {
                        amount = amount + BottledXP.getInstance().getIntFromConfig("inventory.plusAmountShift");
                        if (amount >= XPUtils.getBottles(player) - 1) {
                            amount = XPUtils.getBottles(player);
                            BottledXP.getInstance().playConfigSound(player, "sounds.inventoryClickSound");
                        }
                    } else {
                        amount++;
                        BottledXP.getInstance().playConfigSound(player, "sounds.inventoryClickSound");
                    }
                } else {
                    BottledXP.getInstance().playConfigSound(player, "sounds.failChangeGuiNumber");
                }
            }

            // MINUS BUTTON
            else if (name.equals(BottleInventory.getMinusItemName())) {

                if (amount >= 2) {
                    if (event.isShiftClick()) {
                        amount = amount - BottledXP.getInstance().getIntFromConfig("inventory.minusAmountShift");
                        if (amount < 1) {
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
            }

            // APPLY BUTTON
            else if (name.equals(BottledXP.getInstance().getLanguageManager().getMessage("inventory.applyItem"))) {

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

            // CONVERT BUTTON
            else if (name.equals(BottledXP.getInstance().getLanguageManager().getMessage("inventory.convertItem"))) {
                int converted = XPUtils.getInventoryBottleAmount(player);

                if (converted <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.convertNoXPBottles"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                    player.closeInventory();
                    return;
                }

                XPUtils.convertBottlesToXp(player);
                player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.convertSuccessful")
                        .replace("%bottles%", String.valueOf(converted))
                        .replace("%level%", String.valueOf(XPUtils.getPreviewLevelAfterBottleConvert(player, converted)))
                        .replace("%points%", String.valueOf(XPUtils.getPreviewPointsAfterBottleConvert(player, converted))));
                BottledXP.getInstance().playConfigSound(player, "sounds.successfulSound");
                player.closeInventory();
                return;
            }

            amountHandler.asMap().put(player.getUniqueId().toString(), amount);
            BottleInventory.updateMathButtons(player, amount);
            return;
        }

        // =====================================================
        // UNTEN: XP FLASCHEN SPERREN
        // =====================================================

        if (currentItem != null && currentItem.getType() == Material.EXPERIENCE_BOTTLE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(BottledXP.getInstance().getLanguageManager().getMessage("inventory.inventoryName"))) {
            amountHandler.asMap().remove(event.getPlayer().getUniqueId().toString());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!event.getView().getTitle().equals(
                BottledXP.getInstance().getLanguageManager().getMessage("inventory.inventoryName"))) {
            return;
        }

        int topSize = event.getView().getTopInventory().getSize();

        for (int slot : event.getRawSlots()) {

            // Slot gehört zum oberen Inventar
            if (slot < topSize) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
