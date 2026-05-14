package de.pattyxdhd.bottledxp.utils.itembuilder;

public class ItemBuilder {

    public static SimpleItemBuilder normal() {
        return new SimpleItemBuilder();
    }

    public static ArmorItemBuilder armorBuilder() {
        return new ArmorItemBuilder();
    }

    public static SkullBuilder skullBuilder() {
        return new SkullBuilder();
    }

    public static CustomSkullBuilder customSkullBuilder() {
        return new CustomSkullBuilder();
    }

    public static PotionBuilder potionBuilder() {
        return new PotionBuilder();
    }

    public static EnchantedBookBuilder enchantedBookBuilder() {
        return new EnchantedBookBuilder();
    }

}
