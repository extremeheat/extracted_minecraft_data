package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleBlockPlacer extends BlockPlacer {
   public static final Codec<SimpleBlockPlacer> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   public static final SimpleBlockPlacer INSTANCE = new SimpleBlockPlacer();

   public SimpleBlockPlacer() {
      super();
   }

   protected BlockPlacerType<?> type() {
      return BlockPlacerType.SIMPLE_BLOCK_PLACER;
   }

   public void place(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4) {
      var1.setBlock(var2, var3, 2);
   }
}
