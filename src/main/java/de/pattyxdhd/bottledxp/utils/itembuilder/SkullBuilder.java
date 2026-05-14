package de.pattyxdhd.bottledxp.utils.itembuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkullBuilder {

    private Material material;
    private int amount;
    private boolean unbreakable;
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantmentIntegerMap;
    private String owner;
    private ArrayList<ItemFlag> itemFlags;


    public SkullBuilder() {
        this.material = Material.PLAYER_HEAD;
        this.amount = 1;
        this.unbreakable = false;
        this.enchantmentIntegerMap = new HashMap<>();
        this.itemFlags = new ArrayList<>();
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(this.material, this.amount);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setUnbreakable(this.unbreakable);

        if (this.displayName != null) {
            skullMeta.setDisplayName(this.displayName);
        }

        if (this.lore != null) {
            skullMeta.setLore(this.lore);
        }

        if (this.owner != null) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(this.owner));
        }

        if(this.itemFlags != null){
            itemFlags.forEach(skullMeta::addItemFlags);
        }

        itemStack.setItemMeta(skullMeta);
        this.enchantmentIntegerMap.forEach(itemStack::addUnsafeEnchantment);
        return itemStack;
    }

    public SkullBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public SkullBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SkullBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public SkullBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantmentIntegerMap.put(enchantment, level);
        return this;
    }

    public SkullBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public SkullBuilder addItemFlag(ItemFlag itemFlag){
        this.itemFlags.add(itemFlag);
        return this;
    }

    public SkullBuilder setSkullOwner(String owner) {
        this.owner = owner;
        return this;
    }


}
