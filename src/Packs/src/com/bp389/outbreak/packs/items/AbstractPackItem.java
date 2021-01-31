package com.bp389.outbreak.packs.items;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractPackItem {
    public Rarity rarity;

    public AbstractPackItem(Rarity rarity) {
        this.rarity = rarity;
    }

    public abstract ItemStack get();

    public static AbstractPackItem fromConfig(JavaPlugin toLog, ConfigurationSection source) {
        AbstractPackItem out;

        if (source.getBoolean("item.weapon", false))
            out = new PackWeapon(
                    Rarity.valueOf(source.getString("rarity").toUpperCase()),
                    source.getString("item.id"));
        else {
            Object itemId = source.get("item.id");
            Material material = itemId instanceof String ?
                    Material.getMaterial(((String) itemId).toUpperCase()) :
                    Material.getMaterial((int) itemId);
            if (material == null) {
                toLog.getLogger().warning("The material " + itemId + " cannot be resolved. If you use text IDs, be sure to use Bukkit's and Minecraft's.");
                toLog.getLogger().warning("If this error occurs frequently, it is advised you use numeric IDs in your pack type files.");
                toLog.getLogger().warning("For further information please contact the author.");
                return null;
            }
            out = new PackItem(
                    Rarity.valueOf(source.getString("rarity").toUpperCase()),
                    material,
                    (short) source.getInt("item.data", 0),
                    material.getMaxStackSize() == 1 ? 1 : source.getInt("quantity.min", 1),
                    material.getMaxStackSize() == 1 ? 1 : source.getInt("quantity.max", 1)
            );
        }

        return out;
    }

    public static enum Rarity {
        VERY_COMMON(30, 3),
        COMMON(7, 4),
        USUAL(6, 5),
        MEDIUM(5, 6),
        NICE(4, 8),
        GREAT(3, 10),
        RARE(2, 11),
        AWESOME(1, 15);

        //value when lower = rarer (weight)
        public int weightValue;
        //value when higher = rarer (randomization)
        public int randomValue;

        Rarity(int weightValue, int randomValue) {
            this.weightValue = weightValue;
            this.randomValue = randomValue;
        }
    }

}
