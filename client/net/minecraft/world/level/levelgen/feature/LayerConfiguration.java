package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LayerConfiguration implements FeatureConfiguration {
   public final int height;
   public final BlockState state;

   public LayerConfiguration(int var1, BlockState var2) {
      super();
      this.height = var1;
      this.state = var2;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("height"), var1.createInt(this.height), var1.createString("state"), BlockState.serialize(var1, this.state).getValue())));
   }

   public static <T> LayerConfiguration deserialize(Dynamic<T> var0) {
      int var1 = var0.get("height").asInt(0);
      BlockState var2 = (BlockState)var0.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new LayerConfiguration(var1, var2);
   }
}
