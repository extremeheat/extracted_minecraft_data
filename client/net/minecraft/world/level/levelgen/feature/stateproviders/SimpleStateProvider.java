package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleStateProvider extends BlockStateProvider {
   public static final MapCodec<SimpleStateProvider> CODEC;
   private final BlockState state;

   protected SimpleStateProvider(final BlockState state) {
      super();
      this.state = state;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      return this.state;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("state").xmap(SimpleStateProvider::new, (p) -> p.state);
   }
}
