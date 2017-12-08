package net.darkhax.huntingdim;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.huntingdim.block.BlockHuntingFrame;
import net.darkhax.huntingdim.block.BlockHuntingPortal;
import net.darkhax.huntingdim.dimension.WorldProviderHunting;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "huntingdim", name = "Hunting Dimension", version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@")
public class HuntingDim {

    public static DimensionType dimensionType;

    public static final LoggingHelper LOG = new LoggingHelper("Hunting Dimension");
    public static final RegistryHelper REGISTRY = new RegistryHelper().setTab(CreativeTabs.MISC).enableAutoRegistration();

    public static Block portal;
    public static Block frame;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        new ConfigurationHandler();
        dimensionType = DimensionType.register("hunting_dim", "_hunting", ConfigurationHandler.dimensionId, WorldProviderHunting.class, false);
        DimensionManager.registerDimension(ConfigurationHandler.dimensionId, dimensionType);

        frame = REGISTRY.registerBlock(new BlockHuntingFrame(), "frame");
        portal = REGISTRY.registerBlock(new BlockHuntingPortal(), "portal");
    }
}