package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;

public class ServerChunkCache extends ChunkSource {
   private static final int MAGIC_NUMBER = (int)Math.pow(17.0D, 2.0D);
   private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
   private final DistanceManager distanceManager;
   private final ChunkGenerator<?> generator;
   private final ServerLevel level;
   private final Thread mainThread;
   private final ThreadedLevelLightEngine lightEngine;
   private final ServerChunkCache.MainThreadExecutor mainThreadProcessor;
   public final ChunkMap chunkMap;
   private final DimensionDataStorage dataStorage;
   private long lastInhabitedUpdate;
   private boolean spawnEnemies = true;
   private boolean spawnFriendlies = true;
   private final long[] lastChunkPos = new long[4];
   private final ChunkStatus[] lastChunkStatus = new ChunkStatus[4];
   private final ChunkAccess[] lastChunk = new ChunkAccess[4];

   public ServerChunkCache(ServerLevel var1, File var2, DataFixer var3, StructureManager var4, Executor var5, ChunkGenerator<?> var6, int var7, ChunkProgressListener var8, Supplier<DimensionDataStorage> var9) {
      super();
      this.level = var1;
      this.mainThreadProcessor = new ServerChunkCache.MainThreadExecutor(var1);
      this.generator = var6;
      this.mainThread = Thread.currentThread();
      File var10 = var1.getDimension().getType().getStorageFolder(var2);
      File var11 = new File(var10, "data");
      var11.mkdirs();
      this.dataStorage = new DimensionDataStorage(var11, var3);
      this.chunkMap = new ChunkMap(var1, var2, var3, var4, var5, this.mainThreadProcessor, this, this.getGenerator(), var8, var9, var7);
      this.lightEngine = this.chunkMap.getLightEngine();
      this.distanceManager = this.chunkMap.getDistanceManager();
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

   private void storeInCache(long var1, ChunkAccess var3, ChunkStatus var4) {
      for(int var5 = 3; var5 > 0; --var5) {
         this.lastChunkPos[var5] = this.lastChunkPos[var5 - 1];
         this.lastChunkStatus[var5] = this.lastChunkStatus[var5 - 1];
         this.lastChunk[var5] = this.lastChunk[var5 - 1];
      }

      this.lastChunkPos[0] = var1;
      this.lastChunkStatus[0] = var4;
      this.lastChunk[0] = var3;
   }

   @Nullable
   public ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      if (Thread.currentThread() != this.mainThread) {
         return (ChunkAccess)CompletableFuture.supplyAsync(() -> {
            return this.getChunk(var1, var2, var3, var4);
         }, this.mainThreadProcessor).join();
      } else {
         long var5 = ChunkPos.asLong(var1, var2);

         ChunkAccess var8;
         for(int var7 = 0; var7 < 4; ++var7) {
            if (var5 == this.lastChunkPos[var7] && var3 == this.lastChunkStatus[var7]) {
               var8 = this.lastChunk[var7];
               if (var8 != null || !var4) {
                  return var8;
               }
            }
         }

         CompletableFuture var9 = this.getChunkFutureMainThread(var1, var2, var3, var4);
         this.mainThreadProcessor.managedBlock(var9::isDone);
         var8 = (ChunkAccess)((Either)var9.join()).map((var0) -> {
            return var0;
         }, (var1x) -> {
            if (var4) {
               throw new IllegalStateException("Chunk not there when requested: " + var1x);
            } else {
               return null;
            }
         });
         this.storeInCache(var5, var8, var3);
         return var8;
      }
   }

   @Nullable
   public LevelChunk getChunkNow(int var1, int var2) {
      if (Thread.currentThread() != this.mainThread) {
         return null;
      } else {
         long var3 = ChunkPos.asLong(var1, var2);

         for(int var5 = 0; var5 < 4; ++var5) {
            if (var3 == this.lastChunkPos[var5] && this.lastChunkStatus[var5] == ChunkStatus.FULL) {
               ChunkAccess var6 = this.lastChunk[var5];
               return var6 instanceof LevelChunk ? (LevelChunk)var6 : null;
            }
         }

         ChunkHolder var8 = this.getVisibleChunkIfPresent(var3);
         if (var8 == null) {
            return null;
         } else {
            Either var9 = (Either)var8.getFutureIfPresent(ChunkStatus.FULL).getNow((Object)null);
            if (var9 == null) {
               return null;
            } else {
               ChunkAccess var7 = (ChunkAccess)var9.left().orElse((Object)null);
               if (var7 != null) {
                  this.storeInCache(var3, var7, ChunkStatus.FULL);
                  if (var7 instanceof LevelChunk) {
                     return (LevelChunk)var7;
                  }
               }

               return null;
            }
         }
      }
   }

   private void clearCache() {
      Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
      Arrays.fill(this.lastChunkStatus, (Object)null);
      Arrays.fill(this.lastChunk, (Object)null);
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFuture(int var1, int var2, ChunkStatus var3, boolean var4) {
      boolean var5 = Thread.currentThread() == this.mainThread;
      CompletableFuture var6;
      if (var5) {
         var6 = this.getChunkFutureMainThread(var1, var2, var3, var4);
         this.mainThreadProcessor.managedBlock(var6::isDone);
      } else {
         var6 = CompletableFuture.supplyAsync(() -> {
            return this.getChunkFutureMainThread(var1, var2, var3, var4);
         }, this.mainThreadProcessor).thenCompose((var0) -> {
            return var0;
         });
      }

      return var6;
   }

   private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFutureMainThread(int var1, int var2, ChunkStatus var3, boolean var4) {
      ChunkPos var5 = new ChunkPos(var1, var2);
      long var6 = var5.toLong();
      int var8 = 33 + ChunkStatus.getDistance(var3);
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
               throw new IllegalStateException("No chunk holder after ticket has been added");
            }
         }
      }

      return this.chunkAbsent(var9, var8) ? ChunkHolder.UNLOADED_CHUNK_FUTURE : var9.getOrScheduleFuture(var3, this.chunkMap);
   }

   private boolean chunkAbsent(@Nullable ChunkHolder var1, int var2) {
      return var1 == null || var1.getTicketLevel() > var2;
   }

   public boolean hasChunk(int var1, int var2) {
      ChunkHolder var3 = this.getVisibleChunkIfPresent((new ChunkPos(var1, var2)).toLong());
      int var4 = 33 + ChunkStatus.getDistance(ChunkStatus.FULL);
      return !this.chunkAbsent(var3, var4);
   }

   public BlockGetter getChunkForLighting(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);
      ChunkHolder var5 = this.getVisibleChunkIfPresent(var3);
      if (var5 == null) {
         return null;
      } else {
         int var6 = CHUNK_STATUSES.size() - 1;

         while(true) {
            ChunkStatus var7 = (ChunkStatus)CHUNK_STATUSES.get(var6);
            Optional var8 = ((Either)var5.getFutureIfPresentUnchecked(var7).getNow(ChunkHolder.UNLOADED_CHUNK)).left();
            if (var8.isPresent()) {
               return (BlockGetter)var8.get();
            }

            if (var7 == ChunkStatus.LIGHT.getParent()) {
               return null;
            }

            --var6;
         }
      }
   }

   public Level getLevel() {
      return this.level;
   }

   public boolean pollTask() {
      return this.mainThreadProcessor.pollTask();
   }

   private boolean runDistanceManagerUpdates() {
      boolean var1 = this.distanceManager.runAllUpdates(this.chunkMap);
      boolean var2 = this.chunkMap.promoteChunkMap();
      if (!var1 && !var2) {
         return false;
      } else {
         this.clearCache();
         return true;
      }
   }

   public boolean isEntityTickingChunk(Entity var1) {
      long var2 = ChunkPos.asLong(Mth.floor(var1.x) >> 4, Mth.floor(var1.z) >> 4);
      return this.checkChunkFuture(var2, ChunkHolder::getEntityTickingChunkFuture);
   }

   public boolean isEntityTickingChunk(ChunkPos var1) {
      return this.checkChunkFuture(var1.toLong(), ChunkHolder::getEntityTickingChunkFuture);
   }

   public boolean isTickingChunk(BlockPos var1) {
      long var2 = ChunkPos.asLong(var1.getX() >> 4, var1.getZ() >> 4);
      return this.checkChunkFuture(var2, ChunkHolder::getTickingChunkFuture);
   }

   public boolean isInAccessibleChunk(Entity var1) {
      long var2 = ChunkPos.asLong(Mth.floor(var1.x) >> 4, Mth.floor(var1.z) >> 4);
      return this.checkChunkFuture(var2, ChunkHolder::getFullChunkFuture);
   }

   private boolean checkChunkFuture(long var1, Function<ChunkHolder, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>> var3) {
      ChunkHolder var4 = this.getVisibleChunkIfPresent(var1);
      if (var4 == null) {
         return false;
      } else {
         Either var5 = (Either)((CompletableFuture)var3.apply(var4)).getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK);
         return var5.left().isPresent();
      }
   }

   public void save(boolean var1) {
      this.runDistanceManagerUpdates();
      this.chunkMap.saveAllChunks(var1);
   }

   public void close() throws IOException {
      this.save(true);
      this.lightEngine.close();
      this.chunkMap.close();
   }

   public void tick(BooleanSupplier var1) {
      this.level.getProfiler().push("purge");
      this.distanceManager.purgeStaleTickets();
      this.runDistanceManagerUpdates();
      this.level.getProfiler().popPush("chunks");
      this.tickChunks();
      this.level.getProfiler().popPush("unload");
      this.chunkMap.tick(var1);
      this.level.getProfiler().pop();
      this.clearCache();
   }

   private void tickChunks() {
      long var1 = this.level.getGameTime();
      long var3 = var1 - this.lastInhabitedUpdate;
      this.lastInhabitedUpdate = var1;
      LevelData var5 = this.level.getLevelData();
      boolean var6 = var5.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES;
      boolean var7 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
      if (!var6) {
         this.level.getProfiler().push("pollingChunks");
         int var8 = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
         BlockPos var9 = this.level.getSharedSpawnPos();
         boolean var10 = var5.getGameTime() % 400L == 0L;
         this.level.getProfiler().push("naturalSpawnCount");
         int var11 = this.distanceManager.getNaturalSpawnChunkCount();
         MobCategory[] var12 = MobCategory.values();
         Object2IntMap var13 = this.level.getMobCategoryCounts();
         this.level.getProfiler().pop();
         this.chunkMap.getChunks().forEach((var10x) -> {
            Optional var11x = ((Either)var10x.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK)).left();
            if (var11x.isPresent()) {
               LevelChunk var12x = (LevelChunk)var11x.get();
               this.level.getProfiler().push("broadcast");
               var10x.broadcastChanges(var12x);
               this.level.getProfiler().pop();
               ChunkPos var13x = var10x.getPos();
               if (!this.chunkMap.noPlayersCloseForSpawning(var13x)) {
                  var12x.setInhabitedTime(var12x.getInhabitedTime() + var3);
                  if (var7 && (this.spawnEnemies || this.spawnFriendlies) && this.level.getWorldBorder().isWithinBounds(var12x.getPos())) {
                     this.level.getProfiler().push("spawner");
                     MobCategory[] var14 = var12;
                     int var15 = var12.length;

                     for(int var16 = 0; var16 < var15; ++var16) {
                        MobCategory var17 = var14[var16];
                        if (var17 != MobCategory.MISC && (!var17.isFriendly() || this.spawnFriendlies) && (var17.isFriendly() || this.spawnEnemies) && (!var17.isPersistent() || var10)) {
                           int var18 = var17.getMaxInstancesPerChunk() * var11 / MAGIC_NUMBER;
                           if (var13.getInt(var17) <= var18) {
                              NaturalSpawner.spawnCategoryForChunk(var17, this.level, var12x, var9);
                           }
                        }
                     }

                     this.level.getProfiler().pop();
                  }

                  this.level.tickChunk(var12x, var8);
               }
            }
         });
         this.level.getProfiler().push("customSpawners");
         if (var7) {
            this.generator.tickCustomSpawners(this.level, this.spawnEnemies, this.spawnFriendlies);
         }

         this.level.getProfiler().pop();
         this.level.getProfiler().pop();
      }

      this.chunkMap.tick();
   }

   public String gatherStats() {
      return "ServerChunkCache: " + this.getLoadedChunksCount();
   }

   @VisibleForTesting
   public int getPendingTasksCount() {
      return this.mainThreadProcessor.getPendingTasksCount();
   }

   public ChunkGenerator<?> getGenerator() {
      return this.generator;
   }

   public int getLoadedChunksCount() {
      return this.chunkMap.size();
   }

   public void blockChanged(BlockPos var1) {
      int var2 = var1.getX() >> 4;
      int var3 = var1.getZ() >> 4;
      ChunkHolder var4 = this.getVisibleChunkIfPresent(ChunkPos.asLong(var2, var3));
      if (var4 != null) {
         var4.blockChanged(var1.getX() & 15, var1.getY(), var1.getZ() & 15);
      }

   }

   public void onLightUpdate(LightLayer var1, SectionPos var2) {
      this.mainThreadProcessor.execute(() -> {
         ChunkHolder var3 = this.getVisibleChunkIfPresent(var2.chunk().toLong());
         if (var3 != null) {
            var3.sectionLightChanged(var1, var2.y());
         }

      });
   }

   public <T> void addRegionTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      this.distanceManager.addRegionTicket(var1, var2, var3, var4);
   }

   public <T> void removeRegionTicket(TicketType<T> var1, ChunkPos var2, int var3, T var4) {
      this.distanceManager.removeRegionTicket(var1, var2, var3, var4);
   }

   public void updateChunkForced(ChunkPos var1, boolean var2) {
      this.distanceManager.updateChunkForced(var1, var2);
   }

   public void move(ServerPlayer var1) {
      this.chunkMap.move(var1);
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
      this.chunkMap.setViewDistance(var1);
   }

   public void setSpawnSettings(boolean var1, boolean var2) {
      this.spawnEnemies = var1;
      this.spawnFriendlies = var2;
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

   // $FF: synthetic method
   public LevelLightEngine getLightEngine() {
      return this.getLightEngine();
   }

   // $FF: synthetic method
   public BlockGetter getLevel() {
      return this.getLevel();
   }

   final class MainThreadExecutor extends BlockableEventLoop<Runnable> {
      private MainThreadExecutor(Level var2) {
         super("Chunk source main thread executor for " + Registry.DIMENSION_TYPE.getKey(var2.getDimension().getType()));
      }

      protected Runnable wrapRunnable(Runnable var1) {
         return var1;
      }

      protected boolean shouldRun(Runnable var1) {
         return true;
      }

      protected boolean scheduleExecutables() {
         return true;
      }

      protected Thread getRunningThread() {
         return ServerChunkCache.this.mainThread;
      }

      protected boolean pollTask() {
         if (ServerChunkCache.this.runDistanceManagerUpdates()) {
            return true;
         } else {
            ServerChunkCache.this.lightEngine.tryScheduleUpdate();
            return super.pollTask();
         }
      }

      // $FF: synthetic method
      MainThreadExecutor(Level var2, Object var3) {
         this(var2);
      }
   }
}
