package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ReplaceBlockConfiguration implements FeatureConfiguration {
   public final BlockState target;
   public final BlockState state;

   public ReplaceBlockConfiguration(BlockState var1, BlockState var2) {
      this.target = var1;
      this.state = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("target"), BlockState.serialize(var1, this.target).getValue(), var1.createString("state"), BlockState.serialize(var1, this.state).getValue())));
   }

   public static ReplaceBlockConfiguration deserialize(Dynamic var0) {
      BlockState var1 = (BlockState)var0.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      BlockState var2 = (BlockState)var0.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new ReplaceBlockConfiguration(var1, var2);
   }
}
