package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RotatedBlockProvider extends BlockStateProvider {
   private final Block block;

   public RotatedBlockProvider(Block var1) {
      super(BlockStateProviderType.SIMPLE_STATE_PROVIDER);
      this.block = var1;
   }

   public RotatedBlockProvider(Dynamic var1) {
      this(BlockState.deserialize(var1.get("state").orElseEmptyMap()).getBlock());
   }

   public BlockState getState(Random var1, BlockPos var2) {
      Direction.Axis var3 = Direction.Axis.getRandomAxis(var1);
      return (BlockState)this.block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, var3);
   }

   public Object serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("type"), var1.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.type).toString())).put(var1.createString("state"), BlockState.serialize(var1, this.block.defaultBlockState()).getValue());
      return (new Dynamic(var1, var1.createMap(var2.build()))).getValue();
   }
}
