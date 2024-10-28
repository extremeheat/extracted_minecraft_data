package net.minecraft.world.level.chunk.status;

import java.util.concurrent.Executor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record WorldGenContext(ServerLevel level, ChunkGenerator generator, StructureTemplateManager structureManager, ThreadedLevelLightEngine lightEngine, Executor mainThreadExecutor, LevelChunk.UnsavedListener unsavedListener) {
   public WorldGenContext(ServerLevel var1, ChunkGenerator var2, StructureTemplateManager var3, ThreadedLevelLightEngine var4, Executor var5, LevelChunk.UnsavedListener var6) {
      super();
      this.level = var1;
      this.generator = var2;
      this.structureManager = var3;
      this.lightEngine = var4;
      this.mainThreadExecutor = var5;
      this.unsavedListener = var6;
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

   public Executor mainThreadExecutor() {
      return this.mainThreadExecutor;
   }

   public LevelChunk.UnsavedListener unsavedListener() {
      return this.unsavedListener;
   }
}
