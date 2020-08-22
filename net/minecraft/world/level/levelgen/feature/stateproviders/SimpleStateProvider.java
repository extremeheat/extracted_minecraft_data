package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleStateProvider extends BlockStateProvider {
   private final BlockState state;

   public SimpleStateProvider(BlockState var1) {
      super(BlockStateProviderType.SIMPLE_STATE_PROVIDER);
      this.state = var1;
   }

   public SimpleStateProvider(Dynamic var1) {
      this(BlockState.deserialize(var1.get("state").orElseEmptyMap()));
   }

   public BlockState getState(Random var1, BlockPos var2) {
      return this.state;
   }

   public Object serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("type"), var1.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.type).toString())).put(var1.createString("state"), BlockState.serialize(var1, this.state).getValue());
      return (new Dynamic(var1, var1.createMap(var2.build()))).getValue();
   }
}
