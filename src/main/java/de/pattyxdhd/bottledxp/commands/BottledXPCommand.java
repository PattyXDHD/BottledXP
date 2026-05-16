package de.pattyxdhd.bottledxp.commands;

import de.pattyxdhd.bottledxp.BottledXP;
import de.pattyxdhd.bottledxp.inventory.BottleInventory;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BottledXPCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
                BottledXP.getInstance().reloadConfig();
                BottledXP.getInstance().loadConfig();
                sender.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.reload"));
             return false;
            }

            sender.sendMessage(BottledXP.getInstance().getPrefix() + "Du musst ein Spieler sein.");
            return false;
        }

        Player player = ((Player) sender);

        if (args.length == 0){
            sendHelp(player);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {

            OfflinePlayer value = Bukkit.getOfflinePlayer(args[1]);
            showInfoForOtherPlayer(player, value);

        } else {
            switch (args[0].toLowerCase()){
                case "info": showInfo(player); return false;
                case "fill": handelFill(player, args); return false;
                case "reload": reloadConfig(player); return false;
                case "gui": openGui(player);return false;
                case "convert": convertBottlesToXP(player); return false;
                default: sendHelp(player); return false;
            }
        }

        return false;
    }

    private void convertBottlesToXP(Player player) {

        if (!player.hasPermission("bottledxp.convert")){
            player.sendMessage(BottledXP.getInstance().getNoPerm());
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        int amount = XPUtils.getInventoryBottleAmount(player);

        if (amount <= 0){
            player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.convertNoXPBottles"));
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        XPUtils.convertBottlesToXp(player);
        player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.convertSuccessful")
                .replace("%bottles%", String.valueOf(amount)));
        BottledXP.getInstance().playConfigSound(player, "sounds.successfulSound");

    }

    private void sendHelp(Player player) {
        List<String> infoText = BottledXP.getInstance().getLanguageManager().getMessageList("messages.help");
        for (String text : infoText){
            player.sendMessage(text.replace("%prefix%", BottledXP.getInstance().getPrefix()));
        }
    }

    private void openGui(Player player){

        if (!player.hasPermission("bottledxp.gui")){
            player.sendMessage(BottledXP.getInstance().getNoPerm());
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        player.openInventory(BottleInventory.getInventory(player));

    }

    private void showInfo(Player player) {
        if (!player.hasPermission("bottledxp.info")){
            player.sendMessage(BottledXP.getInstance().getNoPerm());
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        int xp = XPUtils.getCurrentExp(player);
        int space = XPUtils.getAvailableBottleSpace(player.getInventory());
        int bottlesByXp = XPUtils.getBottles(player);
        int levels = player.getLevel();
        int points = XPUtils.getCurrentLevelXp(player);
        List<String> infoText = BottledXP.getInstance().getLanguageManager().getMessageList("messages.info");

        for (String text : infoText){
            player.sendMessage(text
                    .replace("%xp%", String.valueOf(xp))
                    .replace("%space%", String.valueOf(space))
                    .replace("%bottles%", String.valueOf(bottlesByXp))
                    .replace("%level%", String.valueOf(levels))
                    .replace("%points%", String.valueOf(points))
                    .replace("%prefix%", BottledXP.getInstance().getPrefix()));
        }
        BottledXP.getInstance().playConfigSound(player, "sounds.helpSound");
    }

    private void showInfoForOtherPlayer(Player sender, OfflinePlayer value) {

        if (!sender.hasPermission("bottledxp.info.other")){
            sender.sendMessage(BottledXP.getInstance().getNoPerm());
            BottledXP.getInstance().playConfigSound(sender, "sounds.failSound");
            return;
        }

        if (!value.isOnline()){
            sender.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.playerNotOnline").replace("%name%", value.getName()));
            return;
        }

        Player info = Bukkit.getPlayer(value.getUniqueId());

        int xp = XPUtils.getCurrentExp(info);
        int space = XPUtils.getAvailableBottleSpace(info.getInventory());
        int bottlesByXp = XPUtils.getBottles(info);
        int levels = info.getLevel();
        int points = XPUtils.getCurrentLevelXp(info);
        List<String> infoText = BottledXP.getInstance().getLanguageManager().getMessageList("messages.infoOther");

        for (String text : infoText){
            sender.sendMessage(text
                    .replace("%xp%", String.valueOf(xp))
                    .replace("%space%", String.valueOf(space))
                    .replace("%bottles%", String.valueOf(bottlesByXp))
                    .replace("%level%", String.valueOf(levels))
                    .replace("%points%", String.valueOf(points))
                    .replace("%prefix%", BottledXP.getInstance().getPrefix())
                    .replace("%playerName%", info.getName()));
        }

        BottledXP.getInstance().playConfigSound(sender, "sounds.helpSound");
    }

    private void handelFill(Player player, String[] args){
        if (args.length >=1 && args.length <= 2){
            if (args.length == 1){
                if (!player.hasPermission("bottledxp.fill.completeLevel")){
                    player.sendMessage(BottledXP.getInstance().getNoPerm());
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                    return;
                }

                if (XPUtils.getBottles(player) <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.notEnoughXP"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                } else if (XPUtils.getAvailableBottleSpace(player.getInventory()) <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.notEnoughSpace"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                } else {
                    XPUtils.xpBottle(player);
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.filled"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.successfulSound");
                }

            } else {
                if (!player.hasPermission("bottledxp.fill.amount")){
                    player.sendMessage(BottledXP.getInstance().getNoPerm());
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                    return;
                }

                int requested;

                try {
                    requested = Integer.parseInt(args[1]);
                } catch (NumberFormatException exception){
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.noWholeNumber").replace("%noNumber%", args[1]));
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                    return;
                }

                if (requested <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.zeroBottles"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                    return;
                }

                int maxAllowed = 10000;
                if (requested > maxAllowed) {
                    requested = maxAllowed;
                }

                int possible = Math.min(XPUtils.getBottles(player), XPUtils.getAvailableBottleSpace(player.getInventory()));
                if (possible <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.notEnoughSpace"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
                    return;
                }

                int toFill = Math.min(requested, possible);
                XPUtils.fillExactAmount(player, toFill);
                player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.filledAmount").replace("%filled%", String.valueOf(toFill)));
                BottledXP.getInstance().playConfigSound(player, "sounds.successfulSound");
            }
        } else {
            player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.useHelp"));
        }
    }

    private void reloadConfig(Player player) {
        if (!player.hasPermission("bottledxp.reload")){
            player.sendMessage(BottledXP.getInstance().getNoPerm());
            BottledXP.getInstance().playConfigSound(player, "sounds.failSound");
            return;
        }

        BottledXP.getInstance().reloadConfig();
        BottledXP.getInstance().loadConfig();
        player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getLanguageManager().getMessage("messages.reload"));
    }

}
