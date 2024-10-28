package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@FunctionalInterface
public interface PieceGenerator<C extends FeatureConfiguration> {
   void generatePieces(StructurePiecesBuilder var1, Context<C> var2);

   public static record Context<C extends FeatureConfiguration>(C config, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, ChunkPos chunkPos, LevelHeightAccessor heightAccessor, WorldgenRandom random, long seed) {
      public Context(C config, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, ChunkPos chunkPos, LevelHeightAccessor heightAccessor, WorldgenRandom random, long seed) {
         super();
         this.config = config;
         this.chunkGenerator = chunkGenerator;
         this.structureTemplateManager = structureTemplateManager;
         this.chunkPos = chunkPos;
         this.heightAccessor = heightAccessor;
         this.random = random;
         this.seed = seed;
      }

      public C config() {
         return this.config;
      }

      public ChunkGenerator chunkGenerator() {
         return this.chunkGenerator;
      }

      public StructureTemplateManager structureTemplateManager() {
         return this.structureTemplateManager;
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
