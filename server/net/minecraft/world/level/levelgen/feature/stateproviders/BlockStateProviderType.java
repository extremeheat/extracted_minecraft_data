package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> {
   public static final BlockStateProviderType<SimpleStateProvider> SIMPLE_STATE_PROVIDER;
   public static final BlockStateProviderType<WeightedStateProvider> WEIGHTED_STATE_PROVIDER;
   public static final BlockStateProviderType<PlainFlowerProvider> PLAIN_FLOWER_PROVIDER;
   public static final BlockStateProviderType<ForestFlowerProvider> FOREST_FLOWER_PROVIDER;
   public static final BlockStateProviderType<RotatedBlockProvider> ROTATED_BLOCK_PROVIDER;
   private final Codec<P> codec;

   private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String var0, Codec<P> var1) {
      return (BlockStateProviderType)Registry.register(Registry.BLOCKSTATE_PROVIDER_TYPES, (String)var0, new BlockStateProviderType(var1));
   }

   private BlockStateProviderType(Codec<P> var1) {
      super();
      this.codec = var1;
   }

   public Codec<P> codec() {
      return this.codec;
   }

   static {
      SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleStateProvider.CODEC);
      WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", WeightedStateProvider.CODEC);
      PLAIN_FLOWER_PROVIDER = register("plain_flower_provider", PlainFlowerProvider.CODEC);
      FOREST_FLOWER_PROVIDER = register("forest_flower_provider", ForestFlowerProvider.CODEC);
      ROTATED_BLOCK_PROVIDER = register("rotated_block_provider", RotatedBlockProvider.CODEC);
   }
}
