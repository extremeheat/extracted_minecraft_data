package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedStateProvider extends BlockStateProvider {
   public static final Codec<WeightedStateProvider> CODEC;
   private final WeightedList<BlockState> weightedList;

   private static DataResult<WeightedStateProvider> create(WeightedList<BlockState> var0) {
      return var0.isEmpty() ? DataResult.error("WeightedStateProvider with no states") : DataResult.success(new WeightedStateProvider(var0));
   }

   private WeightedStateProvider(WeightedList<BlockState> var1) {
      super();
      this.weightedList = var1;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
   }

   public WeightedStateProvider() {
      this(new WeightedList());
   }

   public WeightedStateProvider add(BlockState var1, int var2) {
      this.weightedList.add(var1, var2);
      return this;
   }

   public BlockState getState(Random var1, BlockPos var2) {
      return (BlockState)this.weightedList.getOne(var1);
   }

   static {
      CODEC = WeightedList.codec(BlockState.CODEC).comapFlatMap(WeightedStateProvider::create, (var0) -> {
         return var0.weightedList;
      }).fieldOf("entries").codec();
   }
}
