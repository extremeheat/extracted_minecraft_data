package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBlobConfiguration implements FeatureConfiguration {
   public final BlockState state;
   public final int startRadius;

   public BlockBlobConfiguration(BlockState var1, int var2) {
      this.state = var1;
      this.startRadius = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("state"), BlockState.serialize(var1, this.state).getValue(), var1.createString("start_radius"), var1.createInt(this.startRadius))));
   }

   public static BlockBlobConfiguration deserialize(Dynamic var0) {
      BlockState var1 = (BlockState)var0.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      int var2 = var0.get("start_radius").asInt(0);
      return new BlockBlobConfiguration(var1, var2);
   }
}
