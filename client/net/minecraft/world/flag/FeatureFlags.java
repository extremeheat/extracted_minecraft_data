package net.minecraft.world.flag;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;

public class FeatureFlags {
   public static final FeatureFlag VANILLA;
   public static final FeatureFlag BUNDLE;
   public static final FeatureFlag TRADE_REBALANCE;
   public static final FeatureFlagRegistry REGISTRY;
   public static final Codec<FeatureFlagSet> CODEC;
   public static final FeatureFlagSet VANILLA_SET;
   public static final FeatureFlagSet DEFAULT_FLAGS;

   public FeatureFlags() {
      super();
   }

   public static String printMissingFlags(FeatureFlagSet var0, FeatureFlagSet var1) {
      return printMissingFlags(REGISTRY, var0, var1);
   }

   public static String printMissingFlags(FeatureFlagRegistry var0, FeatureFlagSet var1, FeatureFlagSet var2) {
      Set var3 = var0.toNames(var2);
      Set var4 = var0.toNames(var1);
      return (String)var3.stream().filter((var1x) -> {
         return !var4.contains(var1x);
      }).map(ResourceLocation::toString).collect(Collectors.joining(", "));
   }

   public static boolean isExperimental(FeatureFlagSet var0) {
      return !var0.isSubsetOf(VANILLA_SET);
   }

   static {
      FeatureFlagRegistry.Builder var0 = new FeatureFlagRegistry.Builder("main");
      VANILLA = var0.createVanilla("vanilla");
      BUNDLE = var0.createVanilla("bundle");
      TRADE_REBALANCE = var0.createVanilla("trade_rebalance");
      REGISTRY = var0.build();
      CODEC = REGISTRY.codec();
      VANILLA_SET = FeatureFlagSet.of(VANILLA);
      DEFAULT_FLAGS = VANILLA_SET;
   }
}
