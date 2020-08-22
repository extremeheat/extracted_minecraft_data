package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class StrongholdFeature extends StructureFeature {
   private boolean isSpotSelected;
   private ChunkPos[] strongholdPos;
   private final List discoveredStarts = Lists.newArrayList();
   private long currentSeed;

   public StrongholdFeature(Function var1) {
      super(var1);
   }

   public boolean isFeatureChunk(BiomeManager var1, ChunkGenerator var2, Random var3, int var4, int var5, Biome var6) {
      if (this.currentSeed != var2.getSeed()) {
         this.reset();
      }

      if (!this.isSpotSelected) {
         this.generatePositions(var2);
         this.isSpotSelected = true;
      }

      ChunkPos[] var7 = this.strongholdPos;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         ChunkPos var10 = var7[var9];
         if (var4 == var10.x && var5 == var10.z) {
            return true;
         }
      }

      return false;
   }

   private void reset() {
      this.isSpotSelected = false;
      this.strongholdPos = null;
      this.discoveredStarts.clear();
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return StrongholdFeature.StrongholdStart::new;
   }

   public String getFeatureName() {
      return "Stronghold";
   }

   public int getLookupRange() {
      return 8;
   }

   @Nullable
   public BlockPos getNearestGeneratedFeature(Level var1, ChunkGenerator var2, BlockPos var3, int var4, boolean var5) {
      if (!var2.getBiomeSource().canGenerateStructure(this)) {
         return null;
      } else {
         if (this.currentSeed != var1.getSeed()) {
            this.reset();
         }

         if (!this.isSpotSelected) {
            this.generatePositions(var2);
            this.isSpotSelected = true;
         }

         BlockPos var6 = null;
         BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
         double var8 = Double.MAX_VALUE;
         ChunkPos[] var10 = this.strongholdPos;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            ChunkPos var13 = var10[var12];
            var7.set((var13.x << 4) + 8, 32, (var13.z << 4) + 8);
            double var14 = var7.distSqr(var3);
            if (var6 == null) {
               var6 = new BlockPos(var7);
               var8 = var14;
            } else if (var14 < var8) {
               var6 = new BlockPos(var7);
               var8 = var14;
            }
         }

         return var6;
      }
   }

   private void generatePositions(ChunkGenerator var1) {
      this.currentSeed = var1.getSeed();
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = Registry.BIOME.iterator();

      while(var3.hasNext()) {
         Biome var4 = (Biome)var3.next();
         if (var4 != null && var1.isBiomeValidStartForStructure(var4, this)) {
            var2.add(var4);
         }
      }

      int var19 = var1.getSettings().getStrongholdsDistance();
      int var20 = var1.getSettings().getStrongholdsCount();
      int var5 = var1.getSettings().getStrongholdsSpread();
      this.strongholdPos = new ChunkPos[var20];
      int var6 = 0;
      Iterator var7 = this.discoveredStarts.iterator();

      while(var7.hasNext()) {
         StructureStart var8 = (StructureStart)var7.next();
         if (var6 < this.strongholdPos.length) {
            this.strongholdPos[var6++] = new ChunkPos(var8.getChunkX(), var8.getChunkZ());
         }
      }

      Random var21 = new Random();
      var21.setSeed(var1.getSeed());
      double var22 = var21.nextDouble() * 3.141592653589793D * 2.0D;
      int var10 = var6;
      if (var6 < this.strongholdPos.length) {
         int var11 = 0;
         int var12 = 0;

         for(int var13 = 0; var13 < this.strongholdPos.length; ++var13) {
            double var14 = (double)(4 * var19 + var19 * var12 * 6) + (var21.nextDouble() - 0.5D) * (double)var19 * 2.5D;
            int var16 = (int)Math.round(Math.cos(var22) * var14);
            int var17 = (int)Math.round(Math.sin(var22) * var14);
            BlockPos var18 = var1.getBiomeSource().findBiomeHorizontal((var16 << 4) + 8, var1.getSeaLevel(), (var17 << 4) + 8, 112, var2, var21);
            if (var18 != null) {
               var16 = var18.getX() >> 4;
               var17 = var18.getZ() >> 4;
            }

            if (var13 >= var10) {
               this.strongholdPos[var13] = new ChunkPos(var16, var17);
            }

            var22 += 6.283185307179586D / (double)var5;
            ++var11;
            if (var11 == var5) {
               ++var12;
               var11 = 0;
               var5 += 2 * var5 / (var12 + 1);
               var5 = Math.min(var5, this.strongholdPos.length - var13);
               var22 += var21.nextDouble() * 3.141592653589793D * 2.0D;
            }
         }
      }

   }

   public static class StrongholdStart extends StructureStart {
      public StrongholdStart(StructureFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(ChunkGenerator var1, StructureManager var2, int var3, int var4, Biome var5) {
         int var6 = 0;
         long var7 = var1.getSeed();

         StrongholdPieces.StartPiece var9;
         do {
            this.pieces.clear();
            this.boundingBox = BoundingBox.getUnknownBox();
            this.random.setLargeFeatureSeed(var7 + (long)(var6++), var3, var4);
            StrongholdPieces.resetPieces();
            var9 = new StrongholdPieces.StartPiece(this.random, (var3 << 4) + 2, (var4 << 4) + 2);
            this.pieces.add(var9);
            var9.addChildren(var9, this.pieces, this.random);
            List var10 = var9.pendingChildren;

            while(!var10.isEmpty()) {
               int var11 = this.random.nextInt(var10.size());
               StructurePiece var12 = (StructurePiece)var10.remove(var11);
               var12.addChildren(var9, this.pieces, this.random);
            }

            this.calculateBoundingBox();
            this.moveBelowSeaLevel(var1.getSeaLevel(), this.random, 10);
         } while(this.pieces.isEmpty() || var9.portalRoomPiece == null);

         ((StrongholdFeature)this.getFeature()).discoveredStarts.add(this);
      }
   }
}
