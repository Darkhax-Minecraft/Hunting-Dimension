package net.darkhax.huntingdim;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.OreDictUtils;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.huntingdim.block.BlockHuntingFrame;
import net.darkhax.huntingdim.block.BlockHuntingPortal;
import net.darkhax.huntingdim.block.ModelFrame;
import net.darkhax.huntingdim.block.TileEntityFrame;
import net.darkhax.huntingdim.creativetab.CreativeTabHunting;
import net.darkhax.huntingdim.dimension.WorldProviderHunting;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.darkhax.huntingdim.handler.DimensionEffectHandler;
import net.darkhax.huntingdim.item.ItemBiomeChanger;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "huntingdim", name = "Hunting Dimension", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.3.533,)", certificateFingerprint = "@FINGERPRINT@")
public class HuntingDimension {

    public static DimensionType dimensionType;

    public static final LoggingHelper LOG = new LoggingHelper("Hunting Dimension");
    public static final RegistryHelper REGISTRY = new RegistryHelper().setTab(new CreativeTabHunting()).enableAutoRegistration();

    public static Block portal;
    public static Block frame;
    public static ItemBlock frameItem;
    public static Item moss;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        new ConfigurationHandler();
        dimensionType = DimensionType.register("hunting_dim", "_hunting", ConfigurationHandler.dimensionId, WorldProviderHunting.class, false);
        DimensionManager.registerDimension(ConfigurationHandler.dimensionId, dimensionType);

        frame = new BlockHuntingFrame();
        frameItem = new ItemBlock(frame);
        REGISTRY.registerBlock(frame, frameItem, "frame");
        GameRegistry.registerTileEntity(TileEntityFrame.class, "huntingdim:frame");
        portal = REGISTRY.registerBlock(new BlockHuntingPortal(), "portal");
        moss = REGISTRY.registerItem(new ItemBiomeChanger(), "biome_changer");

        // Basic moss recipes
        final ItemStack mossPlains = ItemBiomeChanger.setBiome(new ItemStack(moss, 4, 0), Biomes.PLAINS);
        REGISTRY.addShapedRecipe("moss_sappling", mossPlains, "xxx", "xyx", "xxx", 'x', OreDictUtils.TREE_SAPLING, 'y', Blocks.MOSSY_COBBLESTONE);
        REGISTRY.addShapedRecipe("moss_leaves", mossPlains, "xxx", "xyx", "xxx", 'x', OreDictUtils.TREE_LEAVES, 'y', Blocks.MOSSY_COBBLESTONE);
        REGISTRY.addShapedRecipe("moss_vines", mossPlains, "xxx", "xyx", "xxx", 'x', OreDictUtils.VINE, 'y', Blocks.MOSSY_COBBLESTONE);

        // Special moss recipes
        final ItemStack stackMoss = ItemBiomeChanger.setBiome(new ItemStack(moss), Biomes.PLAINS);
        REGISTRY.addShapedRecipe("moss_special_nether", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.HELL), "xxx", "xyx", "xxx", 'x', OreDictUtils.NETHERRACK, 'y', moss);
        REGISTRY.addShapedRecipe("moss_special_end", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.SKY), "xxx", "xyx", "xxx", 'x', OreDictUtils.ENDSTONE, 'y', moss);
        REGISTRY.addShapedRecipe("moss_special_ice", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.ICE_PLAINS), "xxx", "xyx", "xxx", 'x', Blocks.ICE, 'y', moss);
        REGISTRY.addShapedRecipe("moss_special_dessert", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.DESERT), "xxx", "xyx", "xxx", 'x', OreDictUtils.SAND, 'y', moss);
        REGISTRY.addShapedRecipe("moss_special_jungle", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.JUNGLE), "xxx", "xyx", "xxx", 'x', OreDictUtils.DYE_BROWN, 'y', moss);
        REGISTRY.addShapedRecipe("moss_special_swamp_red", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.SWAMPLAND), "xxx", "xyx", "xxx", 'x', Blocks.RED_MUSHROOM, 'y', moss);
        REGISTRY.addShapedRecipe("moss_special_swamp_brown", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.SWAMPLAND), "xxx", "xyx", "xxx", 'x', Blocks.BROWN_MUSHROOM, 'y', moss);
        REGISTRY.addShapedRecipe("moss_special_mesa", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.MESA), "xxx", "xyx", "xxx", 'x', Blocks.HARDENED_CLAY, 'y', moss);
        REGISTRY.addShapedRecipe("moss_special_mesa_stained", ItemBiomeChanger.setBiome(stackMoss.copy(), Biomes.MESA), "xxx", "xyx", "xxx", 'x', Blocks.STAINED_HARDENED_CLAY, 'y', moss);

        // Dimension specific events
        MinecraftForge.EVENT_BUS.register(new DimensionEffectHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerRecipes (RegistryEvent.Register<IRecipe> event) {

        for (final ItemStack logStack : StackUtils.getAllBlocksForOredict(OreDictUtils.LOG_WOOD)) {

            final ItemStack output = BlockHuntingFrame.createFrameVariant(logStack);
            output.setCount(4);

            final ShapedOreRecipe recipe = new ShapedOreRecipe(null, output, "xxx", "xax", "xxx", 'x', logStack, 'a', OreDictUtils.ARROW);
            recipe.setRegistryName("frame_" + StackUtils.getStackIdentifier(logStack).replace(":", "_") + "_" + logStack.getItemDamage());
            event.getRegistry().register(recipe);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onModelBake (ModelBakeEvent event) {

        try {

            // Loads the model from the Json file
            final ResourceLocation modelLocation = new ResourceLocation("huntingdim", "block/frame");
            final IModel raw = ModelLoaderRegistry.getModel(modelLocation);

            // The key of the baked model in the model registry.
            final ModelResourceLocation bakedLocation = new ModelResourceLocation(new ResourceLocation("huntingdim", "frame"), "normal");
            final IBakedModel baked = event.getModelRegistry().getObject(bakedLocation);
            event.getModelRegistry().putObject(bakedLocation, new ModelFrame(baked, raw));

            final ModelResourceLocation itemLocation = new ModelResourceLocation(new ResourceLocation("huntingdim", "frame"), "inventory");
            final IBakedModel bakedItem = event.getModelRegistry().getObject(itemLocation);
            event.getModelRegistry().putObject(itemLocation, new ModelFrame(bakedItem, raw));

        }

        catch (final Exception e) {

            LOG.catching(e);
        }
    }
}