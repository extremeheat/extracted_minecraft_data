package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.MegaTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class MegaTreeFeature extends AbstractTreeFeature {
   public MegaTreeFeature(Function var1) {
      super(var1);
   }

   protected int calcTreeHeigth(Random var1, MegaTreeConfiguration var2) {
      int var3 = var1.nextInt(3) + var2.baseHeight;
      if (var2.heightInterval > 1) {
         var3 += var1.nextInt(var2.heightInterval);
      }

      return var3;
   }

   private boolean checkIsFree(LevelSimulatedReader var1, BlockPos var2, int var3) {
      boolean var4 = true;
      if (var2.getY() >= 1 && var2.getY() + var3 + 1 <= 256) {
         for(int var5 = 0; var5 <= 1 + var3; ++var5) {
            byte var6 = 2;
            if (var5 == 0) {
               var6 = 1;
            } else if (var5 >= 1 + var3 - 2) {
               var6 = 2;
            }

            for(int var7 = -var6; var7 <= var6 && var4; ++var7) {
               for(int var8 = -var6; var8 <= var6 && var4; ++var8) {
                  if (var2.getY() + var5 < 0 || var2.getY() + var5 >= 256 || !isFree(var1, var2.offset(var7, var5, var8))) {
                     var4 = false;
                  }
               }
            }
         }

         return var4;
      } else {
         return false;
      }
   }

   private boolean makeDirtFloor(LevelSimulatedRW var1, BlockPos var2) {
      BlockPos var3 = var2.below();
      if (isGrassOrDirt(var1, var3) && var2.getY() >= 2) {
         this.setDirtAt(var1, var3);
         this.setDirtAt(var1, var3.east());
         this.setDirtAt(var1, var3.south());
         this.setDirtAt(var1, var3.south().east());
         return true;
      } else {
         return false;
      }
   }

   protected boolean prepareTree(LevelSimulatedRW var1, BlockPos var2, int var3) {
      return this.checkIsFree(var1, var2, var3) && this.makeDirtFloor(var1, var2);
   }

   protected void placeDoubleTrunkLeaves(LevelSimulatedRW var1, Random var2, BlockPos var3, int var4, Set var5, BoundingBox var6, TreeConfiguration var7) {
      int var8 = var4 * var4;

      for(int var9 = -var4; var9 <= var4 + 1; ++var9) {
         for(int var10 = -var4; var10 <= var4 + 1; ++var10) {
            int var11 = Math.min(Math.abs(var9), Math.abs(var9 - 1));
            int var12 = Math.min(Math.abs(var10), Math.abs(var10 - 1));
            if (var11 + var12 < 7 && var11 * var11 + var12 * var12 <= var8) {
               this.placeLeaf(var1, var2, var3.offset(var9, 0, var10), var5, var6, var7);
            }
         }
      }

   }

   protected void placeSingleTrunkLeaves(LevelSimulatedRW var1, Random var2, BlockPos var3, int var4, Set var5, BoundingBox var6, TreeConfiguration var7) {
      int var8 = var4 * var4;

      for(int var9 = -var4; var9 <= var4; ++var9) {
         for(int var10 = -var4; var10 <= var4; ++var10) {
            if (var9 * var9 + var10 * var10 <= var8) {
               this.placeLeaf(var1, var2, var3.offset(var9, 0, var10), var5, var6, var7);
            }
         }
      }

   }

   protected void placeTrunk(LevelSimulatedRW var1, Random var2, BlockPos var3, int var4, Set var5, BoundingBox var6, MegaTreeConfiguration var7) {
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = 0; var9 < var4; ++var9) {
         var8.set((Vec3i)var3).move(0, var9, 0);
         if (isFree(var1, var8)) {
            this.placeLog(var1, var2, var8, var5, var6, var7);
         }

         if (var9 < var4 - 1) {
            var8.set((Vec3i)var3).move(1, var9, 0);
            if (isFree(var1, var8)) {
               this.placeLog(var1, var2, var8, var5, var6, var7);
            }

            var8.set((Vec3i)var3).move(1, var9, 1);
            if (isFree(var1, var8)) {
               this.placeLog(var1, var2, var8, var5, var6, var7);
            }

            var8.set((Vec3i)var3).move(0, var9, 1);
            if (isFree(var1, var8)) {
               this.placeLog(var1, var2, var8, var5, var6, var7);
            }
         }
      }

   }
}
