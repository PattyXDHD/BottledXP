package de.pattyxdhd.bottledxp.inventory;

import com.google.common.collect.Lists;
import de.pattyxdhd.bottledxp.BottledXP;
import de.pattyxdhd.bottledxp.utils.XPUtils;
import de.pattyxdhd.bottledxp.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;

public class BottleInventory {

    private static final BottledXP instance = BottledXP.getInstance();

    @Getter
    private static String minusItemName = instance.getStringFromConfig("inventory.minusItem")
            .replace("%normal%", instance.getIntFromConfig("inventory.minusAmountNormal").toString())
            .replace("%shift%", instance.getIntFromConfig("inventory.minusAmountShift").toString());

    @Getter
    private static String plusItemName = instance.getStringFromConfig("inventory.plusItem")
            .replace("%normal%", instance.getIntFromConfig("inventory.plusAmountNormal").toString())
            .replace("%shift%", instance.getIntFromConfig("inventory.plusAmountShift").toString());



    public static Inventory getInventory(Player player){

        int xp = XPUtils.getCurrentExp(player);
        int space = XPUtils.getAvailableBottleSpace(player.getInventory());
        int bottlesByXp = XPUtils.getBottles(player);
        int levels = player.getLevel();
        int points = XPUtils.getCurrentLevelXp(player);

        Inventory bottleInventory = Bukkit.createInventory(player, 27, instance.getStringFromConfig("inventory.inventoryName"));

        // load mathLore
        ArrayList<String> mathLore = Lists.newArrayList();
        instance.getStringListFromConfig("inventory.mathLore").forEach(s -> mathLore.add(s
                .replace("%bottle%", "1")));



        //set apply button
        ItemStack applyItem = ItemBuilder.normal()
                .setMaterial(Material.EMERALD_BLOCK)
                .setDisplayName(instance.getStringFromConfig("inventory.applyItem"))
                .setLore(mathLore)
                .build();
        bottleInventory.setItem(22, applyItem);


        // set Info Item
        ArrayList<String> infoItemLore = Lists.newArrayList();
        instance.getStringListFromConfig("inventory.infoItemLore").forEach(s -> infoItemLore.add(s
                .replace("%xp%", String.valueOf(xp))
                .replace("%space%", String.valueOf(space))
                .replace("%bottles%", String.valueOf(bottlesByXp))
                .replace("%level%", String.valueOf(levels))
                .replace("%points%", String.valueOf(points))));
        ItemStack infoItem = ItemBuilder.normal()
                .setMaterial(Material.EXPERIENCE_BOTTLE)
                .setDisplayName(instance.getStringFromConfig("inventory.infoItemName"))
                .setLore(infoItemLore)
                .build();
        bottleInventory.setItem(4, infoItem);



        //set minus button
        ItemStack minusItem = ItemBuilder.potionBuilder()
                .setMaterial(Material.TIPPED_ARROW)
                .setPotionType(PotionType.INSTANT_HEAL, 1)
                .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                .setDisplayName(instance.getStringFromConfig("inventory.minusItem")
                        .replace("%normal%", instance.getIntFromConfig("inventory.minusAmountNormal").toString())
                        .replace("%shift%", instance.getIntFromConfig("inventory.minusAmountShift").toString()))
                .setLore(mathLore)
                .build();
        bottleInventory.setItem(11, minusItem);


        //set plus button
        ItemStack plusItem = ItemBuilder.potionBuilder()
                .setMaterial(Material.TIPPED_ARROW)
                .setPotionType(PotionType.LUCK, 1)
                .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                .setDisplayName(instance.getStringFromConfig("inventory.plusItem")
                        .replace("%normal%", instance.getIntFromConfig("inventory.plusAmountNormal").toString())
                        .replace("%shift%", instance.getIntFromConfig("inventory.plusAmountShift").toString()))
                .setLore(mathLore)
                .build();
        bottleInventory.setItem(15, plusItem);


        return bottleInventory;
    }


    public static void updateMathButtons(Player player, int math){

        if (!player.getOpenInventory().getTitle().equals(instance.getStringFromConfig("inventory.inventoryName"))){
            Bukkit.getConsoleSender().sendMessage("FEHLER #1111");
            return;
        }


        ArrayList<String> mathLore = Lists.newArrayList();

        instance.getStringListFromConfig("inventory.mathLore").forEach(s -> mathLore.add(s
                .replace("%bottle%", String.valueOf(math))));

        //set apply button
        ItemStack applyItem = ItemBuilder.normal()
                .setMaterial(Material.EMERALD_BLOCK)
                .setDisplayName(instance.getStringFromConfig("inventory.applyItem"))
                .setLore(mathLore)
                .build();
        player.getOpenInventory().setItem(22, applyItem);

        //set minus button
        ItemStack minusItem = ItemBuilder.potionBuilder()
                .setMaterial(Material.TIPPED_ARROW)
                .setPotionType(PotionType.INSTANT_HEAL, 1)
                .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                .setDisplayName(minusItemName)
                .setLore(mathLore)
                .build();
        player.getOpenInventory().setItem(11, minusItem);


        //set plus button
        ItemStack plusItem = ItemBuilder.potionBuilder()
                .setMaterial(Material.TIPPED_ARROW)
                .setPotionType(PotionType.LUCK, 1)
                .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                .setDisplayName(plusItemName)
                .setLore(mathLore)
                .build();
        player.getOpenInventory().setItem(15, plusItem);


    }




}
