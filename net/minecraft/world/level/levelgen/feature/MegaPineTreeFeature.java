package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.MegaTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaPineTreeFeature extends MegaTreeFeature {
   public MegaPineTreeFeature(Function var1) {
      super(var1);
   }

   public boolean doPlace(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, Set var5, BoundingBox var6, MegaTreeConfiguration var7) {
      int var8 = this.calcTreeHeigth(var2, var7);
      if (!this.prepareTree(var1, var3, var8)) {
         return false;
      } else {
         this.createCrown(var1, var2, var3.getX(), var3.getZ(), var3.getY() + var8, 0, var5, var6, var7);
         this.placeTrunk(var1, var2, var3, var8, var4, var6, var7);
         return true;
      }
   }

   private void createCrown(LevelSimulatedRW var1, Random var2, int var3, int var4, int var5, int var6, Set var7, BoundingBox var8, MegaTreeConfiguration var9) {
      int var10 = var2.nextInt(5) + var9.crownHeight;
      int var11 = 0;

      for(int var12 = var5 - var10; var12 <= var5; ++var12) {
         int var13 = var5 - var12;
         int var14 = var6 + Mth.floor((float)var13 / (float)var10 * 3.5F);
         this.placeDoubleTrunkLeaves(var1, var2, new BlockPos(var3, var12, var4), var14 + (var13 > 0 && var14 == var11 && (var12 & 1) == 0 ? 1 : 0), var7, var8, var9);
         var11 = var14;
      }

   }
}
