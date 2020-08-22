package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.LogBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FancyTreeFeature extends AbstractTreeFeature {
   public FancyTreeFeature(Function var1) {
      super(var1);
   }

   private void crossSection(LevelSimulatedRW var1, Random var2, BlockPos var3, float var4, Set var5, BoundingBox var6, SmallTreeConfiguration var7) {
      int var8 = (int)((double)var4 + 0.618D);

      for(int var9 = -var8; var9 <= var8; ++var9) {
         for(int var10 = -var8; var10 <= var8; ++var10) {
            if (Math.pow((double)Math.abs(var9) + 0.5D, 2.0D) + Math.pow((double)Math.abs(var10) + 0.5D, 2.0D) <= (double)(var4 * var4)) {
               this.placeLeaf(var1, var2, var3.offset(var9, 0, var10), var5, var6, var7);
            }
         }
      }

   }

   private float treeShape(int var1, int var2) {
      if ((float)var2 < (float)var1 * 0.3F) {
         return -1.0F;
      } else {
         float var3 = (float)var1 / 2.0F;
         float var4 = var3 - (float)var2;
         float var5 = Mth.sqrt(var3 * var3 - var4 * var4);
         if (var4 == 0.0F) {
            var5 = var3;
         } else if (Math.abs(var4) >= var3) {
            return 0.0F;
         }

         return var5 * 0.5F;
      }
   }

   private float foliageShape(int var1) {
      if (var1 >= 0 && var1 < 5) {
         return var1 != 0 && var1 != 4 ? 3.0F : 2.0F;
      } else {
         return -1.0F;
      }
   }

   private void foliageCluster(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, BoundingBox var5, SmallTreeConfiguration var6) {
      for(int var7 = 0; var7 < 5; ++var7) {
         this.crossSection(var1, var2, var3.above(var7), this.foliageShape(var7), var4, var5, var6);
      }

   }

   private int makeLimb(LevelSimulatedRW var1, Random var2, BlockPos var3, BlockPos var4, boolean var5, Set var6, BoundingBox var7, SmallTreeConfiguration var8) {
      if (!var5 && Objects.equals(var3, var4)) {
         return -1;
      } else {
         BlockPos var9 = var4.offset(-var3.getX(), -var3.getY(), -var3.getZ());
         int var10 = this.getSteps(var9);
         float var11 = (float)var9.getX() / (float)var10;
         float var12 = (float)var9.getY() / (float)var10;
         float var13 = (float)var9.getZ() / (float)var10;

         for(int var14 = 0; var14 <= var10; ++var14) {
            BlockPos var15 = var3.offset((double)(0.5F + (float)var14 * var11), (double)(0.5F + (float)var14 * var12), (double)(0.5F + (float)var14 * var13));
            if (var5) {
               this.setBlock(var1, var15, (BlockState)var8.trunkProvider.getState(var2, var15).setValue(LogBlock.AXIS, this.getLogAxis(var3, var15)), var7);
               var6.add(var15);
            } else if (!isFree(var1, var15)) {
               return var14;
            }
         }

         return -1;
      }
   }

   private int getSteps(BlockPos var1) {
      int var2 = Mth.abs(var1.getX());
      int var3 = Mth.abs(var1.getY());
      int var4 = Mth.abs(var1.getZ());
      if (var4 > var2 && var4 > var3) {
         return var4;
      } else {
         return var3 > var2 ? var3 : var2;
      }
   }

   private Direction.Axis getLogAxis(BlockPos var1, BlockPos var2) {
      Direction.Axis var3 = Direction.Axis.Y;
      int var4 = Math.abs(var2.getX() - var1.getX());
      int var5 = Math.abs(var2.getZ() - var1.getZ());
      int var6 = Math.max(var4, var5);
      if (var6 > 0) {
         if (var4 == var6) {
            var3 = Direction.Axis.X;
         } else if (var5 == var6) {
            var3 = Direction.Axis.Z;
         }
      }

      return var3;
   }

   private void makeFoliage(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, List var5, Set var6, BoundingBox var7, SmallTreeConfiguration var8) {
      Iterator var9 = var5.iterator();

      while(var9.hasNext()) {
         FancyTreeFeature.FoliageCoords var10 = (FancyTreeFeature.FoliageCoords)var9.next();
         if (this.trimBranches(var3, var10.getBranchBase() - var4.getY())) {
            this.foliageCluster(var1, var2, var10, var6, var7, var8);
         }
      }

   }

   private boolean trimBranches(int var1, int var2) {
      return (double)var2 >= (double)var1 * 0.2D;
   }

   private void makeTrunk(LevelSimulatedRW var1, Random var2, BlockPos var3, int var4, Set var5, BoundingBox var6, SmallTreeConfiguration var7) {
      this.makeLimb(var1, var2, var3, var3.above(var4), true, var5, var6, var7);
   }

   private void makeBranches(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, List var5, Set var6, BoundingBox var7, SmallTreeConfiguration var8) {
      Iterator var9 = var5.iterator();

      while(var9.hasNext()) {
         FancyTreeFeature.FoliageCoords var10 = (FancyTreeFeature.FoliageCoords)var9.next();
         int var11 = var10.getBranchBase();
         BlockPos var12 = new BlockPos(var4.getX(), var11, var4.getZ());
         if (!var12.equals(var10) && this.trimBranches(var3, var11 - var4.getY())) {
            this.makeLimb(var1, var2, var12, var10, true, var6, var7, var8);
         }
      }

   }

   public boolean doPlace(LevelSimulatedRW var1, Random var2, BlockPos var3, Set var4, Set var5, BoundingBox var6, SmallTreeConfiguration var7) {
      Random var8 = new Random(var2.nextLong());
      int var9 = this.checkLocation(var1, var2, var3, 5 + var8.nextInt(12), var4, var6, var7);
      if (var9 == -1) {
         return false;
      } else {
         this.setDirtAt(var1, var3.below());
         int var10 = (int)((double)var9 * 0.618D);
         if (var10 >= var9) {
            var10 = var9 - 1;
         }

         double var11 = 1.0D;
         int var13 = (int)(1.382D + Math.pow(1.0D * (double)var9 / 13.0D, 2.0D));
         if (var13 < 1) {
            var13 = 1;
         }

         int var14 = var3.getY() + var10;
         int var15 = var9 - 5;
         ArrayList var16 = Lists.newArrayList();
         var16.add(new FancyTreeFeature.FoliageCoords(var3.above(var15), var14));

         for(; var15 >= 0; --var15) {
            float var17 = this.treeShape(var9, var15);
            if (var17 >= 0.0F) {
               for(int var18 = 0; var18 < var13; ++var18) {
                  double var19 = 1.0D;
                  double var21 = 1.0D * (double)var17 * ((double)var8.nextFloat() + 0.328D);
                  double var23 = (double)(var8.nextFloat() * 2.0F) * 3.141592653589793D;
                  double var25 = var21 * Math.sin(var23) + 0.5D;
                  double var27 = var21 * Math.cos(var23) + 0.5D;
                  BlockPos var29 = var3.offset(var25, (double)(var15 - 1), var27);
                  BlockPos var30 = var29.above(5);
                  if (this.makeLimb(var1, var2, var29, var30, false, var4, var6, var7) == -1) {
                     int var31 = var3.getX() - var29.getX();
                     int var32 = var3.getZ() - var29.getZ();
                     double var33 = (double)var29.getY() - Math.sqrt((double)(var31 * var31 + var32 * var32)) * 0.381D;
                     int var35 = var33 > (double)var14 ? var14 : (int)var33;
                     BlockPos var36 = new BlockPos(var3.getX(), var35, var3.getZ());
                     if (this.makeLimb(var1, var2, var36, var29, false, var4, var6, var7) == -1) {
                        var16.add(new FancyTreeFeature.FoliageCoords(var29, var36.getY()));
                     }
                  }
               }
            }
         }

         this.makeFoliage(var1, var2, var9, var3, var16, var5, var6, var7);
         this.makeTrunk(var1, var2, var3, var10, var4, var6, var7);
         this.makeBranches(var1, var2, var9, var3, var16, var4, var6, var7);
         return true;
      }
   }

   private int checkLocation(LevelSimulatedRW var1, Random var2, BlockPos var3, int var4, Set var5, BoundingBox var6, SmallTreeConfiguration var7) {
      if (!isGrassOrDirtOrFarmland(var1, var3.below())) {
         return -1;
      } else {
         int var8 = this.makeLimb(var1, var2, var3, var3.above(var4 - 1), false, var5, var6, var7);
         if (var8 == -1) {
            return var4;
         } else {
            return var8 < 6 ? -1 : var8;
         }
      }
   }

   static class FoliageCoords extends BlockPos {
      private final int branchBase;

      public FoliageCoords(BlockPos var1, int var2) {
         super(var1.getX(), var1.getY(), var1.getZ());
         this.branchBase = var2;
      }

      public int getBranchBase() {
         return this.branchBase;
      }
   }
}
