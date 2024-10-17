package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
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
import java.util.function.IntConsumer;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.StaticCache2D;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.thread.ConsecutiveExecutor;
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
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
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

public class ChunkMap extends ChunkStorage implements ChunkHolder.PlayerProvider, GeneratingChunkMap {
   private static final ChunkResult<List<ChunkAccess>> UNLOADED_CHUNK_LIST_RESULT = ChunkResult.error("Unloaded chunks found in range");
   private static final CompletableFuture<ChunkResult<List<ChunkAccess>>> UNLOADED_CHUNK_LIST_FUTURE = CompletableFuture.completedFuture(
      UNLOADED_CHUNK_LIST_RESULT
   );
   private static final byte CHUNK_TYPE_REPLACEABLE = -1;
   private static final byte CHUNK_TYPE_UNKNOWN = 0;
   private static final byte CHUNK_TYPE_FULL = 1;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CHUNK_SAVED_PER_TICK = 200;
   private static final int CHUNK_SAVED_EAGERLY_PER_TICK = 20;
   private static final int EAGER_CHUNK_SAVE_COOLDOWN_IN_MILLIS = 10000;
   private static final int MAX_ACTIVE_CHUNK_WRITES = 128;
   public static final int MIN_VIEW_DISTANCE = 2;
   public static final int MAX_VIEW_DISTANCE = 32;
   public static final int FORCED_TICKET_LEVEL = ChunkLevel.byStatus(FullChunkStatus.ENTITY_TICKING);
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap = new Long2ObjectLinkedOpenHashMap();
   private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap = this.updatingChunkMap.clone();
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads = new Long2ObjectLinkedOpenHashMap();
   private final List<ChunkGenerationTask> pendingGenerationTasks = new ArrayList<>();
   final ServerLevel level;
   private final ThreadedLevelLightEngine lightEngine;
   private final BlockableEventLoop<Runnable> mainThreadExecutor;
   private final RandomState randomState;
   private final ChunkGeneratorStructureState chunkGeneratorState;
   private final Supplier<DimensionDataStorage> overworldDataStorage;
   private final PoiManager poiManager;
   final LongSet toDrop = new LongOpenHashSet();
   private boolean modified;
   private final ChunkTaskDispatcher worldgenTaskDispatcher;
   private final ChunkTaskDispatcher lightTaskDispatcher;
   private final ChunkProgressListener progressListener;
   private final ChunkStatusUpdateListener chunkStatusListener;
   private final ChunkMap.DistanceManager distanceManager;
   private final AtomicInteger tickingGenerated = new AtomicInteger();
   private final String storageName;
   private final PlayerMap playerMap = new PlayerMap();
   private final Int2ObjectMap<ChunkMap.TrackedEntity> entityMap = new Int2ObjectOpenHashMap();
   private final Long2ByteMap chunkTypeCache = new Long2ByteOpenHashMap();
   private final Long2LongMap nextChunkSaveTime = new Long2LongOpenHashMap();
   private final LongSet chunksToEagerlySave = new LongLinkedOpenHashSet();
   private final Queue<Runnable> unloadQueue = Queues.newConcurrentLinkedQueue();
   private final AtomicInteger activeChunkWrites = new AtomicInteger();
   private int serverViewDistance;
   private final WorldGenContext worldGenContext;

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
      RegistryAccess var15 = var1.registryAccess();
      long var16 = var1.getSeed();
      if (var8 instanceof NoiseBasedChunkGenerator var18) {
         this.randomState = RandomState.create(var18.generatorSettings().value(), var15.lookupOrThrow(Registries.NOISE), var16);
      } else {
         this.randomState = RandomState.create(NoiseGeneratorSettings.dummy(), var15.lookupOrThrow(Registries.NOISE), var16);
      }

      this.chunkGeneratorState = var8.createState(var15.lookupOrThrow(Registries.STRUCTURE_SET), this.randomState, var16);
      this.mainThreadExecutor = var6;
      ConsecutiveExecutor var20 = new ConsecutiveExecutor(var5, "worldgen");
      this.progressListener = var9;
      this.chunkStatusListener = var10;
      ConsecutiveExecutor var19 = new ConsecutiveExecutor(var5, "light");
      this.worldgenTaskDispatcher = new ChunkTaskDispatcher(var20, var5);
      this.lightTaskDispatcher = new ChunkTaskDispatcher(var19, var5);
      this.lightEngine = new ThreadedLevelLightEngine(var7, this, this.level.dimensionType().hasSkyLight(), var19, this.lightTaskDispatcher);
      this.distanceManager = new ChunkMap.DistanceManager(var5, var6);
      this.overworldDataStorage = var11;
      this.poiManager = new PoiManager(
         new RegionStorageInfo(var2.getLevelId(), var1.dimension(), "poi"), var14.resolve("poi"), var3, var13, var15, var1.getServer(), var1
      );
      this.setServerViewDistance(var12);
      this.worldGenContext = new WorldGenContext(var1, var8, var4, this.lightEngine, var6, this::setChunkUnsaved);
   }

   private void setChunkUnsaved(ChunkPos var1) {
      this.chunksToEagerlySave.add(var1.toLong());
   }

   protected ChunkGenerator generator() {
      return this.worldGenContext.generator();
   }

   protected ChunkGeneratorStructureState generatorState() {
      return this.chunkGeneratorState;
   }

   protected RandomState randomState() {
      return this.randomState;
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
         ChunkStatus var4 = var2.getLatestStatus();
         ChunkAccess var5 = var2.getLatestChunk();
         if (var4 != null) {
            var3 = var3 + "St: \u00a7" + var4.getIndex() + var4 + "\u00a7r\n";
         }

         if (var5 != null) {
            var3 = var3 + "Ch: \u00a7" + var5.getPersistedStatus().getIndex() + var5.getPersistedStatus() + "\u00a7r\n";
         }

         FullChunkStatus var6 = var2.getFullStatus();
         var3 = var3 + '\u00a7' + var6.ordinal() + var6;
         return var3 + "\u00a7r";
      }
   }

   private CompletableFuture<ChunkResult<List<ChunkAccess>>> getChunkRangeFuture(ChunkHolder var1, int var2, IntFunction<ChunkStatus> var3) {
      if (var2 == 0) {
         ChunkStatus var14 = (ChunkStatus)var3.apply(0);
         return var1.scheduleChunkGenerationTask(var14, this).thenApply(var0 -> var0.map(List::of));
      } else {
         int var4 = Mth.square(var2 * 2 + 1);
         ArrayList var5 = new ArrayList(var4);
         ChunkPos var6 = var1.getPos();

         for (int var7 = -var2; var7 <= var2; var7++) {
            for (int var8 = -var2; var8 <= var2; var8++) {
               int var9 = Math.max(Math.abs(var8), Math.abs(var7));
               long var10 = ChunkPos.asLong(var6.x + var8, var6.z + var7);
               ChunkHolder var12 = this.getUpdatingChunkIfPresent(var10);
               if (var12 == null) {
                  return UNLOADED_CHUNK_LIST_FUTURE;
               }

               ChunkStatus var13 = (ChunkStatus)var3.apply(var9);
               var5.add(var12.scheduleChunkGenerationTask(var13, this));
            }
         }

         return Util.sequence(var5).thenApply(var1x -> {
            ArrayList var2x = new ArrayList(var1x.size());

            for (ChunkResult var4x : var1x) {
               if (var4x == null) {
                  throw this.debugFuturesAndCreateReportedException(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
               }

               ChunkAccess var5x = (ChunkAccess)var4x.orElse(null);
               if (var5x == null) {
                  return UNLOADED_CHUNK_LIST_RESULT;
               }

               var2x.add(var5x);
            }

            return ChunkResult.of(var2x);
         });
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
      return this.getChunkRangeFuture(var1, 2, var0 -> ChunkStatus.FULL).thenApply(var0 -> var0.map(var0x -> (LevelChunk)var0x.get(var0x.size() / 2)));
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
               var4 = new ChunkHolder(new ChunkPos(var1), var3, this.level, this.lightEngine, this::onLevelChange, this);
            }

            this.updatingChunkMap.put(var1, var4);
            this.modified = true;
         }

         return var4;
      }
   }

   private void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4) {
      this.worldgenTaskDispatcher.onLevelChange(var1, var2, var3, var4);
      this.lightTaskDispatcher.onLevelChange(var1, var2, var3, var4);
   }

   @Override
   public void close() throws IOException {
      try {
         this.worldgenTaskDispatcher.close();
         this.lightTaskDispatcher.close();
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
               this.mainThreadExecutor.managedBlock(var1x::isReadyForSaving);
               return var1x.getLatestChunk();
            }).filter(var0 -> var0 instanceof ImposterProtoChunk || var0 instanceof LevelChunk).filter(this::save).forEach(var1x -> var3.setTrue());
         } while (var3.isTrue());

         this.poiManager.flushAll();
         this.processUnloads(() -> true);
         this.flushWorker();
      } else {
         this.nextChunkSaveTime.clear();
         long var6 = Util.getMillis();
         ObjectIterator var4 = this.visibleChunkMap.values().iterator();

         while (var4.hasNext()) {
            ChunkHolder var5 = (ChunkHolder)var4.next();
            this.saveChunkIfNeeded(var5, var6);
         }
      }
   }

   protected void tick(BooleanSupplier var1) {
      ProfilerFiller var2 = Profiler.get();
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
         || this.worldgenTaskDispatcher.hasWork()
         || this.lightTaskDispatcher.hasWork()
         || this.distanceManager.hasTickets();
   }

   private void processUnloads(BooleanSupplier var1) {
      for (LongIterator var2 = this.toDrop.iterator(); var2.hasNext(); var2.remove()) {
         long var3 = var2.nextLong();
         ChunkHolder var5 = (ChunkHolder)this.updatingChunkMap.get(var3);
         if (var5 != null) {
            this.updatingChunkMap.remove(var3);
            this.pendingUnloads.put(var3, var5);
            this.modified = true;
            this.scheduleUnload(var3, var5);
         }
      }

      int var4 = Math.max(0, this.unloadQueue.size() - 2000);

      Runnable var6;
      while ((var4 > 0 || var1.getAsBoolean()) && (var6 = this.unloadQueue.poll()) != null) {
         var4--;
         var6.run();
      }

      this.saveChunksEagerly(var1);
   }

   private void saveChunksEagerly(BooleanSupplier var1) {
      long var2 = Util.getMillis();
      int var4 = 0;
      LongIterator var5 = this.chunksToEagerlySave.iterator();

      while (var4 < 20 && this.activeChunkWrites.get() < 128 && var1.getAsBoolean() && var5.hasNext()) {
         long var6 = var5.nextLong();
         ChunkHolder var8 = (ChunkHolder)this.visibleChunkMap.get(var6);
         ChunkAccess var9 = var8 != null ? var8.getLatestChunk() : null;
         if (var9 == null || !var9.isUnsaved()) {
            var5.remove();
         } else if (this.saveChunkIfNeeded(var8, var2)) {
            var4++;
            var5.remove();
         }
      }
   }

   private void scheduleUnload(long var1, ChunkHolder var3) {
      CompletableFuture var4 = var3.getSaveSyncFuture();
      var4.thenRunAsync(() -> {
         CompletableFuture var5 = var3.getSaveSyncFuture();
         if (var5 != var4) {
            this.scheduleUnload(var1, var3);
         } else {
            ChunkAccess var6 = var3.getLatestChunk();
            if (this.pendingUnloads.remove(var1, var3) && var6 != null) {
               if (var6 instanceof LevelChunk var7) {
                  var7.setLoaded(false);
               }

               this.save(var6);
               if (var6 instanceof LevelChunk var8) {
                  this.level.unload(var8);
               }

               this.lightEngine.updateChunkStatus(var6.getPos());
               this.lightEngine.tryScheduleUpdate();
               this.progressListener.onStatusChange(var6.getPos(), null);
               this.nextChunkSaveTime.remove(var6.getPos().toLong());
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

   private CompletableFuture<ChunkAccess> scheduleChunkLoad(ChunkPos var1) {
      CompletableFuture var2 = this.readChunk(var1).thenApplyAsync(var2x -> var2x.map(var2xx -> {
            SerializableChunkData var3x = SerializableChunkData.parse(this.level, this.level.registryAccess(), var2xx);
            if (var3x == null) {
               LOGGER.error("Chunk file at {} is missing level data, skipping", var1);
            }

            return var3x;
         }), Util.backgroundExecutor().forName("parseChunk"));
      CompletableFuture var3 = this.poiManager.prefetch(var1);
      return var2.<Object, Optional>thenCombine(var3, (var0, var1x) -> (Optional)var0).thenApplyAsync(var2x -> {
         Profiler.get().incrementCounter("chunkLoad");
         if (var2x.isPresent()) {
            ProtoChunk var3x = ((SerializableChunkData)var2x.get()).read(this.level, this.poiManager, this.storageInfo(), var1);
            this.markPosition(var1, var3x.getPersistedStatus().getChunkType());
            return var3x;
         } else {
            return this.createEmptyChunk(var1);
         }
      }, this.mainThreadExecutor).exceptionallyAsync(var2x -> this.handleChunkLoadFailure(var2x, var1), this.mainThreadExecutor);
   }

   private ChunkAccess handleChunkLoadFailure(Throwable var1, ChunkPos var2) {
      Throwable var3 = var1 instanceof CompletionException var4 ? var4.getCause() : var1;
      Throwable var9 = var3 instanceof ReportedException var5 ? var5.getCause() : var3;
      boolean var10 = var9 instanceof Error;
      boolean var6 = var9 instanceof IOException || var9 instanceof NbtException;
      if (!var10) {
         if (!var6) {
         }

         this.level.getServer().reportChunkLoadFailure(var9, this.storageInfo(), var2);
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
      return new ProtoChunk(var1, UpgradeData.EMPTY, this.level, this.level.registryAccess().lookupOrThrow(Registries.BIOME), null);
   }

   private void markPositionReplaceable(ChunkPos var1) {
      this.chunkTypeCache.put(var1.toLong(), (byte)-1);
   }

   private byte markPosition(ChunkPos var1, ChunkType var2) {
      return this.chunkTypeCache.put(var1.toLong(), (byte)(var2 == ChunkType.PROTOCHUNK ? -1 : 1));
   }

   @Override
   public GenerationChunkHolder acquireGeneration(long var1) {
      ChunkHolder var3 = (ChunkHolder)this.updatingChunkMap.get(var1);
      var3.increaseGenerationRefCount();
      return var3;
   }

   @Override
   public void releaseGeneration(GenerationChunkHolder var1) {
      var1.decreaseGenerationRefCount();
   }

   @Override
   public CompletableFuture<ChunkAccess> applyStep(GenerationChunkHolder var1, ChunkStep var2, StaticCache2D<GenerationChunkHolder> var3) {
      ChunkPos var4 = var1.getPos();
      if (var2.targetStatus() == ChunkStatus.EMPTY) {
         return this.scheduleChunkLoad(var4);
      } else {
         try {
            GenerationChunkHolder var5 = (GenerationChunkHolder)var3.get(var4.x, var4.z);
            ChunkAccess var9 = var5.getChunkIfPresentUnchecked(var2.targetStatus().getParent());
            if (var9 == null) {
               throw new IllegalStateException("Parent chunk missing");
            } else {
               CompletableFuture var10 = var2.apply(this.worldGenContext, var3, var9);
               this.progressListener.onStatusChange(var4, var2.targetStatus());
               return var10;
            }
         } catch (Exception var8) {
            var8.getStackTrace();
            CrashReport var6 = CrashReport.forThrowable(var8, "Exception generating new chunk");
            CrashReportCategory var7 = var6.addCategory("Chunk to be generated");
            var7.setDetail("Status being generated", () -> var2.targetStatus().getName());
            var7.setDetail("Location", String.format(Locale.ROOT, "%d,%d", var4.x, var4.z));
            var7.setDetail("Position hash", ChunkPos.asLong(var4.x, var4.z));
            var7.setDetail("Generator", this.generator());
            this.mainThreadExecutor.execute(() -> {
               throw new ReportedException(var6);
            });
            throw new ReportedException(var6);
         }
      }
   }

   @Override
   public ChunkGenerationTask scheduleGenerationTask(ChunkStatus var1, ChunkPos var2) {
      ChunkGenerationTask var3 = ChunkGenerationTask.create(this, var1, var2);
      this.pendingGenerationTasks.add(var3);
      return var3;
   }

   private void runGenerationTask(ChunkGenerationTask var1) {
      GenerationChunkHolder var2 = var1.getCenter();
      this.worldgenTaskDispatcher.submit(() -> {
         CompletableFuture var2x = var1.runUntilWait();
         if (var2x != null) {
            var2x.thenRun(() -> this.runGenerationTask(var1));
         }
      }, var2.getPos().toLong(), var2::getQueueLevel);
   }

   @Override
   public void runGenerationTasks() {
      this.pendingGenerationTasks.forEach(this::runGenerationTask);
      this.pendingGenerationTasks.clear();
   }

   public CompletableFuture<ChunkResult<LevelChunk>> prepareTickingChunk(ChunkHolder var1) {
      CompletableFuture var2 = this.getChunkRangeFuture(var1, 1, var0 -> ChunkStatus.FULL);
      CompletableFuture var3 = var2.thenApplyAsync(var2x -> var2x.map(var2xx -> {
            LevelChunk var3x = (LevelChunk)var2xx.get(var2xx.size() / 2);
            var3x.postProcessGeneration(this.level);
            this.level.startTickingChunk(var3x);
            CompletableFuture var4 = var1.getSendSyncFuture();
            if (var4.isDone()) {
               this.onChunkReadyToSend(var3x);
            } else {
               var4.thenAcceptAsync(var2xxx -> this.onChunkReadyToSend(var3x), this.mainThreadExecutor);
            }

            return var3x;
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
      return this.getChunkRangeFuture(var1, 1, ChunkLevel::getStatusAroundFullChunk)
         .thenApply(var0 -> var0.map(var0x -> (LevelChunk)var0x.get(var0x.size() / 2)));
   }

   public int getTickingGenerated() {
      return this.tickingGenerated.get();
   }

   private boolean saveChunkIfNeeded(ChunkHolder var1, long var2) {
      if (var1.wasAccessibleSinceLastSave() && var1.isReadyForSaving()) {
         ChunkAccess var4 = var1.getLatestChunk();
         if (!(var4 instanceof ImposterProtoChunk) && !(var4 instanceof LevelChunk)) {
            return false;
         } else if (!var4.isUnsaved()) {
            return false;
         } else {
            long var5 = var4.getPos().toLong();
            long var7 = this.nextChunkSaveTime.getOrDefault(var5, -1L);
            if (var2 < var7) {
               return false;
            } else {
               boolean var9 = this.save(var4);
               var1.refreshAccessibility();
               if (var9) {
                  this.nextChunkSaveTime.put(var5, var2 + 10000L);
               }

               return var9;
            }
         }
      } else {
         return false;
      }
   }

   private boolean save(ChunkAccess var1) {
      this.poiManager.flush(var1.getPos());
      if (!var1.tryMarkSaved()) {
         return false;
      } else {
         ChunkPos var2 = var1.getPos();

         try {
            ChunkStatus var3 = var1.getPersistedStatus();
            if (var3.getChunkType() != ChunkType.LEVELCHUNK) {
               if (this.isExistingChunkFull(var2)) {
                  return false;
               }

               if (var3 == ChunkStatus.EMPTY && var1.getAllStarts().values().stream().noneMatch(StructureStart::isValid)) {
                  return false;
               }
            }

            Profiler.get().incrementCounter("chunkSave");
            this.activeChunkWrites.incrementAndGet();
            SerializableChunkData var4 = SerializableChunkData.copyOf(this.level, var1);
            CompletableFuture var5 = CompletableFuture.supplyAsync(var4::write, Util.backgroundExecutor());
            this.write(var2, var5::join).handle((var2x, var3x) -> {
               if (var3x != null) {
                  this.level.getServer().reportChunkSaveFailure(var3x, this.storageInfo(), var2);
               }

               this.activeChunkWrites.decrementAndGet();
               return null;
            });
            this.markPosition(var2, var3.getChunkType());
            return true;
         } catch (Exception var6) {
            this.level.getServer().reportChunkSaveFailure(var6, this.storageInfo(), var2);
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

         ChunkType var4 = SerializableChunkData.getChunkTypeFromTag(var3);
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
         Optional var10 = Optional.ofNullable(var9.getLatestChunk());
         Optional var11 = var10.flatMap(var0 -> var0 instanceof LevelChunk ? Optional.of((ChunkAccess)var0) : Optional.empty());
         var2.writeRow(
            var8.x,
            var8.z,
            var9.getTicketLevel(),
            var10.isPresent(),
            var10.map(ChunkAccess::getPersistedStatus).orElse(null),
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
      return this.read(var1).thenApplyAsync(var1x -> var1x.map(this::upgradeChunkTag), Util.backgroundExecutor().forName("upgradeChunk"));
   }

   private CompoundTag upgradeChunkTag(CompoundTag var1) {
      return this.upgradeChunkTag(this.level.dimension(), this.overworldDataStorage, var1, this.generator().getTypeNameForDataFixer());
   }

   void forEachSpawnCandidateChunk(Consumer<ChunkHolder> var1) {
      LongIterator var2 = this.distanceManager.getSpawnCandidateChunks();

      while (var2.hasNext()) {
         long var3 = var2.nextLong();
         ChunkHolder var5 = (ChunkHolder)this.visibleChunkMap.get(var3);
         if (var5 != null && this.anyPlayerCloseEnoughForSpawningInternal(var5.getPos())) {
            var1.accept(var5);
         }
      }
   }

   boolean anyPlayerCloseEnoughForSpawning(ChunkPos var1) {
      return !this.distanceManager.hasPlayersNearby(var1.toLong()) ? false : this.anyPlayerCloseEnoughForSpawningInternal(var1);
   }

   private boolean anyPlayerCloseEnoughForSpawningInternal(ChunkPos var1) {
      for (ServerPlayer var3 : this.playerMap.getAllPlayers()) {
         if (this.playerIsCloseEnoughForSpawning(var3, var1)) {
            return true;
         }
      }

      return false;
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
      protected DistanceManager(final Executor nullx, final Executor nullxx) {
         super(nullx, nullxx);
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

      public TrackedEntity(final Entity nullx, final int nullxx, final int nullxxx, final boolean nullxxxx) {
         super();
         this.serverEntity = new ServerEntity(ChunkMap.this.level, nullx, nullxxx, nullxxxx, this::broadcast);
         this.entity = nullx;
         this.range = nullxx;
         this.lastSectionPos = SectionPos.of(nullx);
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
