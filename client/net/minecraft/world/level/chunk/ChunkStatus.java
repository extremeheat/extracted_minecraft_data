package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ChunkStatus {
   public static final int MAX_STRUCTURE_DISTANCE = 8;
   private static final EnumSet<Heightmap.Types> PRE_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG);
   public static final EnumSet<Heightmap.Types> POST_FEATURES = EnumSet.of(
      Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES
   );
   private static final ChunkStatus.LoadingTask PASSTHROUGH_LOAD_TASK = (var0, var1, var2, var3, var4, var5) -> CompletableFuture.completedFuture(
         Either.left(var5)
      );
   public static final ChunkStatus EMPTY = registerSimple(
      "empty", null, -1, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4) -> {
      }
   );
   public static final ChunkStatus STRUCTURE_STARTS = register(
      "structure_starts", EMPTY, 0, false, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4, var5, var6, var7, var8) -> {
         if (var2.getServer().getWorldData().worldGenOptions().generateStructures()) {
            var3.createStructures(var2.registryAccess(), var2.getChunkSource().getGeneratorState(), var2.structureManager(), var8, var4);
         }
   
         var2.onStructureStartsAvailable(var8);
         return CompletableFuture.completedFuture(Either.left(var8));
      }, (var0, var1, var2, var3, var4, var5) -> {
         var1.onStructureStartsAvailable(var5);
         return CompletableFuture.completedFuture(Either.left(var5));
      }
   );
   public static final ChunkStatus STRUCTURE_REFERENCES = registerSimple(
      "structure_references", STRUCTURE_STARTS, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4) -> {
         WorldGenRegion var5 = new WorldGenRegion(var1, var3, var0, -1);
         var2.createReferences(var5, var1.structureManager().forWorldGenRegion(var5), var4);
      }
   );
   public static final ChunkStatus BIOMES = register(
      "biomes",
      STRUCTURE_REFERENCES,
      8,
      PRE_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8) -> {
         WorldGenRegion var9 = new WorldGenRegion(var2, var7, var0, -1);
         return var3.createBiomes(var1, var2.getChunkSource().randomState(), Blender.of(var9), var2.structureManager().forWorldGenRegion(var9), var8)
            .thenApply(var0x -> Either.left(var0x));
      }
   );
   public static final ChunkStatus NOISE = register(
      "noise",
      BIOMES,
      8,
      PRE_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8) -> {
         WorldGenRegion var9 = new WorldGenRegion(var2, var7, var0, 0);
         return var3.fillFromNoise(var1, Blender.of(var9), var2.getChunkSource().randomState(), var2.structureManager().forWorldGenRegion(var9), var8)
            .thenApply(var0x -> {
               if (var0x instanceof ProtoChunk var1xx) {
                  BelowZeroRetrogen var2xx = ((ProtoChunk)var1xx).getBelowZeroRetrogen();
                  if (var2xx != null) {
                     BelowZeroRetrogen.replaceOldBedrock((ProtoChunk)var1xx);
                     if (var2xx.hasBedrockHoles()) {
                        var2xx.applyBedrockMask((ProtoChunk)var1xx);
                     }
                  }
               }
      
               return Either.left(var0x);
            });
      }
   );
   public static final ChunkStatus SURFACE = registerSimple(
      "surface", NOISE, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4) -> {
         WorldGenRegion var5 = new WorldGenRegion(var1, var3, var0, 0);
         var2.buildSurface(var5, var1.structureManager().forWorldGenRegion(var5), var1.getChunkSource().randomState(), var4);
      }
   );
   public static final ChunkStatus CARVERS = registerSimple(
      "carvers",
      SURFACE,
      8,
      POST_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4) -> {
         WorldGenRegion var5 = new WorldGenRegion(var1, var3, var0, 0);
         if (var4 instanceof ProtoChunk var6) {
            Blender.addAroundOldChunksCarvingMaskFilter(var5, (ProtoChunk)var6);
         }
   
         var2.applyCarvers(
            var5,
            var1.getSeed(),
            var1.getChunkSource().randomState(),
            var1.getBiomeManager(),
            var1.structureManager().forWorldGenRegion(var5),
            var4,
            GenerationStep.Carving.AIR
         );
      }
   );
   public static final ChunkStatus FEATURES = registerSimple(
      "features",
      CARVERS,
      8,
      POST_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4) -> {
         Heightmap.primeHeightmaps(
            var4,
            EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE)
         );
         WorldGenRegion var5 = new WorldGenRegion(var1, var3, var0, 1);
         var2.applyBiomeDecoration(var5, var4, var1.structureManager().forWorldGenRegion(var5));
         Blender.generateBorderTicks(var5, var4);
      }
   );
   public static final ChunkStatus INITIALIZE_LIGHT = register(
      "initialize_light",
      FEATURES,
      0,
      false,
      POST_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8) -> initializeLight(var5, var8),
      (var0, var1, var2, var3, var4, var5) -> initializeLight(var3, var5)
   );
   public static final ChunkStatus LIGHT = register(
      "light",
      INITIALIZE_LIGHT,
      1,
      true,
      POST_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8) -> lightChunk(var5, var8),
      (var0, var1, var2, var3, var4, var5) -> lightChunk(var3, var5)
   );
   public static final ChunkStatus SPAWN = registerSimple(
      "spawn", LIGHT, 0, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4) -> {
         if (!var4.isUpgrading()) {
            var2.spawnOriginalMobs(new WorldGenRegion(var1, var3, var0, -1));
         }
      }
   );
   public static final ChunkStatus FULL = register(
      "full",
      SPAWN,
      0,
      false,
      POST_FEATURES,
      ChunkStatus.ChunkType.LEVELCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8) -> var6.apply(var8),
      (var0, var1, var2, var3, var4, var5) -> var4.apply(var5)
   );
   private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of(
      FULL,
      INITIALIZE_LIGHT,
      CARVERS,
      BIOMES,
      STRUCTURE_STARTS,
      STRUCTURE_STARTS,
      STRUCTURE_STARTS,
      STRUCTURE_STARTS,
      STRUCTURE_STARTS,
      STRUCTURE_STARTS,
      STRUCTURE_STARTS,
      STRUCTURE_STARTS,
      new ChunkStatus[0]
   );
   private static final IntList RANGE_BY_STATUS = Util.make(new IntArrayList(getStatusList().size()), var0 -> {
      int var1 = 0;

      for(int var2 = getStatusList().size() - 1; var2 >= 0; --var2) {
         while(var1 + 1 < STATUS_BY_RANGE.size() && var2 <= STATUS_BY_RANGE.get(var1 + 1).getIndex()) {
            ++var1;
         }

         var0.add(0, var1);
      }
   });
   private final int index;
   private final ChunkStatus parent;
   private final ChunkStatus.GenerationTask generationTask;
   private final ChunkStatus.LoadingTask loadingTask;
   private final int range;
   private final boolean hasLoadDependencies;
   private final ChunkStatus.ChunkType chunkType;
   private final EnumSet<Heightmap.Types> heightmapsAfter;

   private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> initializeLight(ThreadedLevelLightEngine var0, ChunkAccess var1) {
      var1.initializeLightSources();
      ((ProtoChunk)var1).setLightEngine(var0);
      boolean var2 = isLighted(var1);
      return var0.initializeLight(var1, var2).thenApply(Either::left);
   }

   private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> lightChunk(ThreadedLevelLightEngine var0, ChunkAccess var1) {
      boolean var2 = isLighted(var1);
      return var0.lightChunk(var1, var2).thenApply(Either::left);
   }

   private static ChunkStatus registerSimple(
      String var0, @Nullable ChunkStatus var1, int var2, EnumSet<Heightmap.Types> var3, ChunkStatus.ChunkType var4, ChunkStatus.SimpleGenerationTask var5
   ) {
      return register(var0, var1, var2, var3, var4, var5);
   }

   private static ChunkStatus register(
      String var0, @Nullable ChunkStatus var1, int var2, EnumSet<Heightmap.Types> var3, ChunkStatus.ChunkType var4, ChunkStatus.GenerationTask var5
   ) {
      return register(var0, var1, var2, false, var3, var4, var5, PASSTHROUGH_LOAD_TASK);
   }

   private static ChunkStatus register(
      String var0,
      @Nullable ChunkStatus var1,
      int var2,
      boolean var3,
      EnumSet<Heightmap.Types> var4,
      ChunkStatus.ChunkType var5,
      ChunkStatus.GenerationTask var6,
      ChunkStatus.LoadingTask var7
   ) {
      return Registry.register(BuiltInRegistries.CHUNK_STATUS, var0, new ChunkStatus(var1, var2, var3, var4, var5, var6, var7));
   }

   public static List<ChunkStatus> getStatusList() {
      ArrayList var0 = Lists.newArrayList();

      ChunkStatus var1;
      for(var1 = FULL; var1.getParent() != var1; var1 = var1.getParent()) {
         var0.add(var1);
      }

      var0.add(var1);
      Collections.reverse(var0);
      return var0;
   }

   private static boolean isLighted(ChunkAccess var0) {
      return var0.getStatus().isOrAfter(LIGHT) && var0.isLightCorrect();
   }

   public static ChunkStatus getStatusAroundFullChunk(int var0) {
      if (var0 >= STATUS_BY_RANGE.size()) {
         return EMPTY;
      } else {
         return var0 < 0 ? FULL : STATUS_BY_RANGE.get(var0);
      }
   }

   public static int maxDistance() {
      return STATUS_BY_RANGE.size();
   }

   public static int getDistance(ChunkStatus var0) {
      return RANGE_BY_STATUS.getInt(var0.getIndex());
   }

   ChunkStatus(
      @Nullable ChunkStatus var1,
      int var2,
      boolean var3,
      EnumSet<Heightmap.Types> var4,
      ChunkStatus.ChunkType var5,
      ChunkStatus.GenerationTask var6,
      ChunkStatus.LoadingTask var7
   ) {
      super();
      this.parent = var1 == null ? this : var1;
      this.generationTask = var6;
      this.loadingTask = var7;
      this.range = var2;
      this.hasLoadDependencies = var3;
      this.chunkType = var5;
      this.heightmapsAfter = var4;
      this.index = var1 == null ? 0 : var1.getIndex() + 1;
   }

   public int getIndex() {
      return this.index;
   }

   public ChunkStatus getParent() {
      return this.parent;
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> generate(
      Executor var1,
      ServerLevel var2,
      ChunkGenerator var3,
      StructureTemplateManager var4,
      ThreadedLevelLightEngine var5,
      Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var6,
      List<ChunkAccess> var7
   ) {
      ChunkAccess var8 = (ChunkAccess)var7.get(var7.size() / 2);
      ProfiledDuration var9 = JvmProfiler.INSTANCE.onChunkGenerate(var8.getPos(), var2.dimension(), this.toString());
      return this.generationTask.doWork(this, var1, var2, var3, var4, var5, var6, var7, var8).thenApply(var2x -> {
         var2x.ifLeft(var1xx -> {
            if (var1xx instanceof ProtoChunk var2xxx && !var2xxx.getStatus().isOrAfter(this)) {
               var2xxx.setStatus(this);
            }
         });
         if (var9 != null) {
            var9.finish();
         }

         return var2x;
      });
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> load(
      ServerLevel var1,
      StructureTemplateManager var2,
      ThreadedLevelLightEngine var3,
      Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var4,
      ChunkAccess var5
   ) {
      return this.loadingTask.doWork(this, var1, var2, var3, var4, var5);
   }

   public int getRange() {
      return this.range;
   }

   public boolean hasLoadDependencies() {
      return this.hasLoadDependencies;
   }

   public ChunkStatus.ChunkType getChunkType() {
      return this.chunkType;
   }

   public static ChunkStatus byName(String var0) {
      return BuiltInRegistries.CHUNK_STATUS.get(ResourceLocation.tryParse(var0));
   }

   public EnumSet<Heightmap.Types> heightmapsAfter() {
      return this.heightmapsAfter;
   }

   public boolean isOrAfter(ChunkStatus var1) {
      return this.getIndex() >= var1.getIndex();
   }

   @Override
   public String toString() {
      return BuiltInRegistries.CHUNK_STATUS.getKey(this).toString();
   }

   public static enum ChunkType {
      PROTOCHUNK,
      LEVELCHUNK;

      private ChunkType() {
      }
   }

   interface GenerationTask {
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(
         ChunkStatus var1,
         Executor var2,
         ServerLevel var3,
         ChunkGenerator var4,
         StructureTemplateManager var5,
         ThreadedLevelLightEngine var6,
         Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var7,
         List<ChunkAccess> var8,
         ChunkAccess var9
      );
   }

   interface LoadingTask {
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(
         ChunkStatus var1,
         ServerLevel var2,
         StructureTemplateManager var3,
         ThreadedLevelLightEngine var4,
         Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var5,
         ChunkAccess var6
      );
   }

   interface SimpleGenerationTask extends ChunkStatus.GenerationTask {
      @Override
      default CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(
         ChunkStatus var1,
         Executor var2,
         ServerLevel var3,
         ChunkGenerator var4,
         StructureTemplateManager var5,
         ThreadedLevelLightEngine var6,
         Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var7,
         List<ChunkAccess> var8,
         ChunkAccess var9
      ) {
         this.doWork(var1, var3, var4, var8, var9);
         return CompletableFuture.completedFuture(Either.left(var9));
      }

      void doWork(ChunkStatus var1, ServerLevel var2, ChunkGenerator var3, List<ChunkAccess> var4, ChunkAccess var5);
   }
}
