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

   private static DataResult<WeightedStateProvider> create(final WeightedList<BlockState> weightedList) {
      return weightedList.isEmpty() ? DataResult.error(() -> "WeightedStateProvider with no states") : DataResult.success(new WeightedStateProvider(weightedList));
   }

   public WeightedStateProvider(final WeightedList<BlockState> weightedList) {
      super();
      this.weightedList = weightedList;
   }

   public WeightedStateProvider(final WeightedList.Builder<BlockState> weightedList) {
      this(weightedList.build());
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
   }

   public BlockState getState(final RandomSource random, final BlockPos pos) {
      return this.weightedList.getRandomOrThrow(random);
   }

   static {
      CODEC = WeightedList.nonEmptyCodec(BlockState.CODEC).comapFlatMap(WeightedStateProvider::create, (p) -> p.weightedList).fieldOf("entries");
   }
}
