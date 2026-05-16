package de.pattyxdhd.bottledxp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BottledXPCommandTabCompleter implements TabCompleter {

    private static final List<String> commandList = Arrays.asList("help", "info", "reload", "fill", "gui", "convert");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (!(sender instanceof Player)){
            return null;
        }

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> out = new ArrayList<>();
            for (String s : commandList) {
                if (s.startsWith(prefix))
                    out.add(s);
            }
            return out;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("fill")) {
            List<String> suggestions = new ArrayList<>();
            int[] nums = { 1, 5, 10, 16, 32, 64, 128, 256, 512, 1000 };
            for (int n : nums) {
                String str = String.valueOf(n);
                if (str.startsWith(args[1]))
                    suggestions.add(str);
            }
            return suggestions;
        }

        // TODO: fix the tab complete for Player Names that don't match the Case
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            List<String> suggestions = new ArrayList<>();
            for (Player n : Bukkit.getOnlinePlayers()) {
                String str = String.valueOf(n.getName());
                if (str.startsWith((args[1])))
                    suggestions.add(str);
            }
            return suggestions;
        }
        return new ArrayList<>();
    }
}
