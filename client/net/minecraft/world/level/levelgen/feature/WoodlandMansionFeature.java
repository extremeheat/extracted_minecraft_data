package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
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

public class WoodlandMansionFeature extends StructureFeature<NoneFeatureConfiguration> {
   public WoodlandMansionFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected ChunkPos getPotentialFeatureChunkFromLocationWithOffset(ChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
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

   public boolean isFeatureChunk(ChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.getPotentialFeatureChunkFromLocationWithOffset(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.x && var4 == var5.z) {
         Set var6 = var1.getBiomeSource().getBiomesWithin(var3 * 16 + 9, var4 * 16 + 9, 32);
         Iterator var7 = var6.iterator();

         Biome var8;
         do {
            if (!var7.hasNext()) {
               return true;
            }

            var8 = (Biome)var7.next();
         } while(var1.isBiomeValidStartForStructure(var8, Feature.WOODLAND_MANSION));

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
      public WoodlandMansionStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
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

      public void postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         super.postProcess(var1, var2, var3, var4);
         int var5 = this.boundingBox.y0;

         for(int var6 = var3.x0; var6 <= var3.x1; ++var6) {
            for(int var7 = var3.z0; var7 <= var3.z1; ++var7) {
               BlockPos var8 = new BlockPos(var6, var5, var7);
               if (!var1.isEmptyBlock(var8) && this.boundingBox.isInside(var8)) {
                  boolean var9 = false;
                  Iterator var10 = this.pieces.iterator();

                  while(var10.hasNext()) {
                     StructurePiece var11 = (StructurePiece)var10.next();
                     if (var11.getBoundingBox().isInside(var8)) {
                        var9 = true;
                        break;
                     }
                  }

                  if (var9) {
                     for(int var12 = var5 - 1; var12 > 1; --var12) {
                        BlockPos var13 = new BlockPos(var6, var12, var7);
                        if (!var1.isEmptyBlock(var13) && !var1.getBlockState(var13).getMaterial().isLiquid()) {
                           break;
                        }

                        var1.setBlock(var13, Blocks.COBBLESTONE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

      }
   }
}
