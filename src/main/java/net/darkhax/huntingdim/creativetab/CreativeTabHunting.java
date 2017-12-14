package net.darkhax.huntingdim.creativetab;

import net.darkhax.huntingdim.HuntingDimension;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabHunting extends CreativeTabs {

    public CreativeTabHunting () {

        super("huntingdim");
    }

    @Override
    public ItemStack getTabIconItem () {

        return new ItemStack(HuntingDimension.frame);
    }
}
