package com.bp389.outbreak.backpacks;

import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class BackpacksDatabase {
    // yaml backpacks database
    private File directory;
    // backpacks loaded in memory
    private final HashMap<UUID, Inventory> backpacks = new HashMap<>();

    public BackpacksDatabase(File directory) {
        this.directory = directory;
    }

    private File backpackFileOf(UUID playerId){
        return new File(directory, playerId + ".yml");
    }

    private List<ItemStack> deserial(File bagFile){
        return (List<ItemStack>) YamlUtil.quickGet(bagFile, "contents");
    }

    private void serial(File bagFile, Inventory inv){
        serial(bagFile, inv.getContents());
    }

    private void serial(File bagFile, ItemStack[] contents){
        YamlUtil.quickSave(bagFile, "contents", contents);
    }

    /**
     * Saves all backpacks to disk. Mainly used when disabling the module
     */
    public void saveAll(){
        backpacks.entrySet().forEach(entry ->
                serial(backpackFileOf(entry.getKey()), entry.getValue())
        );
    }

    public Inventory backpackOf(Player p){
        return backpackOf(p.getUniqueId());
    }

    public Inventory backpackOf(UUID playerId){
        if(backpacks.containsKey(playerId))
            return backpacks.get(playerId);

        File bagFile = backpackFileOf(playerId);
        Inventory holder = Bukkit.getServer().createInventory(null, InventoryType.CHEST, Translator.tr("backpack-inv"));
        backpacks.put(playerId, holder);
        if(!bagFile.exists())
            return holder;

        List<ItemStack> contents = deserial(bagFile);
        if(contents == null){
            bagFile.delete();
            return holder;
        }

        holder.setContents(contents.toArray(new ItemStack[0]));
        return holder;
    }

    public void clearBackpackOf(Player p) {
        clearBackpackOf(p.getUniqueId());
    }

    public void clearBackpackOf(UUID playerId){
        backpackOf(playerId).clear();
    }
}
