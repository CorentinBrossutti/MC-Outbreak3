package com.bp389.outbreak.packs.items;

import com.bp389.outbreak.util.MathUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PackItem extends AbstractPackItem {
    protected static final Random RANDOM = new Random();

    protected Material material;
    protected short data;
    protected int qtyMin, qtyMax;

    public PackItem(Rarity rarity, Material material, short data, int qtyMin, int qtyMax) {
        super(rarity);

        this.material = material;
        this.data = data;
        this.qtyMin = qtyMin;
        this.qtyMax = Math.max(qtyMin, qtyMax);
    }


    @Override
    public ItemStack get() {
        ItemStack itemStack;
        if(data == 0)
            itemStack = new ItemStack(material,
                                                MathUtil.randomBetween(RANDOM, qtyMin, qtyMax, true));
        else
            itemStack = new ItemStack(material,
                                                MathUtil.randomBetween(RANDOM, qtyMin, qtyMax, true),
                                                data);
        return itemStack;
    }
}
