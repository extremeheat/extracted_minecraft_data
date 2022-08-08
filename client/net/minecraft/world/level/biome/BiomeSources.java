package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class BiomeSources {
   public BiomeSources() {
      super();
   }

   public static Codec<? extends BiomeSource> bootstrap(Registry<Codec<? extends BiomeSource>> var0) {
      Registry.register(var0, (String)"fixed", FixedBiomeSource.CODEC);
      Registry.register(var0, (String)"multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(var0, (String)"checkerboard", CheckerboardColumnBiomeSource.CODEC);
      return (Codec)Registry.register(var0, (String)"the_end", TheEndBiomeSource.CODEC);
   }
}
