package net.minecraft.world.level.chunk.status;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record WorldGenContext(ServerLevel level, ChunkGenerator generator, StructureTemplateManager structureManager, ThreadedLevelLightEngine lightEngine) {
   public WorldGenContext(ServerLevel level, ChunkGenerator generator, StructureTemplateManager structureManager, ThreadedLevelLightEngine lightEngine) {
      super();
      this.level = level;
      this.generator = generator;
      this.structureManager = structureManager;
      this.lightEngine = lightEngine;
   }
}
