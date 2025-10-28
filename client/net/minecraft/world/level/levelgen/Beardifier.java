package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jspecify.annotations.Nullable;

public class Beardifier implements DensityFunctions.BeardifierOrMarker {
   public static final int BEARD_KERNEL_RADIUS = 12;
   private static final int BEARD_KERNEL_SIZE = 24;
   private static final float[] BEARD_KERNEL = (float[])Util.make(new float[13824], (var0) -> {
      for(int var1 = 0; var1 < 24; ++var1) {
         for(int var2 = 0; var2 < 24; ++var2) {
            for(int var3 = 0; var3 < 24; ++var3) {
               var0[var1 * 24 * 24 + var2 * 24 + var3] = (float)computeBeardContribution(var2 - 12, var3 - 12, var1 - 12);
            }
         }
      }

   });
   public static final Beardifier EMPTY = new Beardifier(List.of(), List.of(), (BoundingBox)null);
   private final List<Rigid> pieces;
   private final List<JigsawJunction> junctions;
   private final @Nullable BoundingBox affectedBox;

   public static Beardifier forStructuresInChunk(StructureManager var0, ChunkPos var1) {
      List var2 = var0.startsForStructure((ChunkPos)var1, (Predicate)((var0x) -> var0x.terrainAdaptation() != TerrainAdjustment.NONE));
      if (var2.isEmpty()) {
         return EMPTY;
      } else {
         int var3 = var1.getMinBlockX();
         int var4 = var1.getMinBlockZ();
         ArrayList var5 = new ArrayList();
         ArrayList var6 = new ArrayList();
         BoundingBox var7 = null;

         for(StructureStart var9 : var2) {
            TerrainAdjustment var10 = var9.getStructure().terrainAdaptation();

            for(StructurePiece var12 : var9.getPieces()) {
               if (var12.isCloseToChunk(var1, 12)) {
                  if (var12 instanceof PoolElementStructurePiece) {
                     PoolElementStructurePiece var13 = (PoolElementStructurePiece)var12;
                     StructureTemplatePool.Projection var14 = var13.getElement().getProjection();
                     if (var14 == StructureTemplatePool.Projection.RIGID) {
                        var5.add(new Rigid(var13.getBoundingBox(), var10, var13.getGroundLevelDelta()));
                        var7 = includeBoundingBox(var7, var12.getBoundingBox());
                     }

                     for(JigsawJunction var16 : var13.getJunctions()) {
                        int var17 = var16.getSourceX();
                        int var18 = var16.getSourceZ();
                        if (var17 > var3 - 12 && var18 > var4 - 12 && var17 < var3 + 15 + 12 && var18 < var4 + 15 + 12) {
                           var6.add(var16);
                           BoundingBox var19 = new BoundingBox(new BlockPos(var17, var16.getSourceGroundY(), var18));
                           var7 = includeBoundingBox(var7, var19);
                        }
                     }
                  } else {
                     var5.add(new Rigid(var12.getBoundingBox(), var10, 0));
                     var7 = includeBoundingBox(var7, var12.getBoundingBox());
                  }
               }
            }
         }

         if (var7 == null) {
            return EMPTY;
         } else {
            BoundingBox var20 = var7.inflatedBy(24);
            return new Beardifier(List.copyOf(var5), List.copyOf(var6), var20);
         }
      }
   }

   private static BoundingBox includeBoundingBox(@Nullable BoundingBox var0, BoundingBox var1) {
      return var0 == null ? var1 : BoundingBox.encapsulating(var0, var1);
   }

   @VisibleForTesting
   public Beardifier(List<Rigid> var1, List<JigsawJunction> var2, @Nullable BoundingBox var3) {
      super();
      this.pieces = var1;
      this.junctions = var2;
      this.affectedBox = var3;
   }

   public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
      if (this.affectedBox == null) {
         Arrays.fill(var1, 0.0);
      } else {
         DensityFunctions.BeardifierOrMarker.super.fillArray(var1, var2);
      }

   }

   public double compute(DensityFunction.FunctionContext var1) {
      if (this.affectedBox == null) {
         return 0.0;
      } else {
         int var2 = var1.blockX();
         int var3 = var1.blockY();
         int var4 = var1.blockZ();
         if (!this.affectedBox.isInside(var2, var3, var4)) {
            return 0.0;
         } else {
            double var5 = 0.0;

            for(Rigid var8 : this.pieces) {
               BoundingBox var9 = var8.box();
               int var10 = var8.groundLevelDelta();
               int var11 = Math.max(0, Math.max(var9.minX() - var2, var2 - var9.maxX()));
               int var12 = Math.max(0, Math.max(var9.minZ() - var4, var4 - var9.maxZ()));
               int var13 = var9.minY() + var10;
               int var14 = var3 - var13;
               int var10000;
               switch (var8.terrainAdjustment()) {
                  case NONE:
                     var10000 = 0;
                     break;
                  case BURY:
                  case BEARD_THIN:
                     var10000 = var14;
                     break;
                  case BEARD_BOX:
                     var10000 = Math.max(0, Math.max(var13 - var3, var3 - var9.maxY()));
                     break;
                  case ENCAPSULATE:
                     var10000 = Math.max(0, Math.max(var9.minY() - var3, var3 - var9.maxY()));
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
               }

               int var15 = var10000;
               double var10001;
               switch (var8.terrainAdjustment()) {
                  case NONE:
                     var10001 = 0.0;
                     break;
                  case BURY:
                     var10001 = getBuryContribution((double)var11, (double)var15 / 2.0, (double)var12);
                     break;
                  case BEARD_THIN:
                  case BEARD_BOX:
                     var10001 = getBeardContribution(var11, var15, var12, var14) * 0.8;
                     break;
                  case ENCAPSULATE:
                     var10001 = getBuryContribution((double)var11 / 2.0, (double)var15 / 2.0, (double)var12 / 2.0) * 0.8;
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
               }

               var5 += var10001;
            }

            for(JigsawJunction var17 : this.junctions) {
               int var18 = var2 - var17.getSourceX();
               int var19 = var3 - var17.getSourceGroundY();
               int var20 = var4 - var17.getSourceZ();
               var5 += getBeardContribution(var18, var19, var20, var19) * 0.4;
            }

            return var5;
         }
      }
   }

   public double minValue() {
      return -1.0 / 0.0;
   }

   public double maxValue() {
      return 1.0 / 0.0;
   }

   private static double getBuryContribution(double var0, double var2, double var4) {
      double var6 = Mth.length(var0, var2, var4);
      return Mth.clampedMap(var6, 0.0, 6.0, 1.0, 0.0);
   }

   private static double getBeardContribution(int var0, int var1, int var2, int var3) {
      int var4 = var0 + 12;
      int var5 = var1 + 12;
      int var6 = var2 + 12;
      if (isInKernelRange(var4) && isInKernelRange(var5) && isInKernelRange(var6)) {
         double var7 = (double)var3 + 0.5;
         double var9 = Mth.lengthSquared((double)var0, var7, (double)var2);
         double var11 = -var7 * Mth.fastInvSqrt(var9 / 2.0) / 2.0;
         return var11 * (double)BEARD_KERNEL[var6 * 24 * 24 + var4 * 24 + var5];
      } else {
         return 0.0;
      }
   }

   private static boolean isInKernelRange(int var0) {
      return var0 >= 0 && var0 < 24;
   }

   private static double computeBeardContribution(int var0, int var1, int var2) {
      return computeBeardContribution(var0, (double)var1 + 0.5, var2);
   }

   private static double computeBeardContribution(int var0, double var1, int var3) {
      double var4 = Mth.lengthSquared((double)var0, var1, (double)var3);
      double var6 = Math.pow(2.718281828459045, -var4 / 16.0);
      return var6;
   }

   @VisibleForTesting
   public static record Rigid(BoundingBox box, TerrainAdjustment terrainAdjustment, int groundLevelDelta) {
      public Rigid(BoundingBox var1, TerrainAdjustment var2, int var3) {
         super();
         this.box = var1;
         this.terrainAdjustment = var2;
         this.groundLevelDelta = var3;
      }
   }
}
