package net.minecraft.world.level.chunk.status;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
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
      return var0.getStatus().isOrAfter(ChunkStatus.LIGHT) && var0.isLightCorrect();
   }

   static CompletableFuture<ChunkAccess> generateEmpty(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      return CompletableFuture.completedFuture(var5);
   }

   static CompletableFuture<ChunkAccess> loadPassThrough(WorldGenContext var0, ChunkStatus var1, ToFullChunk var2, ChunkAccess var3) {
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> generateStructureStarts(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      ServerLevel var6 = var0.level();
      if (var6.getServer().getWorldData().worldGenOptions().generateStructures()) {
         var0.generator()
            .createStructures(var6.registryAccess(), var6.getChunkSource().getGeneratorState(), var6.structureManager(), var5, var0.structureManager());
      }

      var6.onStructureStartsAvailable(var5);
      return CompletableFuture.completedFuture(var5);
   }

   static CompletableFuture<ChunkAccess> loadStructureStarts(WorldGenContext var0, ChunkStatus var1, ToFullChunk var2, ChunkAccess var3) {
      var0.level().onStructureStartsAvailable(var3);
      return CompletableFuture.completedFuture(var3);
   }

   static CompletableFuture<ChunkAccess> generateStructureReferences(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      ServerLevel var6 = var0.level();
      WorldGenRegion var7 = new WorldGenRegion(var6, var4, var1, -1);
      var0.generator().createReferences(var7, var6.structureManager().forWorldGenRegion(var7), var5);
      return CompletableFuture.completedFuture(var5);
   }

   static CompletableFuture<ChunkAccess> generateBiomes(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      ServerLevel var6 = var0.level();
      WorldGenRegion var7 = new WorldGenRegion(var6, var4, var1, -1);
      return var0.generator().createBiomes(var2, var6.getChunkSource().randomState(), Blender.of(var7), var6.structureManager().forWorldGenRegion(var7), var5);
   }

   static CompletableFuture<ChunkAccess> generateNoise(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      ServerLevel var6 = var0.level();
      WorldGenRegion var7 = new WorldGenRegion(var6, var4, var1, 0);
      return var0.generator()
         .fillFromNoise(var2, Blender.of(var7), var6.getChunkSource().randomState(), var6.structureManager().forWorldGenRegion(var7), var5)
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

   static CompletableFuture<ChunkAccess> generateSurface(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      ServerLevel var6 = var0.level();
      WorldGenRegion var7 = new WorldGenRegion(var6, var4, var1, 0);
      var0.generator().buildSurface(var7, var6.structureManager().forWorldGenRegion(var7), var6.getChunkSource().randomState(), var5);
      return CompletableFuture.completedFuture(var5);
   }

   static CompletableFuture<ChunkAccess> generateCarvers(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      ServerLevel var6 = var0.level();
      WorldGenRegion var7 = new WorldGenRegion(var6, var4, var1, 0);
      if (var5 instanceof ProtoChunk var8) {
         Blender.addAroundOldChunksCarvingMaskFilter(var7, var8);
      }

      var0.generator()
         .applyCarvers(
            var7,
            var6.getSeed(),
            var6.getChunkSource().randomState(),
            var6.getBiomeManager(),
            var6.structureManager().forWorldGenRegion(var7),
            var5,
            GenerationStep.Carving.AIR
         );
      return CompletableFuture.completedFuture(var5);
   }

   static CompletableFuture<ChunkAccess> generateFeatures(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      ServerLevel var6 = var0.level();
      Heightmap.primeHeightmaps(
         var5,
         EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE)
      );
      WorldGenRegion var7 = new WorldGenRegion(var6, var4, var1, 1);
      var0.generator().applyBiomeDecoration(var7, var5, var6.structureManager().forWorldGenRegion(var7));
      Blender.generateBorderTicks(var7, var5);
      return CompletableFuture.completedFuture(var5);
   }

   static CompletableFuture<ChunkAccess> generateInitializeLight(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      return initializeLight(var0.lightEngine(), var5);
   }

   static CompletableFuture<ChunkAccess> loadInitializeLight(WorldGenContext var0, ChunkStatus var1, ToFullChunk var2, ChunkAccess var3) {
      return initializeLight(var0.lightEngine(), var3);
   }

   private static CompletableFuture<ChunkAccess> initializeLight(ThreadedLevelLightEngine var0, ChunkAccess var1) {
      var1.initializeLightSources();
      ((ProtoChunk)var1).setLightEngine(var0);
      boolean var2 = isLighted(var1);
      return var0.initializeLight(var1, var2);
   }

   static CompletableFuture<ChunkAccess> generateLight(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      return lightChunk(var0.lightEngine(), var5);
   }

   static CompletableFuture<ChunkAccess> loadLight(WorldGenContext var0, ChunkStatus var1, ToFullChunk var2, ChunkAccess var3) {
      return lightChunk(var0.lightEngine(), var3);
   }

   private static CompletableFuture<ChunkAccess> lightChunk(ThreadedLevelLightEngine var0, ChunkAccess var1) {
      boolean var2 = isLighted(var1);
      return var0.lightChunk(var1, var2);
   }

   static CompletableFuture<ChunkAccess> generateSpawn(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      if (!var5.isUpgrading()) {
         var0.generator().spawnOriginalMobs(new WorldGenRegion(var0.level(), var4, var1, -1));
      }

      return CompletableFuture.completedFuture(var5);
   }

   static CompletableFuture<ChunkAccess> generateFull(
      WorldGenContext var0, ChunkStatus var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4, ChunkAccess var5
   ) {
      return var3.apply(var5);
   }

   static CompletableFuture<ChunkAccess> loadFull(WorldGenContext var0, ChunkStatus var1, ToFullChunk var2, ChunkAccess var3) {
      return var2.apply(var3);
   }
}
