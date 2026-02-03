package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BlockPileConfiguration implements FeatureConfiguration {
   public static final Codec<BlockPileConfiguration> CODEC;
   public final BlockStateProvider stateProvider;

   public BlockPileConfiguration(final BlockStateProvider stateProvider) {
      super();
      this.stateProvider = stateProvider;
   }

   static {
      CODEC = BlockStateProvider.CODEC.fieldOf("state_provider").xmap(BlockPileConfiguration::new, (c) -> c.stateProvider).codec();
   }
}
