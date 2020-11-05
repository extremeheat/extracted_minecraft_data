package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ChangeOverTimeFullBlock extends Block implements ChangeOverTimeBlock {
   private final Block changeTo;

   protected ChangeOverTimeFullBlock(BlockBehaviour.Properties var1, Block var2) {
      super(var1);
      this.changeTo = var2;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      super.onPlace(var1, var2, var3, var4, var5);
      this.scheduleChange(var2, this, var3);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      super.tick(var1, var2, var3, var4);
      this.change(var2, var2.getBlockState(var3), var3);
   }

   public BlockState getChangeTo(BlockState var1) {
      return this.changeTo.defaultBlockState();
   }
}
