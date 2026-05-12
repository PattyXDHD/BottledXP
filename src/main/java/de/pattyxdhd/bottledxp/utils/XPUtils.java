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
    public static int xpAmount = 10;

    public int getBottles(Player player) {
        int xp = this.getCurrentExp(player);
        int xpPerBottle = Math.max(1, xpAmount);
        return xp / xpPerBottle;
    }

    public int getCurrentExp(Player player) {
        int level = player.getLevel();
        float progress = player.getExp();
        int xp = this.getExpAtLevel(level);
        xp += Math.round((float)this.getExpToNextLevel(level) * progress);
        return Math.max(0, xp);
    }

    private int getExpAtLevel(int level) {
        if (level >= 31) {
            return (int)((double)4.5F * (double)level * (double)level - (double)162.5F * (double)level + (double)2220.0F);
        } else {
            return level >= 16 ? (int)((double)2.5F * (double)level * (double)level - (double)40.5F * (double)level + (double)360.0F) : level * level + 6 * level;
        }
    }

    private int getExpToNextLevel(int level) {
        if (level >= 31) {
            return 9 * level - 158;
        } else {
            return level >= 16 ? 5 * level - 38 : 2 * level + 7;
        }
    }

    public int getAvailableBottleSpace(Inventory inventory) {
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

    public void xpBottle(Player player) {
        int xpPerBottle = Math.max(1, xpAmount);
        int totalXp = this.getCurrentExp(player);
        int maxByXp = totalXp / xpPerBottle;
        int space = this.getAvailableBottleSpace(player.getInventory());
        int toFill = Math.min(maxByXp, space);
        if (toFill <= 0) {
            player.sendMessage(BottledXP.getInstance().getPrefix() + "§cDu hast nicht genügend XP.");
        } else {
            this.addBottlesRespectingStacks(player.getInventory(), toFill);
            int newTotalXp = totalXp - toFill * xpPerBottle;
            this.setTotalExperience(player, Math.max(0, newTotalXp));
        }
    }

    private void addBottlesRespectingStacks(PlayerInventory inv, int count) {
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

    public void setTotalExperience(Player player, int totalXp) {
        totalXp = Math.max(0, totalXp);
        player.setExp(0.0F);
        player.setLevel(0);
        int level = 0;
        int xp = totalXp;

        while(true) {
            int toNext = this.getExpToNextLevel(level);
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

    public void fillExactAmount(Player player, int amount) {
        int xpPerBottle = Math.max(1, xpAmount);
        int totalXp = this.getCurrentExp(player);
        int maxByXp = totalXp / xpPerBottle;
        int space = this.getAvailableBottleSpace(player.getInventory());
        int toFill = Math.min(amount, Math.min(maxByXp, space));
        if (toFill > 0) {
            this.addBottlesRespectingStacks(player.getInventory(), toFill);
            int newTotalXp = totalXp - toFill * xpPerBottle;
            this.setTotalExperience(player, Math.max(0, newTotalXp));
        }
    }

}
