package net.darkhax.huntingdim.dimension;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.util.WorldUtils;
import net.darkhax.huntingdim.HuntingDimension;
import net.darkhax.huntingdim.handler.ConfigurationHandler;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderHunting extends WorldProvider {

    private static final long MIDNIGHT = 18000;

    @Override
    public IChunkGenerator createChunkGenerator () {

        return ConfigurationHandler.isVoidWorld ? new ChunkGeneratorVoid(this.world) : super.createChunkGenerator();
    }

    @Override
    protected void init () {

        // Set the base biome to match config
        final Biome configBiome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(ConfigurationHandler.defaultBiome));
        this.biomeProvider = new BiomeProviderSingle(configBiome != null ? configBiome : Biomes.PLAINS);

        // If we're not mimicking the surface world, use the configured world generator.
        if (!ConfigurationHandler.mimicSurfaceWorld) {

            for (final WorldType type : WorldType.WORLD_TYPES) {

                if (type != null && ConfigurationHandler.worldType.equalsIgnoreCase(type.getName())) {

                    WorldUtils.setWorldType(this, type);
                    break;
                }
            }
        }

        // Override generator settings
        WorldUtils.setWorldSettings(this, ConfigurationHandler.generatorPreset);
        this.hasSkyLight = true;
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

        return ConfigurationHandler.allowRespawn;
    }

    @Override
    public float calculateCelestialAngle (long worldTime, float partialTicks) {

        return 0.5f;
    }

    @Override
    public boolean isDaytime () {

        return false;
    }

    @Override
    public float getSunBrightness (float par1) {

        return 0;
    }

    @Override
    public float getSunBrightnessFactor (float par1) {

        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor (float p_76562_1_, float p_76562_2_) {

        return ConfigurationHandler.defaultColorVector;
    }
    
    @Override
    public boolean isSurfaceWorld() {
        
        return false;
    }
    
    @Nullable
    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraftforge.client.IRenderHandler getSkyRenderer() {
        
        return RenderHuntingSky.INSTANCE;
    }
}
