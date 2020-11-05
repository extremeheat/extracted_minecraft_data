package net.minecraft.data.worldgen;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class Carvers {
   public static final ConfiguredWorldCarver<ProbabilityFeatureConfiguration> CAVE;
   public static final ConfiguredWorldCarver<ProbabilityFeatureConfiguration> CANYON;
   public static final ConfiguredWorldCarver<ProbabilityFeatureConfiguration> OCEAN_CAVE;
   public static final ConfiguredWorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CANYON;
   public static final ConfiguredWorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CAVE;
   public static final ConfiguredWorldCarver<ProbabilityFeatureConfiguration> NETHER_CAVE;

   private static <WC extends CarverConfiguration> ConfiguredWorldCarver<WC> register(String var0, ConfiguredWorldCarver<WC> var1) {
      return (ConfiguredWorldCarver)BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_CARVER, (String)var0, var1);
   }

   static {
      CAVE = register("cave", WorldCarver.CAVE.configured(new ProbabilityFeatureConfiguration(0.14285715F)));
      CANYON = register("canyon", WorldCarver.CANYON.configured(new ProbabilityFeatureConfiguration(0.02F)));
      OCEAN_CAVE = register("ocean_cave", WorldCarver.CAVE.configured(new ProbabilityFeatureConfiguration(0.06666667F)));
      UNDERWATER_CANYON = register("underwater_canyon", WorldCarver.UNDERWATER_CANYON.configured(new ProbabilityFeatureConfiguration(0.02F)));
      UNDERWATER_CAVE = register("underwater_cave", WorldCarver.UNDERWATER_CAVE.configured(new ProbabilityFeatureConfiguration(0.06666667F)));
      NETHER_CAVE = register("nether_cave", WorldCarver.NETHER_CAVE.configured(new ProbabilityFeatureConfiguration(0.2F)));
   }
}
