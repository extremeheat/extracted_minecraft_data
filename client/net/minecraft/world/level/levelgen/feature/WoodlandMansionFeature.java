package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class WoodlandMansionFeature extends StructureFeature<NoneFeatureConfiguration> {
   public WoodlandMansionFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected boolean linearSeparation() {
      return false;
   }

   protected boolean isFeatureChunk(ChunkGenerator var1, BiomeSource var2, long var3, WorldgenRandom var5, int var6, int var7, Biome var8, ChunkPos var9, NoneFeatureConfiguration var10) {
      Set var11 = var2.getBiomesWithin(SectionPos.sectionToBlockCoord(var6, 9), var1.getSeaLevel(), SectionPos.sectionToBlockCoord(var7, 9), 32);
      Iterator var12 = var11.iterator();

      Biome var13;
      do {
         if (!var12.hasNext()) {
            return true;
         }

         var13 = (Biome)var12.next();
      } while(var13.getGenerationSettings().isValidStart(this));

      return false;
   }

   public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
      return WoodlandMansionFeature.WoodlandMansionStart::new;
   }

   public static class WoodlandMansionStart extends StructureStart<NoneFeatureConfiguration> {
      public WoodlandMansionStart(StructureFeature<NoneFeatureConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, NoneFeatureConfiguration var7) {
         Rotation var8 = Rotation.getRandom(this.random);
         byte var9 = 5;
         byte var10 = 5;
         if (var8 == Rotation.CLOCKWISE_90) {
            var9 = -5;
         } else if (var8 == Rotation.CLOCKWISE_180) {
            var9 = -5;
            var10 = -5;
         } else if (var8 == Rotation.COUNTERCLOCKWISE_90) {
            var10 = -5;
         }

         int var11 = SectionPos.sectionToBlockCoord(var4, 7);
         int var12 = SectionPos.sectionToBlockCoord(var5, 7);
         int var13 = var2.getFirstOccupiedHeight(var11, var12, Heightmap.Types.WORLD_SURFACE_WG);
         int var14 = var2.getFirstOccupiedHeight(var11, var12 + var10, Heightmap.Types.WORLD_SURFACE_WG);
         int var15 = var2.getFirstOccupiedHeight(var11 + var9, var12, Heightmap.Types.WORLD_SURFACE_WG);
         int var16 = var2.getFirstOccupiedHeight(var11 + var9, var12 + var10, Heightmap.Types.WORLD_SURFACE_WG);
         int var17 = Math.min(Math.min(var13, var14), Math.min(var15, var16));
         if (var17 >= 60) {
            BlockPos var18 = new BlockPos(SectionPos.sectionToBlockCoord(var4, 8), var17 + 1, SectionPos.sectionToBlockCoord(var5, 8));
            LinkedList var19 = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(var3, var18, var8, var19, this.random);
            this.pieces.addAll(var19);
            this.calculateBoundingBox();
         }
      }

      public void placeInChunk(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6) {
         super.placeInChunk(var1, var2, var3, var4, var5, var6);
         int var7 = this.boundingBox.y0;

         for(int var8 = var5.x0; var8 <= var5.x1; ++var8) {
            for(int var9 = var5.z0; var9 <= var5.z1; ++var9) {
               BlockPos var10 = new BlockPos(var8, var7, var9);
               if (!var1.isEmptyBlock(var10) && this.boundingBox.isInside(var10)) {
                  boolean var11 = false;
                  Iterator var12 = this.pieces.iterator();

                  while(var12.hasNext()) {
                     StructurePiece var13 = (StructurePiece)var12.next();
                     if (var13.getBoundingBox().isInside(var10)) {
                        var11 = true;
                        break;
                     }
                  }

                  if (var11) {
                     for(int var14 = var7 - 1; var14 > 1; --var14) {
                        BlockPos var15 = new BlockPos(var8, var14, var9);
                        if (!var1.isEmptyBlock(var15) && !var1.getBlockState(var15).getMaterial().isLiquid()) {
                           break;
                        }

                        var1.setBlock(var15, Blocks.COBBLESTONE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

      }
   }
}
