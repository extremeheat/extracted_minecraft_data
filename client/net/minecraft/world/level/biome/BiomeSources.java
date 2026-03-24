package net.minecraft.world.level.biome;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class BiomeSources {
   public BiomeSources() {
      super();
   }

   public static MapCodec<? extends BiomeSource> bootstrap(final Registry<MapCodec<? extends BiomeSource>> registry) {
      Registry.register(registry, (String)"fixed", FixedBiomeSource.CODEC);
      Registry.register(registry, (String)"multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(registry, (String)"checkerboard", CheckerboardColumnBiomeSource.CODEC);
      return (MapCodec)Registry.register(registry, (String)"the_end", TheEndBiomeSource.CODEC);
   }
}
