package de.pattyxdhd.bottledxp.utils.itembuilder;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleItemBuilder {

    private Material material;
    private int amount;
    private boolean unbreakable;
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantmentIntegerMap;
    private ArrayList<ItemFlag> itemFlags;

    public SimpleItemBuilder() {
        this.material = Material.GRASS_BLOCK;
        this.amount = 1;
        this.unbreakable = false;
        this.enchantmentIntegerMap = new HashMap<>();
        this.itemFlags = new ArrayList<>();
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(this.material, this.amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(this.unbreakable);

        if (this.displayName != null) {
            itemMeta.setDisplayName(this.displayName);
        }

        if (this.lore != null) {
            itemMeta.setLore(this.lore);
        }

        if(this.itemFlags != null){
            itemFlags.forEach(itemMeta::addItemFlags);
        }

        itemStack.setItemMeta(itemMeta);
        this.enchantmentIntegerMap.forEach(itemStack::addUnsafeEnchantment);
        return itemStack;
    }

    public SimpleItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public SimpleItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    @Deprecated
    public SimpleItemBuilder setDurability(short durability) {
        return this;
    }

    public SimpleItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SimpleItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public SimpleItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantmentIntegerMap.put(enchantment, level);
        return this;
    }

    public SimpleItemBuilder addItemFlag(ItemFlag itemFlag){
        this.itemFlags.add(itemFlag);
        return this;
    }

    public SimpleItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

}
