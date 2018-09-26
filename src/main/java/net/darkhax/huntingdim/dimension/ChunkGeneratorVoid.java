package net.darkhax.huntingdim.dimension;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorFlat;

public class ChunkGeneratorVoid extends ChunkGeneratorFlat {

    private final World world;

    public ChunkGeneratorVoid (World world) {

        super(world, world.getSeed(), false, null);
        this.world = world;
    }

    @Override
    public void populate (int x, int z) {

    }

    @Override
    public Chunk generateChunk (int x, int z) {

        final Chunk chunk = new Chunk(this.world, new ChunkPrimer(), x, z);
        final Biome[] biomes = this.world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);

        final byte[] chunkBiomes = chunk.getBiomeArray();
        for (int i = 0; i < chunkBiomes.length; ++i) {

            chunkBiomes[i] = (byte) Biome.getIdForBiome(biomes[i]);
        }

        chunk.generateSkylightMap();
        return chunk;
    }
}