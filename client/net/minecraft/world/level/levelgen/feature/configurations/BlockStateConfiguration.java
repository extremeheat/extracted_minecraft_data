package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateConfiguration implements FeatureConfiguration {
   public static final Codec<BlockStateConfiguration> CODEC;
   public final BlockState state;

   public BlockStateConfiguration(BlockState var1) {
      super();
      this.state = var1;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("state").xmap(BlockStateConfiguration::new, (var0) -> {
         return var0.state;
      }).codec();
   }
}
