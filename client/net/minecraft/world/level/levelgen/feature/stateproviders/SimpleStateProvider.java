package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleStateProvider extends BlockStateProvider {
   public static final Codec<SimpleStateProvider> CODEC = BlockState.CODEC.fieldOf("state").xmap(SimpleStateProvider::new, var0 -> var0.state).codec();
   private final BlockState state;

   protected SimpleStateProvider(BlockState var1) {
      super();
      this.state = var1;
   }

   @Override
   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
   }

   @Override
   public BlockState getState(RandomSource var1, BlockPos var2) {
      return this.state;
   }
}
