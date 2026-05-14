package de.pattyxdhd.bottledxp.utils.itembuilder;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorItemBuilder {

    private Material material = Material.LEATHER_CHESTPLATE;
    private int amount = 1;
    private boolean unbreakable = false;
    private String displayName;
    private List<String> lore;
    private Color color = Color.WHITE;
    private Map<Enchantment, Integer> enchantmentIntegerMap;
    private ArrayList<ItemFlag> itemFlags;

    public ArmorItemBuilder(){
        this.material = Material.LEATHER_HELMET;
        this.amount = 1;
        this.unbreakable = false;
        this.enchantmentIntegerMap = new HashMap<>();
        this.itemFlags = new ArrayList<>();
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(this.material, this.amount);
        ItemMeta armorMeta = itemStack.getItemMeta();
        armorMeta.setUnbreakable(this.unbreakable);
        if (this.displayName != null) {
            armorMeta.setDisplayName(this.displayName);
        }
        if (this.lore != null) {
            armorMeta.setLore(this.lore);
        }

        if (armorMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) armorMeta).setColor(this.color);
        }
        this.itemFlags.forEach(armorMeta::addItemFlags);
        this.enchantmentIntegerMap.forEach(itemStack::addUnsafeEnchantment);
        itemStack.setItemMeta(armorMeta);

        return itemStack;
    }

    public ArmorItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ArmorItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ArmorItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ArmorItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ArmorItemBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public ArmorItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.enchantmentIntegerMap.put(enchantment, level);
        return this;
    }

    public ArmorItemBuilder addItemFlag(ItemFlag itemFlag){
        this.itemFlags.add(itemFlag);
        return this;
    }

    public ArmorItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

}
