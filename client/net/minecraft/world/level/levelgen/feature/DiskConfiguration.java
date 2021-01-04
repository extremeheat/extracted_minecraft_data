package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DiskConfiguration implements FeatureConfiguration {
   public final BlockState state;
   public final int radius;
   public final int ySize;
   public final List<BlockState> targets;

   public DiskConfiguration(BlockState var1, int var2, int var3, List<BlockState> var4) {
      super();
      this.state = var1;
      this.radius = var2;
      this.ySize = var3;
      this.targets = var4;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("state"), BlockState.serialize(var1, this.state).getValue(), var1.createString("radius"), var1.createInt(this.radius), var1.createString("y_size"), var1.createInt(this.ySize), var1.createString("targets"), var1.createList(this.targets.stream().map((var1x) -> {
         return BlockState.serialize(var1, var1x).getValue();
      })))));
   }

   public static <T> DiskConfiguration deserialize(Dynamic<T> var0) {
      BlockState var1 = (BlockState)var0.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      int var2 = var0.get("radius").asInt(0);
      int var3 = var0.get("y_size").asInt(0);
      List var4 = var0.get("targets").asList(BlockState::deserialize);
      return new DiskConfiguration(var1, var2, var3, var4);
   }
}
