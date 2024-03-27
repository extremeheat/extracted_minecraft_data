package net.minecraft.world.level.chunk;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public class ChunkGenerators {
   public ChunkGenerators() {
      super();
   }

   public static MapCodec<? extends ChunkGenerator> bootstrap(Registry<MapCodec<? extends ChunkGenerator>> var0) {
      Registry.register(var0, "noise", NoiseBasedChunkGenerator.CODEC);
      Registry.register(var0, "flat", FlatLevelSource.CODEC);
      return Registry.register(var0, "debug", DebugLevelSource.CODEC);
   }
}
