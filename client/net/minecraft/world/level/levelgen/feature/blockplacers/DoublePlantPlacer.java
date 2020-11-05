package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DoublePlantPlacer extends BlockPlacer {
   public static final Codec<DoublePlantPlacer> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   public static final DoublePlantPlacer INSTANCE = new DoublePlantPlacer();

   public DoublePlantPlacer() {
      super();
   }

   protected BlockPlacerType<?> type() {
      return BlockPlacerType.DOUBLE_PLANT_PLACER;
   }

   public void place(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4) {
      ((DoublePlantBlock)var3.getBlock()).placeAt(var1, var2, 2);
   }
}
