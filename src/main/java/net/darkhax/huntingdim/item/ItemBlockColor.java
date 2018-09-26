package net.darkhax.huntingdim.item;

import net.darkhax.bookshelf.item.IColorfulItem;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemBlock;

public class ItemBlockColor extends ItemBlock implements IColorfulItem {

    public ItemBlockColor (Block block) {

        super(block);
    }

    @Override
    public IItemColor getColorHandler () {

        return (stack, index) -> ConfigurationHandler.defaultColorPacked;
    }
}
