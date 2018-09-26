package net.darkhax.huntingdim.addon.tcon;

import net.minecraft.item.Item;
import slimeknights.tconstruct.library.tinkering.TinkersItem;

public class TconUtils {

    public static boolean isTconWeapon (Item item) {

        if (item instanceof TinkersItem) {

            return ((TinkersItem) item).hasCategory(Category.WEAPON);
        }

        return false;
    }
}