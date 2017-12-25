package net.darkhax.huntingdim;

import java.util.Calendar;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.OreDictUtils;
import net.darkhax.huntingdim.block.BlockHuntingFrame;
import net.darkhax.huntingdim.block.BlockHuntingPortal;
import net.darkhax.huntingdim.creativetab.CreativeTabHunting;
import net.darkhax.huntingdim.dimension.WorldProviderHunting;
import net.darkhax.huntingdim.dimension.events.EventXmas;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.darkhax.huntingdim.handler.DimensionEffectHandler;
import net.darkhax.huntingdim.item.ItemBiomeChanger;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "huntingdim", name = "Hunting Dimension", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.3.510,)", certificateFingerprint = "@FINGERPRINT@")
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
        portal = REGISTRY.registerBlock(new BlockHuntingPortal(), "portal");
        moss = REGISTRY.registerItem(new ItemBiomeChanger(), "biome_changer");

        REGISTRY.addShapedRecipe("portal_frame", new ItemStack(frameItem, 4), "xxx", "xyx", "xxx", 'x', OreDictUtils.LOG_WOOD, 'y', OreDictUtils.ARROW);

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
        MinecraftForge.EVENT_BUS.register(new DimensionEffectHandler());
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

        final Calendar calendar = Calendar.getInstance();

        // If xmas time, initialize xmas stuff. 24th to 26th.
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {

            new EventXmas();
        }
    }
}