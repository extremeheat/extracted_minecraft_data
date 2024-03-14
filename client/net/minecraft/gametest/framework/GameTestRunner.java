package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class GameTestRunner {
   public static final int DEFAULT_TESTS_PER_ROW = 8;
   private static final Logger LOGGER = LogUtils.getLogger();
   final ServerLevel level;
   private final GameTestTicker testTicker;
   private final List<GameTestInfo> allTestInfos;
   private ImmutableList<GameTestBatch> batches;
   final List<GameTestBatchListener> batchListeners = Lists.newArrayList();
   private final List<GameTestInfo> scheduledForRerun = Lists.newArrayList();
   private final GameTestRunner.GameTestBatcher testBatcher;
   private boolean stopped = true;
   @Nullable
   GameTestBatch currentBatch;
   private final GameTestRunner.StructureSpawner existingStructureSpawner;
   private final GameTestRunner.StructureSpawner newStructureSpawner;

   protected GameTestRunner(
      GameTestRunner.GameTestBatcher var1,
      Collection<GameTestBatch> var2,
      ServerLevel var3,
      GameTestTicker var4,
      GameTestRunner.StructureSpawner var5,
      GameTestRunner.StructureSpawner var6
   ) {
      super();
      this.level = var3;
      this.testTicker = var4;
      this.testBatcher = var1;
      this.existingStructureSpawner = var5;
      this.newStructureSpawner = var6;
      this.batches = ImmutableList.copyOf(var2);
      this.allTestInfos = this.batches.stream().flatMap(var0 -> var0.gameTestInfos().stream()).collect(Collectors.toList());
      var4.setRunner(this);
      this.allTestInfos.forEach(var0 -> var0.addListener(new ReportGameListener()));
   }

   public List<GameTestInfo> getTestInfos() {
      return this.allTestInfos;
   }

   public void start() {
      this.stopped = false;
      this.runBatch(0);
   }

   public void stop() {
      this.stopped = true;
      if (this.currentBatch != null) {
         this.currentBatch.afterBatchFunction().accept(this.level);
      }
   }

   public void rerunTest(GameTestInfo var1) {
      GameTestInfo var2 = var1.copyReset();
      var1.getListeners().forEach(var3 -> var3.testAddedForRerun(var1, var2, this));
      this.allTestInfos.add(var2);
      this.scheduledForRerun.add(var2);
      if (this.stopped) {
         this.runScheduledRerunTests();
      }
   }

   void runBatch(final int var1) {
      if (var1 >= this.batches.size()) {
         this.runScheduledRerunTests();
      } else {
         this.currentBatch = (GameTestBatch)this.batches.get(var1);
         Collection var2 = this.createStructuresForBatch(this.currentBatch.gameTestInfos());
         String var3 = this.currentBatch.name();
         LOGGER.info("Running test batch '{}' ({} tests)...", var3, var2.size());
         this.currentBatch.beforeBatchFunction().accept(this.level);
         this.batchListeners.forEach(var1x -> var1x.testBatchStarting(this.currentBatch));
         final MultipleTestTracker var4 = new MultipleTestTracker();
         var2.forEach(var4::addTestToTrack);
         var4.addListener(new GameTestListener() {
            private void testCompleted() {
               if (var4.isDone()) {
                  GameTestRunner.this.currentBatch.afterBatchFunction().accept(GameTestRunner.this.level);
                  GameTestRunner.this.batchListeners.forEach(var1xxx -> var1xxx.testBatchFinished(GameTestRunner.this.currentBatch));
                  LongArraySet var1x = new LongArraySet(GameTestRunner.this.level.getForcedChunks());
                  var1x.forEach(var1xxx -> GameTestRunner.this.level.setChunkForced(ChunkPos.getX(var1xxx), ChunkPos.getZ(var1xxx), false));
                  GameTestRunner.this.runBatch(var1 + 1);
               }
            }

            @Override
            public void testStructureLoaded(GameTestInfo var1x) {
            }

            @Override
            public void testPassed(GameTestInfo var1x, GameTestRunner var2) {
               this.testCompleted();
            }

            @Override
            public void testFailed(GameTestInfo var1x, GameTestRunner var2) {
               this.testCompleted();
            }

            @Override
            public void testAddedForRerun(GameTestInfo var1x, GameTestInfo var2, GameTestRunner var3) {
            }
         });
         var2.forEach(this.testTicker::add);
      }
   }

   private void runScheduledRerunTests() {
      if (!this.scheduledForRerun.isEmpty()) {
         LOGGER.info(
            "Starting re-run of tests: {}", this.scheduledForRerun.stream().map(var0 -> var0.getTestFunction().testName()).collect(Collectors.joining(", "))
         );
         this.batches = ImmutableList.copyOf(this.testBatcher.batch(this.scheduledForRerun));
         this.scheduledForRerun.clear();
         this.stopped = false;
         this.runBatch(0);
      } else {
         this.batches = ImmutableList.of();
         this.stopped = true;
      }
   }

   public void addListener(GameTestBatchListener var1) {
      this.batchListeners.add(var1);
   }

   private Collection<GameTestInfo> createStructuresForBatch(Collection<GameTestInfo> var1) {
      return var1.stream().map(this::spawn).flatMap(Optional::stream).toList();
   }

   private Optional<GameTestInfo> spawn(GameTestInfo var1) {
      return var1.getStructureBlockPos() == null ? this.newStructureSpawner.spawnStructure(var1) : this.existingStructureSpawner.spawnStructure(var1);
   }

   public static void clearMarkers(ServerLevel var0) {
      DebugPackets.sendGameTestClearPacket(var0);
   }

   public static class Builder {
      private final ServerLevel level;
      private final GameTestTicker testTicker = GameTestTicker.SINGLETON;
      private final GameTestRunner.GameTestBatcher batcher = GameTestBatchFactory.fromGameTestInfo();
      private final GameTestRunner.StructureSpawner existingStructureSpawner = GameTestRunner.StructureSpawner.IN_PLACE;
      private GameTestRunner.StructureSpawner newStructureSpawner = GameTestRunner.StructureSpawner.NOT_SET;
      private final Collection<GameTestBatch> batches;

      private Builder(Collection<GameTestBatch> var1, ServerLevel var2) {
         super();
         this.batches = var1;
         this.level = var2;
      }

      public static GameTestRunner.Builder fromBatches(Collection<GameTestBatch> var0, ServerLevel var1) {
         return new GameTestRunner.Builder(var0, var1);
      }

      public static GameTestRunner.Builder fromInfo(Collection<GameTestInfo> var0, ServerLevel var1) {
         return fromBatches(GameTestBatchFactory.fromGameTestInfo().batch(var0), var1);
      }

      public GameTestRunner.Builder newStructureSpawner(GameTestRunner.StructureSpawner var1) {
         this.newStructureSpawner = var1;
         return this;
      }

      public GameTestRunner build() {
         return new GameTestRunner(this.batcher, this.batches, this.level, this.testTicker, this.existingStructureSpawner, this.newStructureSpawner);
      }
   }

   public interface GameTestBatcher {
      Collection<GameTestBatch> batch(Collection<GameTestInfo> var1);
   }

   public interface StructureSpawner {
      GameTestRunner.StructureSpawner IN_PLACE = var0 -> Optional.of(var0.prepareTestStructure().placeStructure().startExecution(1));
      GameTestRunner.StructureSpawner NOT_SET = var0 -> Optional.empty();

      Optional<GameTestInfo> spawnStructure(GameTestInfo var1);
   }
}
