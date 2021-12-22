package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

@FunctionalInterface
public interface PostPlacementProcessor {
   PostPlacementProcessor NONE = (var0, var1, var2, var3, var4, var5, var6) -> {
   };

   void afterPlace(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, PiecesContainer var7);
}
