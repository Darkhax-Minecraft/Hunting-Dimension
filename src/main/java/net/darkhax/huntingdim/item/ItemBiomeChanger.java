package net.darkhax.huntingdim.item;

import java.util.List;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.item.IColorfulItem;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.huntingdim.WorldUtils;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBiomeChanger extends Item implements IColorfulItem {

    public ItemBiomeChanger () {

        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

        tooltip.add(getBiomeForStack(stack).getBiomeName());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick (World worldIn, EntityPlayer playerIn, EnumHand handIn) {

        WorldUtils.setBiomes(worldIn, playerIn.getPosition(), ItemBiomeChanger.getBiomeForStack(playerIn.getHeldItem(handIn)));

        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> items) {

        if (this.isInCreativeTab(tab)) {

            for (final Biome biome : Biome.REGISTRY) {

                if (!biome.isMutation()) {

                    items.add(this.createFromBiome(biome));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getColorHandler () {

        return new Color();
    }

    public class Color implements IItemColor {

        @Override
        public int colorMultiplier (ItemStack stack, int tintIndex) {

            // System.out.println(getColorForStack(stack));
            return getColorForStack(stack, true);
        }

    }

    public ItemStack createFromBiome (Biome biome) {

        final ItemStack stack = new ItemStack(this);
        final NBTTagCompound tag = StackUtils.prepareStackTag(stack);
        tag.setInteger("HeldBiome", Biome.getIdForBiome(biome));
        return stack;
    }

    public static Biome getBiomeForStack (ItemStack stack) {

        final NBTTagCompound tag = StackUtils.prepareStackTag(stack);
        return Biome.getBiome(tag.getInteger("HeldBiome"));
    }

    @SideOnly(Side.CLIENT)
    public static int getColorForStack (ItemStack stack, boolean isGrass) {

        final Biome biome = getBiomeForStack(stack);
        return isGrass ? biome.getGrassColorAtPos(new BlockPos(0, 255, 0)) : biome.getSkyColorByTemp(biome.getDefaultTemperature());
    }
}
