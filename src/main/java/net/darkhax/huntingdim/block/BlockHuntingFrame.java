package net.darkhax.huntingdim.block;

import net.darkhax.huntingdim.HuntingDimension;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHuntingFrame extends Block {

    public BlockHuntingFrame () {

        super(Material.WOOD);
        this.setHardness(3.0F);
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public boolean onBlockActivated (World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (worldIn.isRemote) {

            return false;
        }

        final ItemStack stack = playerIn.getHeldItem(hand);
        final Item item = stack.getItem();

        if (item instanceof ItemSword || item instanceof ItemBow || item == Items.FLINT_AND_STEEL) {

            ((BlockHuntingPortal) HuntingDimension.portal).trySpawnPortal(worldIn, pos.offset(facing));
            return true;
        }

        return false;
    }
}