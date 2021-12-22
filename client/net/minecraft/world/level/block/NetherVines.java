package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class NetherVines {
   private static final double BONEMEAL_GROW_PROBABILITY_DECREASE_RATE = 0.826D;
   public static final double GROW_PER_TICK_PROBABILITY = 0.1D;

   public NetherVines() {
      super();
   }

   public static boolean isValidGrowthState(BlockState var0) {
      return var0.isAir();
   }

   public static int getBlocksToGrowWhenBonemealed(Random var0) {
      double var1 = 1.0D;

      int var3;
      for(var3 = 0; var0.nextDouble() < var1; ++var3) {
         var1 *= 0.826D;
      }

      return var3;
   }
}
