package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FancyTrunkPlacer extends TrunkPlacer {
   public static final Codec<FancyTrunkPlacer> CODEC = RecordCodecBuilder.create((var0) -> {
      return trunkPlacerParts(var0).apply(var0, FancyTrunkPlacer::new);
   });

   public FancyTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.FANCY_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7) {
      boolean var8 = true;
      int var9 = var3 + 2;
      int var10 = Mth.floor((double)var9 * 0.618D);
      if (!var7.fromSapling) {
         setDirtAt(var1, var4.below());
      }

      double var11 = 1.0D;
      int var13 = Math.min(1, Mth.floor(1.382D + Math.pow(1.0D * (double)var9 / 13.0D, 2.0D)));
      int var14 = var4.getY() + var10;
      int var15 = var9 - 5;
      ArrayList var16 = Lists.newArrayList();
      var16.add(new FancyTrunkPlacer.FoliageCoords(var4.above(var15), var14));

      for(; var15 >= 0; --var15) {
         float var17 = this.treeShape(var9, var15);
         if (var17 >= 0.0F) {
            for(int var18 = 0; var18 < var13; ++var18) {
               double var19 = 1.0D;
               double var21 = 1.0D * (double)var17 * ((double)var2.nextFloat() + 0.328D);
               double var23 = (double)(var2.nextFloat() * 2.0F) * 3.141592653589793D;
               double var25 = var21 * Math.sin(var23) + 0.5D;
               double var27 = var21 * Math.cos(var23) + 0.5D;
               BlockPos var29 = var4.offset(var25, (double)(var15 - 1), var27);
               BlockPos var30 = var29.above(5);
               if (this.makeLimb(var1, var2, var29, var30, false, var5, var6, var7)) {
                  int var31 = var4.getX() - var29.getX();
                  int var32 = var4.getZ() - var29.getZ();
                  double var33 = (double)var29.getY() - Math.sqrt((double)(var31 * var31 + var32 * var32)) * 0.381D;
                  int var35 = var33 > (double)var14 ? var14 : (int)var33;
                  BlockPos var36 = new BlockPos(var4.getX(), var35, var4.getZ());
                  if (this.makeLimb(var1, var2, var36, var29, false, var5, var6, var7)) {
                     var16.add(new FancyTrunkPlacer.FoliageCoords(var29, var36.getY()));
                  }
               }
            }
         }
      }

      this.makeLimb(var1, var2, var4, var4.above(var10), true, var5, var6, var7);
      this.makeBranches(var1, var2, var9, var4, var16, var5, var6, var7);
      ArrayList var37 = Lists.newArrayList();
      Iterator var38 = var16.iterator();

      while(var38.hasNext()) {
         FancyTrunkPlacer.FoliageCoords var39 = (FancyTrunkPlacer.FoliageCoords)var38.next();
         if (this.trimBranches(var9, var39.getBranchBase() - var4.getY())) {
            var37.add(var39.attachment);
         }
      }

      return var37;
   }

   private boolean makeLimb(LevelSimulatedRW var1, Random var2, BlockPos var3, BlockPos var4, boolean var5, Set<BlockPos> var6, BoundingBox var7, TreeConfiguration var8) {
      if (!var5 && Objects.equals(var3, var4)) {
         return true;
      } else {
         BlockPos var9 = var4.offset(-var3.getX(), -var3.getY(), -var3.getZ());
         int var10 = this.getSteps(var9);
         float var11 = (float)var9.getX() / (float)var10;
         float var12 = (float)var9.getY() / (float)var10;
         float var13 = (float)var9.getZ() / (float)var10;

         for(int var14 = 0; var14 <= var10; ++var14) {
            BlockPos var15 = var3.offset((double)(0.5F + (float)var14 * var11), (double)(0.5F + (float)var14 * var12), (double)(0.5F + (float)var14 * var13));
            if (var5) {
               setBlock(var1, var15, (BlockState)var8.trunkProvider.getState(var2, var15).setValue(RotatedPillarBlock.AXIS, this.getLogAxis(var3, var15)), var7);
               var6.add(var15.immutable());
            } else if (!TreeFeature.isFree(var1, var15)) {
               return false;
            }
         }

         return true;
      }
   }

   private int getSteps(BlockPos var1) {
      int var2 = Mth.abs(var1.getX());
      int var3 = Mth.abs(var1.getY());
      int var4 = Mth.abs(var1.getZ());
      return Math.max(var2, Math.max(var3, var4));
   }

   private Direction.Axis getLogAxis(BlockPos var1, BlockPos var2) {
      Direction.Axis var3 = Direction.Axis.Y;
      int var4 = Math.abs(var2.getX() - var1.getX());
      int var5 = Math.abs(var2.getZ() - var1.getZ());
      int var6 = Math.max(var4, var5);
      if (var6 > 0) {
         if (var4 == var6) {
            var3 = Direction.Axis.X;
         } else {
            var3 = Direction.Axis.Z;
         }
      }

      return var3;
   }

   private boolean trimBranches(int var1, int var2) {
      return (double)var2 >= (double)var1 * 0.2D;
   }

   private void makeBranches(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, List<FancyTrunkPlacer.FoliageCoords> var5, Set<BlockPos> var6, BoundingBox var7, TreeConfiguration var8) {
      Iterator var9 = var5.iterator();

      while(var9.hasNext()) {
         FancyTrunkPlacer.FoliageCoords var10 = (FancyTrunkPlacer.FoliageCoords)var9.next();
         int var11 = var10.getBranchBase();
         BlockPos var12 = new BlockPos(var4.getX(), var11, var4.getZ());
         if (!var12.equals(var10.attachment.foliagePos()) && this.trimBranches(var3, var11 - var4.getY())) {
            this.makeLimb(var1, var2, var12, var10.attachment.foliagePos(), true, var6, var7, var8);
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

   static class FoliageCoords {
      private final FoliagePlacer.FoliageAttachment attachment;
      private final int branchBase;

      public FoliageCoords(BlockPos var1, int var2) {
         super();
         this.attachment = new FoliagePlacer.FoliageAttachment(var1, 0, false);
         this.branchBase = var2;
      }

      public int getBranchBase() {
         return this.branchBase;
      }
   }
}
