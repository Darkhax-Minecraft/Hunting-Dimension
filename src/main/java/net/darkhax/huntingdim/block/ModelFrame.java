package net.darkhax.huntingdim.block;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import net.darkhax.bookshelf.client.model.block.CachedDynamicBakedModel;
import net.darkhax.bookshelf.util.RenderUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFrame extends CachedDynamicBakedModel {

    private final Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> {

        assert location != null;
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    };

    private final ImmutableMap<? extends IModelPart, TRSRTransformation> transforms;

    public ModelFrame (IBakedModel baked, IModel raw) {

        super(baked, raw);
        this.transforms = RenderUtils.copyTransforms(baked);
    }

    @Override
    public String getCacheKey (IBlockState state, EnumFacing side) {

        ItemStack stack = ((IExtendedBlockState) state).getValue(BlockHuntingFrame.BASE_BLOCK);

        if (stack == null || stack.isEmpty()) {

            stack = new ItemStack(Blocks.LOG);
        }

        return RenderUtils.getParticleTexture(stack).getIconName();
    }

    @Override
    public String getCacheKey (ItemStack stack, World world, EntityLivingBase entity) {

        return RenderUtils.getParticleTexture(BlockHuntingFrame.getVariantFromTag(stack.getTagCompound())).getIconName();
    }

    @Override
    public IBakedModel generateBlockModel (String key) {

        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        final String texture = key == null ? "minecraft:blocks/stone" : key;
        builder.put("texture", texture);
        builder.put("particle", texture);
        return this.getRaw().retexture(builder.build()).bake(new SimpleModelState(this.transforms), DefaultVertexFormats.BLOCK, this.textureGetter);
    }
}