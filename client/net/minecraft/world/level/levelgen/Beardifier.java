package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

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
   private final ObjectListIterator<Rigid> pieceIterator;
   private final ObjectListIterator<JigsawJunction> junctionIterator;

   public static Beardifier forStructuresInChunk(StructureManager var0, ChunkPos var1) {
      int var2 = var1.getMinBlockX();
      int var3 = var1.getMinBlockZ();
      ObjectArrayList var4 = new ObjectArrayList(10);
      ObjectArrayList var5 = new ObjectArrayList(32);
      var0.startsForStructure(var1, (var0x) -> {
         return var0x.terrainAdaptation() != TerrainAdjustment.NONE;
      }).forEach((var5x) -> {
         TerrainAdjustment var6 = var5x.getStructure().terrainAdaptation();
         Iterator var7 = var5x.getPieces().iterator();

         while(true) {
            while(true) {
               StructurePiece var8;
               do {
                  if (!var7.hasNext()) {
                     return;
                  }

                  var8 = (StructurePiece)var7.next();
               } while(!var8.isCloseToChunk(var1, 12));

               if (var8 instanceof PoolElementStructurePiece var9) {
                  StructureTemplatePool.Projection var10 = var9.getElement().getProjection();
                  if (var10 == StructureTemplatePool.Projection.RIGID) {
                     var4.add(new Rigid(var9.getBoundingBox(), var6, var9.getGroundLevelDelta()));
                  }

                  Iterator var11 = var9.getJunctions().iterator();

                  while(var11.hasNext()) {
                     JigsawJunction var12 = (JigsawJunction)var11.next();
                     int var13 = var12.getSourceX();
                     int var14 = var12.getSourceZ();
                     if (var13 > var2 - 12 && var14 > var3 - 12 && var13 < var2 + 15 + 12 && var14 < var3 + 15 + 12) {
                        var5.add(var12);
                     }
                  }
               } else {
                  var4.add(new Rigid(var8.getBoundingBox(), var6, 0));
               }
            }
         }
      });
      return new Beardifier(var4.iterator(), var5.iterator());
   }

   @VisibleForTesting
   public Beardifier(ObjectListIterator<Rigid> var1, ObjectListIterator<JigsawJunction> var2) {
      super();
      this.pieceIterator = var1;
      this.junctionIterator = var2;
   }

   public double compute(DensityFunction.FunctionContext var1) {
      int var2 = var1.blockX();
      int var3 = var1.blockY();
      int var4 = var1.blockZ();

      double var5;
      int var9;
      int var10;
      double var10001;
      for(var5 = 0.0; this.pieceIterator.hasNext(); var5 += var10001) {
         Rigid var7 = (Rigid)this.pieceIterator.next();
         BoundingBox var8 = var7.box();
         var9 = var7.groundLevelDelta();
         var10 = Math.max(0, Math.max(var8.minX() - var2, var2 - var8.maxX()));
         int var11 = Math.max(0, Math.max(var8.minZ() - var4, var4 - var8.maxZ()));
         int var12 = var8.minY() + var9;
         int var13 = var3 - var12;
         int var10000;
         switch (var7.terrainAdjustment()) {
            case NONE:
               var10000 = 0;
               break;
            case BURY:
            case BEARD_THIN:
               var10000 = var13;
               break;
            case BEARD_BOX:
               var10000 = Math.max(0, Math.max(var12 - var3, var3 - var8.maxY()));
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         int var14 = var10000;
         switch (var7.terrainAdjustment()) {
            case NONE:
               var10001 = 0.0;
               break;
            case BURY:
               var10001 = getBuryContribution(var10, var14, var11);
               break;
            case BEARD_THIN:
            case BEARD_BOX:
               var10001 = getBeardContribution(var10, var14, var11, var13) * 0.8;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }
      }

      this.pieceIterator.back(2147483647);

      while(this.junctionIterator.hasNext()) {
         JigsawJunction var15 = (JigsawJunction)this.junctionIterator.next();
         int var16 = var2 - var15.getSourceX();
         var9 = var3 - var15.getSourceGroundY();
         var10 = var4 - var15.getSourceZ();
         var5 += getBeardContribution(var16, var9, var10, var9) * 0.4;
      }

      this.junctionIterator.back(2147483647);
      return var5;
   }

   public double minValue() {
      return -1.0 / 0.0;
   }

   public double maxValue() {
      return 1.0 / 0.0;
   }

   private static double getBuryContribution(int var0, int var1, int var2) {
      double var3 = Mth.length((double)var0, (double)var1 / 2.0, (double)var2);
      return Mth.clampedMap(var3, 0.0, 6.0, 1.0, 0.0);
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
   public static record Rigid(BoundingBox a, TerrainAdjustment b, int c) {
      private final BoundingBox box;
      private final TerrainAdjustment terrainAdjustment;
      private final int groundLevelDelta;

      public Rigid(BoundingBox var1, TerrainAdjustment var2, int var3) {
         super();
         this.box = var1;
         this.terrainAdjustment = var2;
         this.groundLevelDelta = var3;
      }

      public BoundingBox box() {
         return this.box;
      }

      public TerrainAdjustment terrainAdjustment() {
         return this.terrainAdjustment;
      }

      public int groundLevelDelta() {
         return this.groundLevelDelta;
      }
   }
}
