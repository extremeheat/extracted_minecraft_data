package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleBlockConfiguration implements FeatureConfiguration {
   public final BlockState toPlace;
   public final List placeOn;
   public final List placeIn;
   public final List placeUnder;

   public SimpleBlockConfiguration(BlockState var1, List var2, List var3, List var4) {
      this.toPlace = var1;
      this.placeOn = var2;
      this.placeIn = var3;
      this.placeUnder = var4;
   }

   public SimpleBlockConfiguration(BlockState var1, BlockState[] var2, BlockState[] var3, BlockState[] var4) {
      this(var1, (List)Lists.newArrayList(var2), (List)Lists.newArrayList(var3), (List)Lists.newArrayList(var4));
   }

   public Dynamic serialize(DynamicOps var1) {
      Object var2 = BlockState.serialize(var1, this.toPlace).getValue();
      Object var3 = var1.createList(this.placeOn.stream().map((var1x) -> {
         return BlockState.serialize(var1, var1x).getValue();
      }));
      Object var4 = var1.createList(this.placeIn.stream().map((var1x) -> {
         return BlockState.serialize(var1, var1x).getValue();
      }));
      Object var5 = var1.createList(this.placeUnder.stream().map((var1x) -> {
         return BlockState.serialize(var1, var1x).getValue();
      }));
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("to_place"), var2, var1.createString("place_on"), var3, var1.createString("place_in"), var4, var1.createString("place_under"), var5)));
   }

   public static SimpleBlockConfiguration deserialize(Dynamic var0) {
      BlockState var1 = (BlockState)var0.get("to_place").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      List var2 = var0.get("place_on").asList(BlockState::deserialize);
      List var3 = var0.get("place_in").asList(BlockState::deserialize);
      List var4 = var0.get("place_under").asList(BlockState::deserialize);
      return new SimpleBlockConfiguration(var1, var2, var3, var4);
   }
}
