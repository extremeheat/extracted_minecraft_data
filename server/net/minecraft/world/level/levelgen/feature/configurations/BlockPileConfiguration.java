package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BlockPileConfiguration implements FeatureConfiguration {
   public static final Codec<BlockPileConfiguration> CODEC;
   public final BlockStateProvider stateProvider;

   public BlockPileConfiguration(BlockStateProvider var1) {
      super();
      this.stateProvider = var1;
   }

   static {
      CODEC = BlockStateProvider.CODEC.fieldOf("state_provider").xmap(BlockPileConfiguration::new, (var0) -> {
         return var0.stateProvider;
      }).codec();
   }
}
