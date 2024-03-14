package net.minecraft.world.level.chunk.status;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record WorldGenContext(ServerLevel a, ChunkGenerator b, StructureTemplateManager c, ThreadedLevelLightEngine d) {
   private final ServerLevel level;
   private final ChunkGenerator generator;
   private final StructureTemplateManager structureManager;
   private final ThreadedLevelLightEngine lightEngine;

   public WorldGenContext(ServerLevel var1, ChunkGenerator var2, StructureTemplateManager var3, ThreadedLevelLightEngine var4) {
      super();
      this.level = var1;
      this.generator = var2;
      this.structureManager = var3;
      this.lightEngine = var4;
   }
}
