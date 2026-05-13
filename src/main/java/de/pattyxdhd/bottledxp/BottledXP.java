package de.pattyxdhd.bottledxp;

import com.google.common.collect.Lists;
import de.pattyxdhd.bottledxp.commands.BottledXPCommand;
import de.pattyxdhd.bottledxp.commands.BottledXPCommandTabCompleter;
import de.pattyxdhd.bottledxp.listener.ExpBottleListener;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BottledXP extends JavaPlugin {

    // Made by PattyXDHD with ♥

    @Getter
    private static BottledXP instance;

    @Getter
    private String prefix = "§8▌ §aBottledXP §8» §7";

    private final String defaultPrefix = "§8▌ §aBottledXP §8» §7";

    @Getter
    private String noPerm = prefix + "§4Dazu hast du keinen Zugriff.";

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadConfig();

        PluginCommand bottlexpCommand = getCommand("bottledxp");
        bottlexpCommand.setExecutor(new BottledXPCommand());
        bottlexpCommand.setTabCompleter(new BottledXPCommandTabCompleter());

        Bukkit.getPluginManager().registerEvents(new ExpBottleListener(), this);

        Bukkit.getConsoleSender().sendMessage(defaultPrefix + "§aPlugin geladen.");
        Bukkit.getConsoleSender().sendMessage(defaultPrefix + "§9Version: §bv" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(defaultPrefix + "§cPlugin entladen.");
    }

    public void loadConfig(){
        if (!getStringFromConfig("messages.prefix").equals("§c<Missing config entry.>")){
            prefix = getStringFromConfig("messages.prefix");
        }
        noPerm = prefix + getStringFromConfig("messages.noPerm");

        try {
            XPUtils.setXpAmount(getIntFromConfig("xpPerBottle"));
        } catch (Exception e){
            XPUtils.setXpAmount(7);
        }
    }

    public String getStringFromConfig(String configPath){
        String output = getConfig().getString(configPath);
        if (output == null) return "§c<Missing config entry.>";
        output = ChatColor.translateAlternateColorCodes('&', output);
        return output.replace("%prefix%", getPrefix());
    }

    public boolean getBooleanFromConfig(String configPath){
        return getConfig().getBoolean(configPath);
    }

    public Integer getIntFromConfig(String configPath){
        return getConfig().getInt(configPath);
    }

    public Double getDoubleFromConfig(String configPath) {
        return getConfig().getDouble(configPath);
    }

    public float getFloatFromConfig(String configPath) {
        return (float) getConfig().getDouble(configPath);
    }

    public List<String> getStringListFromConfig(String configPath){
        List<String> output = getConfig().getStringList(configPath);
        if (output == null) return Lists.newArrayList("§c<Missing config entry.");
        List<String> outputWithColor = Lists.newArrayList();
        for (String out : output){
            outputWithColor.add(ChatColor.translateAlternateColorCodes('&', out).replaceAll("%prefix%", getPrefix()));
        }
        return outputWithColor;
    }

    public void playConfigSound(Player player, String configPath){
        boolean use = getBooleanFromConfig("sounds.use");
        if (!use){
            return;
        }

        String soundName = getStringFromConfig(configPath);
        Sound sound;
        try {
            sound = Sound.valueOf(soundName);
        } catch (IllegalArgumentException exception){
            Bukkit.getConsoleSender().sendMessage(defaultPrefix + "§4Invalid sound name in config.yml: §c" + soundName);
            return;
        }

        float volume = getFloatFromConfig("sounds.volume");

        if (volume == 0.0f){
            Bukkit.getConsoleSender().sendMessage(defaultPrefix + "§4Invalid float in config.yml: §c" + volume);
            return;
        }

        player.playSound(player.getLocation(), sound, volume, 1);
    }

}
