package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

public class WoodlandMansionFeature extends StructureFeature<NoneFeatureConfiguration> {
   public WoodlandMansionFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, WoodlandMansionFeature::pieceGeneratorSupplier, WoodlandMansionFeature::afterPlace);
   }

   protected boolean linearSeparation() {
      return false;
   }

   private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> var0) {
      WorldgenRandom var1 = new WorldgenRandom(new LegacyRandomSource(0L));
      var1.setLargeFeatureSeed(var0.seed(), var0.chunkPos().field_504, var0.chunkPos().field_505);
      Rotation var2 = Rotation.getRandom(var1);
      byte var3 = 5;
      byte var4 = 5;
      if (var2 == Rotation.CLOCKWISE_90) {
         var3 = -5;
      } else if (var2 == Rotation.CLOCKWISE_180) {
         var3 = -5;
         var4 = -5;
      } else if (var2 == Rotation.COUNTERCLOCKWISE_90) {
         var4 = -5;
      }

      int var5 = var0.chunkPos().getBlockX(7);
      int var6 = var0.chunkPos().getBlockZ(7);
      int[] var7 = var0.getCornerHeights(var5, var3, var6, var4);
      int var8 = Math.min(Math.min(var7[0], var7[1]), Math.min(var7[2], var7[3]));
      if (var8 < 60) {
         return Optional.empty();
      } else if (!var0.validBiome().test(var0.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(var5), QuartPos.fromBlock(var7[0]), QuartPos.fromBlock(var6)))) {
         return Optional.empty();
      } else {
         BlockPos var9 = new BlockPos(var0.chunkPos().getMiddleBlockX(), var8 + 1, var0.chunkPos().getMiddleBlockZ());
         return Optional.of((var3x, var4x) -> {
            LinkedList var5 = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(var4x.structureManager(), var9, var2, var5, var1);
            Objects.requireNonNull(var3x);
            var5.forEach(var3x::addPiece);
         });
      }
   }

   private static void afterPlace(WorldGenLevel var0, StructureFeatureManager var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5, PiecesContainer var6) {
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
      int var8 = var0.getMinBuildHeight();
      BoundingBox var9 = var6.calculateBoundingBox();
      int var10 = var9.minY();

      for(int var11 = var4.minX(); var11 <= var4.maxX(); ++var11) {
         for(int var12 = var4.minZ(); var12 <= var4.maxZ(); ++var12) {
            var7.set(var11, var10, var12);
            if (!var0.isEmptyBlock(var7) && var9.isInside(var7) && var6.isInsidePiece(var7)) {
               for(int var13 = var10 - 1; var13 > var8; --var13) {
                  var7.setY(var13);
                  if (!var0.isEmptyBlock(var7) && !var0.getBlockState(var7).getMaterial().isLiquid()) {
                     break;
                  }

                  var0.setBlock(var7, Blocks.COBBLESTONE.defaultBlockState(), 2);
               }
            }
         }
      }

   }
}
