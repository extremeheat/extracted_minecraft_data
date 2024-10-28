package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

public class FancyTrunkPlacer extends TrunkPlacer {
   public static final MapCodec<FancyTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return trunkPlacerParts(var0).apply(var0, FancyTrunkPlacer::new);
   });
   private static final double TRUNK_HEIGHT_SCALE = 0.618;
   private static final double CLUSTER_DENSITY_MAGIC = 1.382;
   private static final double BRANCH_SLOPE = 0.381;
   private static final double BRANCH_LENGTH_MAGIC = 0.328;

   public FancyTrunkPlacer(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected TrunkPlacerType<?> type() {
      return TrunkPlacerType.FANCY_TRUNK_PLACER;
   }

   public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, TreeConfiguration var6) {
      boolean var7 = true;
      int var8 = var4 + 2;
      int var9 = Mth.floor((double)var8 * 0.618);
      setDirtAt(var1, var2, var3, var5.below(), var6);
      double var10 = 1.0;
      int var12 = Math.min(1, Mth.floor(1.382 + Math.pow(1.0 * (double)var8 / 13.0, 2.0)));
      int var13 = var5.getY() + var9;
      int var14 = var8 - 5;
      ArrayList var15 = Lists.newArrayList();
      var15.add(new FoliageCoords(var5.above(var14), var13));

      for(; var14 >= 0; --var14) {
         float var16 = treeShape(var8, var14);
         if (!(var16 < 0.0F)) {
            for(int var17 = 0; var17 < var12; ++var17) {
               double var18 = 1.0;
               double var20 = 1.0 * (double)var16 * ((double)var3.nextFloat() + 0.328);
               double var22 = (double)(var3.nextFloat() * 2.0F) * 3.141592653589793;
               double var24 = var20 * Math.sin(var22) + 0.5;
               double var26 = var20 * Math.cos(var22) + 0.5;
               BlockPos var28 = var5.offset(Mth.floor(var24), var14 - 1, Mth.floor(var26));
               BlockPos var29 = var28.above(5);
               if (this.makeLimb(var1, var2, var3, var28, var29, false, var6)) {
                  int var30 = var5.getX() - var28.getX();
                  int var31 = var5.getZ() - var28.getZ();
                  double var32 = (double)var28.getY() - Math.sqrt((double)(var30 * var30 + var31 * var31)) * 0.381;
                  int var34 = var32 > (double)var13 ? var13 : (int)var32;
                  BlockPos var35 = new BlockPos(var5.getX(), var34, var5.getZ());
                  if (this.makeLimb(var1, var2, var3, var35, var28, false, var6)) {
                     var15.add(new FoliageCoords(var28, var35.getY()));
                  }
               }
            }
         }
      }

      this.makeLimb(var1, var2, var3, var5, var5.above(var9), true, var6);
      this.makeBranches(var1, var2, var3, var8, var5, var15, var6);
      ArrayList var36 = Lists.newArrayList();
      Iterator var37 = var15.iterator();

      while(var37.hasNext()) {
         FoliageCoords var38 = (FoliageCoords)var37.next();
         if (this.trimBranches(var8, var38.getBranchBase() - var5.getY())) {
            var36.add(var38.attachment);
         }
      }

      return var36;
   }

   private boolean makeLimb(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, BlockPos var4, BlockPos var5, boolean var6, TreeConfiguration var7) {
      if (!var6 && Objects.equals(var4, var5)) {
         return true;
      } else {
         BlockPos var8 = var5.offset(-var4.getX(), -var4.getY(), -var4.getZ());
         int var9 = this.getSteps(var8);
         float var10 = (float)var8.getX() / (float)var9;
         float var11 = (float)var8.getY() / (float)var9;
         float var12 = (float)var8.getZ() / (float)var9;

         for(int var13 = 0; var13 <= var9; ++var13) {
            BlockPos var14 = var4.offset(Mth.floor(0.5F + (float)var13 * var10), Mth.floor(0.5F + (float)var13 * var11), Mth.floor(0.5F + (float)var13 * var12));
            if (var6) {
               this.placeLog(var1, var2, var3, var14, var7, (var3x) -> {
                  return (BlockState)var3x.trySetValue(RotatedPillarBlock.AXIS, this.getLogAxis(var4, var14));
               });
            } else if (!this.isFree(var1, var14)) {
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
      return (double)var2 >= (double)var1 * 0.2;
   }

   private void makeBranches(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, int var4, BlockPos var5, List<FoliageCoords> var6, TreeConfiguration var7) {
      Iterator var8 = var6.iterator();

      while(var8.hasNext()) {
         FoliageCoords var9 = (FoliageCoords)var8.next();
         int var10 = var9.getBranchBase();
         BlockPos var11 = new BlockPos(var5.getX(), var10, var5.getZ());
         if (!var11.equals(var9.attachment.pos()) && this.trimBranches(var4, var10 - var5.getY())) {
            this.makeLimb(var1, var2, var3, var11, var9.attachment.pos(), true, var7);
         }
      }

   }

   private static float treeShape(int var0, int var1) {
      if ((float)var1 < (float)var0 * 0.3F) {
         return -1.0F;
      } else {
         float var2 = (float)var0 / 2.0F;
         float var3 = var2 - (float)var1;
         float var4 = Mth.sqrt(var2 * var2 - var3 * var3);
         if (var3 == 0.0F) {
            var4 = var2;
         } else if (Math.abs(var3) >= var2) {
            return 0.0F;
         }

         return var4 * 0.5F;
      }
   }

   static class FoliageCoords {
      final FoliagePlacer.FoliageAttachment attachment;
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
