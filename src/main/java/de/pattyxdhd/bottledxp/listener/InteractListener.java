package de.pattyxdhd.bottledxp.listener;

import de.pattyxdhd.bottledxp.BottledXP;
import de.pattyxdhd.bottledxp.inventory.BottleInventory;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InteractListener implements Listener {

    @EventHandler
    public void onBlockRightClickForConvert(PlayerInteractEvent event){

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!BottledXP.getInstance().getBooleanFromConfig("blockInteractConvert.use")) return;
        if (block == null) return;

        String blockString = BottledXP.getInstance().getStringFromConfig("blockInteractConvert.block");
        Material blockMaterial = Material.matchMaterial(blockString);
        String itemString = BottledXP.getInstance().getStringFromConfig("blockInteractConvert.usedItem");
        Material itemMaterial = Material.matchMaterial(itemString);

        if (blockMaterial == null || block.getType() != blockMaterial) return;

        ItemStack hand = event.getItem();
        if (hand == null || hand.getType() != itemMaterial) return;

        if (!player.hasPermission("bottledxp.interact.convert")) {
            event.setCancelled(true);
            BottledXP.getInstance().sendActionbar(player, "blockInteractConvert.hotbarMessageNoPerm", Map.of(
                    "%prefix%", BottledXP.getInstance().getPrefix()
            ));
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        event.setCancelled(true);

        if (BottledXP.getInstance().getBooleanFromConfig("replaceInteractWithGUI")){
            player.openInventory(BottleInventory.getInventory(player));
            return;
        }

        int amount = XPUtils.getInventoryBottleAmount(player);

        if (amount <= 0){
            BottledXP.getInstance().sendActionbar(player, "blockInteractConvert.convertNoXPBottles", Map.of(
                    "%prefix%", BottledXP.getInstance().getPrefix()
            ));
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        XPUtils.convertBottlesToXp(player);
        BottledXP.getInstance().sendActionbar(player, "blockInteractConvert.convertSuccessful", Map.of(
                "%prefix%", BottledXP.getInstance().getPrefix(),
                "%bottles%", String.valueOf(amount)
        ));
        BottledXP.getInstance().playConfigSound(player, "sounds.successfulSound");

    }

    @EventHandler
    public void onBlockRightClickForFill(PlayerInteractEvent event){

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!BottledXP.getInstance().getBooleanFromConfig("blockInteractFill.use")) return;
        if (block == null) return;

        String blockString = BottledXP.getInstance().getStringFromConfig("blockInteractFill.block");
        Material blockMaterial = Material.matchMaterial(blockString);
        String itemString = BottledXP.getInstance().getStringFromConfig("blockInteractFill.usedItem");
        Material itemMaterial = Material.matchMaterial(itemString);

        if (blockMaterial == null || block.getType() != blockMaterial) return;

        ItemStack hand = event.getItem();
        if (hand == null || hand.getType() != itemMaterial) return;

        if (!player.hasPermission("bottledxp.interact.fill")) {
            BottledXP.getInstance().sendActionbar(player, "blockInteractFill.hotbarMessageNoPerm", Map.of(
                    "%prefix%", BottledXP.getInstance().getPrefix()
            ));
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        event.setCancelled(true);

        if (BottledXP.getInstance().getBooleanFromConfig("replaceInteractWithGUI")){
            player.openInventory(BottleInventory.getInventory(player));
            return;
        }

        if (XPUtils.getAvailableBottleSpace(player.getInventory()) <= 0) {
            BottledXP.getInstance().sendActionbar(player, "blockInteractFill.notEnoughSpace");
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        Integer toFill;

        try {
            toFill = BottledXP.getInstance().getIntFromConfig("blockInteractFill.bottlesPerClick");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(BottledXP.getInstance().getPrefix() + "§4Invalid Number in config.yml: §cblockInteractFill.bottlesPerClick");
            return;
        }

        if (toFill == null) {
            Bukkit.getConsoleSender().sendMessage(BottledXP.getInstance().getPrefix() + "§4Invalid Number in config.yml: §cblockInteractFill.bottlesPerClick");
            return;
        }

        if (toFill > XPUtils.getBottles(player)){
            BottledXP.getInstance().sendActionbar(player, "blockInteractFill.notEnoughXP");
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }


        if (event.getPlayer().isSneaking() && player.hasPermission("bottledxp.interact.completeLevel")){
            int maxBottles = XPUtils.getBottles(player);

            if (maxBottles <= 0) {
                BottledXP.getInstance().sendActionbar(player, "blockInteractFill.notEnoughXP");
                BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            } else if (XPUtils.getAvailableBottleSpace(player.getInventory()) <= 0) {
                BottledXP.getInstance().sendActionbar(player, "blockInteractFill.notEnoughSpace");
                BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            } else {
                XPUtils.xpBottle(player);
                BottledXP.getInstance().sendActionbar(player, "blockInteractFill.filledAllMessage", Map.of(
                        "%bottles%", String.valueOf(maxBottles)
                ));
                BottledXP.getInstance().playConfigSound(player, "sounds.successfulSound");
            }
            return;
        }

        XPUtils.fillExactAmount(player, toFill);
        BottledXP.getInstance().sendActionbar(player, "blockInteractFill.filledMessage", Map.of(
                "%bottles%", String.valueOf(XPUtils.getBottles(player))
        ));
        BottledXP.getInstance().playConfigSound(player, "sounds.successfulSound");

    }

    @EventHandler
    public void onBlockLeftClick(PlayerInteractEvent event){

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!BottledXP.getInstance().getBooleanFromConfig("blockInteractFill.use")) return;
        if (block == null) return;

        String blockString = BottledXP.getInstance().getStringFromConfig("blockInteractFill.block");
        Material blockMaterial = Material.matchMaterial(blockString);
        String itemString = BottledXP.getInstance().getStringFromConfig("blockInteractFill.usedItem");
        Material itemMaterial = Material.matchMaterial(itemString);

        if (blockMaterial == null || block.getType() != blockMaterial) return;

        ItemStack hand = event.getItem();
        if (hand == null || hand.getType() != itemMaterial) return;

        if (!player.hasPermission("bottledxp.interact.info")) {
            BottledXP.getInstance().sendActionbar(player, "blockInteractFill.hotbarMessageNoPerm", Map.of(
                    "%prefix%", BottledXP.getInstance().getPrefix()
            ));
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        event.setCancelled(true);

        int xp = XPUtils.getCurrentExp(player);
        int space = XPUtils.getAvailableBottleSpace(player.getInventory());
        int bottlesByXp = XPUtils.getBottles(player);

        String firstLine = BottledXP.getInstance().getLanguageManager().getMessage("blockInteractFill.infoTitleFirstLine")
                .replace("%xp%", String.valueOf(xp))
                .replace("%space%", String.valueOf(space))
                .replace("%bottles%", String.valueOf(bottlesByXp))
                .replace("%prefix%", BottledXP.getInstance().getPrefix());
        String secondLine = BottledXP.getInstance().getLanguageManager().getMessage("blockInteractFill.infoTitleSecondLine")
                .replace("%xp%", String.valueOf(xp))
                .replace("%space%", String.valueOf(space))
                .replace("%bottles%", String.valueOf(bottlesByXp))
                .replace("%prefix%", BottledXP.getInstance().getPrefix());

        int stayTime = BottledXP.getInstance().getIntFromConfig("blockInteractFill.infoTitleStayTime")*20;

        player.sendTitle(firstLine, secondLine, 20, stayTime, 20);
        BottledXP.getInstance().playConfigSound(player, "sounds.helpSound");
    }

}
