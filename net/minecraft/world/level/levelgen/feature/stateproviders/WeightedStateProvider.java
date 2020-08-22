package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.behavior.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedStateProvider extends BlockStateProvider {
   private final WeightedList weightedList;

   private WeightedStateProvider(WeightedList var1) {
      super(BlockStateProviderType.WEIGHTED_STATE_PROVIDER);
      this.weightedList = var1;
   }

   public WeightedStateProvider() {
      this(new WeightedList());
   }

   public WeightedStateProvider(Dynamic var1) {
      this(new WeightedList(var1.get("entries").orElseEmptyList(), BlockState::deserialize));
   }

   public WeightedStateProvider add(BlockState var1, int var2) {
      this.weightedList.add(var1, var2);
      return this;
   }

   public BlockState getState(Random var1, BlockPos var2) {
      return (BlockState)this.weightedList.getOne(var1);
   }

   public Object serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("type"), var1.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.type).toString())).put(var1.createString("entries"), this.weightedList.serialize(var1, (var1x) -> {
         return BlockState.serialize(var1, var1x);
      }));
      return (new Dynamic(var1, var1.createMap(var2.build()))).getValue();
   }
}
