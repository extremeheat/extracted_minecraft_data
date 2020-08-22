package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.MegaTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaJungleTreeFeature extends MegaTreeFeature {
   public MegaJungleTreeFeature(Function var1) {
      super(var1);
   }

   public boolean doPlace(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, Set var5, BoundingBox var6, MegaTreeConfiguration var7) {
      int var8 = this.calcTreeHeigth(var2, var7);
      if (!this.prepareTree(var1, var3, var8)) {
         return false;
      } else {
         this.createCrown(var1, var2, var3.above(var8), 2, var5, var6, var7);

         for(int var9 = var3.getY() + var8 - 2 - var2.nextInt(4); var9 > var3.getY() + var8 / 2; var9 -= 2 + var2.nextInt(4)) {
            float var10 = var2.nextFloat() * 6.2831855F;
            int var11 = var3.getX() + (int)(0.5F + Mth.cos(var10) * 4.0F);
            int var12 = var3.getZ() + (int)(0.5F + Mth.sin(var10) * 4.0F);

            int var13;
            for(var13 = 0; var13 < 5; ++var13) {
               var11 = var3.getX() + (int)(1.5F + Mth.cos(var10) * (float)var13);
               var12 = var3.getZ() + (int)(1.5F + Mth.sin(var10) * (float)var13);
               BlockPos var14 = new BlockPos(var11, var9 - 3 + var13 / 2, var12);
               this.placeLog(var1, var2, var14, var4, var6, var7);
            }

            var13 = 1 + var2.nextInt(2);
            int var17 = var9;

            for(int var15 = var9 - var13; var15 <= var17; ++var15) {
               int var16 = var15 - var17;
               this.placeSingleTrunkLeaves(var1, var2, new BlockPos(var11, var15, var12), 1 - var16, var5, var6, var7);
            }
         }

         this.placeTrunk(var1, var2, var3, var8, var4, var6, var7);
         return true;
      }
   }

   private void createCrown(LevelSimulatedRW var1, Random var2, BlockPos var3, int var4, Set var5, BoundingBox var6, TreeConfiguration var7) {
      boolean var8 = true;

      for(int var9 = -2; var9 <= 0; ++var9) {
         this.placeDoubleTrunkLeaves(var1, var2, var3.above(var9), var4 + 1 - var9, var5, var6, var7);
      }

   }
}
