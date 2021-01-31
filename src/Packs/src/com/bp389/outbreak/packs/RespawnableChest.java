package com.bp389.outbreak.packs;

import com.bp389.outbreak.packs.items.AbstractPackItem;
import com.bp389.outbreak.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RespawnableChest extends LootPackPoint {
    private static final int CHEST_SIZE = 27;

    public RespawnableChest(JavaPlugin plugin, Location location,
                            PackType type) {
        super(plugin, location, type);
    }

    private Inventory getChest() {
        Block block = location.getBlock();
        Chest chest;
        if (block.getType() != Material.CHEST) {
            block.setType(Material.CHEST);
            chest = (Chest)location.getBlock().getState();
            //when updating do it a better way ?
            chest.getData().setData(
                    axisFaceToData(MathUtil.BlockFacing.locationToFace(location))
            );
            chest.update(true);
        }

        chest = ((Chest)location.getBlock().getState());
        return chest.getBlockInventory();
    }

    @Override
    public void delete() {
        super.delete();
        Block block = location.getBlock();
        if (block.getState() instanceof Chest) {
            ((Chest)block.getState()).getBlockInventory().clear();
            block.setType(Material.AIR);
        }
    }

    @Override
    public String specification() {
        return "chest";
    }

    @Override
    public void generate() {
        List<ItemStack> out = Arrays.stream(type.contents).filter(
                item -> RNG.nextInt(item.rarity.randomValue) == 0
        ).map(AbstractPackItem::get).collect(Collectors.toList());

        //we ensure we do not have more items than what a chest can contain
        //unlikely but we never know
        if(out.size() > CHEST_SIZE)
            out = out.subList(0, CHEST_SIZE - 1);

        Inventory inv = getChest();
        inv.clear();
        if(out.size() == 0)
            return;

        //fill up some air and put the items in the inventory
        int maxAirBetweenItems = (CHEST_SIZE - 1) / out.size(), index = 0, temp;
        for (ItemStack itemStack : out) {
            temp = RNG.nextInt(maxAirBetweenItems + 1);
            for (int i = 0; i < temp; i++)
                inv.setItem(index++, new ItemStack(Material.AIR));
            inv.setItem(index++, itemStack);
        }
    }

    private static byte axisFaceToData(BlockFace face) {
        switch (face) {
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
        }
        return axisFaceToData(BlockFace.NORTH);
    }

}
