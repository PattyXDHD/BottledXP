package de.pattyxdhd.bottledxp;

import de.pattyxdhd.bottledxp.commands.BottledXPCommand;
import de.pattyxdhd.bottledxp.commands.BottledXPCommandTabCompleter;
import de.pattyxdhd.bottledxp.inventory.BottleInventory;
import de.pattyxdhd.bottledxp.language.LanguageManager;
import de.pattyxdhd.bottledxp.listener.ExpBottleListener;
import de.pattyxdhd.bottledxp.listener.InteractListener;
import de.pattyxdhd.bottledxp.listener.InventoryListener;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class BottledXP extends JavaPlugin {

    // Made by PattyXDHD with ♥

    @Getter
    private static BottledXP instance;

    @Getter
    private String prefix = "§8▌ §aBottledXP §8» §7";

    private final String defaultPrefix = "§8▌ §aBottledXP §8» §7";

    @Getter
    private String noPerm = prefix + "§4You don''t have Permission for that.";

    @Getter
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();
        loadCommands();
        loadListeners();

        Bukkit.getConsoleSender().sendMessage(defaultPrefix + "§aPlugin enabled.");
        Bukkit.getConsoleSender().sendMessage(defaultPrefix + "§9Version: §bv" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(defaultPrefix + "§cPlugin disabled.");
    }

    public void loadConfig(){

        saveDefaultConfig();

        languageManager = new LanguageManager(this);
        languageManager.loadLanguage();

        prefix = languageManager.getMessage("messages.prefix");
        noPerm = prefix + languageManager.getMessage("messages.noPerm");

        try {
            XPUtils.setXpAmount(getIntFromConfig("xpPerBottle"));
        } catch (Exception e){
            XPUtils.setXpAmount(7);
        }

        BottleInventory.loadItemNames();

    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new ExpBottleListener(), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
    }

    private void loadCommands() {
        PluginCommand bottledxpCommand = getCommand("bottledxp");
        bottledxpCommand.setExecutor(new BottledXPCommand());
        bottledxpCommand.setTabCompleter(new BottledXPCommandTabCompleter());
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

    public float getFloatFromConfig(String configPath) {
        return (float) getConfig().getDouble(configPath);
    }

    public void playConfigSound(Player player, String configPath){
        boolean use = getBooleanFromConfig("sounds.use");
        if (!use){
            return;
        }

        String soundName = getStringFromConfig(configPath);
        Sound sound;

        if (soundName.equals("NONE")){
            return;
        }

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

    public void sendActionbar(Player player, String configPath){
        String message = BottledXP.getInstance().getLanguageManager().getMessage(configPath);
        if (hasSpigot()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
            if (!message.startsWith(prefix)) message = prefix + message;
            player.sendMessage(message);
        }
    }

    public void sendActionbar(Player player, String configPath, Map<String, String> replacements){
        String message = BottledXP.getInstance().getLanguageManager().getMessage(configPath);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String s = entry.getKey();
            String s2 = entry.getValue();
            message = message.replace(s, s2);
        }

        if (hasSpigot()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
            if (!message.startsWith(prefix)) message = prefix + message;
            player.sendMessage(message);
        }
    }

    public static boolean hasSpigot() {
        try {
            Player.class.getMethod("spigot");
            return true;
        } catch (NoSuchMethodException exception) {
            return false;
        }
    }

}
