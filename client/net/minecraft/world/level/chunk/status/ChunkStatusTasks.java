package net.minecraft.world.level.chunk.status;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;

public class ChunkStatusTasks {
   public ChunkStatusTasks() {
      super();
   }

   private static boolean isLighted(ChunkAccess var0) {
      return var0.getPersistedStatus().isOrAfter(ChunkStatus.LIGHT) && var0.isLightCorrect();
   }

   static CompletableFuture<ChunkAccess> passThrough(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> generateStructureStarts(
      WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3
   ) {
      ServerLevel var4 = var0.level();
      if (var4.getServer().getWorldData().worldGenOptions().generateStructures()) {
         var0.generator()
            .createStructures(var4.registryAccess(), var4.getChunkSource().getGeneratorState(), var4.structureManager(), var3, var0.structureManager());
      }

      var4.onStructureStartsAvailable(var3);
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> loadStructureStarts(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      var0.level().onStructureStartsAvailable(var3);
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> generateStructureReferences(
      WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3
   ) {
      ServerLevel var4 = var0.level();
      WorldGenRegion var5 = new WorldGenRegion(var4, var2, var1, var3);
      var0.generator().createReferences(var5, var4.structureManager().forWorldGenRegion(var5), var3);
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> generateBiomes(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      ServerLevel var4 = var0.level();
      WorldGenRegion var5 = new WorldGenRegion(var4, var2, var1, var3);
      return var0.generator().createBiomes(var4.getChunkSource().randomState(), Blender.of(var5), var4.structureManager().forWorldGenRegion(var5), var3);
   }

   static CompletableFuture<ChunkAccess> generateNoise(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      ServerLevel var4 = var0.level();
      WorldGenRegion var5 = new WorldGenRegion(var4, var2, var1, var3);
      return var0.generator()
         .fillFromNoise(Blender.of(var5), var4.getChunkSource().randomState(), var4.structureManager().forWorldGenRegion(var5), var3)
         .thenApply(var0x -> {
            if (var0x instanceof ProtoChunk var1x) {
               BelowZeroRetrogen var2x = var1x.getBelowZeroRetrogen();
               if (var2x != null) {
                  BelowZeroRetrogen.replaceOldBedrock(var1x);
                  if (var2x.hasBedrockHoles()) {
                     var2x.applyBedrockMask(var1x);
                  }
               }
            }

            return (ChunkAccess)var0x;
         });
   }

   static CompletableFuture<ChunkAccess> generateSurface(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      ServerLevel var4 = var0.level();
      WorldGenRegion var5 = new WorldGenRegion(var4, var2, var1, var3);
      var0.generator().buildSurface(var5, var4.structureManager().forWorldGenRegion(var5), var4.getChunkSource().randomState(), var3);
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> generateCarvers(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      ServerLevel var4 = var0.level();
      WorldGenRegion var5 = new WorldGenRegion(var4, var2, var1, var3);
      if (var3 instanceof ProtoChunk var6) {
         Blender.addAroundOldChunksCarvingMaskFilter(var5, var6);
      }

      var0.generator()
         .applyCarvers(
            var5,
            var4.getSeed(),
            var4.getChunkSource().randomState(),
            var4.getBiomeManager(),
            var4.structureManager().forWorldGenRegion(var5),
            var3,
            GenerationStep.Carving.AIR
         );
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> generateFeatures(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      ServerLevel var4 = var0.level();
      Heightmap.primeHeightmaps(
         var3,
         EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE)
      );
      WorldGenRegion var5 = new WorldGenRegion(var4, var2, var1, var3);
      var0.generator().applyBiomeDecoration(var5, var3, var4.structureManager().forWorldGenRegion(var5));
      Blender.generateBorderTicks(var5, var3);
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> initializeLight(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      ThreadedLevelLightEngine var4 = var0.lightEngine();
      var3.initializeLightSources();
      ((ProtoChunk)var3).setLightEngine(var4);
      boolean var5 = isLighted(var3);
      return var4.initializeLight(var3, var5);
   }

   static CompletableFuture<ChunkAccess> light(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      boolean var4 = isLighted(var3);
      return var0.lightEngine().lightChunk(var3, var4);
   }

   static CompletableFuture<ChunkAccess> generateSpawn(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      if (!var3.isUpgrading()) {
         var0.generator().spawnOriginalMobs(new WorldGenRegion(var0.level(), var2, var1, var3));
      }

      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> full(WorldGenContext var0, ChunkStep var1, StaticCache2D<GenerationChunkHolder> var2, ChunkAccess var3) {
      ChunkPos var4 = var3.getPos();
      GenerationChunkHolder var5 = (GenerationChunkHolder)var2.get(var4.x, var4.z);
      return CompletableFuture.supplyAsync(() -> {
         ProtoChunk var3x = (ProtoChunk)var3;
         ServerLevel var5x = var0.level();
         LevelChunk var4x;
         if (var3x instanceof ImposterProtoChunk) {
            var4x = ((ImposterProtoChunk)var3x).getWrapped();
         } else {
            var4x = new LevelChunk(var5x, var3x, var2xx -> postLoadProtoChunk(var5x, var3x.getEntities()));
            var5.replaceProtoChunk(new ImposterProtoChunk(var4x, false));
         }

         var4x.setFullStatus(var5::getFullStatus);
         var4x.runPostLoad();
         var4x.setLoaded(true);
         var4x.registerAllBlockEntitiesAfterLevelLoad();
         var4x.registerTickContainerInLevel(var5x);
         return var4x;
      }, var3x -> var0.mainThreadMailBox().tell(ChunkTaskPriorityQueueSorter.message(var3x, var4.toLong(), var5::getTicketLevel)));
   }

   private static void postLoadProtoChunk(ServerLevel var0, List<CompoundTag> var1) {
      if (!var1.isEmpty()) {
         var0.addWorldGenChunkEntities(EntityType.loadEntitiesRecursive(var1, var0));
      }
   }
}
