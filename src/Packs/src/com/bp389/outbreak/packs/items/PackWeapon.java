package com.bp389.outbreak.packs.items;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.inventory.ItemStack;

public class PackWeapon extends AbstractPackItem {
    private static final CSUtility CS_UTILITY = new CSUtility();

    protected String weaponTitle;

    public PackWeapon(Rarity rarity, String weaponTitle) {
        super(rarity);

        this.weaponTitle = weaponTitle;
    }

    @Override
    public ItemStack get() {
        return CS_UTILITY.generateWeapon(weaponTitle);
    }
}
