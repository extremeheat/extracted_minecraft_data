package net.minecraft.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;

interface IChunkGeneratorFactory<C extends IChunkGenSettings, T extends IChunkGenerator<C>> {
   T create(World var1, BiomeProvider var2, C var3);
}
