package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateConfiguration implements FeatureConfiguration {
   public static final Codec<BlockStateConfiguration> CODEC;
   public final BlockState state;

   public BlockStateConfiguration(final BlockState state) {
      super();
      this.state = state;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("state").xmap(BlockStateConfiguration::new, (c) -> c.state).codec();
   }
}
