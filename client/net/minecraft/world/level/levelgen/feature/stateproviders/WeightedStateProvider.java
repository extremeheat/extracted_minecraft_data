package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedStateProvider extends BlockStateProvider {
   public static final MapCodec<WeightedStateProvider> CODEC;
   private final WeightedList<BlockState> weightedList;

   private static DataResult<WeightedStateProvider> create(WeightedList<BlockState> var0) {
      return var0.isEmpty() ? DataResult.error(() -> "WeightedStateProvider with no states") : DataResult.success(new WeightedStateProvider(var0));
   }

   public WeightedStateProvider(WeightedList<BlockState> var1) {
      super();
      this.weightedList = var1;
   }

   public WeightedStateProvider(WeightedList.Builder<BlockState> var1) {
      this(var1.build());
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
   }

   public BlockState getState(RandomSource var1, BlockPos var2) {
      return this.weightedList.getRandomOrThrow(var1);
   }

   static {
      CODEC = WeightedList.nonEmptyCodec(BlockState.CODEC).comapFlatMap(WeightedStateProvider::create, (var0) -> var0.weightedList).fieldOf("entries");
   }
}
