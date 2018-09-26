package net.darkhax.huntingdim.block;

import java.util.List;
import java.util.Random;

import net.darkhax.bookshelf.block.BlockTileEntity;
import net.darkhax.bookshelf.block.IColorfulBlock;
import net.darkhax.bookshelf.block.property.PropertyObject;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.huntingdim.HuntingDimension;
import net.darkhax.huntingdim.addon.tcon.TconUtils;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHuntingFrame extends BlockTileEntity implements IColorfulBlock {

    public static final PropertyObject<ItemStack> BASE_BLOCK = new PropertyObject<>("base_block", ItemStack.class);

    public BlockHuntingFrame () {

        super(Material.WOOD);
        this.setHardness(3.0F);
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public BlockStateContainer createBlockState () {

        return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[] { BASE_BLOCK });
    }

    @Override
    public IBlockState getExtendedState (IBlockState state, IBlockAccess world, BlockPos pos) {

        return ((IExtendedBlockState) state).withProperty(BASE_BLOCK, getVariant(world, pos));
    }

    @Override
    public void onBlockPlacedBy (World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

        if (worldIn.getTileEntity(pos) instanceof TileEntityFrame) {

            final TileEntityFrame tile = (TileEntityFrame) worldIn.getTileEntity(pos);

            if (tile != null) {

                tile.setBaseStack(getVariant(stack));
            }
        }
    }

    @Override
    public void getSubBlocks (CreativeTabs tab, NonNullList<ItemStack> items) {

        for (final Tuple<ItemStack, ItemStack> variant : HuntingDimension.frameVariants) {

            items.add(variant.getSecond());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {

        tooltip.add(getVariantFromTag(stack.getTagCompound()).getDisplayName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer () {

        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated (World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (worldIn.isRemote) {

            return false;
        }

        final ItemStack stack = playerIn.getHeldItem(hand);
        final Item item = stack.getItem();

        if (item instanceof ItemSword || item instanceof ItemBow || Loader.isModLoaded("tconstruct") && TconUtils.isTconWeapon(item)) {

            ((BlockHuntingPortal) HuntingDimension.portal).trySpawnPortal(worldIn, pos.offset(facing));
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void neighborChanged (IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {

        if (worldIn.getBlockState(fromPos).getBlock() instanceof BlockFire) {

            ((BlockHuntingPortal) HuntingDimension.portal).trySpawnPortal(worldIn, fromPos);
        }
    }

    @Override
    public TileEntity createNewTileEntity (World worldIn, int meta) {

        return new TileEntityFrame();
    }

    @Override
    public ItemStack getPickBlock (IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {

        return createFrameVariant(getVariant(world, pos));
    }

    @Override
    public void onBlockExploded (World world, BlockPos pos, Explosion explosion) {

        StackUtils.dropStackInWorld(world, pos, createFrameVariant(getVariant(world, pos)));
        world.setBlockToAir(pos);
        
        this.onExplosionDestroy(world, pos, explosion);
    }

    @Override
    public int quantityDropped (Random rnd) {

        return 0;
    }

    @Override
    public boolean removedByPlayer (IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {

        if (!player.isCreative()) {

            StackUtils.dropStackInWorld(world, pos, createFrameVariant(getVariant(world, pos)));
        }

        return world.setBlockToAir(pos);
    }

    public static ItemStack createFrameVariant (ItemStack wood) {

        final ItemStack stack = new ItemStack(HuntingDimension.frame);
        StackUtils.prepareStackTag(stack).setTag("BaseBlock", wood.writeToNBT(new NBTTagCompound()));
        return stack;
    }

    public static ItemStack getVariant (IBlockAccess world, BlockPos pos) {

        final TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityFrame) {

            return ((TileEntityFrame) tile).getBaseStack();
        }

        return getVariantFromTag(null);
    }

    public static ItemStack getVariant (ItemStack stack) {

        return getVariantFromTag(stack.getTagCompound());
    }

    public static ItemStack getVariantFromTag (NBTTagCompound tag) {

        ItemStack stack = new ItemStack(Blocks.LOG);

        if (tag != null && tag.hasKey("BaseBlock")) {

            final ItemStack tagStack = new ItemStack(tag.getCompoundTag("BaseBlock"));

            if (!tagStack.isEmpty()) {

                stack = tagStack;
            }
        }

        return stack;
    }

    @Override
    public IBlockColor getColorHandler () {

        return (state, world, pos, index) -> ConfigurationHandler.defaultColorPacked;
    }
}