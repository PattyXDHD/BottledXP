package de.pattyxdhd.bottledxp.listener;

import de.pattyxdhd.bottledxp.BottledXP;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExpBottleListener implements Listener {

    private final XPUtils xpUtils = new XPUtils();

    @EventHandler
    public void onExp(ExpBottleEvent expBottleEvent){
        expBottleEvent.setExperience(XPUtils.getXpAmount());
    }


    @EventHandler
    public void onBlockRightClick(PlayerInteractEvent event){

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
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(BottledXP.getInstance().getStringFromConfig("blockInteractFill.hotbarMessageNoPerm").replace("%prefix%", BottledXP.getInstance().getPrefix())));
            BottledXP.getInstance().playConfigSound(player, "sounds.fail");
            return;
        }

        event.setCancelled(true);

        if (xpUtils.getAvailableBottleSpace(player.getInventory()) <= 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(BottledXP.getInstance().getStringFromConfig("blockInteractFill.notEnoughSpace")));
            BottledXP.getInstance().playConfigSound(player, "sounds.fail");
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

        if (toFill > xpUtils.getBottles(player)){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(BottledXP.getInstance().getStringFromConfig("blockInteractFill.notEnoughXP")));
            BottledXP.getInstance().playConfigSound(player, "sounds.fail");
            return;
        }


        if (event.getPlayer().isSneaking() && player.hasPermission("bottledxp.interact.completeLevel")){
            int maxBottles = xpUtils.getBottles(player);

            if (maxBottles <= 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(BottledXP.getInstance().getStringFromConfig("blockInteractFill.notEnoughXP")));
                BottledXP.getInstance().playConfigSound(player, "sounds.fail");
            } else if (xpUtils.getAvailableBottleSpace(player.getInventory()) <= 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(BottledXP.getInstance().getStringFromConfig("blockInteractFill.notEnoughSpace")));
                BottledXP.getInstance().playConfigSound(player, "sounds.fail");
            } else {
                xpUtils.xpBottle(player);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(BottledXP.getInstance().getStringFromConfig("blockInteractFill.filledAllMessage").replace("%bottles%", String.valueOf(maxBottles))));
                BottledXP.getInstance().playConfigSound(player, "sounds.successfullyFilled");
            }
            return;
        }

        xpUtils.fillExactAmount(player, toFill);

        if (BottledXP.getInstance().getBooleanFromConfig("blockInteractFill.useHotbarMessage")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(BottledXP.getInstance().getStringFromConfig("blockInteractFill.filledMessage").replace("%bottles%", String.valueOf(xpUtils.getBottles(player)))));
        }

        if (BottledXP.getInstance().getBooleanFromConfig("blockInteractFill.useSound")) {
            BottledXP.getInstance().playConfigSound(player, "sounds.successfullyFilled");
        }

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
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(BottledXP.getInstance().getStringFromConfig("blockInteractFill.hotbarMessageNoPerm").replace("%prefix%", BottledXP.getInstance().getPrefix())));
            BottledXP.getInstance().playConfigSound(player, "sounds.fail");
            return;
        }

        event.setCancelled(true);

        int xp = xpUtils.getCurrentExp(player);
        int space = xpUtils.getAvailableBottleSpace(player.getInventory());
        int bottlesByXp = xpUtils.getBottles(player);

        String firstLine = BottledXP.getInstance().getStringFromConfig("blockInteractFill.infoTitleFirstLine")
                .replace("%xp%", String.valueOf(xp))
                .replace("%space%", String.valueOf(space))
                .replace("%bottles%", String.valueOf(bottlesByXp))
                .replace("%prefix%", BottledXP.getInstance().getPrefix());
        String secondLine = BottledXP.getInstance().getStringFromConfig("blockInteractFill.infoTitleSecondLine")
                .replace("%xp%", String.valueOf(xp))
                .replace("%space%", String.valueOf(space))
                .replace("%bottles%", String.valueOf(bottlesByXp))
                .replace("%prefix%", BottledXP.getInstance().getPrefix());

        int stayTime = BottledXP.getInstance().getIntFromConfig("blockInteractFill.infoTitleStayTime")*20;

        player.sendTitle(firstLine, secondLine, 20, stayTime, 20);
    }

}
