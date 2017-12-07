package net.darkhax.huntingdim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.darkhax.huntingdim.block.BlockHuntingFrame;
import net.darkhax.huntingdim.block.BlockHuntingPortal;
import net.darkhax.huntingdim.dimension.WorldProviderHunting;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "huntingdim", name = "Hunting Dimension", version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@")
public class HuntingDim {

    public static DimensionType dimensionType;

    public static Block portal;
    public static Block frame;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        final ChunkGeneratorSettings.Serializer serial = new ChunkGeneratorSettings.Serializer();

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(serial.serialize(ChunkGeneratorSettings.Factory.jsonToFactory(""), null, null)));

        MinecraftForge.EVENT_BUS.register(this);
        dimensionType = DimensionType.register("hunting_dim", "_hunting", 28885, WorldProviderHunting.class, false);
        DimensionManager.registerDimension(28885, dimensionType);
    }

    @SubscribeEvent
    public void registerBlock (RegistryEvent.Register<Block> regi) {

        portal = new BlockHuntingPortal();
        portal.setRegistryName("portal");
        regi.getRegistry().register(portal);

        frame = new BlockHuntingFrame();
        frame.setRegistryName("frame");
        regi.getRegistry().register(frame);
    }

    @SubscribeEvent
    public void registerItem (RegistryEvent.Register<Item> regi) {

        final Item itemportal = new ItemBlock(portal);
        itemportal.setRegistryName("portal");
        regi.getRegistry().register(itemportal);

        final Item itemframe = new ItemBlock(frame);
        itemframe.setRegistryName("frame");
        regi.getRegistry().register(itemframe);
    }
}