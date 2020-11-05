package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleStateProvider extends BlockStateProvider {
   public static final Codec<SimpleStateProvider> CODEC;
   private final BlockState state;

   public SimpleStateProvider(BlockState var1) {
      super();
      this.state = var1;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
   }

   public BlockState getState(Random var1, BlockPos var2) {
      return this.state;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("state").xmap(SimpleStateProvider::new, (var0) -> {
         return var0.state;
      }).codec();
   }
}
