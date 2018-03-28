package net.darkhax.huntingdim.dimension;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class TeleporterHunting extends Teleporter {

    private final IBlockState portalFrameBlockState;
    private final IBlockState portalBlockState;
    private final Block portalBlock;

    public TeleporterHunting (WorldServer worldIn, IBlockState portalFrameBlockState, IBlockState portalBlockState) {

        super(worldIn);
        this.portalFrameBlockState = portalFrameBlockState;
        this.portalBlockState = portalBlockState;
        this.portalBlock = portalBlockState.getBlock();
    }

    @Override
    public void placeInPortal (Entity entity, float rotationYaw) {

        if (entityIn instanceof EntityPlayerMP && !((EntityPlayerMP)entityIn).capabilities.isCreativeMode)
				ReflectionHelper.setPrivateValue(EntityPlayerMP.class, (EntityPlayerMP)entityIn, true, "invulnerableDimensionChange", "field_184851_cj");
        
        // If a portal doesn't exist, make a new one.
        if (!this.placeInExistingPortal(entity, rotationYaw)) {

            this.makePortal(entity);
            this.placeInExistingPortal(entity, rotationYaw);
        }
    }

    @Override
    public boolean placeInExistingPortal (Entity entityIn, float rotationYaw) {

        final long chunkId = ChunkPos.asLong(MathHelper.floor(entityIn.posX), MathHelper.floor(entityIn.posZ));

        double distance = -1.0D;
        boolean doesPortalExist = true;
        BlockPos location = BlockPos.ORIGIN;

        // Handles when a portal exists
        if (this.destinationCoordinateCache.containsKey(chunkId)) {

            final PortalPosition portalPosition = this.destinationCoordinateCache.get(chunkId);
            distance = 0.0D;
            location = portalPosition;
            portalPosition.lastUpdateTime = this.world.getTotalWorldTime();
            doesPortalExist = false;

        }

        // Searches for a nearby portal
        else {
            final BlockPos entityPos = new BlockPos(entityIn);

            for (int offsetX = -128; offsetX <= 128; ++offsetX) {

                BlockPos positionCache;

                for (int offsetZ = -128; offsetZ <= 128; ++offsetZ) {

                    for (BlockPos currentPos = entityPos.add(offsetX, this.world.getActualHeight() - 1 - entityPos.getY(), offsetZ); currentPos.getY() >= 0; currentPos = positionCache) {

                        positionCache = currentPos.down();

                        if (this.world.getBlockState(currentPos).getBlock() == this.portalBlock) {

                            while (this.world.getBlockState(positionCache = currentPos.down()).getBlock() == this.portalBlock) {

                                currentPos = positionCache;
                            }

                            final double distanceToEntity = currentPos.distanceSq(entityPos);

                            if (distance < 0.0D || distanceToEntity < distance) {

                                distance = distanceToEntity;
                                location = currentPos;
                            }
                        }
                    }
                }
            }
        }

        if (distance >= 0.0D) {

            // Updatex existing portal
            if (doesPortalExist) {

                this.destinationCoordinateCache.put(chunkId, new PortalPosition(location, this.world.getTotalWorldTime()));
            }

            double tpX = location.getX() + 0.5D;
            double tpY = location.getY() + 0.5D;
            double tpZ = location.getZ() + 0.5D;
            EnumFacing direction = null;

            if (this.world.getBlockState(location.west()).getBlock() == this.portalBlock) {
                direction = EnumFacing.NORTH;
            }

            if (this.world.getBlockState(location.east()).getBlock() == this.portalBlock) {
                direction = EnumFacing.SOUTH;
            }

            if (this.world.getBlockState(location.north()).getBlock() == this.portalBlock) {
                direction = EnumFacing.EAST;
            }

            if (this.world.getBlockState(location.south()).getBlock() == this.portalBlock) {
                direction = EnumFacing.WEST;
            }

            final EnumFacing enumfacing1 = EnumFacing.getHorizontal(MathHelper.floor(entityIn.rotationYaw * 4.0F / 360.0F + 0.5D) & 3);

            if (direction != null) {
                EnumFacing enumfacing2 = direction.rotateYCCW();
                final BlockPos blockpos2 = location.offset(direction);
                boolean flag2 = this.isInsideBlock(blockpos2);
                boolean flag3 = this.isInsideBlock(blockpos2.offset(enumfacing2));

                if (flag3 && flag2) {
                    location = location.offset(enumfacing2);
                    direction = direction.getOpposite();
                    enumfacing2 = enumfacing2.getOpposite();
                    final BlockPos blockpos3 = location.offset(direction);
                    flag2 = this.isInsideBlock(blockpos3);
                    flag3 = this.isInsideBlock(blockpos3.offset(enumfacing2));
                }

                float f6 = 0.5F;
                float f1 = 0.5F;

                if (!flag3 && flag2) {
                    f6 = 1.0F;
                }
                else if (flag3 && !flag2) {
                    f6 = 0.0F;
                }
                else if (flag3) {
                    f1 = 0.0F;
                }

                tpX = location.getX() + 0.5D;
                tpY = location.getY() + 0.5D;
                tpZ = location.getZ() + 0.5D;
                tpX += enumfacing2.getFrontOffsetX() * f6 + direction.getFrontOffsetX() * f1;
                tpZ += enumfacing2.getFrontOffsetZ() * f6 + direction.getFrontOffsetZ() * f1;
                float f2 = 0.0F;
                float f3 = 0.0F;
                float f4 = 0.0F;
                float f5 = 0.0F;

                if (direction == enumfacing1) {
                    f2 = 1.0F;
                    f3 = 1.0F;
                }
                else if (direction == enumfacing1.getOpposite()) {
                    f2 = -1.0F;
                    f3 = -1.0F;
                }
                else if (direction == enumfacing1.rotateY()) {
                    f4 = 1.0F;
                    f5 = -1.0F;
                }
                else {
                    f4 = -1.0F;
                    f5 = 1.0F;
                }

                final double d2 = entityIn.motionX;
                final double d3 = entityIn.motionZ;
                entityIn.motionX = d2 * f2 + d3 * f5;
                entityIn.motionZ = d2 * f4 + d3 * f3;
                entityIn.rotationYaw = rotationYaw - enumfacing1.getHorizontalIndex() * 90 + direction.getHorizontalIndex() * 90;
            }
            else {
                entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0D;
            }

            entityIn.setLocationAndAngles(tpX, tpY, tpZ, entityIn.rotationYaw, entityIn.rotationPitch);
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isInsideBlock (BlockPos position) {

        return !this.world.isAirBlock(position) || !this.world.isAirBlock(position.up());
    }

    @Override
    public boolean makePortal (Entity entityIn) {

        double d0 = -1.0D;
        final int j = MathHelper.floor(entityIn.posX);
        final int k = MathHelper.floor(entityIn.posY);
        final int l = MathHelper.floor(entityIn.posZ);
        int i1 = j;
        int j1 = k;
        int k1 = l;
        int l1 = 0;
        final int i2 = this.random.nextInt(4);
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int j2 = j - 16; j2 <= j + 16; ++j2) {
            final double d1 = j2 + 0.5D - entityIn.posX;

            for (int l2 = l - 16; l2 <= l + 16; ++l2) {
                final double d2 = l2 + 0.5D - entityIn.posZ;
                label293 :

                for (int j3 = this.world.getActualHeight() - 1; j3 >= 0; --j3) {
                    if (this.world.isAirBlock(blockPos.setPos(j2, j3, l2))) {
                        while (j3 > 0 && this.world.isAirBlock(blockPos.setPos(j2, j3 - 1, l2))) {
                            --j3;
                        }

                        for (int k3 = i2; k3 < i2 + 4; ++k3) {
                            int l3 = k3 % 2;
                            int i4 = 1 - l3;

                            if (k3 % 4 >= 2) {
                                l3 = -l3;
                                i4 = -i4;
                            }

                            for (int j4 = 0; j4 < 3; ++j4) {
                                for (int k4 = 0; k4 < 4; ++k4) {
                                    for (int l4 = -1; l4 < 4; ++l4) {
                                        final int i5 = j2 + (k4 - 1) * l3 + j4 * i4;
                                        final int j5 = j3 + l4;
                                        final int k5 = l2 + (k4 - 1) * i4 - j4 * l3;
                                        blockPos.setPos(i5, j5, k5);

                                        if (l4 < 0 && !this.world.getBlockState(blockPos).getMaterial().isSolid() || l4 >= 0 && !this.world.isAirBlock(blockPos)) {
                                            continue label293;
                                        }
                                    }
                                }
                            }

                            final double d5 = j3 + 0.5D - entityIn.posY;
                            final double d7 = d1 * d1 + d5 * d5 + d2 * d2;

                            if (d0 < 0.0D || d7 < d0) {
                                d0 = d7;
                                i1 = j2;
                                j1 = j3;
                                k1 = l2;
                                l1 = k3 % 4;
                            }
                        }
                    }
                }
            }
        }

        if (d0 < 0.0D) {
            for (int l5 = j - 16; l5 <= j + 16; ++l5) {
                final double d3 = l5 + 0.5D - entityIn.posX;

                for (int j6 = l - 16; j6 <= l + 16; ++j6) {
                    final double d4 = j6 + 0.5D - entityIn.posZ;
                    label231 :

                    for (int i7 = this.world.getActualHeight() - 1; i7 >= 0; --i7) {
                        if (this.world.isAirBlock(blockPos.setPos(l5, i7, j6))) {
                            while (i7 > 0 && this.world.isAirBlock(blockPos.setPos(l5, i7 - 1, j6))) {
                                --i7;
                            }

                            for (int k7 = i2; k7 < i2 + 2; ++k7) {
                                final int j8 = k7 % 2;
                                final int j9 = 1 - j8;

                                for (int j10 = 0; j10 < 4; ++j10) {
                                    for (int j11 = -1; j11 < 4; ++j11) {
                                        final int j12 = l5 + (j10 - 1) * j8;
                                        final int i13 = i7 + j11;
                                        final int j13 = j6 + (j10 - 1) * j9;
                                        blockPos.setPos(j12, i13, j13);

                                        if (j11 < 0 && !this.world.getBlockState(blockPos).getMaterial().isSolid() || j11 >= 0 && !this.world.isAirBlock(blockPos)) {
                                            continue label231;
                                        }
                                    }
                                }

                                final double d6 = i7 + 0.5D - entityIn.posY;
                                final double d8 = d3 * d3 + d6 * d6 + d4 * d4;

                                if (d0 < 0.0D || d8 < d0) {
                                    d0 = d8;
                                    i1 = l5;
                                    j1 = i7;
                                    k1 = j6;
                                    l1 = k7 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        final int i6 = i1;
        int k2 = j1;
        final int k6 = k1;
        int l6 = l1 % 2;
        int i3 = 1 - l6;

        if (l1 % 4 >= 2) {
            l6 = -l6;
            i3 = -i3;
        }

        if (d0 < 0.0D) {
            j1 = MathHelper.clamp(j1, 70, this.world.getActualHeight() - 10);
            k2 = j1;

            for (int j7 = -1; j7 <= 1; ++j7) {
                for (int l7 = 1; l7 < 3; ++l7) {
                    for (int k8 = -1; k8 < 3; ++k8) {
                        final int k9 = i6 + (l7 - 1) * l6 + j7 * i3;
                        final int k10 = k2 + k8;
                        final int k11 = k6 + (l7 - 1) * i3 - j7 * l6;
                        final boolean flag = k8 < 0;
                        this.world.setBlockState(new BlockPos(k9, k10, k11), flag ? this.portalFrameBlockState : Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        final IBlockState iblockstate = this.portalBlockState.withProperty(BlockPortal.AXIS, l6 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);

        for (int i8 = 0; i8 < 4; ++i8) {
            for (int l8 = 0; l8 < 4; ++l8) {
                for (int l9 = -1; l9 < 4; ++l9) {
                    final int l10 = i6 + (l8 - 1) * l6;
                    final int l11 = k2 + l9;
                    final int k12 = k6 + (l8 - 1) * i3;
                    final boolean flag1 = l8 == 0 || l8 == 3 || l9 == -1 || l9 == 3;
                    this.world.setBlockState(new BlockPos(l10, l11, k12), flag1 ? this.portalFrameBlockState : iblockstate, 2);
                }
            }

            for (int i9 = 0; i9 < 4; ++i9) {
                for (int i10 = -1; i10 < 4; ++i10) {
                    final int i11 = i6 + (i9 - 1) * l6;
                    final int i12 = k2 + i10;
                    final int l12 = k6 + (i9 - 1) * i3;
                    final BlockPos blockpos = new BlockPos(i11, i12, l12);
                    this.world.notifyNeighborsOfStateChange(blockpos, this.world.getBlockState(blockpos).getBlock(), false);
                }
            }
        }

        return true;
    }
}
