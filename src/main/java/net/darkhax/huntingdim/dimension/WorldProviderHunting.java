package net.darkhax.huntingdim.dimension;

import net.darkhax.huntingdim.HuntingDimension;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProviderSingle;

public class WorldProviderHunting extends WorldProvider {

    private static final long MIDNIGHT = 18000;

    @Override
    protected void init () {

        this.biomeProvider = new BiomeProviderSingle(Biomes.PLAINS);

        // If we're not mimicking the surface world, use the configured world generator.
        if (!ConfigurationHandler.mimicSurfaceWorld) {

            for (final WorldType type : WorldType.WORLD_TYPES) {

                if (ConfigurationHandler.worldType.equalsIgnoreCase(type.getName())) {

                    this.terrainType = type;
                    break;
                }
            }
        }

        // Override generator settings
        this.generatorSettings = ConfigurationHandler.generatorPreset;
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