package net.darkhax.huntingdim;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.OreDictUtils;
import net.darkhax.huntingdim.block.BlockHuntingFrame;
import net.darkhax.huntingdim.block.BlockHuntingPortal;
import net.darkhax.huntingdim.dimension.WorldProviderHunting;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.darkhax.huntingdim.handler.DimensionEffectHandler;
import net.darkhax.huntingdim.item.ItemBiomeChanger;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "huntingdim", name = "Hunting Dimension", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.3.509,)", certificateFingerprint = "@FINGERPRINT@")
public class HuntingDim {

    public static DimensionType dimensionType;

    public static final LoggingHelper LOG = new LoggingHelper("Hunting Dimension");
    public static final RegistryHelper REGISTRY = new RegistryHelper().setTab(CreativeTabs.MISC).enableAutoRegistration();

    public static Block portal;
    public static Block frame;
    public static ItemBlock frameItem;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        new ConfigurationHandler();
        dimensionType = DimensionType.register("hunting_dim", "_hunting", ConfigurationHandler.dimensionId, WorldProviderHunting.class, false);
        DimensionManager.registerDimension(ConfigurationHandler.dimensionId, dimensionType);

        frame = new BlockHuntingFrame();
        frameItem = new ItemBlock(frame);
        REGISTRY.registerBlock(frame, frameItem, "frame");
        portal = REGISTRY.registerBlock(new BlockHuntingPortal(), "portal");
        REGISTRY.registerItem(new ItemBiomeChanger(), "biome_changer");

        REGISTRY.addShapedRecipe("portal_frame", new ItemStack(frameItem, 4), "xxx", "xyx", "xxx", 'x', OreDictUtils.LOG_WOOD, 'y', OreDictUtils.ARROW);

        MinecraftForge.EVENT_BUS.register(new DimensionEffectHandler());
    }
}