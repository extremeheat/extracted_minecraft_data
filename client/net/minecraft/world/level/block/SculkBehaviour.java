package net.minecraft.world.level.block;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public interface SculkBehaviour {
   SculkBehaviour DEFAULT = new SculkBehaviour() {
      public boolean attemptSpreadVein(LevelAccessor var1, BlockPos var2, BlockState var3, @Nullable Collection<Direction> var4, boolean var5) {
         if (var4 == null) {
            return ((SculkVeinBlock)Blocks.SCULK_VEIN).getSameSpaceSpreader().spreadAll(var1.getBlockState(var2), var1, var2, var5) > 0L;
         } else if (!var4.isEmpty()) {
            return !var3.isAir() && !var3.getFluidState().is((Fluid)Fluids.WATER) ? false : SculkVeinBlock.regrow(var1, var2, var3, var4);
         } else {
            return SculkBehaviour.super.attemptSpreadVein(var1, var2, var3, var4, var5);
         }
      }

      public int attemptUseCharge(SculkSpreader.ChargeCursor var1, LevelAccessor var2, BlockPos var3, RandomSource var4, SculkSpreader var5, boolean var6) {
         return var1.getDecayDelay() > 0 ? var1.getCharge() : 0;
      }

      public int updateDecayDelay(int var1) {
         return Math.max(var1 - 1, 0);
      }
   };

   default byte getSculkSpreadDelay() {
      return 1;
   }

   default void onDischarged(LevelAccessor var1, BlockState var2, BlockPos var3, RandomSource var4) {
   }

   default boolean depositCharge(LevelAccessor var1, BlockPos var2, RandomSource var3) {
      return false;
   }

   default boolean attemptSpreadVein(LevelAccessor var1, BlockPos var2, BlockState var3, @Nullable Collection<Direction> var4, boolean var5) {
      return ((MultifaceBlock)Blocks.SCULK_VEIN).getSpreader().spreadAll(var3, var1, var2, var5) > 0L;
   }

   default boolean canChangeBlockStateOnSpread() {
      return true;
   }

   default int updateDecayDelay(int var1) {
      return 1;
   }

   int attemptUseCharge(SculkSpreader.ChargeCursor var1, LevelAccessor var2, BlockPos var3, RandomSource var4, SculkSpreader var5, boolean var6);
}
