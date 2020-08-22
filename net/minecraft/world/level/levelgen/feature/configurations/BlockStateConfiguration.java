package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateConfiguration implements FeatureConfiguration {
   public final BlockState state;

   public BlockStateConfiguration(BlockState var1) {
      this.state = var1;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("state"), BlockState.serialize(var1, this.state).getValue())));
   }

   public static BlockStateConfiguration deserialize(Dynamic var0) {
      BlockState var1 = (BlockState)var0.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new BlockStateConfiguration(var1);
   }
}
