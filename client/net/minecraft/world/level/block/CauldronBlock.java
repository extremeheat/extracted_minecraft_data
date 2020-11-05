package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CauldronBlock extends AbstractCauldronBlock {
   public CauldronBlock(BlockBehaviour.Properties var1) {
      super(var1, CauldronInteraction.EMPTY);
   }

   protected static boolean shouldHandleRain(Level var0, BlockPos var1) {
      if (var0.random.nextInt(20) != 1) {
         return false;
      } else {
         return var0.getBiome(var1).getTemperature(var1) >= 0.15F;
      }
   }

   public void handleRain(BlockState var1, Level var2, BlockPos var3) {
      if (shouldHandleRain(var2, var3)) {
         var2.setBlockAndUpdate(var3, Blocks.WATER_CAULDRON.defaultBlockState());
      }
   }
}
