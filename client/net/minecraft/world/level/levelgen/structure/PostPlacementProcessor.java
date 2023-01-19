package net.minecraft.world.level.levelgen.structure;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

@FunctionalInterface
public interface PostPlacementProcessor {
   PostPlacementProcessor NONE = (var0, var1, var2, var3, var4, var5, var6) -> {
   };

   void afterPlace(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, PiecesContainer var7);
}
