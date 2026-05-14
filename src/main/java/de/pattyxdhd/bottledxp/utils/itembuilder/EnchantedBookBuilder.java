package de.pattyxdhd.bottledxp.utils.itembuilder;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantedBookBuilder {

    private Material material;
    private int amount;
    private boolean unbreakable;
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantmentIntegerMap;
    private ArrayList<ItemFlag> itemFlags;

    public EnchantedBookBuilder() {
        this.material = Material.ENCHANTED_BOOK;
        this.amount = 1;
        this.unbreakable = false;
        this.enchantmentIntegerMap = new HashMap<>();
        this.itemFlags = new ArrayList<>();
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(this.material, this.amount);
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
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

        if(!enchantmentIntegerMap.isEmpty()){
            this.enchantmentIntegerMap.forEach((enchantment, integer) -> {
                itemMeta.addStoredEnchant(enchantment, integer, true);
            });
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public EnchantedBookBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public EnchantedBookBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public EnchantedBookBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public EnchantedBookBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantmentIntegerMap.put(enchantment, level);
        return this;
    }

    public EnchantedBookBuilder addItemFlag(ItemFlag itemFlag){
        this.itemFlags.add(itemFlag);
        return this;
    }

    public EnchantedBookBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

}
