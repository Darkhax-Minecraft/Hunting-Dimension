package net.darkhax.huntingdim.dimension;

import net.darkhax.huntingdim.HuntingDimension;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderHunting extends WorldProvider {

    private static final long MIDNIGHT = 18000;

    @Override
    public IChunkGenerator createChunkGenerator () {

        return this.terrainType.getChunkGenerator(this.world, ConfigurationHandler.generatorPreset);
    }

    @Override
    protected void init () {

        this.biomeProvider = new BiomeProviderSingle(Biomes.PLAINS);
    }

    @Override
    public DimensionType getDimensionType () {

        return HuntingDimension.dimensionType;
    }

    @Override
    public int getAverageGroundLevel () {

        return 70;
    }

    @Override
    public boolean canRespawnHere () {

        return false;
    }

    @Override
    public boolean isSurfaceWorld () {

        return false;
    }

    @Override
    public float calculateCelestialAngle (long worldTime, float partialTicks) {

        return 0f;
    }

    @Override
    public boolean isDaytime () {

        return false;
    }

    @Override
    public void setWorldTime (long time) {

        this.world.getWorldInfo().setWorldTime(MIDNIGHT);
    }

    @Override
    public long getWorldTime () {

        return MIDNIGHT;
    }
}