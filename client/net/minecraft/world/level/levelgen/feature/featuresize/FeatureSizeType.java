package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FeatureSizeType<P extends FeatureSize> {
   public static final FeatureSizeType<TwoLayersFeatureSize> TWO_LAYERS_FEATURE_SIZE;
   public static final FeatureSizeType<ThreeLayersFeatureSize> THREE_LAYERS_FEATURE_SIZE;
   private final MapCodec<P> codec;

   private static <P extends FeatureSize> FeatureSizeType<P> register(String var0, MapCodec<P> var1) {
      return (FeatureSizeType)Registry.register(BuiltInRegistries.FEATURE_SIZE_TYPE, (String)var0, new FeatureSizeType(var1));
   }

   private FeatureSizeType(MapCodec<P> var1) {
      super();
      this.codec = var1;
   }

   public MapCodec<P> codec() {
      return this.codec;
   }

   static {
      TWO_LAYERS_FEATURE_SIZE = register("two_layers_feature_size", TwoLayersFeatureSize.CODEC);
      THREE_LAYERS_FEATURE_SIZE = register("three_layers_feature_size", ThreeLayersFeatureSize.CODEC);
   }
}
