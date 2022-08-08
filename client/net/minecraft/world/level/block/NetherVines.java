package net.minecraft.world.level.block;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class NetherVines {
   private static final double BONEMEAL_GROW_PROBABILITY_DECREASE_RATE = 0.826;
   public static final double GROW_PER_TICK_PROBABILITY = 0.1;

   public NetherVines() {
      super();
   }

   public static boolean isValidGrowthState(BlockState var0) {
      return var0.isAir();
   }

   public static int getBlocksToGrowWhenBonemealed(RandomSource var0) {
      double var1 = 1.0;

      int var3;
      for(var3 = 0; var0.nextDouble() < var1; ++var3) {
         var1 *= 0.826;
      }

      return var3;
   }
}
