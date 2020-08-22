package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class WoodlandMansionFeature extends StructureFeature {
   public WoodlandMansionFeature(Function var1) {
      super(var1);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.getSettings().getWoodlandMansionSpacing();
      int var8 = var1.getSettings().getWoodlandMangionSeparation();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((WorldgenRandom)var2).setLargeFeatureWithSalt(var1.getSeed(), var13, var14, 10387319);
      var13 *= var7;
      var14 *= var7;
      var13 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      var14 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      return new ChunkPos(var13, var14);
   }

   public boolean isFeatureChunk(BiomeManager var1, ChunkGenerator var2, Random var3, int var4, int var5, Biome var6) {
      ChunkPos var7 = this.getPotentialFeatureChunkFromLocationWithOffset(var2, var3, var4, var5, 0, 0);
      if (var4 == var7.x && var5 == var7.z) {
         Set var8 = var2.getBiomeSource().getBiomesWithin(var4 * 16 + 9, var2.getSeaLevel(), var5 * 16 + 9, 32);
         Iterator var9 = var8.iterator();

         Biome var10;
         do {
            if (!var9.hasNext()) {
               return true;
            }

            var10 = (Biome)var9.next();
         } while(var2.isBiomeValidStartForStructure(var10, this));

         return false;
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return WoodlandMansionFeature.WoodlandMansionStart::new;
   }

   public String getFeatureName() {
      return "Mansion";
   }

   public int getLookupRange() {
      return 8;
   }

   public static class WoodlandMansionStart extends StructureStart {
      public WoodlandMansionStart(StructureFeature var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(ChunkGenerator var1, StructureManager var2, int var3, int var4, Biome var5) {
         Rotation var6 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         byte var7 = 5;
         byte var8 = 5;
         if (var6 == Rotation.CLOCKWISE_90) {
            var7 = -5;
         } else if (var6 == Rotation.CLOCKWISE_180) {
            var7 = -5;
            var8 = -5;
         } else if (var6 == Rotation.COUNTERCLOCKWISE_90) {
            var8 = -5;
         }

         int var9 = (var3 << 4) + 7;
         int var10 = (var4 << 4) + 7;
         int var11 = var1.getFirstOccupiedHeight(var9, var10, Heightmap.Types.WORLD_SURFACE_WG);
         int var12 = var1.getFirstOccupiedHeight(var9, var10 + var8, Heightmap.Types.WORLD_SURFACE_WG);
         int var13 = var1.getFirstOccupiedHeight(var9 + var7, var10, Heightmap.Types.WORLD_SURFACE_WG);
         int var14 = var1.getFirstOccupiedHeight(var9 + var7, var10 + var8, Heightmap.Types.WORLD_SURFACE_WG);
         int var15 = Math.min(Math.min(var11, var12), Math.min(var13, var14));
         if (var15 >= 60) {
            BlockPos var16 = new BlockPos(var3 * 16 + 8, var15 + 1, var4 * 16 + 8);
            LinkedList var17 = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(var2, var16, var6, var17, this.random);
            this.pieces.addAll(var17);
            this.calculateBoundingBox();
         }
      }

      public void postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         super.postProcess(var1, var2, var3, var4, var5);
         int var6 = this.boundingBox.y0;

         for(int var7 = var4.x0; var7 <= var4.x1; ++var7) {
            for(int var8 = var4.z0; var8 <= var4.z1; ++var8) {
               BlockPos var9 = new BlockPos(var7, var6, var8);
               if (!var1.isEmptyBlock(var9) && this.boundingBox.isInside(var9)) {
                  boolean var10 = false;
                  Iterator var11 = this.pieces.iterator();

                  while(var11.hasNext()) {
                     StructurePiece var12 = (StructurePiece)var11.next();
                     if (var12.getBoundingBox().isInside(var9)) {
                        var10 = true;
                        break;
                     }
                  }

                  if (var10) {
                     for(int var13 = var6 - 1; var13 > 1; --var13) {
                        BlockPos var14 = new BlockPos(var7, var13, var8);
                        if (!var1.isEmptyBlock(var14) && !var1.getBlockState(var14).getMaterial().isLiquid()) {
                           break;
                        }

                        var1.setBlock(var14, Blocks.COBBLESTONE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

      }
   }
}
