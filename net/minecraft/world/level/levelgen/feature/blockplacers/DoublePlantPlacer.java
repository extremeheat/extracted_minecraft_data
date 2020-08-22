package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DoublePlantPlacer extends BlockPlacer {
   public DoublePlantPlacer() {
      super(BlockPlacerType.DOUBLE_PLANT_PLACER);
   }

   public DoublePlantPlacer(Dynamic var1) {
      this();
   }

   public void place(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4) {
      ((DoublePlantBlock)var3.getBlock()).placeAt(var1, var2, 2);
   }

   public Object serialize(DynamicOps var1) {
      return (new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("type"), var1.createString(Registry.BLOCK_PLACER_TYPES.getKey(this.type).toString()))))).getValue();
   }
}
