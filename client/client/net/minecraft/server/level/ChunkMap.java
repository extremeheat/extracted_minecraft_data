package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public class ChunkMap extends ChunkStorage implements ChunkHolder.PlayerProvider {
   private static final byte CHUNK_TYPE_REPLACEABLE = -1;
   private static final byte CHUNK_TYPE_UNKNOWN = 0;
   private static final byte CHUNK_TYPE_FULL = 1;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CHUNK_SAVED_PER_TICK = 200;
   private static final int CHUNK_SAVED_EAGERLY_PER_TICK = 20;
   private static final int EAGER_CHUNK_SAVE_COOLDOWN_IN_MILLIS = 10000;
   public static final int MIN_VIEW_DISTANCE = 2;
   public static final int MAX_VIEW_DISTANCE = 32;
   public static final int FORCED_TICKET_LEVEL = ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING);
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap = new Long2ObjectLinkedOpenHashMap();
   private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap = this.updatingChunkMap.clone();
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads = new Long2ObjectLinkedOpenHashMap();
   private final LongSet entitiesInLevel = new LongOpenHashSet();
   final ServerLevel level;
   private final ThreadedLevelLightEngine lightEngine;
   private final BlockableEventLoop<Runnable> mainThreadExecutor;
   private ChunkGenerator generator;
   private final RandomState randomState;
   private final ChunkGeneratorStructureState chunkGeneratorState;
   private final Supplier<DimensionDataStorage> overworldDataStorage;
   private final PoiManager poiManager;
   final LongSet toDrop = new LongOpenHashSet();
   private boolean modified;
   private final ChunkTaskPriorityQueueSorter queueSorter;
   private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> worldgenMailbox;
   private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> mainThreadMailbox;
   private final ChunkProgressListener progressListener;
   private final ChunkStatusUpdateListener chunkStatusListener;
   private final ChunkMap.DistanceManager distanceManager;
   private final AtomicInteger tickingGenerated = new AtomicInteger();
   private final String storageName;
   private final PlayerMap playerMap = new PlayerMap();
   private final Int2ObjectMap<ChunkMap.TrackedEntity> entityMap = new Int2ObjectOpenHashMap();
   private final Long2ByteMap chunkTypeCache = new Long2ByteOpenHashMap();
   private final Long2LongMap chunkSaveCooldowns = new Long2LongOpenHashMap();
   private final Queue<Runnable> unloadQueue = Queues.newConcurrentLinkedQueue();
   private int serverViewDistance;
   private WorldGenContext worldGenContext;

   public ChunkMap(
      ServerLevel var1,
      LevelStorageSource.LevelStorageAccess var2,
      DataFixer var3,
      StructureTemplateManager var4,
      Executor var5,
      BlockableEventLoop<Runnable> var6,
      LightChunkGetter var7,
      ChunkGenerator var8,
      ChunkProgressListener var9,
      ChunkStatusUpdateListener var10,
      Supplier<DimensionDataStorage> var11,
      int var12,
      boolean var13
   ) {
      super(new RegionStorageInfo(var2.getLevelId(), var1.dimension(), "chunk"), var2.getDimensionPath(var1.dimension()).resolve("region"), var3, var13);
      Path var14 = var2.getDimensionPath(var1.dimension());
      this.storageName = var14.getFileName().toString();
      this.level = var1;
      this.generator = var8;
      RegistryAccess var15 = var1.registryAccess();
      long var16 = var1.getSeed();
      if (var8 instanceof NoiseBasedChunkGenerator var18) {
         this.randomState = RandomState.create(var18.generatorSettings().value(), var15.lookupOrThrow(Registries.NOISE), var16);
      } else {
         this.randomState = RandomState.create(NoiseGeneratorSettings.dummy(), var15.lookupOrThrow(Registries.NOISE), var16);
      }

      this.chunkGeneratorState = var8.createState(var15.lookupOrThrow(Registries.STRUCTURE_SET), this.randomState, var16);
      this.mainThreadExecutor = var6;
      ProcessorMailbox var21 = ProcessorMailbox.create(var5, "worldgen");
      ProcessorHandle var19 = ProcessorHandle.of("main", var6::tell);
      this.progressListener = var9;
      this.chunkStatusListener = var10;
      ProcessorMailbox var20 = ProcessorMailbox.create(var5, "light");
      this.queueSorter = new ChunkTaskPriorityQueueSorter(ImmutableList.of(var21, var19, var20), var5, 2147483647);
      this.worldgenMailbox = this.queueSorter.getProcessor(var21, false);
      this.mainThreadMailbox = this.queueSorter.getProcessor(var19, false);
      this.lightEngine = new ThreadedLevelLightEngine(var7, this, this.level.dimensionType().hasSkyLight(), var20, this.queueSorter.getProcessor(var20, false));
      this.distanceManager = new ChunkMap.DistanceManager(var5, var6);
      this.overworldDataStorage = var11;
      this.poiManager = new PoiManager(new RegionStorageInfo(var2.getLevelId(), var1.dimension(), "poi"), var14.resolve("poi"), var3, var13, var15, var1);
      this.setServerViewDistance(var12);
      this.worldGenContext = new WorldGenContext(var1, var8, var4, this.lightEngine);
   }

   protected ChunkGenerator generator() {
      return this.generator;
   }

   protected ChunkGeneratorStructureState generatorState() {
      return this.chunkGeneratorState;
   }

   protected RandomState randomState() {
      return this.randomState;
   }

   public void debugReloadGenerator() {
      DataResult var1 = ChunkGenerator.CODEC.encodeStart(JsonOps.INSTANCE, this.generator);
      DataResult var2 = var1.flatMap(var0 -> ChunkGenerator.CODEC.parse(JsonOps.INSTANCE, var0));
      var2.ifSuccess(
         var1x -> {
            this.generator = var1x;
            this.worldGenContext = new WorldGenContext(
               this.worldGenContext.level(), var1x, this.worldGenContext.structureManager(), this.worldGenContext.lightEngine()
            );
         }
      );
   }

   private static double euclideanDistanceSquared(ChunkPos var0, Entity var1) {
      double var2 = (double)SectionPos.sectionToBlockCoord(var0.x, 8);
      double var4 = (double)SectionPos.sectionToBlockCoord(var0.z, 8);
      double var6 = var2 - var1.getX();
      double var8 = var4 - var1.getZ();
      return var6 * var6 + var8 * var8;
   }

   boolean isChunkTracked(ServerPlayer var1, int var2, int var3) {
      return var1.getChunkTrackingView().contains(var2, var3) && !var1.connection.chunkSender.isPending(ChunkPos.asLong(var2, var3));
   }

   private boolean isChunkOnTrackedBorder(ServerPlayer var1, int var2, int var3) {
      if (!this.isChunkTracked(var1, var2, var3)) {
         return false;
      } else {
         for (int var4 = -1; var4 <= 1; var4++) {
            for (int var5 = -1; var5 <= 1; var5++) {
               if ((var4 != 0 || var5 != 0) && !this.isChunkTracked(var1, var2 + var4, var3 + var5)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected ThreadedLevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   @Nullable
   protected ChunkHolder getUpdatingChunkIfPresent(long var1) {
      return (ChunkHolder)this.updatingChunkMap.get(var1);
   }

   @Nullable
   protected ChunkHolder getVisibleChunkIfPresent(long var1) {
      return (ChunkHolder)this.visibleChunkMap.get(var1);
   }

   protected IntSupplier getChunkQueueLevel(long var1) {
      return () -> {
         ChunkHolder var3 = this.getVisibleChunkIfPresent(var1);
         return var3 == null
            ? ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1
            : Math.min(var3.getQueueLevel(), ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1);
      };
   }

   public String getChunkDebugData(ChunkPos var1) {
      ChunkHolder var2 = this.getVisibleChunkIfPresent(var1.toLong());
      if (var2 == null) {
         return "null";
      } else {
         String var3 = var2.getTicketLevel() + "\n";
         ChunkStatus var4 = var2.getLastAvailableStatus();
         ChunkAccess var5 = var2.getLastAvailable();
         if (var4 != null) {
            var3 = var3 + "St: \u00a7" + var4.getIndex() + var4 + "\u00a7r\n";
         }

         if (var5 != null) {
            var3 = var3 + "Ch: \u00a7" + var5.getStatus().getIndex() + var5.getStatus() + "\u00a7r\n";
         }

         FullChunkStatus var6 = var2.getFullStatus();
         var3 = var3 + '\u00a7' + var6.ordinal() + var6;
         return var3 + "\u00a7r";
      }
   }

   private CompletableFuture<ChunkResult<List<ChunkAccess>>> getChunkRangeFuture(ChunkHolder var1, int var2, IntFunction<ChunkStatus> var3) {
      if (var2 == 0) {
         ChunkStatus var18 = (ChunkStatus)var3.apply(0);
         return var1.getOrScheduleFuture(var18, this).thenApply(var0 -> var0.map(List::of));
      } else {
         ArrayList var4 = new ArrayList();
         ArrayList var5 = new ArrayList();
         ChunkPos var6 = var1.getPos();
         int var7 = var6.x;
         int var8 = var6.z;

         for (int var9 = -var2; var9 <= var2; var9++) {
            for (int var10 = -var2; var10 <= var2; var10++) {
               int var11 = Math.max(Math.abs(var10), Math.abs(var9));
               ChunkPos var12 = new ChunkPos(var7 + var10, var8 + var9);
               long var13 = var12.toLong();
               ChunkHolder var15 = this.getUpdatingChunkIfPresent(var13);
               if (var15 == null) {
                  return CompletableFuture.completedFuture(ChunkResult.error(() -> "Unloaded " + var12));
               }

               ChunkStatus var16 = (ChunkStatus)var3.apply(var11);
               CompletableFuture var17 = var15.getOrScheduleFuture(var16, this);
               var5.add(var15);
               var4.add(var17);
            }
         }

         CompletableFuture var19 = Util.sequence(var4);
         CompletableFuture var20 = var19.thenApply(
            var4x -> {
               ArrayList var5x = Lists.newArrayList();
               int var6x = 0;
   
               for (ChunkResult var8x : var4x) {
                  if (var8x == null) {
                     throw this.debugFuturesAndCreateReportedException(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
                  }
   
                  ChunkAccess var9x = (ChunkAccess)var8x.orElse(null);
                  if (var9x == null) {
                     int var10x = var6x;
                     return ChunkResult.error(
                        () -> "Unloaded " + new ChunkPos(var7 + var10x % (var2 * 2 + 1), var8 + var10x / (var2 * 2 + 1)) + " " + var8x.getError()
                     );
                  }
   
                  var5x.add(var9x);
                  var6x++;
               }
   
               return ChunkResult.of(var5x);
            }
         );

         for (ChunkHolder var22 : var5) {
            var22.addSaveDependency("getChunkRangeFuture " + var6 + " " + var2, var20);
         }

         return var20;
      }
   }

   public ReportedException debugFuturesAndCreateReportedException(IllegalStateException var1, String var2) {
      StringBuilder var3 = new StringBuilder();
      Consumer var4 = var1x -> var1x.getAllFutures().forEach(var2x -> {
            ChunkStatus var3x = (ChunkStatus)var2x.getFirst();
            CompletableFuture var4x = (CompletableFuture)var2x.getSecond();
            if (var4x != null && var4x.isDone() && var4x.join() == null) {
               var3.append(var1x.getPos()).append(" - status: ").append(var3x).append(" future: ").append(var4x).append(System.lineSeparator());
            }
         });
      var3.append("Updating:").append(System.lineSeparator());
      this.updatingChunkMap.values().forEach(var4);
      var3.append("Visible:").append(System.lineSeparator());
      this.visibleChunkMap.values().forEach(var4);
      CrashReport var5 = CrashReport.forThrowable(var1, "Chunk loading");
      CrashReportCategory var6 = var5.addCategory("Chunk loading");
      var6.setDetail("Details", var2);
      var6.setDetail("Futures", var3);
      return new ReportedException(var5);
   }

   public CompletableFuture<ChunkResult<LevelChunk>> prepareEntityTickingChunk(ChunkHolder var1) {
      return this.getChunkRangeFuture(var1, 2, var0 -> ChunkStatus.FULL)
         .thenApplyAsync(var0 -> var0.map(var0x -> (LevelChunk)var0x.get(var0x.size() / 2)), this.mainThreadExecutor);
   }

   @Nullable
   ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5) {
      if (!ChunkLevel.isLoaded(var5) && !ChunkLevel.isLoaded(var3)) {
         return var4;
      } else {
         if (var4 != null) {
            var4.setTicketLevel(var3);
         }

         if (var4 != null) {
            if (!ChunkLevel.isLoaded(var3)) {
               this.toDrop.add(var1);
            } else {
               this.toDrop.remove(var1);
            }
         }

         if (ChunkLevel.isLoaded(var3) && var4 == null) {
            var4 = (ChunkHolder)this.pendingUnloads.remove(var1);
            if (var4 != null) {
               var4.setTicketLevel(var3);
            } else {
               var4 = new ChunkHolder(new ChunkPos(var1), var3, this.level, this.lightEngine, this.queueSorter, this);
            }

            this.updatingChunkMap.put(var1, var4);
            this.modified = true;
         }

         return var4;
      }
   }

   @Override
   public void close() throws IOException {
      try {
         this.queueSorter.close();
         this.poiManager.close();
      } finally {
         super.close();
      }
   }

   protected void saveAllChunks(boolean var1) {
      if (var1) {
         List var2 = this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).peek(ChunkHolder::refreshAccessibility).toList();
         MutableBoolean var3 = new MutableBoolean();

         do {
            var3.setFalse();
            var2.stream().map(var1x -> {
               CompletableFuture var2x;
               do {
                  var2x = var1x.getChunkToSave();
                  this.mainThreadExecutor.managedBlock(var2x::isDone);
               } while (var2x != var1x.getChunkToSave());

               return (ChunkAccess)var2x.join();
            }).filter(var0 -> var0 instanceof ImposterProtoChunk || var0 instanceof LevelChunk).filter(this::save).forEach(var1x -> var3.setTrue());
         } while (var3.isTrue());

         this.processUnloads(() -> true);
         this.flushWorker();
      } else {
         this.visibleChunkMap.values().forEach(this::saveChunkIfNeeded);
      }
   }

   protected void tick(BooleanSupplier var1) {
      ProfilerFiller var2 = this.level.getProfiler();
      var2.push("poi");
      this.poiManager.tick(var1);
      var2.popPush("chunk_unload");
      if (!this.level.noSave()) {
         this.processUnloads(var1);
      }

      var2.pop();
   }

   public boolean hasWork() {
      return this.lightEngine.hasLightWork()
         || !this.pendingUnloads.isEmpty()
         || !this.updatingChunkMap.isEmpty()
         || this.poiManager.hasWork()
         || !this.toDrop.isEmpty()
         || !this.unloadQueue.isEmpty()
         || this.queueSorter.hasWork()
         || this.distanceManager.hasTickets();
   }

   private void processUnloads(BooleanSupplier var1) {
      LongIterator var2 = this.toDrop.iterator();

      for (int var3 = 0; var2.hasNext() && (var1.getAsBoolean() || var3 < 200 || this.toDrop.size() > 2000); var2.remove()) {
         long var4 = var2.nextLong();
         ChunkHolder var6 = (ChunkHolder)this.updatingChunkMap.remove(var4);
         if (var6 != null) {
            this.pendingUnloads.put(var4, var6);
            this.modified = true;
            var3++;
            this.scheduleUnload(var4, var6);
         }
      }

      int var5 = Math.max(0, this.unloadQueue.size() - 2000);

      Runnable var8;
      while ((var1.getAsBoolean() || var5 > 0) && (var8 = this.unloadQueue.poll()) != null) {
         var5--;
         var8.run();
      }

      int var9 = 0;
      ObjectIterator var7 = this.visibleChunkMap.values().iterator();

      while (var9 < 20 && var1.getAsBoolean() && var7.hasNext()) {
         if (this.saveChunkIfNeeded((ChunkHolder)var7.next())) {
            var9++;
         }
      }
   }

   private void scheduleUnload(long var1, ChunkHolder var3) {
      CompletableFuture var4 = var3.getChunkToSave();
      var4.thenAcceptAsync(var5 -> {
         CompletableFuture var6 = var3.getChunkToSave();
         if (var6 != var4) {
            this.scheduleUnload(var1, var3);
         } else {
            if (this.pendingUnloads.remove(var1, var3) && var5 != null) {
               if (var5 instanceof LevelChunk) {
                  ((LevelChunk)var5).setLoaded(false);
               }

               this.save(var5);
               if (this.entitiesInLevel.remove(var1) && var5 instanceof LevelChunk var7) {
                  this.level.unload(var7);
               }

               this.lightEngine.updateChunkStatus(var5.getPos());
               this.lightEngine.tryScheduleUpdate();
               this.progressListener.onStatusChange(var5.getPos(), null);
               this.chunkSaveCooldowns.remove(var5.getPos().toLong());
            }
         }
      }, this.unloadQueue::add).whenComplete((var1x, var2) -> {
         if (var2 != null) {
            LOGGER.error("Failed to save chunk {}", var3.getPos(), var2);
         }
      });
   }

   protected boolean promoteChunkMap() {
      if (!this.modified) {
         return false;
      } else {
         this.visibleChunkMap = this.updatingChunkMap.clone();
         this.modified = false;
         return true;
      }
   }

   public CompletableFuture<ChunkResult<ChunkAccess>> schedule(ChunkHolder var1, ChunkStatus var2) {
      ChunkPos var3 = var1.getPos();
      if (var2 == ChunkStatus.EMPTY) {
         return this.scheduleChunkLoad(var3).thenApply(ChunkResult::of);
      } else {
         if (var2 == ChunkStatus.LIGHT) {
            this.distanceManager.addTicket(TicketType.LIGHT, var3, ChunkLevel.byStatus(ChunkStatus.LIGHT), var3);
         }

         if (!var2.hasLoadDependencies()) {
            ChunkAccess var4 = var1.getOrScheduleFuture(var2.getParent(), this).getNow(ChunkHolder.UNLOADED_CHUNK).orElse(null);
            if (var4 != null && var4.getStatus().isOrAfter(var2)) {
               CompletableFuture var5 = var2.load(this.worldGenContext, var2x -> this.protoChunkToFullChunk(var1, var2x), var4);
               this.progressListener.onStatusChange(var3, var2);
               return var5.thenApply(ChunkResult::of);
            }
         }

         return this.scheduleChunkGeneration(var1, var2);
      }
   }

   private CompletableFuture<ChunkAccess> scheduleChunkLoad(ChunkPos var1) {
      return this.readChunk(var1).thenApply(var1x -> var1x.filter(var1xx -> {
            boolean var2 = isChunkDataValid(var1xx);
            if (!var2) {
               LOGGER.error("Chunk file at {} is missing level data, skipping", var1);
            }

            return var2;
         })).thenApplyAsync(var2 -> {
         this.level.getProfiler().incrementCounter("chunkLoad");
         if (var2.isPresent()) {
            ProtoChunk var3 = ChunkSerializer.read(this.level, this.poiManager, var1, var2.get());
            this.markPosition(var1, var3.getStatus().getChunkType());
            return var3;
         } else {
            return this.createEmptyChunk(var1);
         }
      }, this.mainThreadExecutor).exceptionallyAsync(var2 -> this.handleChunkLoadFailure(var2, var1), this.mainThreadExecutor);
   }

   private static boolean isChunkDataValid(CompoundTag var0) {
      return var0.contains("Status", 8);
   }

   private ChunkAccess handleChunkLoadFailure(Throwable var1, ChunkPos var2) {
      Throwable var3 = var1 instanceof CompletionException var4 ? var4.getCause() : var1;
      Throwable var9 = var3 instanceof ReportedException var5 ? var5.getCause() : var3;
      boolean var10 = var9 instanceof Error;
      boolean var6 = var9 instanceof IOException || var9 instanceof ChunkSerializer.ChunkReadException;
      if (!var10 && var6) {
         LOGGER.error("Couldn't load chunk {}", var2, var9);
         this.level.getServer().reportChunkLoadFailure(var2);
         return this.createEmptyChunk(var2);
      } else {
         CrashReport var7 = CrashReport.forThrowable(var1, "Exception loading chunk");
         CrashReportCategory var8 = var7.addCategory("Chunk being loaded");
         var8.setDetail("pos", var2);
         this.markPositionReplaceable(var2);
         throw new ReportedException(var7);
      }
   }

   private ChunkAccess createEmptyChunk(ChunkPos var1) {
      this.markPositionReplaceable(var1);
      return new ProtoChunk(var1, UpgradeData.EMPTY, this.level, this.level.registryAccess().registryOrThrow(Registries.BIOME), null);
   }

   private void markPositionReplaceable(ChunkPos var1) {
      this.chunkTypeCache.put(var1.toLong(), (byte)-1);
   }

   private byte markPosition(ChunkPos var1, ChunkType var2) {
      return this.chunkTypeCache.put(var1.toLong(), (byte)(var2 == ChunkType.PROTOCHUNK ? -1 : 1));
   }

   private CompletableFuture<ChunkResult<ChunkAccess>> scheduleChunkGeneration(ChunkHolder var1, ChunkStatus var2) {
      ChunkPos var3 = var1.getPos();
      CompletableFuture var4 = this.getChunkRangeFuture(var1, var2.getRange(), var2x -> this.getDependencyStatus(var2, var2x));
      this.level.getProfiler().incrementCounter(() -> "chunkGenerate " + var2);
      Executor var5 = var2x -> this.worldgenMailbox.tell(ChunkTaskPriorityQueueSorter.message(var1, var2x));
      return var4.thenComposeAsync(var5x -> {
         List var6 = (List)var5x.orElse(null);
         if (var6 == null) {
            this.releaseLightTicket(var3);
            return CompletableFuture.completedFuture(ChunkResult.error(var5x::getError));
         } else {
            try {
               ChunkAccess var7 = (ChunkAccess)var6.get(var6.size() / 2);
               CompletableFuture var11;
               if (var7.getStatus().isOrAfter(var2)) {
                  var11 = var2.load(this.worldGenContext, var2xx -> this.protoChunkToFullChunk(var1, var2xx), var7);
               } else {
                  var11 = var2.generate(this.worldGenContext, var5, var2xx -> this.protoChunkToFullChunk(var1, var2xx), var6);
               }

               this.progressListener.onStatusChange(var3, var2);
               return var11.thenApply(ChunkResult::of);
            } catch (Exception var10) {
               var10.getStackTrace();
               CrashReport var8 = CrashReport.forThrowable(var10, "Exception generating new chunk");
               CrashReportCategory var9 = var8.addCategory("Chunk to be generated");
               var9.setDetail("Status being generated", () -> BuiltInRegistries.CHUNK_STATUS.getKey(var2).toString());
               var9.setDetail("Location", String.format(Locale.ROOT, "%d,%d", var3.x, var3.z));
               var9.setDetail("Position hash", ChunkPos.asLong(var3.x, var3.z));
               var9.setDetail("Generator", this.generator);
               this.mainThreadExecutor.execute(() -> {
                  throw new ReportedException(var8);
               });
               throw new ReportedException(var8);
            }
         }
      }, var5);
   }

   protected void releaseLightTicket(ChunkPos var1) {
      this.mainThreadExecutor
         .tell(
            Util.name(
               () -> this.distanceManager.removeTicket(TicketType.LIGHT, var1, ChunkLevel.byStatus(ChunkStatus.LIGHT), var1),
               () -> "release light ticket " + var1
            )
         );
   }

   private ChunkStatus getDependencyStatus(ChunkStatus var1, int var2) {
      ChunkStatus var3;
      if (var2 == 0) {
         var3 = var1.getParent();
      } else {
         var3 = ChunkStatus.getStatusAroundFullChunk(ChunkStatus.getDistance(var1) + var2);
      }

      return var3;
   }

   private static void postLoadProtoChunk(ServerLevel var0, List<CompoundTag> var1) {
      if (!var1.isEmpty()) {
         var0.addWorldGenChunkEntities(EntityType.loadEntitiesRecursive(var1, var0));
      }
   }

   private CompletableFuture<ChunkAccess> protoChunkToFullChunk(ChunkHolder var1, ChunkAccess var2) {
      return CompletableFuture.supplyAsync(() -> {
         ChunkPos var3 = var1.getPos();
         ProtoChunk var4 = (ProtoChunk)var2;
         LevelChunk var5;
         if (var4 instanceof ImposterProtoChunk) {
            var5 = ((ImposterProtoChunk)var4).getWrapped();
         } else {
            var5 = new LevelChunk(this.level, var4, var2xx -> postLoadProtoChunk(this.level, var4.getEntities()));
            var1.replaceProtoChunk(new ImposterProtoChunk(var5, false));
         }

         var5.setFullStatus(() -> ChunkLevel.fullStatus(var1.getTicketLevel()));
         var5.runPostLoad();
         if (this.entitiesInLevel.add(var3.toLong())) {
            var5.setLoaded(true);
            var5.registerAllBlockEntitiesAfterLevelLoad();
            var5.registerTickContainerInLevel(this.level);
         }

         return var5;
      }, var2x -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(var2x, var1.getPos().toLong(), var1::getTicketLevel)));
   }

   public CompletableFuture<ChunkResult<LevelChunk>> prepareTickingChunk(ChunkHolder var1) {
      CompletableFuture var2 = this.getChunkRangeFuture(var1, 1, var0 -> ChunkStatus.FULL);
      CompletableFuture var3 = var2.<ChunkResult<LevelChunk>>thenApplyAsync(
            var0 -> var0.map(var0x -> (LevelChunk)var0x.get(var0x.size() / 2)),
            var2x -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(var1, var2x))
         )
         .thenApplyAsync(var2x -> var2x.ifSuccess(var2xx -> {
               var2xx.postProcessGeneration();
               this.level.startTickingChunk(var2xx);
               CompletableFuture var3x = var1.getChunkSendSyncFuture();
               if (var3x.isDone()) {
                  this.onChunkReadyToSend(var2xx);
               } else {
                  var3x.thenAcceptAsync(var2xxx -> this.onChunkReadyToSend(var2xx), this.mainThreadExecutor);
               }
            }), this.mainThreadExecutor);
      var3.handle((var1x, var2x) -> {
         this.tickingGenerated.getAndIncrement();
         return null;
      });
      return var3;
   }

   private void onChunkReadyToSend(LevelChunk var1) {
      ChunkPos var2 = var1.getPos();

      for (ServerPlayer var4 : this.playerMap.getAllPlayers()) {
         if (var4.getChunkTrackingView().contains(var2)) {
            markChunkPendingToSend(var4, var1);
         }
      }
   }

   public CompletableFuture<ChunkResult<LevelChunk>> prepareAccessibleChunk(ChunkHolder var1) {
      return this.getChunkRangeFuture(var1, 1, ChunkStatus::getStatusAroundFullChunk)
         .thenApplyAsync(
            var0 -> var0.map(var0x -> (LevelChunk)var0x.get(var0x.size() / 2)),
            var2 -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(var1, var2))
         );
   }

   public int getTickingGenerated() {
      return this.tickingGenerated.get();
   }

   private boolean saveChunkIfNeeded(ChunkHolder var1) {
      if (!var1.wasAccessibleSinceLastSave()) {
         return false;
      } else {
         ChunkAccess var2 = var1.getChunkToSave().getNow(null);
         if (!(var2 instanceof ImposterProtoChunk) && !(var2 instanceof LevelChunk)) {
            return false;
         } else {
            long var3 = var2.getPos().toLong();
            long var5 = this.chunkSaveCooldowns.getOrDefault(var3, -1L);
            long var7 = System.currentTimeMillis();
            if (var7 < var5) {
               return false;
            } else {
               boolean var9 = this.save(var2);
               var1.refreshAccessibility();
               if (var9) {
                  this.chunkSaveCooldowns.put(var3, var7 + 10000L);
               }

               return var9;
            }
         }
      }
   }

   private boolean save(ChunkAccess var1) {
      this.poiManager.flush(var1.getPos());
      if (!var1.isUnsaved()) {
         return false;
      } else {
         var1.setUnsaved(false);
         ChunkPos var2 = var1.getPos();

         try {
            ChunkStatus var3 = var1.getStatus();
            if (var3.getChunkType() != ChunkType.LEVELCHUNK) {
               if (this.isExistingChunkFull(var2)) {
                  return false;
               }

               if (var3 == ChunkStatus.EMPTY && var1.getAllStarts().values().stream().noneMatch(StructureStart::isValid)) {
                  return false;
               }
            }

            this.level.getProfiler().incrementCounter("chunkSave");
            CompoundTag var4 = ChunkSerializer.write(this.level, var1);
            this.write(var2, var4).exceptionallyAsync(var2x -> {
               this.level.getServer().reportChunkSaveFailure(var2);
               return null;
            }, this.mainThreadExecutor);
            this.markPosition(var2, var3.getChunkType());
            return true;
         } catch (Exception var5) {
            LOGGER.error("Failed to save chunk {},{}", new Object[]{var2.x, var2.z, var5});
            this.level.getServer().reportChunkSaveFailure(var2);
            return false;
         }
      }
   }

   private boolean isExistingChunkFull(ChunkPos var1) {
      byte var2 = this.chunkTypeCache.get(var1.toLong());
      if (var2 != 0) {
         return var2 == 1;
      } else {
         CompoundTag var3;
         try {
            var3 = this.readChunk(var1).join().orElse(null);
            if (var3 == null) {
               this.markPositionReplaceable(var1);
               return false;
            }
         } catch (Exception var5) {
            LOGGER.error("Failed to read chunk {}", var1, var5);
            this.markPositionReplaceable(var1);
            return false;
         }

         ChunkType var4 = ChunkSerializer.getChunkTypeFromTag(var3);
         return this.markPosition(var1, var4) == 1;
      }
   }

   protected void setServerViewDistance(int var1) {
      int var2 = Mth.clamp(var1, 2, 32);
      if (var2 != this.serverViewDistance) {
         this.serverViewDistance = var2;
         this.distanceManager.updatePlayerTickets(this.serverViewDistance);

         for (ServerPlayer var4 : this.playerMap.getAllPlayers()) {
            this.updateChunkTracking(var4);
         }
      }
   }

   int getPlayerViewDistance(ServerPlayer var1) {
      return Mth.clamp(var1.requestedViewDistance(), 2, this.serverViewDistance);
   }

   private void markChunkPendingToSend(ServerPlayer var1, ChunkPos var2) {
      LevelChunk var3 = this.getChunkToSend(var2.toLong());
      if (var3 != null) {
         markChunkPendingToSend(var1, var3);
      }
   }

   private static void markChunkPendingToSend(ServerPlayer var0, LevelChunk var1) {
      var0.connection.chunkSender.markChunkPendingToSend(var1);
   }

   private static void dropChunk(ServerPlayer var0, ChunkPos var1) {
      var0.connection.chunkSender.dropChunk(var0, var1);
   }

   @Nullable
   public LevelChunk getChunkToSend(long var1) {
      ChunkHolder var3 = this.getVisibleChunkIfPresent(var1);
      return var3 == null ? null : var3.getChunkToSend();
   }

   public int size() {
      return this.visibleChunkMap.size();
   }

   public net.minecraft.server.level.DistanceManager getDistanceManager() {
      return this.distanceManager;
   }

   protected Iterable<ChunkHolder> getChunks() {
      return Iterables.unmodifiableIterable(this.visibleChunkMap.values());
   }

   void dumpChunks(Writer var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder()
         .addColumn("x")
         .addColumn("z")
         .addColumn("level")
         .addColumn("in_memory")
         .addColumn("status")
         .addColumn("full_status")
         .addColumn("accessible_ready")
         .addColumn("ticking_ready")
         .addColumn("entity_ticking_ready")
         .addColumn("ticket")
         .addColumn("spawning")
         .addColumn("block_entity_count")
         .addColumn("ticking_ticket")
         .addColumn("ticking_level")
         .addColumn("block_ticks")
         .addColumn("fluid_ticks")
         .build(var1);
      TickingTracker var3 = this.distanceManager.tickingTracker();
      ObjectBidirectionalIterator var4 = this.visibleChunkMap.long2ObjectEntrySet().iterator();

      while (var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         long var6 = var5.getLongKey();
         ChunkPos var8 = new ChunkPos(var6);
         ChunkHolder var9 = (ChunkHolder)var5.getValue();
         Optional var10 = Optional.ofNullable(var9.getLastAvailable());
         Optional var11 = var10.flatMap(var0 -> var0 instanceof LevelChunk ? Optional.of((ChunkAccess)var0) : Optional.empty());
         var2.writeRow(
            var8.x,
            var8.z,
            var9.getTicketLevel(),
            var10.isPresent(),
            var10.map(ChunkAccess::getStatus).orElse(null),
            var11.map(LevelChunk::getFullStatus).orElse(null),
            printFuture(var9.getFullChunkFuture()),
            printFuture(var9.getTickingChunkFuture()),
            printFuture(var9.getEntityTickingChunkFuture()),
            this.distanceManager.getTicketDebugString(var6),
            this.anyPlayerCloseEnoughForSpawning(var8),
            var11.<Integer>map(var0 -> var0.getBlockEntities().size()).orElse(0),
            var3.getTicketDebugString(var6),
            var3.getLevel(var6),
            var11.<Integer>map(var0 -> var0.getBlockTicks().count()).orElse(0),
            var11.<Integer>map(var0 -> var0.getFluidTicks().count()).orElse(0)
         );
      }
   }

   private static String printFuture(CompletableFuture<ChunkResult<LevelChunk>> var0) {
      try {
         ChunkResult var1 = (ChunkResult)var0.getNow(null);
         if (var1 != null) {
            return var1.isSuccess() ? "done" : "unloaded";
         } else {
            return "not completed";
         }
      } catch (CompletionException var2) {
         return "failed " + var2.getCause().getMessage();
      } catch (CancellationException var3) {
         return "cancelled";
      }
   }

   private CompletableFuture<Optional<CompoundTag>> readChunk(ChunkPos var1) {
      return this.read(var1).thenApplyAsync(var1x -> var1x.map(this::upgradeChunkTag), Util.backgroundExecutor());
   }

   private CompoundTag upgradeChunkTag(CompoundTag var1) {
      return this.upgradeChunkTag(this.level.dimension(), this.overworldDataStorage, var1, this.generator.getTypeNameForDataFixer());
   }

   boolean anyPlayerCloseEnoughForSpawning(ChunkPos var1) {
      if (!this.distanceManager.hasPlayersNearby(var1.toLong())) {
         return false;
      } else {
         for (ServerPlayer var3 : this.playerMap.getAllPlayers()) {
            if (this.playerIsCloseEnoughForSpawning(var3, var1)) {
               return true;
            }
         }

         return false;
      }
   }

   public List<ServerPlayer> getPlayersCloseForSpawning(ChunkPos var1) {
      long var2 = var1.toLong();
      if (!this.distanceManager.hasPlayersNearby(var2)) {
         return List.of();
      } else {
         Builder var4 = ImmutableList.builder();

         for (ServerPlayer var6 : this.playerMap.getAllPlayers()) {
            if (this.playerIsCloseEnoughForSpawning(var6, var1)) {
               var4.add(var6);
            }
         }

         return var4.build();
      }
   }

   private boolean playerIsCloseEnoughForSpawning(ServerPlayer var1, ChunkPos var2) {
      if (var1.isSpectator()) {
         return false;
      } else {
         double var3 = euclideanDistanceSquared(var2, var1);
         return var3 < 16384.0;
      }
   }

   private boolean skipPlayer(ServerPlayer var1) {
      return var1.isSpectator() && !this.level.getGameRules().getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS);
   }

   void updatePlayerStatus(ServerPlayer var1, boolean var2) {
      boolean var3 = this.skipPlayer(var1);
      boolean var4 = this.playerMap.ignoredOrUnknown(var1);
      if (var2) {
         this.playerMap.addPlayer(var1, var3);
         this.updatePlayerPos(var1);
         if (!var3) {
            this.distanceManager.addPlayer(SectionPos.of(var1), var1);
         }

         var1.setChunkTrackingView(ChunkTrackingView.EMPTY);
         this.updateChunkTracking(var1);
      } else {
         SectionPos var5 = var1.getLastSectionPos();
         this.playerMap.removePlayer(var1);
         if (!var4) {
            this.distanceManager.removePlayer(var5, var1);
         }

         this.applyChunkTrackingView(var1, ChunkTrackingView.EMPTY);
      }
   }

   private void updatePlayerPos(ServerPlayer var1) {
      SectionPos var2 = SectionPos.of(var1);
      var1.setLastSectionPos(var2);
   }

   public void move(ServerPlayer var1) {
      ObjectIterator var2 = this.entityMap.values().iterator();

      while (var2.hasNext()) {
         ChunkMap.TrackedEntity var3 = (ChunkMap.TrackedEntity)var2.next();
         if (var3.entity == var1) {
            var3.updatePlayers(this.level.players());
         } else {
            var3.updatePlayer(var1);
         }
      }

      SectionPos var7 = var1.getLastSectionPos();
      SectionPos var8 = SectionPos.of(var1);
      boolean var4 = this.playerMap.ignored(var1);
      boolean var5 = this.skipPlayer(var1);
      boolean var6 = var7.asLong() != var8.asLong();
      if (var6 || var4 != var5) {
         this.updatePlayerPos(var1);
         if (!var4) {
            this.distanceManager.removePlayer(var7, var1);
         }

         if (!var5) {
            this.distanceManager.addPlayer(var8, var1);
         }

         if (!var4 && var5) {
            this.playerMap.ignorePlayer(var1);
         }

         if (var4 && !var5) {
            this.playerMap.unIgnorePlayer(var1);
         }

         this.updateChunkTracking(var1);
      }
   }

   private void updateChunkTracking(ServerPlayer var1) {
      ChunkPos var2 = var1.chunkPosition();
      int var3 = this.getPlayerViewDistance(var1);
      if (var1.getChunkTrackingView() instanceof ChunkTrackingView.Positioned var4 && var4.center().equals(var2) && var4.viewDistance() == var3) {
         return;
      }

      this.applyChunkTrackingView(var1, ChunkTrackingView.of(var2, var3));
   }

   private void applyChunkTrackingView(ServerPlayer var1, ChunkTrackingView var2) {
      if (var1.level() == this.level) {
         ChunkTrackingView var3 = var1.getChunkTrackingView();
         if (var2 instanceof ChunkTrackingView.Positioned var4
            && (!(var3 instanceof ChunkTrackingView.Positioned var5) || !var5.center().equals(var4.center()))) {
            var1.connection.send(new ClientboundSetChunkCacheCenterPacket(var4.center().x, var4.center().z));
         }

         ChunkTrackingView.difference(var3, var2, var2x -> this.markChunkPendingToSend(var1, var2x), var1x -> dropChunk(var1, var1x));
         var1.setChunkTrackingView(var2);
      }
   }

   @Override
   public List<ServerPlayer> getPlayers(ChunkPos var1, boolean var2) {
      Set var3 = this.playerMap.getAllPlayers();
      Builder var4 = ImmutableList.builder();

      for (ServerPlayer var6 : var3) {
         if (var2 && this.isChunkOnTrackedBorder(var6, var1.x, var1.z) || !var2 && this.isChunkTracked(var6, var1.x, var1.z)) {
            var4.add(var6);
         }
      }

      return var4.build();
   }

   protected void addEntity(Entity var1) {
      if (!(var1 instanceof EnderDragonPart)) {
         EntityType var2 = var1.getType();
         int var3 = var2.clientTrackingRange() * 16;
         if (var3 != 0) {
            int var4 = var2.updateInterval();
            if (this.entityMap.containsKey(var1.getId())) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Entity is already tracked!"));
            } else {
               ChunkMap.TrackedEntity var5 = new ChunkMap.TrackedEntity(var1, var3, var4, var2.trackDeltas());
               this.entityMap.put(var1.getId(), var5);
               var5.updatePlayers(this.level.players());
               if (var1 instanceof ServerPlayer var6) {
                  this.updatePlayerStatus(var6, true);
                  ObjectIterator var7 = this.entityMap.values().iterator();

                  while (var7.hasNext()) {
                     ChunkMap.TrackedEntity var8 = (ChunkMap.TrackedEntity)var7.next();
                     if (var8.entity != var6) {
                        var8.updatePlayer(var6);
                     }
                  }
               }
            }
         }
      }
   }

   protected void removeEntity(Entity var1) {
      if (var1 instanceof ServerPlayer var2) {
         this.updatePlayerStatus(var2, false);
         ObjectIterator var3 = this.entityMap.values().iterator();

         while (var3.hasNext()) {
            ChunkMap.TrackedEntity var4 = (ChunkMap.TrackedEntity)var3.next();
            var4.removePlayer(var2);
         }
      }

      ChunkMap.TrackedEntity var5 = (ChunkMap.TrackedEntity)this.entityMap.remove(var1.getId());
      if (var5 != null) {
         var5.broadcastRemoved();
      }
   }

   protected void tick() {
      for (ServerPlayer var2 : this.playerMap.getAllPlayers()) {
         this.updateChunkTracking(var2);
      }

      ArrayList var9 = Lists.newArrayList();
      List var10 = this.level.players();
      ObjectIterator var3 = this.entityMap.values().iterator();

      while (var3.hasNext()) {
         ChunkMap.TrackedEntity var4 = (ChunkMap.TrackedEntity)var3.next();
         SectionPos var5 = var4.lastSectionPos;
         SectionPos var6 = SectionPos.of(var4.entity);
         boolean var7 = !Objects.equals(var5, var6);
         if (var7) {
            var4.updatePlayers(var10);
            Entity var8 = var4.entity;
            if (var8 instanceof ServerPlayer) {
               var9.add((ServerPlayer)var8);
            }

            var4.lastSectionPos = var6;
         }

         if (var7 || this.distanceManager.inEntityTickingRange(var6.chunk().toLong())) {
            var4.serverEntity.sendChanges();
         }
      }

      if (!var9.isEmpty()) {
         var3 = this.entityMap.values().iterator();

         while (var3.hasNext()) {
            ChunkMap.TrackedEntity var12 = (ChunkMap.TrackedEntity)var3.next();
            var12.updatePlayers(var9);
         }
      }
   }

   public void broadcast(Entity var1, Packet<?> var2) {
      ChunkMap.TrackedEntity var3 = (ChunkMap.TrackedEntity)this.entityMap.get(var1.getId());
      if (var3 != null) {
         var3.broadcast(var2);
      }
   }

   protected void broadcastAndSend(Entity var1, Packet<?> var2) {
      ChunkMap.TrackedEntity var3 = (ChunkMap.TrackedEntity)this.entityMap.get(var1.getId());
      if (var3 != null) {
         var3.broadcastAndSend(var2);
      }
   }

   public void resendBiomesForChunks(List<ChunkAccess> var1) {
      HashMap var2 = new HashMap();

      for (ChunkAccess var4 : var1) {
         ChunkPos var5 = var4.getPos();
         LevelChunk var6;
         if (var4 instanceof LevelChunk var7) {
            var6 = var7;
         } else {
            var6 = this.level.getChunk(var5.x, var5.z);
         }

         for (ServerPlayer var8 : this.getPlayers(var5, false)) {
            var2.computeIfAbsent(var8, var0 -> new ArrayList<>()).add(var6);
         }
      }

      var2.forEach((var0, var1x) -> var0.connection.send(ClientboundChunksBiomesPacket.forChunks((List<LevelChunk>)var1x)));
   }

   protected PoiManager getPoiManager() {
      return this.poiManager;
   }

   public String getStorageName() {
      return this.storageName;
   }

   void onFullChunkStatusChange(ChunkPos var1, FullChunkStatus var2) {
      this.chunkStatusListener.onChunkStatusChange(var1, var2);
   }

   public void waitForLightBeforeSending(ChunkPos var1, int var2) {
      int var3 = var2 + 1;
      ChunkPos.rangeClosed(var1, var3).forEach(var1x -> {
         ChunkHolder var2x = this.getVisibleChunkIfPresent(var1x.toLong());
         if (var2x != null) {
            var2x.addSendDependency(this.lightEngine.waitForPendingTasks(var1x.x, var1x.z));
         }
      });
   }

   class DistanceManager extends net.minecraft.server.level.DistanceManager {
      protected DistanceManager(Executor var2, Executor var3) {
         super(var2, var3);
      }

      @Override
      protected boolean isChunkToRemove(long var1) {
         return ChunkMap.this.toDrop.contains(var1);
      }

      @Nullable
      @Override
      protected ChunkHolder getChunk(long var1) {
         return ChunkMap.this.getUpdatingChunkIfPresent(var1);
      }

      @Nullable
      @Override
      protected ChunkHolder updateChunkScheduling(long var1, int var3, @Nullable ChunkHolder var4, int var5) {
         return ChunkMap.this.updateChunkScheduling(var1, var3, var4, var5);
      }
   }

   class TrackedEntity {
      final ServerEntity serverEntity;
      final Entity entity;
      private final int range;
      SectionPos lastSectionPos;
      private final Set<ServerPlayerConnection> seenBy = Sets.newIdentityHashSet();

      public TrackedEntity(Entity var2, int var3, int var4, boolean var5) {
         super();
         this.serverEntity = new ServerEntity(ChunkMap.this.level, var2, var4, var5, this::broadcast);
         this.entity = var2;
         this.range = var3;
         this.lastSectionPos = SectionPos.of(var2);
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof ChunkMap.TrackedEntity ? ((ChunkMap.TrackedEntity)var1).entity.getId() == this.entity.getId() : false;
      }

      @Override
      public int hashCode() {
         return this.entity.getId();
      }

      public void broadcast(Packet<?> var1) {
         for (ServerPlayerConnection var3 : this.seenBy) {
            var3.send(var1);
         }
      }

      public void broadcastAndSend(Packet<?> var1) {
         this.broadcast(var1);
         if (this.entity instanceof ServerPlayer) {
            ((ServerPlayer)this.entity).connection.send(var1);
         }
      }

      public void broadcastRemoved() {
         for (ServerPlayerConnection var2 : this.seenBy) {
            this.serverEntity.removePairing(var2.getPlayer());
         }
      }

      public void removePlayer(ServerPlayer var1) {
         if (this.seenBy.remove(var1.connection)) {
            this.serverEntity.removePairing(var1);
         }
      }

      public void updatePlayer(ServerPlayer var1) {
         if (var1 != this.entity) {
            Vec3 var2 = var1.position().subtract(this.entity.position());
            int var3 = ChunkMap.this.getPlayerViewDistance(var1);
            double var4 = (double)Math.min(this.getEffectiveRange(), var3 * 16);
            double var6 = var2.x * var2.x + var2.z * var2.z;
            double var8 = var4 * var4;
            boolean var10 = var6 <= var8
               && this.entity.broadcastToPlayer(var1)
               && ChunkMap.this.isChunkTracked(var1, this.entity.chunkPosition().x, this.entity.chunkPosition().z);
            if (var10) {
               if (this.seenBy.add(var1.connection)) {
                  this.serverEntity.addPairing(var1);
               }
            } else if (this.seenBy.remove(var1.connection)) {
               this.serverEntity.removePairing(var1);
            }
         }
      }

      private int scaledRange(int var1) {
         return ChunkMap.this.level.getServer().getScaledTrackingDistance(var1);
      }

      private int getEffectiveRange() {
         int var1 = this.range;

         for (Entity var3 : this.entity.getIndirectPassengers()) {
            int var4 = var3.getType().clientTrackingRange() * 16;
            if (var4 > var1) {
               var1 = var4;
            }
         }

         return this.scaledRange(var1);
      }

      public void updatePlayers(List<ServerPlayer> var1) {
         for (ServerPlayer var3 : var1) {
            this.updatePlayer(var3);
         }
      }
   }
}
