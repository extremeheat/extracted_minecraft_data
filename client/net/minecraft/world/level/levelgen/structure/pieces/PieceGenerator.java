package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

@FunctionalInterface
public interface PieceGenerator<C extends FeatureConfiguration> {
   void generatePieces(StructurePiecesBuilder var1, PieceGenerator.Context<C> var2);

   public static record Context<C extends FeatureConfiguration>(C a, ChunkGenerator b, StructureManager c, ChunkPos d, LevelHeightAccessor e, WorldgenRandom f, long g) {
      private final C config;
      private final ChunkGenerator chunkGenerator;
      private final StructureManager structureManager;
      private final ChunkPos chunkPos;
      private final LevelHeightAccessor heightAccessor;
      private final WorldgenRandom random;
      private final long seed;

      public Context(C var1, ChunkGenerator var2, StructureManager var3, ChunkPos var4, LevelHeightAccessor var5, WorldgenRandom var6, long var7) {
         super();
         this.config = var1;
         this.chunkGenerator = var2;
         this.structureManager = var3;
         this.chunkPos = var4;
         this.heightAccessor = var5;
         this.random = var6;
         this.seed = var7;
      }

      public C config() {
         return this.config;
      }

      public ChunkGenerator chunkGenerator() {
         return this.chunkGenerator;
      }

      public StructureManager structureManager() {
         return this.structureManager;
      }

      public ChunkPos chunkPos() {
         return this.chunkPos;
      }

      public LevelHeightAccessor heightAccessor() {
         return this.heightAccessor;
      }

      public WorldgenRandom random() {
         return this.random;
      }

      public long seed() {
         return this.seed;
      }
   }
}
