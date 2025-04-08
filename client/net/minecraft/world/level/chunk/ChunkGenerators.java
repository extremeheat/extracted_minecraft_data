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
      Registry.register(var0, (String)"noise", NoiseBasedChunkGenerator.CODEC);
      Registry.register(var0, (String)"flat", FlatLevelSource.CODEC);
      return (MapCodec)Registry.register(var0, (String)"debug", DebugLevelSource.CODEC);
   }
}
