package de.pattyxdhd.bottledxp.commands;

import de.pattyxdhd.bottledxp.BottledXP;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BottledXPCommand implements CommandExecutor {

    private final XPUtils xpUtils = new XPUtils();


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
                BottledXP.getInstance().reloadConfig();
                BottledXP.getInstance().loadConfig();
                sender.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.reload"));
             return false;
            }

            sender.sendMessage(BottledXP.getInstance().getPrefix() + "Du musst ein Spieler sein.");
            return false;
        }

        Player player = ((Player) sender);

        if (args.length == 0){
            sendHelp(player);
        }else {
            switch (args[0].toLowerCase()){
                case "info": showInfo(player); return false;
                case "fill": handelFill(player, args); return false;
                case "reload": reloadConfig(player); return false;
                default: sendHelp(player); return false;
            }
        }

        return false;
    }

    private void sendHelp(Player player) {
        List<String> infoText = BottledXP.getInstance().getStringListFromConfig("messages.help");
        for (String text : infoText){
            player.sendMessage(text.replace("%prefix%", BottledXP.getInstance().getPrefix()));
        }
    }

    private void showInfo(Player player) {
        if (!player.hasPermission("bottledxp.info")){
            player.sendMessage(BottledXP.getInstance().getNoPerm());
            BottledXP.getInstance().playConfigSound(player, "sounds.fail");
            return;
        }

        int xp = xpUtils.getCurrentExp(player);
        int space = xpUtils.getAvailableBottleSpace(player.getInventory());
        int bottlesByXp = xpUtils.getBottles(player);
        List<String> infoText = BottledXP.getInstance().getStringListFromConfig("messages.info");

        for (String text : infoText){
            player.sendMessage(text
                    .replace("%xp%", String.valueOf(xp))
                    .replace("%space%", String.valueOf(space))
                    .replace("%bottles%", String.valueOf(bottlesByXp))
                    .replace("%prefix%", BottledXP.getInstance().getPrefix()));
        }
    }

    private void handelFill(Player player, String[] args){
        if (args.length >=1 && args.length <= 2){
            if (args.length == 1){
                if (!player.hasPermission("bottledxp.fill.completeLevel")){
                    player.sendMessage(BottledXP.getInstance().getNoPerm());
                    BottledXP.getInstance().playConfigSound(player, "sounds.fail");
                    return;
                }

                if (xpUtils.getBottles(player) <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.notEnoughXP"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.fail");
                } else if (xpUtils.getAvailableBottleSpace(player.getInventory()) <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.notEnoughSpace"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.fail");
                } else {
                    xpUtils.xpBottle(player);
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.filled"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.successfullyFilled");
                }

            } else {
                if (!player.hasPermission("bottledxp.fill.amount")){
                    player.sendMessage(BottledXP.getInstance().getNoPerm());
                    BottledXP.getInstance().playConfigSound(player, "sounds.fail");
                    return;
                }

                int requested;

                try {
                    requested = Integer.parseInt(args[1]);
                } catch (NumberFormatException exception){
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.noWholeNumber").replace("%noNumber%", args[1]));
                    BottledXP.getInstance().playConfigSound(player, "sounds.fail");
                    return;
                }

                if (requested <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.zeroBottles"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.fail");
                    return;
                }

                int maxAllowed = 10000;
                if (requested > maxAllowed) {
                    requested = maxAllowed;
                }

                int possible = Math.min(xpUtils.getBottles(player), xpUtils.getAvailableBottleSpace(player.getInventory()));
                if (possible <= 0) {
                    player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.notEnoughSpace"));
                    BottledXP.getInstance().playConfigSound(player, "sounds.fail");
                    return;
                }

                int toFill = Math.min(requested, possible);
                xpUtils.fillExactAmount(player, toFill);
                player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.filledAmount").replace("%filled%", String.valueOf(toFill)));
                BottledXP.getInstance().playConfigSound(player, "sounds.successfullyFilled");
            }
        } else {
            player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.useHelp"));
        }
    }

    private void reloadConfig(Player player) {
        if (!player.hasPermission("bottledxp.reload")){
            player.sendMessage(BottledXP.getInstance().getNoPerm());
            BottledXP.getInstance().playConfigSound(player, "sounds.fail");
            return;
        }

        BottledXP.getInstance().reloadConfig();
        BottledXP.getInstance().loadConfig();
        player.sendMessage(BottledXP.getInstance().getPrefix() + BottledXP.getInstance().getStringFromConfig("messages.reload"));
    }

}
