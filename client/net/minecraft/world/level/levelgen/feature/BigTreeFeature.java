package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LogBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BigTreeFeature extends AbstractTreeFeature<NoneFeatureConfiguration> {
   private static final BlockState LOG;
   private static final BlockState LEAVES;

   public BigTreeFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2) {
      super(var1, var2);
   }

   private void crossSection(LevelSimulatedRW var1, BlockPos var2, float var3, BoundingBox var4, Set<BlockPos> var5) {
      int var6 = (int)((double)var3 + 0.618D);

      for(int var7 = -var6; var7 <= var6; ++var7) {
         for(int var8 = -var6; var8 <= var6; ++var8) {
            if (Math.pow((double)Math.abs(var7) + 0.5D, 2.0D) + Math.pow((double)Math.abs(var8) + 0.5D, 2.0D) <= (double)(var3 * var3)) {
               BlockPos var9 = var2.offset(var7, 0, var8);
               if (isAirOrLeaves(var1, var9)) {
                  this.setBlock(var5, var1, var9, LEAVES, var4);
               }
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

   private void foliageCluster(LevelSimulatedRW var1, BlockPos var2, BoundingBox var3, Set<BlockPos> var4) {
      for(int var5 = 0; var5 < 5; ++var5) {
         this.crossSection(var1, var2.above(var5), this.foliageShape(var5), var3, var4);
      }

   }

   private int makeLimb(Set<BlockPos> var1, LevelSimulatedRW var2, BlockPos var3, BlockPos var4, boolean var5, BoundingBox var6) {
      if (!var5 && Objects.equals(var3, var4)) {
         return -1;
      } else {
         BlockPos var7 = var4.offset(-var3.getX(), -var3.getY(), -var3.getZ());
         int var8 = this.getSteps(var7);
         float var9 = (float)var7.getX() / (float)var8;
         float var10 = (float)var7.getY() / (float)var8;
         float var11 = (float)var7.getZ() / (float)var8;

         for(int var12 = 0; var12 <= var8; ++var12) {
            BlockPos var13 = var3.offset((double)(0.5F + (float)var12 * var9), (double)(0.5F + (float)var12 * var10), (double)(0.5F + (float)var12 * var11));
            if (var5) {
               this.setBlock(var1, var2, var13, (BlockState)LOG.setValue(LogBlock.AXIS, this.getLogAxis(var3, var13)), var6);
            } else if (!isFree(var2, var13)) {
               return var12;
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

   private void makeFoliage(LevelSimulatedRW var1, int var2, BlockPos var3, List<BigTreeFeature.FoliageCoords> var4, BoundingBox var5, Set<BlockPos> var6) {
      Iterator var7 = var4.iterator();

      while(var7.hasNext()) {
         BigTreeFeature.FoliageCoords var8 = (BigTreeFeature.FoliageCoords)var7.next();
         if (this.trimBranches(var2, var8.getBranchBase() - var3.getY())) {
            this.foliageCluster(var1, var8, var5, var6);
         }
      }

   }

   private boolean trimBranches(int var1, int var2) {
      return (double)var2 >= (double)var1 * 0.2D;
   }

   private void makeTrunk(Set<BlockPos> var1, LevelSimulatedRW var2, BlockPos var3, int var4, BoundingBox var5) {
      this.makeLimb(var1, var2, var3, var3.above(var4), true, var5);
   }

   private void makeBranches(Set<BlockPos> var1, LevelSimulatedRW var2, int var3, BlockPos var4, List<BigTreeFeature.FoliageCoords> var5, BoundingBox var6) {
      Iterator var7 = var5.iterator();

      while(var7.hasNext()) {
         BigTreeFeature.FoliageCoords var8 = (BigTreeFeature.FoliageCoords)var7.next();
         int var9 = var8.getBranchBase();
         BlockPos var10 = new BlockPos(var4.getX(), var9, var4.getZ());
         if (!var10.equals(var8) && this.trimBranches(var3, var9 - var4.getY())) {
            this.makeLimb(var1, var2, var10, var8, true, var6);
         }
      }

   }

   public boolean doPlace(Set<BlockPos> var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5) {
      Random var6 = new Random(var3.nextLong());
      int var7 = this.checkLocation(var1, var2, var4, 5 + var6.nextInt(12), var5);
      if (var7 == -1) {
         return false;
      } else {
         this.setDirtAt(var2, var4.below());
         int var8 = (int)((double)var7 * 0.618D);
         if (var8 >= var7) {
            var8 = var7 - 1;
         }

         double var9 = 1.0D;
         int var11 = (int)(1.382D + Math.pow(1.0D * (double)var7 / 13.0D, 2.0D));
         if (var11 < 1) {
            var11 = 1;
         }

         int var12 = var4.getY() + var8;
         int var13 = var7 - 5;
         ArrayList var14 = Lists.newArrayList();
         var14.add(new BigTreeFeature.FoliageCoords(var4.above(var13), var12));

         for(; var13 >= 0; --var13) {
            float var15 = this.treeShape(var7, var13);
            if (var15 >= 0.0F) {
               for(int var16 = 0; var16 < var11; ++var16) {
                  double var17 = 1.0D;
                  double var19 = 1.0D * (double)var15 * ((double)var6.nextFloat() + 0.328D);
                  double var21 = (double)(var6.nextFloat() * 2.0F) * 3.141592653589793D;
                  double var23 = var19 * Math.sin(var21) + 0.5D;
                  double var25 = var19 * Math.cos(var21) + 0.5D;
                  BlockPos var27 = var4.offset(var23, (double)(var13 - 1), var25);
                  BlockPos var28 = var27.above(5);
                  if (this.makeLimb(var1, var2, var27, var28, false, var5) == -1) {
                     int var29 = var4.getX() - var27.getX();
                     int var30 = var4.getZ() - var27.getZ();
                     double var31 = (double)var27.getY() - Math.sqrt((double)(var29 * var29 + var30 * var30)) * 0.381D;
                     int var33 = var31 > (double)var12 ? var12 : (int)var31;
                     BlockPos var34 = new BlockPos(var4.getX(), var33, var4.getZ());
                     if (this.makeLimb(var1, var2, var34, var27, false, var5) == -1) {
                        var14.add(new BigTreeFeature.FoliageCoords(var27, var34.getY()));
                     }
                  }
               }
            }
         }

         this.makeFoliage(var2, var7, var4, var14, var5, var1);
         this.makeTrunk(var1, var2, var4, var8, var5);
         this.makeBranches(var1, var2, var7, var4, var14, var5);
         return true;
      }
   }

   private int checkLocation(Set<BlockPos> var1, LevelSimulatedRW var2, BlockPos var3, int var4, BoundingBox var5) {
      if (!isGrassOrDirtOrFarmland(var2, var3.below())) {
         return -1;
      } else {
         int var6 = this.makeLimb(var1, var2, var3, var3.above(var4 - 1), false, var5);
         if (var6 == -1) {
            return var4;
         } else {
            return var6 < 6 ? -1 : var6;
         }
      }
   }

   static {
      LOG = Blocks.OAK_LOG.defaultBlockState();
      LEAVES = Blocks.OAK_LEAVES.defaultBlockState();
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
