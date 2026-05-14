package de.pattyxdhd.bottledxp.utils.itembuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class PotionBuilder {

    private Material material;
    private int amount;
    private boolean unbreakable;
    private String displayName;
    private List<String> lore;
    private ArrayList<ItemFlag> itemFlags;
    private PotionType potionType;
    private boolean splash;
    private boolean extended;
    private boolean upgraded;

    public PotionBuilder() {
        this.material = Material.POTION;
        this.amount = 1;
        this.unbreakable = false;
        this.itemFlags = new ArrayList<>();
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(this.splash ? Material.SPLASH_POTION : this.material, this.amount);
        PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
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

        if(this.potionType != null){
            itemMeta.setBasePotionData(new PotionData(this.potionType, this.extended, this.upgraded));
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public PotionBuilder setMaterial(Material material){
        this.material = material;
        return this;
    }

    public PotionBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    @Deprecated
    public PotionBuilder setDurability(short durability) {
        return this;
    }

    public PotionBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public PotionBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public PotionBuilder addItemFlag(ItemFlag itemFlag){
        this.itemFlags.add(itemFlag);
        return this;
    }

    public PotionBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public PotionBuilder setPotionType(PotionType potionType, int level){
        this.potionType = potionType;
        this.upgraded = level > 1;
        return this;
    }

    public PotionBuilder setSplash(boolean splash){
        this.splash = splash;
        return this;
    }

    public PotionBuilder setExtendDuration(boolean duration){
        this.extended = duration;
        return this;
    }

}
