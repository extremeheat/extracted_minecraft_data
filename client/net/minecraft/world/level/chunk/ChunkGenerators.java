package net.minecraft.world.level.chunk;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

public class ChunkGenerators {
   public ChunkGenerators() {
      super();
   }

   public static void bootstrap(final WritableRegistry<MapCodec<? extends ChunkGenerator>> registry) {
      Registry.register(registry, (String)"noise", NoiseBasedChunkGenerator.CODEC);
      Registry.register(registry, (String)"flat", FlatLevelSource.CODEC);
      Registry.register(registry, (String)"debug", DebugLevelSource.CODEC);
   }
}
