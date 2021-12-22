package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeOverTimeBlock<T extends Enum<T>> {
   int SCAN_DISTANCE = 4;

   Optional<BlockState> getNext(BlockState var1);

   float getChanceModifier();

   default void onRandomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      float var5 = 0.05688889F;
      if (var4.nextFloat() < 0.05688889F) {
         this.applyChangeOverTime(var1, var2, var3, var4);
      }

   }

   T getAge();

   default void applyChangeOverTime(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      int var5 = this.getAge().ordinal();
      int var6 = 0;
      int var7 = 0;
      Iterator var8 = BlockPos.withinManhattan(var3, 4, 4, 4).iterator();

      while(var8.hasNext()) {
         BlockPos var9 = (BlockPos)var8.next();
         int var10 = var9.distManhattan(var3);
         if (var10 > 4) {
            break;
         }

         if (!var9.equals(var3)) {
            BlockState var11 = var2.getBlockState(var9);
            Block var12 = var11.getBlock();
            if (var12 instanceof ChangeOverTimeBlock) {
               Enum var13 = ((ChangeOverTimeBlock)var12).getAge();
               if (this.getAge().getClass() == var13.getClass()) {
                  int var14 = var13.ordinal();
                  if (var14 < var5) {
                     return;
                  }

                  if (var14 > var5) {
                     ++var7;
                  } else {
                     ++var6;
                  }
               }
            }
         }
      }

      float var15 = (float)(var7 + 1) / (float)(var7 + var6 + 1);
      float var16 = var15 * var15 * this.getChanceModifier();
      if (var4.nextFloat() < var16) {
         this.getNext(var1).ifPresent((var2x) -> {
            var2.setBlockAndUpdate(var3, var2x);
         });
      }

   }
}
