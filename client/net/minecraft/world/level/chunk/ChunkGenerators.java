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

   public static MapCodec<? extends ChunkGenerator> bootstrap(final Registry<MapCodec<? extends ChunkGenerator>> registry) {
      Registry.register(registry, (String)"noise", NoiseBasedChunkGenerator.CODEC);
      Registry.register(registry, (String)"flat", FlatLevelSource.CODEC);
      return (MapCodec)Registry.register(registry, (String)"debug", DebugLevelSource.CODEC);
   }
}
