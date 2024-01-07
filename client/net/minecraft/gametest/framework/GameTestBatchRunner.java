package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class GameTestBatchRunner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final BlockPos firstTestNorthWestCorner;
   final ServerLevel level;
   private final GameTestTicker testTicker;
   private final int testsPerRow;
   private final List<GameTestInfo> allTestInfos;
   private final List<Pair<GameTestBatch, Collection<GameTestInfo>>> batches;
   private int count;
   private AABB rowBounds;
   private final BlockPos.MutableBlockPos nextTestNorthWestCorner;

   public GameTestBatchRunner(Collection<GameTestBatch> var1, BlockPos var2, Rotation var3, ServerLevel var4, GameTestTicker var5, int var6) {
      super();
      this.nextTestNorthWestCorner = var2.mutable();
      this.rowBounds = new AABB(this.nextTestNorthWestCorner);
      this.firstTestNorthWestCorner = var2;
      this.level = var4;
      this.testTicker = var5;
      this.testsPerRow = var6;
      this.batches = var1.stream().map(var2x -> {
         Collection var3xx = var2x.getTestFunctions().stream().map(var2xx -> new GameTestInfo(var2xx, var3, var4)).collect(ImmutableList.toImmutableList());
         return Pair.of(var2x, var3xx);
      }).collect(ImmutableList.toImmutableList());
      this.allTestInfos = this.batches.stream().flatMap(var0 -> ((Collection)var0.getSecond()).stream()).collect(ImmutableList.toImmutableList());
   }

   public List<GameTestInfo> getTestInfos() {
      return this.allTestInfos;
   }

   public void start() {
      this.runBatch(0);
   }

   void runBatch(final int var1) {
      if (var1 < this.batches.size()) {
         Pair var2 = (Pair)this.batches.get(var1);
         final GameTestBatch var3 = (GameTestBatch)var2.getFirst();
         Collection var4 = (Collection)var2.getSecond();
         Map var5 = this.createStructuresForBatch(var4);
         String var6 = var3.getName();
         LOGGER.info("Running test batch '{}' ({} tests)...", var6, var4.size());
         var3.runBeforeBatchFunction(this.level);
         final MultipleTestTracker var7 = new MultipleTestTracker();
         var4.forEach(var7::addTestToTrack);
         var7.addListener(new GameTestListener() {
            private void testCompleted() {
               if (var7.isDone()) {
                  var3.runAfterBatchFunction(GameTestBatchRunner.this.level);
                  LongArraySet var1x = new LongArraySet(GameTestBatchRunner.this.level.getForcedChunks());
                  var1x.forEach(var1xxx -> GameTestBatchRunner.this.level.setChunkForced(ChunkPos.getX(var1xxx), ChunkPos.getZ(var1xxx), false));
                  GameTestBatchRunner.this.runBatch(var1 + 1);
               }
            }

            @Override
            public void testStructureLoaded(GameTestInfo var1x) {
            }

            @Override
            public void testPassed(GameTestInfo var1x) {
               this.testCompleted();
            }

            @Override
            public void testFailed(GameTestInfo var1x) {
               this.testCompleted();
            }
         });
         var4.forEach(var2x -> {
            BlockPos var3xx = (BlockPos)var5.get(var2x);
            GameTestRunner.runTest(var2x, var3xx, this.testTicker);
         });
      }
   }

   private Map<GameTestInfo, BlockPos> createStructuresForBatch(Collection<GameTestInfo> var1) {
      HashMap var2 = Maps.newHashMap();

      for(GameTestInfo var4 : var1) {
         BlockPos var5 = new BlockPos(this.nextTestNorthWestCorner);
         StructureBlockEntity var6 = StructureUtils.prepareTestStructure(var4, var5, var4.getRotation(), this.level);
         AABB var7 = StructureUtils.getStructureBounds(var6);
         var4.setStructureBlockPos(var6.getBlockPos());
         var2.put(var4, new BlockPos(this.nextTestNorthWestCorner));
         this.rowBounds = this.rowBounds.minmax(var7);
         this.nextTestNorthWestCorner.move((int)var7.getXsize() + 5, 0, 0);
         if (this.count++ % this.testsPerRow == this.testsPerRow - 1) {
            this.nextTestNorthWestCorner.move(0, 0, (int)this.rowBounds.getZsize() + 6);
            this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
            this.rowBounds = new AABB(this.nextTestNorthWestCorner);
         }
      }

      return var2;
   }
}
