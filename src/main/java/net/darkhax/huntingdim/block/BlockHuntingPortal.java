package net.darkhax.huntingdim.block;

import java.util.Random;

import com.google.common.cache.LoadingCache;

import net.darkhax.huntingdim.HuntingDim;
import net.darkhax.huntingdim.dimension.TeleporterHunting;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHuntingPortal extends BlockPortal {

    public BlockHuntingPortal () {
        
        super();
        this.setBlockUnbreakable();
        this.setSoundType(SoundType.GLASS);
        this.setLightLevel(0.75F);
    }
    
    @Override
    public void updateTick (World worldIn, BlockPos pos, IBlockState state, Random rand) {

        // TODO possibly spawn mobs?
    }

    public static int getMetaForAxis (EnumFacing.Axis axis) {

        if (axis == EnumFacing.Axis.X) {
            return 1;
        }
        else {
            return axis == EnumFacing.Axis.Z ? 2 : 0;
        }
    }

    @Override
    public boolean trySpawnPortal (World worldIn, BlockPos pos) {

        final BlockHuntingPortal.Size blockportal$size = new BlockHuntingPortal.Size(worldIn, pos, EnumFacing.Axis.X);

        if (blockportal$size.isValid() && blockportal$size.portalBlockCount == 0) {
            blockportal$size.placePortalBlocks();
            return true;
        }
        else {
            final BlockHuntingPortal.Size blockportal$size1 = new BlockHuntingPortal.Size(worldIn, pos, EnumFacing.Axis.Z);

            if (blockportal$size1.isValid() && blockportal$size1.portalBlockCount == 0) {
                blockportal$size1.placePortalBlocks();
                return true;
            }
            else {
                return false;
            }
        }
    }

    @Override
    public void neighborChanged (IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {

        final EnumFacing.Axis enumfacing$axis = state.getValue(AXIS);

        if (enumfacing$axis == EnumFacing.Axis.X) {
            final BlockHuntingPortal.Size blockportal$size = new BlockHuntingPortal.Size(worldIn, pos, EnumFacing.Axis.X);

            if (!blockportal$size.isValid() || blockportal$size.portalBlockCount < blockportal$size.width * blockportal$size.height) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        else if (enumfacing$axis == EnumFacing.Axis.Z) {
            final BlockHuntingPortal.Size blockportal$size1 = new BlockHuntingPortal.Size(worldIn, pos, EnumFacing.Axis.Z);

            if (!blockportal$size1.isValid() || blockportal$size1.portalBlockCount < blockportal$size1.width * blockportal$size1.height) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    /**
     * Called When an Entity Collided with the Block
     */
    @Override
    public void onEntityCollidedWithBlock (World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {

        if (worldIn.isRemote || !entityIn.isSneaking()) {
            return;
        }

        if (!entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.isNonBoss()) {

            if (entityIn instanceof EntityPlayerMP) {
                final EntityPlayerMP player = (EntityPlayerMP) entityIn;

                if (player.timeUntilPortal > 0) {
                    player.timeUntilPortal = player.getPortalCooldown();

                }
                else if (player.dimension != ConfigurationHandler.dimensionId) {
                    // player is not in the dimension

                    final int dimension = ConfigurationHandler.dimensionId;

                    if (!ForgeHooks.onTravelToDimension(player, dimension)) {
                        return;
                    }

                    player.setSneaking(false);
                    player.timeUntilPortal = player.getPortalCooldown();
                    player.mcServer.getPlayerList().transferPlayerToDimension(player, dimension, new TeleporterHunting(player.mcServer.getWorld(dimension), HuntingDim.frame.getDefaultState(), this.getDefaultState()));

                }
                else {

                    final int dimension = 0;

                    if (!ForgeHooks.onTravelToDimension(player, dimension)) {
                        return;
                    }

                    player.setSneaking(false);
                    player.timeUntilPortal = player.getPortalCooldown();
                    player.mcServer.getPlayerList().transferPlayerToDimension(player, dimension, new TeleporterHunting(player.mcServer.getWorld(dimension), HuntingDim.frame.getDefaultState(), this.getDefaultState()));
                }
            }

        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick (IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        if (rand.nextInt(100) == 0) {
            worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int i = 0; i < 4; ++i) {
            double d0 = pos.getX() + rand.nextFloat();
            final double d1 = pos.getY() + rand.nextFloat();
            double d2 = pos.getZ() + rand.nextFloat();
            double d3 = (rand.nextFloat() - 0.5D) * 0.5D;
            final double d4 = (rand.nextFloat() - 0.5D) * 0.5D;
            double d5 = (rand.nextFloat() - 0.5D) * 0.5D;
            final int j = rand.nextInt(2) * 2 - 1;

            if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
                d0 = pos.getX() + 0.5D + 0.25D * j;
                d3 = rand.nextFloat() * 2.0F * j;
            }
            else {
                d2 = pos.getZ() + 0.5D + 0.25D * j;
                d5 = rand.nextFloat() * 2.0F * j;
            }

            worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, d0, d1, d2, 0f, 255f, 0f);
        }
    }

    @Override
    public BlockPattern.PatternHelper createPatternHelper (World worldIn, BlockPos p_181089_2_) {

        EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Z;
        BlockHuntingPortal.Size blockportal$size = new BlockHuntingPortal.Size(worldIn, p_181089_2_, EnumFacing.Axis.X);
        final LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.createLoadingCache(worldIn, true);

        if (!blockportal$size.isValid()) {
            enumfacing$axis = EnumFacing.Axis.X;
            blockportal$size = new BlockHuntingPortal.Size(worldIn, p_181089_2_, EnumFacing.Axis.Z);
        }

        if (!blockportal$size.isValid()) {
            return new BlockPattern.PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1);
        }
        else {
            final int[] aint = new int[EnumFacing.AxisDirection.values().length];
            final EnumFacing enumfacing = blockportal$size.rightDir.rotateYCCW();
            final BlockPos blockpos = blockportal$size.bottomLeft.up(blockportal$size.getHeight() - 1);

            for (final EnumFacing.AxisDirection enumfacing$axisdirection : EnumFacing.AxisDirection.values()) {
                final BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);

                for (int i = 0; i < blockportal$size.getWidth(); ++i) {
                    for (int j = 0; j < blockportal$size.getHeight(); ++j) {
                        final BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i, j, 1);

                        if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getMaterial() != Material.AIR) {
                            ++aint[enumfacing$axisdirection.ordinal()];
                        }
                    }
                }
            }

            EnumFacing.AxisDirection enumfacing$axisdirection1 = EnumFacing.AxisDirection.POSITIVE;

            for (final EnumFacing.AxisDirection enumfacing$axisdirection2 : EnumFacing.AxisDirection.values()) {
                if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1.ordinal()]) {
                    enumfacing$axisdirection1 = enumfacing$axisdirection2;
                }
            }

            return new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection1 ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection1, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);
        }
    }

    public static class Size {
        private final World world;
        private final EnumFacing.Axis axis;
        private final EnumFacing rightDir;
        private final EnumFacing leftDir;
        private int portalBlockCount;
        private BlockPos bottomLeft;
        private int height;
        private int width;

        public Size (World worldIn, BlockPos p_i45694_2_, EnumFacing.Axis p_i45694_3_) {

            this.world = worldIn;
            this.axis = p_i45694_3_;

            if (p_i45694_3_ == EnumFacing.Axis.X) {
                this.leftDir = EnumFacing.EAST;
                this.rightDir = EnumFacing.WEST;
            }
            else {
                this.leftDir = EnumFacing.NORTH;
                this.rightDir = EnumFacing.SOUTH;
            }

            for (final BlockPos blockpos = p_i45694_2_; p_i45694_2_.getY() > blockpos.getY() - 21 && p_i45694_2_.getY() > 0 && this.isEmptyBlock(worldIn.getBlockState(p_i45694_2_.down())); p_i45694_2_ = p_i45694_2_.down()) {
                ;
            }

            final int i = this.getDistanceUntilEdge(p_i45694_2_, this.leftDir) - 1;

            if (i >= 0) {
                this.bottomLeft = p_i45694_2_.offset(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);

                if (this.width < 2 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }

            if (this.bottomLeft != null) {
                this.height = this.calculatePortalHeight();
            }
        }

        protected int getDistanceUntilEdge (BlockPos p_180120_1_, EnumFacing p_180120_2_) {

            int i;

            for (i = 0; i < 22; ++i) {
                final BlockPos blockpos = p_180120_1_.offset(p_180120_2_, i);

                if (!this.isEmptyBlock(this.world.getBlockState(blockpos)) || this.world.getBlockState(blockpos.down()).getBlock() != HuntingDim.frame) {
                    break;
                }
            }

            final Block block = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i)).getBlock();
            return block == HuntingDim.frame ? i : 0;
        }

        public int getHeight () {

            return this.height;
        }

        public int getWidth () {

            return this.width;
        }

        protected int calculatePortalHeight () {

            label56 :

            for (this.height = 0; this.height < 21; ++this.height) {
                for (int i = 0; i < this.width; ++i) {
                    final BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
                    Block block = this.world.getBlockState(blockpos).getBlock();

                    if (!this.isEmptyBlock(this.world.getBlockState(blockpos))) {
                        break label56;
                    }

                    if (block == HuntingDim.portal) {
                        ++this.portalBlockCount;
                    }

                    if (i == 0) {
                        block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();

                        if (block != HuntingDim.frame) {
                            break label56;
                        }
                    }
                    else if (i == this.width - 1) {
                        block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();

                        if (block != HuntingDim.frame) {
                            break label56;
                        }
                    }
                }
            }

            for (int j = 0; j < this.width; ++j) {
                if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != HuntingDim.frame) {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            }
            else {
                this.bottomLeft = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }

        protected boolean isEmptyBlock (IBlockState blockIn) {

            return blockIn.getMaterial() == Material.AIR || blockIn.getBlock() == Blocks.FIRE || blockIn.getBlock() == HuntingDim.portal;
        }

        public boolean isValid () {

            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void placePortalBlocks () {

            for (int i = 0; i < this.width; ++i) {
                final BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

                for (int j = 0; j < this.height; ++j) {
                    this.world.setBlockState(blockpos.up(j), HuntingDim.portal.getDefaultState().withProperty(BlockPortal.AXIS, this.axis), 2);
                }
            }
        }
    }
}