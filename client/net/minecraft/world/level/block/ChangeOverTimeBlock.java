package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangeOverTimeBlock<T extends Enum<T>> {
   int SCAN_DISTANCE = 4;

   Optional<BlockState> getNext(BlockState var1);

   float getChanceModifier();

   default void changeOverTime(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      float var5 = 0.05688889F;
      if (var4.nextFloat() < 0.05688889F) {
         this.getNextState(var1, var2, var3, var4).ifPresent((var2x) -> {
            var2.setBlockAndUpdate(var3, var2x);
         });
      }

   }

   T getAge();

   default Optional<BlockState> getNextState(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
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
            Block var12 = var2.getBlockState(var9).getBlock();
            if (var12 instanceof ChangeOverTimeBlock) {
               ChangeOverTimeBlock var11 = (ChangeOverTimeBlock)var12;
               Enum var16 = var11.getAge();
               if (this.getAge().getClass() == var16.getClass()) {
                  int var13 = var16.ordinal();
                  if (var13 < var5) {
                     return Optional.empty();
                  }

                  if (var13 > var5) {
                     ++var7;
                  } else {
                     ++var6;
                  }
               }
            }
         }
      }

      float var14 = (float)(var7 + 1) / (float)(var7 + var6 + 1);
      float var15 = var14 * var14 * this.getChanceModifier();
      return var4.nextFloat() < var15 ? this.getNext(var1) : Optional.empty();
   }
}
