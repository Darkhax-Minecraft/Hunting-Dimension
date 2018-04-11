package net.darkhax.huntingdim.block;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityFrame extends TileEntityBasic {

    private ItemStack baseStack;

    public TileEntityFrame () {

        this.baseStack = new ItemStack(Blocks.LOG);
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        dataTag.setTag("BaseBlock", this.baseStack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.baseStack = BlockHuntingFrame.getVariantFromTag(dataTag);
    }

    public ItemStack getBaseStack () {

        return this.baseStack;
    }

    public void setBaseStack (ItemStack baseStack) {

        this.baseStack = baseStack;
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newState) {

        return true;
    }
}
