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
   private static final ChunkStatus.LoadingTask PASSTHROUGH_LOAD_TASK = (var0, var1, var2, var3, var4, var5) -> {
      if (var5 instanceof ProtoChunk var6 && !var5.getStatus().isOrAfter(var0)) {
         var6.setStatus(var0);
      }

      return CompletableFuture.completedFuture(Either.left(var5));
   };
   public static final ChunkStatus EMPTY = registerSimple(
      "empty", null, -1, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4) -> {
      }
   );
   public static final ChunkStatus STRUCTURE_STARTS = register(
      "structure_starts", EMPTY, 0, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4, var5, var6, var7, var8, var9) -> {
         if (!var8.getStatus().isOrAfter(var0)) {
            if (var2.getServer().getWorldData().worldGenSettings().generateStructures()) {
               var3.createStructures(var2.registryAccess(), var2.getChunkSource().randomState(), var2.structureManager(), var8, var4, var2.getSeed());
            }
   
            if (var8 instanceof ProtoChunk var10) {
               var10.setStatus(var0);
            }
   
            var2.onStructureStartsAvailable(var8);
         }
   
         return CompletableFuture.completedFuture(Either.left(var8));
      }, (var0, var1, var2, var3, var4, var5) -> {
         if (!var5.getStatus().isOrAfter(var0)) {
            if (var5 instanceof ProtoChunk var6) {
               var6.setStatus(var0);
            }
   
            var1.onStructureStartsAvailable(var5);
         }
   
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
      (var0, var1, var2, var3, var4, var5, var6, var7, var8, var9) -> {
         if (!var9 && var8.getStatus().isOrAfter(var0)) {
            return CompletableFuture.completedFuture(Either.left(var8));
         } else {
            WorldGenRegion var10 = new WorldGenRegion(var2, var7, var0, -1);
            return var3.createBiomes(
                  var2.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY),
                  var1,
                  var2.getChunkSource().randomState(),
                  Blender.of(var10),
                  var2.structureManager().forWorldGenRegion(var10),
                  var8
               )
               .thenApply(var1x -> {
                  if (var1x instanceof ProtoChunk) {
                     ((ProtoChunk)var1x).setStatus(var0);
                  }
      
                  return Either.left(var1x);
               });
         }
      }
   );
   public static final ChunkStatus NOISE = register(
      "noise",
      BIOMES,
      8,
      PRE_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8, var9) -> {
         if (!var9 && var8.getStatus().isOrAfter(var0)) {
            return CompletableFuture.completedFuture(Either.left(var8));
         } else {
            WorldGenRegion var10 = new WorldGenRegion(var2, var7, var0, 0);
            return var3.fillFromNoise(var1, Blender.of(var10), var2.getChunkSource().randomState(), var2.structureManager().forWorldGenRegion(var10), var8)
               .thenApply(var1x -> {
                  if (var1x instanceof ProtoChunk var2x) {
                     BelowZeroRetrogen var3x = ((ProtoChunk)var2x).getBelowZeroRetrogen();
                     if (var3x != null) {
                        BelowZeroRetrogen.replaceOldBedrock((ProtoChunk)var2x);
                        if (var3x.hasBedrockHoles()) {
                           var3x.applyBedrockMask((ProtoChunk)var2x);
                        }
                     }
      
                     ((ProtoChunk)var2x).setStatus(var0);
                  }
      
                  return Either.left(var1x);
               });
         }
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
      PRE_FEATURES,
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
   public static final ChunkStatus LIQUID_CARVERS = registerSimple(
      "liquid_carvers", CARVERS, 8, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4) -> {
      }
   );
   public static final ChunkStatus FEATURES = register(
      "features",
      LIQUID_CARVERS,
      8,
      POST_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8, var9) -> {
         ProtoChunk var10 = (ProtoChunk)var8;
         var10.setLightEngine(var5);
         if (var9 || !var8.getStatus().isOrAfter(var0)) {
            Heightmap.primeHeightmaps(
               var8,
               EnumSet.of(
                  Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE
               )
            );
            WorldGenRegion var11 = new WorldGenRegion(var2, var7, var0, 1);
            var3.applyBiomeDecoration(var11, var8, var2.structureManager().forWorldGenRegion(var11));
            Blender.generateBorderTicks(var11, var8);
            var10.setStatus(var0);
         }
   
         return var5.retainData(var8).thenApply(Either::left);
      },
      (var0, var1, var2, var3, var4, var5) -> var3.retainData(var5).thenApply(Either::left)
   );
   public static final ChunkStatus LIGHT = register(
      "light",
      FEATURES,
      1,
      POST_FEATURES,
      ChunkStatus.ChunkType.PROTOCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8, var9) -> lightChunk(var0, var5, var8),
      (var0, var1, var2, var3, var4, var5) -> lightChunk(var0, var3, var5)
   );
   public static final ChunkStatus SPAWN = registerSimple(
      "spawn", LIGHT, 0, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4) -> {
         if (!var4.isUpgrading()) {
            var2.spawnOriginalMobs(new WorldGenRegion(var1, var3, var0, -1));
         }
      }
   );
   public static final ChunkStatus HEIGHTMAPS = registerSimple(
      "heightmaps", SPAWN, 0, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (var0, var1, var2, var3, var4) -> {
      }
   );
   public static final ChunkStatus FULL = register(
      "full",
      HEIGHTMAPS,
      0,
      POST_FEATURES,
      ChunkStatus.ChunkType.LEVELCHUNK,
      (var0, var1, var2, var3, var4, var5, var6, var7, var8, var9) -> var6.apply(var8),
      (var0, var1, var2, var3, var4, var5) -> var4.apply(var5)
   );
   private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of(
      FULL,
      FEATURES,
      LIQUID_CARVERS,
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
   private final String name;
   private final int index;
   private final ChunkStatus parent;
   private final ChunkStatus.GenerationTask generationTask;
   private final ChunkStatus.LoadingTask loadingTask;
   private final int range;
   private final ChunkStatus.ChunkType chunkType;
   private final EnumSet<Heightmap.Types> heightmapsAfter;

   private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> lightChunk(
      ChunkStatus var0, ThreadedLevelLightEngine var1, ChunkAccess var2
   ) {
      boolean var3 = isLighted(var0, var2);
      if (!var2.getStatus().isOrAfter(var0)) {
         ((ProtoChunk)var2).setStatus(var0);
      }

      return var1.lightChunk(var2, var3).thenApply(Either::left);
   }

   private static ChunkStatus registerSimple(
      String var0, @Nullable ChunkStatus var1, int var2, EnumSet<Heightmap.Types> var3, ChunkStatus.ChunkType var4, ChunkStatus.SimpleGenerationTask var5
   ) {
      return register(var0, var1, var2, var3, var4, var5);
   }

   private static ChunkStatus register(
      String var0, @Nullable ChunkStatus var1, int var2, EnumSet<Heightmap.Types> var3, ChunkStatus.ChunkType var4, ChunkStatus.GenerationTask var5
   ) {
      return register(var0, var1, var2, var3, var4, var5, PASSTHROUGH_LOAD_TASK);
   }

   private static ChunkStatus register(
      String var0,
      @Nullable ChunkStatus var1,
      int var2,
      EnumSet<Heightmap.Types> var3,
      ChunkStatus.ChunkType var4,
      ChunkStatus.GenerationTask var5,
      ChunkStatus.LoadingTask var6
   ) {
      return Registry.register(Registry.CHUNK_STATUS, var0, new ChunkStatus(var0, var1, var2, var3, var4, var5, var6));
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

   private static boolean isLighted(ChunkStatus var0, ChunkAccess var1) {
      return var1.getStatus().isOrAfter(var0) && var1.isLightCorrect();
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
      String var1,
      @Nullable ChunkStatus var2,
      int var3,
      EnumSet<Heightmap.Types> var4,
      ChunkStatus.ChunkType var5,
      ChunkStatus.GenerationTask var6,
      ChunkStatus.LoadingTask var7
   ) {
      super();
      this.name = var1;
      this.parent = var2 == null ? this : var2;
      this.generationTask = var6;
      this.loadingTask = var7;
      this.range = var3;
      this.chunkType = var5;
      this.heightmapsAfter = var4;
      this.index = var2 == null ? 0 : var2.getIndex() + 1;
   }

   public int getIndex() {
      return this.index;
   }

   public String getName() {
      return this.name;
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
      List<ChunkAccess> var7,
      boolean var8
   ) {
      ChunkAccess var9 = (ChunkAccess)var7.get(var7.size() / 2);
      ProfiledDuration var10 = JvmProfiler.INSTANCE.onChunkGenerate(var9.getPos(), var2.dimension(), this.name);
      CompletableFuture var11 = this.generationTask.doWork(this, var1, var2, var3, var4, var5, var6, var7, var9, var8);
      return var10 != null ? var11.thenApply(var1x -> {
         var10.finish();
         return var1x;
      }) : var11;
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

   public ChunkStatus.ChunkType getChunkType() {
      return this.chunkType;
   }

   public static ChunkStatus byName(String var0) {
      return Registry.CHUNK_STATUS.get(ResourceLocation.tryParse(var0));
   }

   public EnumSet<Heightmap.Types> heightmapsAfter() {
      return this.heightmapsAfter;
   }

   public boolean isOrAfter(ChunkStatus var1) {
      return this.getIndex() >= var1.getIndex();
   }

   @Override
   public String toString() {
      return Registry.CHUNK_STATUS.getKey(this).toString();
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
         ChunkAccess var9,
         boolean var10
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
      // $QF: Could not properly define all variable types!
      // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
         ChunkAccess var9,
         boolean var10
      ) {
         if (var10 || !var9.getStatus().isOrAfter(var1)) {
            this.doWork(var1, var3, var4, var8, var9);
            if (var9 instanceof ProtoChunk var11) {
               var11.setStatus(var1);
            }
         }

         return CompletableFuture.completedFuture(Either.left(var9));
      }

      void doWork(ChunkStatus var1, ServerLevel var2, ChunkGenerator var3, List<ChunkAccess> var4, ChunkAccess var5);
   }
}
