package net.darkhax.huntingdim.block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.google.common.cache.LoadingCache;

import net.darkhax.bookshelf.block.IColorfulBlock;
import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.util.WorldUtils;
import net.darkhax.huntingdim.HuntingDimension;
import net.darkhax.huntingdim.Messages;
import net.darkhax.huntingdim.dimension.TeleporterHunting;
import net.darkhax.huntingdim.event.EntityTravelToDimensionEventWrapped;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHuntingPortal extends BlockPortal implements IColorfulBlock {

    private final Method playSound = ReflectionHelper.findMethod(EntityLiving.class, "getAmbientSound", "func_184639_G");

    public BlockHuntingPortal () {

        super();
        this.setBlockUnbreakable();
        this.setSoundType(SoundType.GLASS);
        this.setLightLevel(0.75F);
    }

    @Override
    public void updateTick (World worldIn, BlockPos pos, IBlockState state, Random rand) {

        if (worldIn.getGameRules().getBoolean("doMobSpawning") && ConfigurationHandler.chanceSpawn > 0 && rand.nextInt(ConfigurationHandler.chanceSpawn) < worldIn.getDifficulty().getDifficultyId()) {

            BlockPos blockpos;

            for (blockpos = pos; !worldIn.getBlockState(blockpos).isSideSolid(worldIn, blockpos, EnumFacing.UP) && blockpos.getY() > 0; blockpos = blockpos.down()) {
                ;
            }

            if (blockpos.getY() > 0 && !worldIn.getBlockState(blockpos.up()).isNormalCube()) {

                final EntityLiving living = this.getMobForChunk(worldIn, pos);

                if (living != null) {

                    living.setLocationAndAngles(blockpos.getX() + 0.5D, blockpos.getY() + 1.1D, blockpos.getZ() + 0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
                    living.rotationYawHead = living.rotationYaw;
                    living.renderYawOffset = living.rotationYaw;
                    living.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(living)), (IEntityLivingData) null);
                    worldIn.spawnEntity(living);
                    living.playLivingSound();
                }
            }
        }
    }

    @Override
    public boolean trySpawnPortal (World worldIn, BlockPos pos) {

        BlockHuntingPortal.FrameBuilder frameBuilder = new BlockHuntingPortal.FrameBuilder(worldIn, pos, EnumFacing.Axis.X);

        if (frameBuilder.isValid() && frameBuilder.portalBlockCount == 0) {

            frameBuilder.placePortalBlocks();
            return true;
        }

        else {

            frameBuilder = new BlockHuntingPortal.FrameBuilder(worldIn, pos, EnumFacing.Axis.Z);

            if (frameBuilder.isValid() && frameBuilder.portalBlockCount == 0) {

                frameBuilder.placePortalBlocks();
                return true;
            }
        }

        return false;
    }

    @Override
    public void neighborChanged (IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {

        final EnumFacing.Axis axis = state.getValue(AXIS);

        if (axis == EnumFacing.Axis.X) {

            final BlockHuntingPortal.FrameBuilder frameBuilder = new BlockHuntingPortal.FrameBuilder(worldIn, pos, EnumFacing.Axis.X);

            if (!frameBuilder.isValid() || frameBuilder.portalBlockCount < frameBuilder.width * frameBuilder.height) {

                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }

        else if (axis == EnumFacing.Axis.Z) {

            final BlockHuntingPortal.FrameBuilder frameBuilder = new BlockHuntingPortal.FrameBuilder(worldIn, pos, EnumFacing.Axis.Z);

            if (!frameBuilder.isValid() || frameBuilder.portalBlockCount < frameBuilder.width * frameBuilder.height) {

                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock (World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {

        // Ignore client side players, and players who are not sneaking.
        if (worldIn.isRemote || !entityIn.isSneaking()) {

            return;
        }

        // Only allow real players to teleport.
        if (entityIn instanceof EntityPlayerMP) {

            final EntityPlayerMP player = (EntityPlayerMP) entityIn;

            // Mounted players can not go through.
            if (entityIn.isRiding() || entityIn.isBeingRidden()) {

                Messages.TELEPORTER_MOUNTED.sendMessage(player);
            }

            // If the player is in our dim already, send them to the overworld.
            final int dimension = player.dimension == ConfigurationHandler.dimensionId ? ConfigurationHandler.returnDimension : ConfigurationHandler.dimensionId;

            // Fire Forge's hooks and events.
            if (EntityTravelToDimensionEventWrapped.onTravelToDimension(player, dimension)) {

                // Stop the player from sneaking, so they don't get sent back.
                player.setSneaking(false);

                // If player is going into the hunting dimension
                if (!WorldUtils.isDimension(player.getEntityWorld(), HuntingDimension.dimensionType)) {

                    // Give player 5 seconds of resistance when entering the portal.
                    player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 4, true, false));
                }

                // Teleport the player using custom teleporter.
                player.mcServer.getPlayerList().transferPlayerToDimension(player, dimension, new TeleporterHunting(player.mcServer.getWorld(dimension), HuntingDimension.frame.getDefaultState(), this.getDefaultState()));
            }

            // Event was canceled
            else {

                Messages.TELEPORTER_CANCELED.sendMessage(player);
            }
        }

        // Player is invalid
        else {

            // Let them know
            if (entityIn instanceof ICommandSender) {

                Messages.TELEPORTER_INVALID_PLAYER.sendMessage(entityIn);
            }

            // Put debug info in the logs
            HuntingDimension.LOG.info("Could not teleport {} because they are an invalid player entity! Class is {}", entityIn.getDisplayName().getUnformattedText(), entityIn.getClass().toString());
        }
    }

    private EntityLiving getMobForChunk (World world, BlockPos pos) {

        final Biome biome = world.getBiome(pos);

        if (biome != null) {

            final List<SpawnListEntry> validMobs = biome.getSpawnableList(EnumCreatureType.MONSTER);

            if (validMobs != null && !validMobs.isEmpty()) {

                final SpawnListEntry entry = validMobs.get(Constants.RANDOM.nextInt(validMobs.size()));

                if (entry != null) {

                    try {

                        final Entity entity = entry.newInstance(world);

                        if (entity instanceof EntityLiving) {

                            return (EntityLiving) entity;
                        }
                    }

                    catch (final Exception e) {

                    }
                }
            }
        }

        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick (IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        if (ConfigurationHandler.chanceSound > 0 && rand.nextInt(ConfigurationHandler.chanceSound) == 0) {

            final EntityLiving living = this.getMobForChunk(worldIn, pos);

            if (living != null) {

                try {

                    final SoundEvent sound = (SoundEvent) this.playSound.invoke(living);

                    if (sound != null) {

                        worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, SoundCategory.HOSTILE, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
                    }
                }

                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

                }

                living.setDead();
            }
        }

        for (int i = 0; i < 4; ++i) {

            final double x = pos.getX() + rand.nextFloat();
            final double y = pos.getY() + rand.nextFloat();
            final double z = pos.getZ() + rand.nextFloat();

            worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, ConfigurationHandler.defaultColorVector.x, ConfigurationHandler.defaultColorVector.y, ConfigurationHandler.defaultColorVector.z);
        }
    }

    @Override
    public BlockPattern.PatternHelper createPatternHelper (World worldIn, BlockPos position) {

        EnumFacing.Axis axis = EnumFacing.Axis.Z;

        BlockHuntingPortal.FrameBuilder frameBuilder = new BlockHuntingPortal.FrameBuilder(worldIn, position, EnumFacing.Axis.X);
        final LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.createLoadingCache(worldIn, true);

        if (!frameBuilder.isValid()) {

            axis = EnumFacing.Axis.X;
            frameBuilder = new BlockHuntingPortal.FrameBuilder(worldIn, position, EnumFacing.Axis.Z);
        }

        if (!frameBuilder.isValid()) {

            return new BlockPattern.PatternHelper(position, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1);
        }

        else {
            final int[] aint = new int[EnumFacing.AxisDirection.values().length];
            final EnumFacing enumfacing = frameBuilder.rightDir.rotateYCCW();
            final BlockPos blockpos = frameBuilder.bottomLeft.up(frameBuilder.getHeight() - 1);

            for (final EnumFacing.AxisDirection orientation : EnumFacing.AxisDirection.values()) {

                final BlockPattern.PatternHelper patternHelper = new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == orientation ? blockpos : blockpos.offset(frameBuilder.rightDir, frameBuilder.getWidth() - 1), EnumFacing.getFacingFromAxis(orientation, axis), EnumFacing.UP, loadingcache, frameBuilder.getWidth(), frameBuilder.getHeight(), 1);

                for (int i = 0; i < frameBuilder.getWidth(); ++i) {
                    for (int j = 0; j < frameBuilder.getHeight(); ++j) {
                        final BlockWorldState blockworldstate = patternHelper.translateOffset(i, j, 1);

                        if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getMaterial() != Material.AIR) {
                            ++aint[orientation.ordinal()];
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

            return new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection1 ? blockpos : blockpos.offset(frameBuilder.rightDir, frameBuilder.getWidth() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection1, axis), EnumFacing.UP, loadingcache, frameBuilder.getWidth(), frameBuilder.getHeight(), 1);
        }
    }

    public static class FrameBuilder {
        private final World world;
        private final EnumFacing.Axis axis;
        private final EnumFacing rightDir;
        private final EnumFacing leftDir;
        private int portalBlockCount;
        private BlockPos bottomLeft;
        private int height;
        private int width;

        public FrameBuilder (World worldIn, BlockPos position, EnumFacing.Axis axis) {

            this.world = worldIn;
            this.axis = axis;

            if (axis == EnumFacing.Axis.X) {
                this.leftDir = EnumFacing.EAST;
                this.rightDir = EnumFacing.WEST;
            }
            else {
                this.leftDir = EnumFacing.NORTH;
                this.rightDir = EnumFacing.SOUTH;
            }

            for (final BlockPos blockpos = position; position.getY() > blockpos.getY() - 21 && position.getY() > 0 && this.isEmptyBlock(worldIn.getBlockState(position.down())); position = position.down()) {
                ;
            }

            final int i = this.getDistanceUntilEdge(position, this.leftDir) - 1;

            if (i >= 0) {
                this.bottomLeft = position.offset(this.leftDir, i);
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

                if (!this.isEmptyBlock(this.world.getBlockState(blockpos)) || this.world.getBlockState(blockpos.down()).getBlock() != HuntingDimension.frame) {
                    break;
                }
            }

            final Block block = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i)).getBlock();
            return block == HuntingDimension.frame ? i : 0;
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

                    if (block == HuntingDimension.portal) {
                        ++this.portalBlockCount;
                    }

                    if (i == 0) {
                        block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();

                        if (block != HuntingDimension.frame) {
                            break label56;
                        }
                    }
                    else if (i == this.width - 1) {
                        block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();

                        if (block != HuntingDimension.frame) {
                            break label56;
                        }
                    }
                }
            }

            for (int j = 0; j < this.width; ++j) {
                if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != HuntingDimension.frame) {
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

            return blockIn.getMaterial() == Material.AIR || blockIn.getBlock() == Blocks.FIRE || blockIn.getBlock() == HuntingDimension.portal;
        }

        public boolean isValid () {

            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void placePortalBlocks () {

            for (int i = 0; i < this.width; ++i) {
                final BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

                for (int j = 0; j < this.height; ++j) {
                    this.world.setBlockState(blockpos.up(j), HuntingDimension.portal.getDefaultState().withProperty(BlockPortal.AXIS, this.axis), 2);
                }
            }
        }
    }

    @Override
    public IBlockColor getColorHandler () {

        return (state, world, pos, index) -> ConfigurationHandler.defaultColorPacked;
    }
}