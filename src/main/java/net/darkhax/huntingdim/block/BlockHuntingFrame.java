package net.darkhax.huntingdim.block;

import net.darkhax.huntingdim.HuntingDim;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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

        if (playerIn.getHeldItemMainhand().getItem() == Items.FLINT_AND_STEEL) {

            ((BlockHuntingPortal) HuntingDim.portal).trySpawnPortal(worldIn, pos.offset(facing));
            return true;
        }

        return false;
    }
}