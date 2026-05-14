package de.pattyxdhd.bottledxp.utils.itembuilder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CustomSkullBuilder {

    private Material material;
    private int amount;
    private boolean unbreakable;
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantmentIntegerMap;
    private String url;

    public CustomSkullBuilder() {
        this.material = Material.PLAYER_HEAD;
        this.amount = 1;
        this.unbreakable = false;
        this.enchantmentIntegerMap = new HashMap<>();
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(this.material, this.amount);
        ItemMeta skullMeta = itemStack.getItemMeta();
        skullMeta.setUnbreakable(this.unbreakable);

        if (this.displayName != null) {
            skullMeta.setDisplayName(this.displayName);
        }

        if (this.lore != null) {
            skullMeta.setLore(this.lore);
        }

        if (this.url != null) {
            applyTexture(skullMeta, this.url);
        }

        itemStack.setItemMeta(skullMeta);
        this.enchantmentIntegerMap.forEach(itemStack::addUnsafeEnchantment);
        return itemStack;
    }

    public CustomSkullBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public CustomSkullBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    @Deprecated
    public CustomSkullBuilder setDurability(short durability) {
        return this;
    }

    public CustomSkullBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public CustomSkullBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public CustomSkullBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantmentIntegerMap.put(enchantment, level);
        return this;
    }

    public CustomSkullBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public CustomSkullBuilder setSkullURL(String url) {
        this.url = url;
        return this;
    }

    private void applyTexture(ItemMeta skullMeta, String texture) {
        if (!(skullMeta instanceof SkullMeta) || applyBukkitProfile((SkullMeta) skullMeta, texture)) {
            return;
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            if (profileField.getType().isAssignableFrom(GameProfile.class)) {
                profileField.set(skullMeta, profile);
            }
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
    }

    private boolean applyBukkitProfile(SkullMeta skullMeta, String texture) {
        try {
            String skinUrl = extractSkinUrl(texture);
            if (skinUrl == null) {
                return false;
            }

            Method createProfile = Bukkit.class.getMethod("createPlayerProfile", UUID.class, String.class);
            Class<?> playerProfileClass = Class.forName("org.bukkit.profile.PlayerProfile");
            Class<?> playerTexturesClass = Class.forName("org.bukkit.profile.PlayerTextures");
            Object profile = createProfile.invoke(null, UUID.randomUUID(), null);
            Object textures = profile.getClass().getMethod("getTextures").invoke(profile);
            textures.getClass().getMethod("setSkin", URL.class).invoke(textures, new URL(skinUrl));
            profile.getClass().getMethod("setTextures", playerTexturesClass).invoke(profile, textures);
            skullMeta.getClass().getMethod("setOwnerProfile", playerProfileClass).invoke(skullMeta, profile);
            return true;
        } catch (ReflectiveOperationException | IllegalArgumentException | java.net.MalformedURLException ignored) {
            return false;
        }
    }

    private String extractSkinUrl(String texture) {
        if (texture.startsWith("http://") || texture.startsWith("https://")) {
            return texture;
        }

        try {
            String json = new String(Base64.getDecoder().decode(texture), StandardCharsets.UTF_8);
            JsonObject object = new JsonParser().parse(json).getAsJsonObject();
            return object.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException error) {
            return null;
        }
    }

}
