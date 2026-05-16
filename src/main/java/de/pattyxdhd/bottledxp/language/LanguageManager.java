package de.pattyxdhd.bottledxp.language;

import com.google.common.collect.Lists;
import de.pattyxdhd.bottledxp.BottledXP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class LanguageManager {

    private final BottledXP plugin;
    private FileConfiguration languageConfig;

    public LanguageManager(BottledXP plugin) {
        this.plugin = plugin;
    }

    public void loadLanguage() {
        saveLanguageFile("lang/en_US.yml");
        saveLanguageFile("lang/de_DE.yml");

        String language = plugin.getConfig().getString("language", "de_DE");

        File languageFile = new File(plugin.getDataFolder(), "lang/" + language + ".yml");

        if (!languageFile.exists()) {
            languageFile = new File(plugin.getDataFolder(), "lang/de_DE.yml");
        }

        this.languageConfig = YamlConfiguration.loadConfiguration(languageFile);
    }

    private void saveLanguageFile(String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);

        if (!file.exists()) {
            plugin.saveResource(resourcePath, false);
            Bukkit.getConsoleSender().sendMessage(BottledXP.getInstance().getPrefix() + "§7Created: " + resourcePath);
        }


    }

    public String getMessage(String path) {
        String message = languageConfig.getString(path);

        if (message == null) {
            return "§c<Missing language entry: " + path + ">";
        }

        String prefix = languageConfig.getString("messages.prefix", "§8▌ §aBottledXP §8» §7");

        message = message.replace("%prefix%", prefix);

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> getMessageList(String path) {
        List<String> messages = languageConfig.getStringList(path);
        List<String> messagesConverted = Lists.newArrayList();

        if (messages.isEmpty()){
            messagesConverted.add("§c<Missing language entry: " + path + ">");
            return messagesConverted;
        }

        String prefix = languageConfig.getString("messages.prefix");

        messages.forEach(message -> messagesConverted.add(ChatColor.translateAlternateColorCodes('&', message.replace("%prefix%", prefix))));

        return messagesConverted;
    }


}
