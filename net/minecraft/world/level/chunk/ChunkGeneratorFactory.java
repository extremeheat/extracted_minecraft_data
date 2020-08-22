package net.minecraft.world.level.chunk;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

interface ChunkGeneratorFactory {
   ChunkGenerator create(Level var1, BiomeSource var2, ChunkGeneratorSettings var3);
}
