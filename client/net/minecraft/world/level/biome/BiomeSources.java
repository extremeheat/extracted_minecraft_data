package net.minecraft.world.level.biome;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public class BiomeSources {
   public BiomeSources() {
      super();
   }

   public static void bootstrap(final WritableRegistry<MapCodec<? extends BiomeSource>> registry) {
      Registry.register(registry, (String)"fixed", FixedBiomeSource.CODEC);
      Registry.register(registry, (String)"multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(registry, (String)"checkerboard", CheckerboardColumnBiomeSource.CODEC);
      Registry.register(registry, (String)"the_end", TheEndBiomeSource.CODEC);
   }
}
