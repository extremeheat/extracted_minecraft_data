package net.minecraft.world.level.chunk.status;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public class ChunkStatus {
   public static final int MAX_STRUCTURE_DISTANCE = 8;
   private static final EnumSet<Heightmap.Types> PRE_FEATURES;
   public static final EnumSet<Heightmap.Types> POST_FEATURES;
   public static final ChunkStatus EMPTY;
   public static final ChunkStatus STRUCTURE_STARTS;
   public static final ChunkStatus STRUCTURE_REFERENCES;
   public static final ChunkStatus BIOMES;
   public static final ChunkStatus NOISE;
   public static final ChunkStatus SURFACE;
   public static final ChunkStatus CARVERS;
   public static final ChunkStatus FEATURES;
   public static final ChunkStatus INITIALIZE_LIGHT;
   public static final ChunkStatus LIGHT;
   public static final ChunkStatus SPAWN;
   public static final ChunkStatus FULL;
   private static final List<ChunkStatus> STATUS_BY_RANGE;
   private static final IntList RANGE_BY_STATUS;
   private final int index;
   private final ChunkStatus parent;
   private final GenerationTask generationTask;
   private final LoadingTask loadingTask;
   private final int range;
   private final boolean hasLoadDependencies;
   private final ChunkType chunkType;
   private final EnumSet<Heightmap.Types> heightmapsAfter;

   private static ChunkStatus register(String var0, @Nullable ChunkStatus var1, int var2, boolean var3, EnumSet<Heightmap.Types> var4, ChunkType var5, GenerationTask var6, LoadingTask var7) {
      return (ChunkStatus)Registry.register(BuiltInRegistries.CHUNK_STATUS, (String)var0, new ChunkStatus(var1, var2, var3, var4, var5, var6, var7));
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

   public static ChunkStatus getStatusAroundFullChunk(int var0) {
      if (var0 >= STATUS_BY_RANGE.size()) {
         return EMPTY;
      } else {
         return var0 < 0 ? FULL : (ChunkStatus)STATUS_BY_RANGE.get(var0);
      }
   }

   public static int maxDistance() {
      return STATUS_BY_RANGE.size();
   }

   public static int getDistance(ChunkStatus var0) {
      return RANGE_BY_STATUS.getInt(var0.getIndex());
   }

   ChunkStatus(@Nullable ChunkStatus var1, int var2, boolean var3, EnumSet<Heightmap.Types> var4, ChunkType var5, GenerationTask var6, LoadingTask var7) {
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

   public CompletableFuture<ChunkAccess> generate(WorldGenContext var1, Executor var2, ToFullChunk var3, List<ChunkAccess> var4) {
      ChunkAccess var5 = (ChunkAccess)var4.get(var4.size() / 2);
      ProfiledDuration var6 = JvmProfiler.INSTANCE.onChunkGenerate(var5.getPos(), var1.level().dimension(), this.toString());
      return this.generationTask.doWork(var1, this, var2, var3, var4, var5).thenApply((var2x) -> {
         if (var2x instanceof ProtoChunk var3) {
            if (!var3.getStatus().isOrAfter(this)) {
               var3.setStatus(this);
            }
         }

         if (var6 != null) {
            var6.finish();
         }

         return var2x;
      });
   }

   public CompletableFuture<ChunkAccess> load(WorldGenContext var1, ToFullChunk var2, ChunkAccess var3) {
      return this.loadingTask.doWork(var1, this, var2, var3);
   }

   public int getRange() {
      return this.range;
   }

   public boolean hasLoadDependencies() {
      return this.hasLoadDependencies;
   }

   public ChunkType getChunkType() {
      return this.chunkType;
   }

   public static ChunkStatus byName(String var0) {
      return (ChunkStatus)BuiltInRegistries.CHUNK_STATUS.get(ResourceLocation.tryParse(var0));
   }

   public EnumSet<Heightmap.Types> heightmapsAfter() {
      return this.heightmapsAfter;
   }

   public boolean isOrAfter(ChunkStatus var1) {
      return this.getIndex() >= var1.getIndex();
   }

   public String toString() {
      return BuiltInRegistries.CHUNK_STATUS.getKey(this).toString();
   }

   static {
      PRE_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG);
      POST_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
      EMPTY = register("empty", (ChunkStatus)null, -1, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateEmpty, ChunkStatusTasks::loadPassThrough);
      STRUCTURE_STARTS = register("structure_starts", EMPTY, 0, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateStructureStarts, ChunkStatusTasks::loadStructureStarts);
      STRUCTURE_REFERENCES = register("structure_references", STRUCTURE_STARTS, 8, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateStructureReferences, ChunkStatusTasks::loadPassThrough);
      BIOMES = register("biomes", STRUCTURE_REFERENCES, 8, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateBiomes, ChunkStatusTasks::loadPassThrough);
      NOISE = register("noise", BIOMES, 8, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateNoise, ChunkStatusTasks::loadPassThrough);
      SURFACE = register("surface", NOISE, 8, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateSurface, ChunkStatusTasks::loadPassThrough);
      CARVERS = register("carvers", SURFACE, 8, false, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateCarvers, ChunkStatusTasks::loadPassThrough);
      FEATURES = register("features", CARVERS, 8, false, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateFeatures, ChunkStatusTasks::loadPassThrough);
      INITIALIZE_LIGHT = register("initialize_light", FEATURES, 0, false, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateInitializeLight, ChunkStatusTasks::loadInitializeLight);
      LIGHT = register("light", INITIALIZE_LIGHT, 1, true, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateLight, ChunkStatusTasks::loadLight);
      SPAWN = register("spawn", LIGHT, 1, false, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateSpawn, ChunkStatusTasks::loadPassThrough);
      FULL = register("full", SPAWN, 0, false, POST_FEATURES, ChunkType.LEVELCHUNK, ChunkStatusTasks::generateFull, ChunkStatusTasks::loadFull);
      STATUS_BY_RANGE = ImmutableList.of(FULL, INITIALIZE_LIGHT, CARVERS, BIOMES, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, new ChunkStatus[0]);
      RANGE_BY_STATUS = (IntList)Util.make(new IntArrayList(getStatusList().size()), (var0) -> {
         int var1 = 0;

         for(int var2 = getStatusList().size() - 1; var2 >= 0; --var2) {
            while(var1 + 1 < STATUS_BY_RANGE.size() && var2 <= ((ChunkStatus)STATUS_BY_RANGE.get(var1 + 1)).getIndex()) {
               ++var1;
            }

            var0.add(0, var1);
         }

      });
   }

   @FunctionalInterface
   protected interface GenerationTask {
      CompletableFuture<ChunkAccess> doWork(WorldGenContext var1, ChunkStatus var2, Executor var3, ToFullChunk var4, List<ChunkAccess> var5, ChunkAccess var6);
   }

   @FunctionalInterface
   protected interface LoadingTask {
      CompletableFuture<ChunkAccess> doWork(WorldGenContext var1, ChunkStatus var2, ToFullChunk var3, ChunkAccess var4);
   }
}
