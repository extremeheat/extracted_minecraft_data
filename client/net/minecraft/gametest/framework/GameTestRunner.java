package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
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
   private final GameTestBatcher testBatcher;
   private boolean stopped = true;
   @Nullable
   GameTestBatch currentBatch;
   private final StructureSpawner existingStructureSpawner;
   private final StructureSpawner newStructureSpawner;
   final boolean haltOnError;

   protected GameTestRunner(GameTestBatcher var1, Collection<GameTestBatch> var2, ServerLevel var3, GameTestTicker var4, StructureSpawner var5, StructureSpawner var6, boolean var7) {
      super();
      this.level = var3;
      this.testTicker = var4;
      this.testBatcher = var1;
      this.existingStructureSpawner = var5;
      this.newStructureSpawner = var6;
      this.batches = ImmutableList.copyOf(var2);
      this.haltOnError = var7;
      this.allTestInfos = (List)this.batches.stream().flatMap((var0) -> {
         return var0.gameTestInfos().stream();
      }).collect(Util.toMutableList());
      var4.setRunner(this);
      this.allTestInfos.forEach((var0) -> {
         var0.addListener(new ReportGameListener());
      });
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
      var1.getListeners().forEach((var3) -> {
         var3.testAddedForRerun(var1, var2, this);
      });
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
         this.existingStructureSpawner.onBatchStart(this.level);
         this.newStructureSpawner.onBatchStart(this.level);
         Collection var2 = this.createStructuresForBatch(this.currentBatch.gameTestInfos());
         String var3 = this.currentBatch.name();
         LOGGER.info("Running test batch '{}' ({} tests)...", var3, var2.size());
         this.currentBatch.beforeBatchFunction().accept(this.level);
         this.batchListeners.forEach((var1x) -> {
            var1x.testBatchStarting(this.currentBatch);
         });
         final MultipleTestTracker var4 = new MultipleTestTracker();
         Objects.requireNonNull(var4);
         var2.forEach(var4::addTestToTrack);
         var4.addListener(new GameTestListener() {
            private void testCompleted() {
               if (var4.isDone()) {
                  GameTestRunner.this.currentBatch.afterBatchFunction().accept(GameTestRunner.this.level);
                  GameTestRunner.this.batchListeners.forEach((var1xx) -> {
                     var1xx.testBatchFinished(GameTestRunner.this.currentBatch);
                  });
                  LongArraySet var1x = new LongArraySet(GameTestRunner.this.level.getForcedChunks());
                  var1x.forEach((var1xx) -> {
                     GameTestRunner.this.level.setChunkForced(ChunkPos.getX(var1xx), ChunkPos.getZ(var1xx), false);
                  });
                  GameTestRunner.this.runBatch(var1 + 1);
               }

            }

            public void testStructureLoaded(GameTestInfo var1x) {
            }

            public void testPassed(GameTestInfo var1x, GameTestRunner var2) {
               this.testCompleted();
            }

            public void testFailed(GameTestInfo var1x, GameTestRunner var2) {
               if (GameTestRunner.this.haltOnError) {
                  GameTestRunner.this.currentBatch.afterBatchFunction().accept(GameTestRunner.this.level);
                  LongArraySet var3 = new LongArraySet(GameTestRunner.this.level.getForcedChunks());
                  var3.forEach((var1xx) -> {
                     GameTestRunner.this.level.setChunkForced(ChunkPos.getX(var1xx), ChunkPos.getZ(var1xx), false);
                  });
                  GameTestTicker.SINGLETON.clear();
               } else {
                  this.testCompleted();
               }

            }

            public void testAddedForRerun(GameTestInfo var1x, GameTestInfo var2, GameTestRunner var3) {
            }
         });
         GameTestTicker var10001 = this.testTicker;
         Objects.requireNonNull(var10001);
         var2.forEach(var10001::add);
      }
   }

   private void runScheduledRerunTests() {
      if (!this.scheduledForRerun.isEmpty()) {
         LOGGER.info("Starting re-run of tests: {}", this.scheduledForRerun.stream().map((var0) -> {
            return var0.getTestFunction().testName();
         }).collect(Collectors.joining(", ")));
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

   public interface GameTestBatcher {
      Collection<GameTestBatch> batch(Collection<GameTestInfo> var1);
   }

   public interface StructureSpawner {
      StructureSpawner IN_PLACE = (var0) -> {
         return Optional.of(var0.prepareTestStructure().placeStructure().startExecution(1));
      };
      StructureSpawner NOT_SET = (var0) -> {
         return Optional.empty();
      };

      Optional<GameTestInfo> spawnStructure(GameTestInfo var1);

      default void onBatchStart(ServerLevel var1) {
      }
   }

   public static class Builder {
      private final ServerLevel level;
      private final GameTestTicker testTicker;
      private GameTestBatcher batcher;
      private StructureSpawner existingStructureSpawner;
      private StructureSpawner newStructureSpawner;
      private final Collection<GameTestBatch> batches;
      private boolean haltOnError;

      private Builder(Collection<GameTestBatch> var1, ServerLevel var2) {
         super();
         this.testTicker = GameTestTicker.SINGLETON;
         this.batcher = GameTestBatchFactory.fromGameTestInfo();
         this.existingStructureSpawner = GameTestRunner.StructureSpawner.IN_PLACE;
         this.newStructureSpawner = GameTestRunner.StructureSpawner.NOT_SET;
         this.haltOnError = false;
         this.batches = var1;
         this.level = var2;
      }

      public static Builder fromBatches(Collection<GameTestBatch> var0, ServerLevel var1) {
         return new Builder(var0, var1);
      }

      public static Builder fromInfo(Collection<GameTestInfo> var0, ServerLevel var1) {
         return fromBatches(GameTestBatchFactory.fromGameTestInfo().batch(var0), var1);
      }

      public Builder haltOnError(boolean var1) {
         this.haltOnError = var1;
         return this;
      }

      public Builder newStructureSpawner(StructureSpawner var1) {
         this.newStructureSpawner = var1;
         return this;
      }

      public Builder existingStructureSpawner(StructureGridSpawner var1) {
         this.existingStructureSpawner = var1;
         return this;
      }

      public Builder batcher(GameTestBatcher var1) {
         this.batcher = var1;
         return this;
      }

      public GameTestRunner build() {
         return new GameTestRunner(this.batcher, this.batches, this.level, this.testTicker, this.existingStructureSpawner, this.newStructureSpawner, this.haltOnError);
      }
   }
}
