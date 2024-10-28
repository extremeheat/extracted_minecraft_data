package net.minecraft.world.level.chunk.status;

import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record WorldGenContext(ServerLevel level, ChunkGenerator generator, StructureTemplateManager structureManager, ThreadedLevelLightEngine lightEngine, ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> mainThreadMailBox) {
   public WorldGenContext(ServerLevel var1, ChunkGenerator var2, StructureTemplateManager var3, ThreadedLevelLightEngine var4, ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> var5) {
      super();
      this.level = var1;
      this.generator = var2;
      this.structureManager = var3;
      this.lightEngine = var4;
      this.mainThreadMailBox = var5;
   }

   public ServerLevel level() {
      return this.level;
   }

   public ChunkGenerator generator() {
      return this.generator;
   }

   public StructureTemplateManager structureManager() {
      return this.structureManager;
   }

   public ThreadedLevelLightEngine lightEngine() {
      return this.lightEngine;
   }

   public ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> mainThreadMailBox() {
      return this.mainThreadMailBox;
   }
}
