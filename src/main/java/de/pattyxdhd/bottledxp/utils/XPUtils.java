package de.pattyxdhd.bottledxp.utils;

import de.pattyxdhd.bottledxp.BottledXP;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class XPUtils {

    @Getter @Setter
    public static int xpAmount = 7;

    public static int getBottles(Player player) {
        int xp = getCurrentExp(player);
        int xpPerBottle = Math.max(1, xpAmount);
        return xp / xpPerBottle;
    }

    public static int getCurrentExp(Player player) {
        int level = player.getLevel();
        float progress = player.getExp();
        int xp = getExpAtLevel(level);
        xp += Math.round((float)getExpToNextLevel(level) * progress);
        return Math.max(0, xp);
    }

    private static int getExpAtLevel(int level) {
        if (level >= 31) {
            return (int)((double)4.5F * (double)level * (double)level - (double)162.5F * (double)level + (double)2220.0F);
        } else {
            return level >= 16 ? (int)((double)2.5F * (double)level * (double)level - (double)40.5F * (double)level + (double)360.0F) : level * level + 6 * level;
        }
    }

    private static int getExpToNextLevel(int level) {
        if (level >= 31) {
            return 9 * level - 158;
        } else {
            return level >= 16 ? 5 * level - 38 : 2 * level + 7;
        }
    }

    public static int getAvailableBottleSpace(Inventory inventory) {
        int space = 0;

        for(ItemStack item : inventory.getStorageContents()) {
            if (item != null && item.getType() != Material.AIR) {
                if (item.getType() == Material.EXPERIENCE_BOTTLE) {
                    space += 64 - item.getAmount();
                }
            } else {
                space += 64;
            }
        }

        return space;
    }

    public static void xpBottle(Player player) {
        int xpPerBottle = Math.max(1, xpAmount);
        int totalXp = getCurrentExp(player);
        int maxByXp = totalXp / xpPerBottle;
        int space = getAvailableBottleSpace(player.getInventory());
        int toFill = Math.min(maxByXp, space);
        if (toFill <= 0) {
            player.sendMessage(BottledXP.getInstance().getPrefix() + "§cDu hast nicht genügend XP.");
        } else {
            addBottlesRespectingStacks(player.getInventory(), toFill);
            int newTotalXp = totalXp - toFill * xpPerBottle;
            setTotalExperience(player, Math.max(0, newTotalXp));
        }
    }

    private static void addBottlesRespectingStacks(PlayerInventory inv, int count) {
        if (count > 0) {
            ItemStack[] contents = inv.getStorageContents();

            for(int i = 0; i < contents.length && count > 0; ++i) {
                ItemStack it = contents[i];
                if (it != null && it.getType() == Material.EXPERIENCE_BOTTLE && it.getAmount() < 64) {
                    int canAdd = Math.min(64 - it.getAmount(), count);
                    it.setAmount(it.getAmount() + canAdd);
                    count -= canAdd;
                }
            }

            inv.setStorageContents(contents);
        }

        while(count > 0) {
            int stack = Math.min(64, count);
            ItemStack toAdd = new ItemStack(Material.EXPERIENCE_BOTTLE, stack);
            inv.addItem(toAdd);
            count -= stack;
        }

    }

    public static void setTotalExperience(Player player, int totalXp) {
        totalXp = Math.max(0, totalXp);
        player.setExp(0.0F);
        player.setLevel(0);
        int level = 0;
        int xp = totalXp;

        while(true) {
            int toNext = getExpToNextLevel(level);
            if (xp < toNext) {
                player.setLevel(level);
                if (toNext > 0) {
                    player.setExp((float)xp / (float)toNext);
                } else {
                    player.setExp(0.0F);
                }

                return;
            }

            xp -= toNext;
            ++level;
        }
    }

    public static void fillExactAmount(Player player, int amount) {
        int xpPerBottle = Math.max(1, xpAmount);
        int totalXp = getCurrentExp(player);
        int maxByXp = totalXp / xpPerBottle;
        int space = getAvailableBottleSpace(player.getInventory());
        int toFill = Math.min(amount, Math.min(maxByXp, space));
        if (toFill > 0) {
            addBottlesRespectingStacks(player.getInventory(), toFill);
            int newTotalXp = totalXp - toFill * xpPerBottle;
            setTotalExperience(player, Math.max(0, newTotalXp));
        }
    }

    private static int getXpToNextLevel(int level) {
        if (level <= 16) {
            return 2 * level + 7;
        } else if (level <= 31) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    public static int getCurrentLevelXp(Player player) {
        int level = player.getLevel();
        float progress = player.getExp();

        int xpForLevel = getXpToNextLevel(level);

        return (int) (progress * xpForLevel);
    }

    public static void convertBottlesToXp(Player player){
        PlayerInventory inventory = player.getInventory();

        int bottles = 0;
        ItemStack[] contents = inventory.getStorageContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null || item.getType() != Material.EXPERIENCE_BOTTLE) {
                continue;
            }

            bottles += item.getAmount();
            contents[i] = null;
        }

        if (bottles <= 0) {
            return;
        }

        inventory.setStorageContents(contents);

        int xpToAdd = bottles * Math.max(1, xpAmount);
        int currentXp = getCurrentExp(player);

        setTotalExperience(player, currentXp + xpToAdd);
    }

    public static int getInventoryBottleAmount(Player player){
        PlayerInventory inventory = player.getInventory();

        int bottles = 0;
        ItemStack[] contents = inventory.getStorageContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null || item.getType() != Material.EXPERIENCE_BOTTLE) {
                continue;
            }

            bottles += item.getAmount();
            contents[i] = null;
        }
        return bottles;
    }

    public static int getPreviewLevelAfterFill(Player player, int bottles) {
        int xpPerBottle = Math.max(1, xpAmount);
        int totalXp = getCurrentExp(player) - bottles * xpPerBottle;

        totalXp = Math.max(0, totalXp);

        int level = 0;

        while (totalXp >= getExpToNextLevel(level)) {
            totalXp -= getExpToNextLevel(level);
            level++;
        }

        return level;
    }

    public static int getPreviewPointsAfterFill(Player player, int bottles) {
        int xpPerBottle = Math.max(1, xpAmount);
        int totalXp = getCurrentExp(player) - bottles * xpPerBottle;

        totalXp = Math.max(0, totalXp);

        int level = 0;

        while (totalXp >= getExpToNextLevel(level)) {
            totalXp -= getExpToNextLevel(level);
            level++;
        }

        return totalXp;
    }


    public static int getPreviewLevelAfterBottleConvert(Player player, int bottles) {
        int xpPerBottle = Math.max(1, xpAmount);
        int totalXp = getCurrentExp(player) + bottles * xpPerBottle;

        int level = 0;

        while (totalXp >= getExpToNextLevel(level)) {
            totalXp -= getExpToNextLevel(level);
            level++;
        }

        return level;
    }

    public static int getPreviewPointsAfterBottleConvert(Player player, int bottles) {
        int xpPerBottle = Math.max(1, xpAmount);
        int totalXp = getCurrentExp(player) + bottles * xpPerBottle;

        int level = 0;

        while (totalXp >= getExpToNextLevel(level)) {
            totalXp -= getExpToNextLevel(level);
            level++;
        }

        return totalXp;
    }



}
