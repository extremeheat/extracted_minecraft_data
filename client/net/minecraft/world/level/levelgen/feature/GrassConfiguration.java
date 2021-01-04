package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GrassConfiguration implements FeatureConfiguration {
   public final BlockState state;

   public GrassConfiguration(BlockState var1) {
      super();
      this.state = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("state"), BlockState.serialize(var1, this.state).getValue())));
   }

   public static <T> GrassConfiguration deserialize(Dynamic<T> var0) {
      BlockState var1 = (BlockState)var0.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new GrassConfiguration(var1);
   }
}
