package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class ServerChunkCache extends ChunkSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DistanceManager distanceManager;
   final ServerLevel level;
   final Thread mainThread;
   final ThreadedLevelLightEngine lightEngine;
   private final ServerChunkCache.MainThreadExecutor mainThreadProcessor;
   public final ChunkMap chunkMap;
   private final DimensionDataStorage dataStorage;
   private long lastInhabitedUpdate;
   private boolean spawnEnemies = true;
   private boolean spawnFriendlies = true;
   private static final int CACHE_SIZE = 4;
   private final long[] lastChunkPos = new long[4];
   private final ChunkStatus[] lastChunkStatus = new ChunkStatus[4];
   private final ChunkAccess[] lastChunk = new ChunkAccess[4];
   private final List<LevelChunk> tickingChunks = new ArrayList<>();
   private final Set<ChunkHolder> chunkHoldersToBroadcast = new ReferenceOpenHashSet();
   @Nullable
   @VisibleForDebug
   private NaturalSpawner.SpawnState lastSpawnState;

   public ServerChunkCache(
      ServerLevel var1,
      LevelStorageSource.LevelStorageAccess var2,
      DataFixer var3,
      StructureTemplateManager var4,
      Executor var5,
      ChunkGenerator var6,
      int var7,
      int var8,
      boolean var9,
      ChunkProgressListener var10,
      ChunkStatusUpdateListener var11,
      Supplier<DimensionDataStorage> var12
   ) {
      super();
      this.level = var1;
      this.mainThreadProcessor = new ServerChunkCache.MainThreadExecutor(var1);
      this.mainThread = Thread.currentThread();
      Path var13 = var2.getDimensionPath(var1.dimension()).resolve("data");

      try {
         FileUtil.createDirectoriesSafe(var13);
      } catch (IOException var15) {
         LOGGER.error("Failed to create dimension data storage directory", var15);
      }

      this.dataStorage = new DimensionDataStorage(var13, var3, var1.registryAccess());
      this.chunkMap = new ChunkMap(var1, var2, var3, var4, var5, this.mainThreadProcessor, this, var6, var10, var11, var12, var7, var9);
      this.lightEngine = this.chunkMap.getLightEngine();
      this.distanceManager = this.chunkMap.getDistanceManager();
      this.distanceManager.updateSimulationDistance(var8);
      this.clearCache();
   }

   public ThreadedLevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   @Nullable
   private ChunkHolder getVisibleChunkIfPresent(long var1) {
      return this.chunkMap.getVisibleChunkIfPresent(var1);
   }

   public int getTickingGenerated() {
      return this.chunkMap.getTickingGenerated();
   }

   private void storeInCache(long var1, @Nullable ChunkAccess var3, ChunkStatus var4) {
      for (int var5 = 3; var5 > 0; var5--) {
         this.lastChunkPos[var5] = this.lastChunkPos[var5 - 1];
         this.lastChunkStatus[var5] = this.lastChunkStatus[var5 - 1];
         this.lastChunk[var5] = this.lastChunk[var5 - 1];
      }

      this.lastChunkPos[0] = var1;
      this.lastChunkStatus[0] = var4;
      this.lastChunk[0] = var3;
   }

   @Nullable
   @Override
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      if (Thread.currentThread() != this.mainThread) {
         return CompletableFuture.<ChunkAccess>supplyAsync(() -> this.getChunk(var1, var2, var3, var4), this.mainThreadProcessor).join();
      } else {
         ProfilerFiller var5 = this.level.getProfiler();
         var5.incrementCounter("getChunk");
         long var6 = ChunkPos.asLong(var1, var2);

         for (int var8 = 0; var8 < 4; var8++) {
            if (var6 == this.lastChunkPos[var8] && var3 == this.lastChunkStatus[var8]) {
               ChunkAccess var9 = this.lastChunk[var8];
               if (var9 != null || !var4) {
                  return var9;
               }
            }
         }

         var5.incrementCounter("getChunkCacheMiss");
         CompletableFuture var11 = this.getChunkFutureMainThread(var1, var2, var3, var4);
         this.mainThreadProcessor.managedBlock(var11::isDone);
         ChunkResult var12 = (ChunkResult)var11.join();
         ChunkAccess var10 = (ChunkAccess)var12.orElse(null);
         if (var10 == null && var4) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Chunk not there when requested: " + var12.getError()));
         } else {
            this.storeInCache(var6, var10, var3);
            return var10;
         }
      }
   }

   @Nullable
   @Override
   public LevelChunk getChunkNow(int var1, int var2) {
      if (Thread.currentThread() != this.mainThread) {
         return null;
      } else {
         this.level.getProfiler().incrementCounter("getChunkNow");
         long var3 = ChunkPos.asLong(var1, var2);

         for (int var5 = 0; var5 < 4; var5++) {
            if (var3 == this.lastChunkPos[var5] && this.lastChunkStatus[var5] == ChunkStatus.FULL) {
               ChunkAccess var6 = this.lastChunk[var5];
               return var6 instanceof LevelChunk ? (LevelChunk)var6 : null;
            }
         }

         ChunkHolder var7 = this.getVisibleChunkIfPresent(var3);
         if (var7 == null) {
            return null;
         } else {
            ChunkAccess var8 = var7.getChunkIfPresent(ChunkStatus.FULL);
            if (var8 != null) {
               this.storeInCache(var3, var8, ChunkStatus.FULL);
               if (var8 instanceof LevelChunk) {
                  return (LevelChunk)var8;
               }
            }

            return null;
         }
      }
   }

   private void clearCache() {
      Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
      Arrays.fill(this.lastChunkStatus, null);
      Arrays.fill(this.lastChunk, null);
   }

   public CompletableFuture<ChunkResult<ChunkAccess>> getChunkFuture(int var1, int var2, ChunkStatus var3, boolean var4) {
      boolean var5 = Thread.currentThread() == this.mainThread;
      CompletableFuture var6;
      if (var5) {
         var6 = this.getChunkFutureMainThread(var1, var2, var3, var4);
         this.mainThreadProcessor.managedBlock(var6::isDone);
      } else {
         var6 = CompletableFuture.<CompletableFuture<ChunkResult<ChunkAccess>>>supplyAsync(
               () -> this.getChunkFutureMainThread(var1, var2, var3, var4), this.mainThreadProcessor
            )
            .thenCompose(var0 -> (CompletionStage<ChunkResult<ChunkAccess>>)var0);
      }

      return var6;
   }

   private CompletableFuture<ChunkResult<ChunkAccess>> getChunkFutureMainThread(int var1, int var2, ChunkStatus var3, boolean var4) {
      ChunkPos var5 = new ChunkPos(var1, var2);
      long var6 = var5.toLong();
      int var8 = ChunkLevel.byStatus(var3);
      ChunkHolder var9 = this.getVisibleChunkIfPresent(var6);
      if (var4) {
         this.distanceManager.addTicket(TicketType.UNKNOWN, var5, var8, var5);
         if (this.chunkAbsent(var9, var8)) {
            ProfilerFiller var10 = this.level.getProfiler();
            var10.push("chunkLoad");
            this.runDistanceManagerUpdates();
            var9 = this.getVisibleChunkIfPresent(var6);
            var10.pop();
            if (this.chunkAbsent(var9, var8)) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("No chunk holder after ticket has been added"));
            }
         }
      }

      return this.chunkAbsent(var9, var8) ? GenerationChunkHolder.UNLOADED_CHUNK_FUTURE : var9.scheduleChunkGenerationTask(var3, this.chunkMap);
   }

   private boolean chunkAbsent(@Nullable ChunkHolder var1, int var2) {
      return var1 == null || var1.getTicketLevel() > var2;
   }

   @Override
   public boolean hasChunk(int var1, int var2) {
      ChunkHolder var3 = this.getVisibleChunkIfPresent(new ChunkPos(var1, var2).toLong());
      int var4 = ChunkLevel.byStatus(ChunkStatus.FULL);
      return !this.chunkAbsent(var3, var4);
   }

   @Nullable
   @Override
   public LightChunk getChunkForLighting(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);
      ChunkHolder var5 = this.getVisibleChunkIfPresent(var3);
      return var5 == null ? null : var5.getChunkIfPresentUnchecked(ChunkStatus.INITIALIZE_LIGHT.getParent());
   }

   public Level getLevel() {
      return this.level;
   }

   public boolean pollTask() {
      return this.mainThreadProcessor.pollTask();
   }

   boolean runDistanceManagerUpdates() {
      boolean var1 = this.distanceManager.runAllUpdates(this.chunkMap);
      boolean var2 = this.chunkMap.promoteChunkMap();
      this.chunkMap.runGenerationTasks();
      if (!var1 && !var2) {
         return false;
      } else {
         this.clearCache();
         return true;
      }
   }

   public boolean isPositionTicking(long var1) {
      if (!this.level.shouldTickBlocksAt(var1)) {
         return false;
      } else {
         ChunkHolder var3 = this.getVisibleChunkIfPresent(var1);
         return var3 == null ? false : var3.getTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).isSuccess();
      }
   }

   public void save(boolean var1) {
      this.runDistanceManagerUpdates();
      this.chunkMap.saveAllChunks(var1);
   }

   @Override
   public void close() throws IOException {
      this.save(true);
      this.dataStorage.close();
      this.lightEngine.close();
      this.chunkMap.close();
   }

   @Override
   public void tick(BooleanSupplier var1, boolean var2) {
      this.level.getProfiler().push("purge");
      if (this.level.tickRateManager().runsNormally() || !var2) {
         this.distanceManager.purgeStaleTickets();
      }

      this.runDistanceManagerUpdates();
      this.level.getProfiler().popPush("chunks");
      if (var2) {
         this.tickChunks();
         this.chunkMap.tick();
      }

      this.level.getProfiler().popPush("unload");
      this.chunkMap.tick(var1);
      this.level.getProfiler().pop();
      this.clearCache();
   }

   private void tickChunks() {
      long var1 = this.level.getGameTime();
      long var3 = var1 - this.lastInhabitedUpdate;
      this.lastInhabitedUpdate = var1;
      if (!this.level.isDebug()) {
         ProfilerFiller var5 = this.level.getProfiler();
         var5.push("pollingChunks");
         if (this.level.tickRateManager().runsNormally()) {
            List var6 = this.tickingChunks;

            try {
               var5.push("filteringTickingChunks");
               this.collectTickingChunks(var6);
               var5.popPush("shuffleChunks");
               Util.shuffle(var6, this.level.random);
               this.tickChunks(var5, var3, var6);
               var5.pop();
            } finally {
               var6.clear();
            }
         }

         this.broadcastChangedChunks(var5);
         var5.pop();
      }
   }

   private void broadcastChangedChunks(ProfilerFiller var1) {
      var1.push("broadcast");

      for (ChunkHolder var3 : this.chunkHoldersToBroadcast) {
         LevelChunk var4 = var3.getTickingChunk();
         if (var4 != null) {
            var3.broadcastChanges(var4);
         }
      }

      this.chunkHoldersToBroadcast.clear();
      var1.pop();
   }

   private void collectTickingChunks(List<LevelChunk> var1) {
      this.chunkMap.forEachSpawnCandidateChunk(var2 -> {
         LevelChunk var3 = var2.getTickingChunk();
         if (var3 != null && this.level.isNaturalSpawningAllowed(var2.getPos())) {
            var1.add(var3);
         }
      });
   }

   private void tickChunks(ProfilerFiller var1, long var2, List<LevelChunk> var4) {
      var1.popPush("naturalSpawnCount");
      int var5 = this.distanceManager.getNaturalSpawnChunkCount();
      NaturalSpawner.SpawnState var6 = NaturalSpawner.createState(
         var5, this.level.getAllEntities(), this::getFullChunk, new LocalMobCapCalculator(this.chunkMap)
      );
      this.lastSpawnState = var6;
      var1.popPush("spawnAndTick");
      boolean var7 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
      int var8 = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
      List var9;
      if (var7 && (this.spawnEnemies || this.spawnFriendlies)) {
         boolean var10 = this.level.getLevelData().getGameTime() % 400L == 0L;
         var9 = NaturalSpawner.getFilteredSpawningCategories(var6, this.spawnFriendlies, this.spawnEnemies, var10);
      } else {
         var9 = List.of();
      }

      for (LevelChunk var11 : var4) {
         ChunkPos var12 = var11.getPos();
         var11.incrementInhabitedTime(var2);
         if (!var9.isEmpty() && this.level.getWorldBorder().isWithinBounds(var12)) {
            NaturalSpawner.spawnForChunk(this.level, var11, var6, var9);
         }

         if (this.level.shouldTickBlocksAt(var12.toLong())) {
            this.level.tickChunk(var11, var8);
         }
      }

      var1.popPush("customSpawners");
      if (var7) {
         this.level.tickCustomSpawners(this.spawnEnemies, this.spawnFriendlies);
      }
   }

   private void getFullChunk(long var1, Consumer<LevelChunk> var3) {
      ChunkHolder var4 = this.getVisibleChunkIfPresent(var1);
      if (var4 != null) {
         var4.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).ifSuccess(var3);
      }
   }

   @Override
   public String gatherStats() {
      return Integer.toString(this.getLoadedChunksCount());
   }

   @VisibleForTesting
   public int getPendingTasksCount() {
      return this.mainThreadProcessor.getPendingTasksCount();
   }

   public ChunkGenerator getGenerator() {
      return this.chunkMap.generator();
   }

   public ChunkGeneratorStructureState getGeneratorState() {
      return this.chunkMap.generatorState();
   }

   public RandomState randomState() {
      return this.chunkMap.randomState();
   }

   @Override
   public int getLoadedChunksCount() {
      return this.chunkMap.size();
   }

   public void blockChanged(BlockPos var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getX());
      int var3 = SectionPos.blockToSectionCoord(var1.getZ());
      ChunkHolder var4 = this.getVisibleChunkIfPresent(ChunkPos.asLong(var2, var3));
      if (var4 != null && var4.blockChanged(var1)) {
         this.chunkHoldersToBroadcast.add(var4);
      }
   }

   @Override
   public void onLightUpdate(LightLayer var1, SectionPos var2) {
      this.mainThreadProcessor.execute(() -> {
         ChunkHolder var3 = this.getVisibleChunkIfPresent(var2.chunk().toLong());
         if (var3 != null && var3.sectionLightChanged(var1, var2.y())) {
            this.chunkHoldersToBroadcast.add(var3);
         }
      });
   }

   public <T> void addRegionTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      this.distanceManager.addRegionTicket(var1, var2, var3, var4);
   }

   public <T> void removeRegionTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      this.distanceManager.removeRegionTicket(var1, var2, var3, var4);
   }

   @Override
   public void updateChunkForced(ChunkPos var1, boolean var2) {
      this.distanceManager.updateChunkForced(var1, var2);
   }

   public void move(ServerPlayer var1) {
      if (!var1.isRemoved()) {
         this.chunkMap.move(var1);
      }
   }

   public void removeEntity(Entity var1) {
      this.chunkMap.removeEntity(var1);
   }

   public void addEntity(Entity var1) {
      this.chunkMap.addEntity(var1);
   }

   public void broadcastAndSend(Entity var1, Packet<?> var2) {
      this.chunkMap.broadcastAndSend(var1, var2);
   }

   public void broadcast(Entity var1, Packet<?> var2) {
      this.chunkMap.broadcast(var1, var2);
   }

   public void setViewDistance(int var1) {
      this.chunkMap.setServerViewDistance(var1);
   }

   public void setSimulationDistance(int var1) {
      this.distanceManager.updateSimulationDistance(var1);
   }

   @Override
   public void setSpawnSettings(boolean var1) {
      this.spawnEnemies = var1;
      this.spawnFriendlies = this.spawnFriendlies;
   }

   public String getChunkDebugData(ChunkPos var1) {
      return this.chunkMap.getChunkDebugData(var1);
   }

   public DimensionDataStorage getDataStorage() {
      return this.dataStorage;
   }

   public PoiManager getPoiManager() {
      return this.chunkMap.getPoiManager();
   }

   public ChunkScanAccess chunkScanner() {
      return this.chunkMap.chunkScanner();
   }

   @Nullable
   @VisibleForDebug
   public NaturalSpawner.SpawnState getLastSpawnState() {
      return this.lastSpawnState;
   }

   public void removeTicketsOnClosing() {
      this.distanceManager.removeTicketsOnClosing();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   final class MainThreadExecutor extends BlockableEventLoop<Runnable> {
      MainThreadExecutor(final Level nullx) {
         super("Chunk source main thread executor for " + nullx.dimension().location());
      }

      @Override
      public void managedBlock(BooleanSupplier var1) {
         super.managedBlock(() -> MinecraftServer.throwIfFatalException() && var1.getAsBoolean());
      }

      @Override
      protected Runnable wrapRunnable(Runnable var1) {
         return var1;
      }

      @Override
      protected boolean shouldRun(Runnable var1) {
         return true;
      }

      @Override
      protected boolean scheduleExecutables() {
         return true;
      }

      @Override
      protected Thread getRunningThread() {
         return ServerChunkCache.this.mainThread;
      }

      @Override
      protected void doRunTask(Runnable var1) {
         ServerChunkCache.this.level.getProfiler().incrementCounter("runTask");
         super.doRunTask(var1);
      }

      @Override
      protected boolean pollTask() {
         if (ServerChunkCache.this.runDistanceManagerUpdates()) {
            return true;
         } else {
            ServerChunkCache.this.lightEngine.tryScheduleUpdate();
            return super.pollTask();
         }
      }
   }
}
