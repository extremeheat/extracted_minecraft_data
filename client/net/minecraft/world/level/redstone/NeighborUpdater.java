package net.minecraft.world.level.redstone;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface NeighborUpdater {
   Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};

   void shapeUpdate(Direction var1, BlockState var2, BlockPos var3, BlockPos var4, int var5, int var6);

   void neighborChanged(BlockPos var1, Block var2, @Nullable Orientation var3);

   void neighborChanged(BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5);

   default void updateNeighborsAtExceptFromFacing(BlockPos var1, Block var2, @Nullable Direction var3, @Nullable Orientation var4) {
      Direction[] var5 = UPDATE_ORDER;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         if (var8 != var3) {
            this.neighborChanged(var1.relative(var8), var2, (Orientation)null);
         }
      }

   }

   static void executeShapeUpdate(LevelAccessor var0, Direction var1, BlockPos var2, BlockPos var3, BlockState var4, int var5, int var6) {
      BlockState var7 = var0.getBlockState(var2);
      if ((var5 & 128) == 0 || !var7.is(Blocks.REDSTONE_WIRE)) {
         BlockState var8 = var7.updateShape(var0, var0, var2, var1, var3, var4, var0.getRandom());
         Block.updateOrDestroy(var7, var8, var0, var2, var5, var6);
      }
   }

   static void executeUpdate(Level var0, BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5) {
      try {
         var1.handleNeighborChanged(var0, var2, var3, var4, var5);
      } catch (Throwable var9) {
         CrashReport var7 = CrashReport.forThrowable(var9, "Exception while updating neighbours");
         CrashReportCategory var8 = var7.addCategory("Block being updated");
         var8.setDetail("Source block type", () -> {
            try {
               return String.format(Locale.ROOT, "ID #%s (%s // %s)", BuiltInRegistries.BLOCK.getKey(var3), var3.getDescriptionId(), var3.getClass().getCanonicalName());
            } catch (Throwable var2) {
               return "ID #" + String.valueOf(BuiltInRegistries.BLOCK.getKey(var3));
            }
         });
         CrashReportCategory.populateBlockDetails(var8, var0, var2, var1);
         throw new ReportedException(var7);
      }
   }
}
