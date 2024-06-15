package net.minecraft.world.level.biome;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class BiomeSources {
   public BiomeSources() {
      super();
   }

   public static MapCodec<? extends BiomeSource> bootstrap(Registry<MapCodec<? extends BiomeSource>> var0) {
      Registry.register(var0, "fixed", FixedBiomeSource.CODEC);
      Registry.register(var0, "multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(var0, "checkerboard", CheckerboardColumnBiomeSource.CODEC);
      return Registry.register(var0, "the_end", TheEndBiomeSource.CODEC);
   }
}
