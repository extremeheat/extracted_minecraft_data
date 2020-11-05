package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeOverTimeBlock {
   default int getChangeInterval(Random var1) {
      return 1200000 + var1.nextInt(768000);
   }

   BlockState getChangeTo(BlockState var1);

   default void scheduleChange(Level var1, Block var2, BlockPos var3) {
      var1.getBlockTicks().scheduleTick(var3, var2, this.getChangeInterval(var1.getRandom()));
   }

   default void change(Level var1, BlockState var2, BlockPos var3) {
      var1.setBlockAndUpdate(var3, this.getChangeTo(var2));
   }
}
