package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class EndCityFeature extends StructureFeature<NoneFeatureConfiguration> {
   private static final int RANDOM_SALT = 10387313;

   public EndCityFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1, EndCityFeature::pieceGeneratorSupplier);
   }

   protected boolean linearSeparation() {
      return false;
   }

   private static int getYPositionForFeature(ChunkPos var0, ChunkGenerator var1, LevelHeightAccessor var2) {
      Random var3 = new Random((long)(var0.field_504 + var0.field_505 * 10387313));
      Rotation var4 = Rotation.getRandom(var3);
      byte var5 = 5;
      byte var6 = 5;
      if (var4 == Rotation.CLOCKWISE_90) {
         var5 = -5;
      } else if (var4 == Rotation.CLOCKWISE_180) {
         var5 = -5;
         var6 = -5;
      } else if (var4 == Rotation.COUNTERCLOCKWISE_90) {
         var6 = -5;
      }

      int var7 = var0.getBlockX(7);
      int var8 = var0.getBlockZ(7);
      int var9 = var1.getFirstOccupiedHeight(var7, var8, Heightmap.Types.WORLD_SURFACE_WG, var2);
      int var10 = var1.getFirstOccupiedHeight(var7, var8 + var6, Heightmap.Types.WORLD_SURFACE_WG, var2);
      int var11 = var1.getFirstOccupiedHeight(var7 + var5, var8, Heightmap.Types.WORLD_SURFACE_WG, var2);
      int var12 = var1.getFirstOccupiedHeight(var7 + var5, var8 + var6, Heightmap.Types.WORLD_SURFACE_WG, var2);
      return Math.min(Math.min(var9, var10), Math.min(var11, var12));
   }

   private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> var0) {
      int var1 = getYPositionForFeature(var0.chunkPos(), var0.chunkGenerator(), var0.heightAccessor());
      if (var1 < 60) {
         return Optional.empty();
      } else {
         BlockPos var2 = var0.chunkPos().getMiddleBlockPosition(var1);
         return !var0.validBiome().test(var0.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(var2.getX()), QuartPos.fromBlock(var2.getY()), QuartPos.fromBlock(var2.getZ()))) ? Optional.empty() : Optional.of((var1x, var2x) -> {
            Rotation var3 = Rotation.getRandom(var2x.random());
            ArrayList var4 = Lists.newArrayList();
            EndCityPieces.startHouseTower(var2x.structureManager(), var2, var3, var4, var2x.random());
            Objects.requireNonNull(var1x);
            var4.forEach(var1x::addPiece);
         });
      }
   }
}
