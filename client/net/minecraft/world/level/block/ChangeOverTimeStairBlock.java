package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ChangeOverTimeStairBlock extends StairBlock implements ChangeOverTimeBlock {
   private final Block changeTo;

   public ChangeOverTimeStairBlock(BlockState var1, BlockBehaviour.Properties var2, Block var3) {
      super(var1, var2);
      this.changeTo = var3;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      super.onPlace(var1, var2, var3, var4, var5);
      this.scheduleChange(var2, this, var3);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      this.change(var2, var1, var3);
   }

   public BlockState getChangeTo(BlockState var1) {
      return (BlockState)((BlockState)((BlockState)((BlockState)this.changeTo.defaultBlockState().setValue(FACING, var1.getValue(FACING))).setValue(HALF, var1.getValue(HALF))).setValue(SHAPE, var1.getValue(SHAPE))).setValue(WATERLOGGED, var1.getValue(WATERLOGGED));
   }
}
