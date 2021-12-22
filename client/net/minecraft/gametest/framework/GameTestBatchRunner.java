package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestBatchRunner {
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockPos firstTestNorthWestCorner;
   final ServerLevel level;
   private final GameTestTicker testTicker;
   private final int testsPerRow;
   private final List<GameTestInfo> allTestInfos;
   private final List<Pair<GameTestBatch, Collection<GameTestInfo>>> batches;
   private final BlockPos.MutableBlockPos nextTestNorthWestCorner;

   public GameTestBatchRunner(Collection<GameTestBatch> var1, BlockPos var2, Rotation var3, ServerLevel var4, GameTestTicker var5, int var6) {
      super();
      this.nextTestNorthWestCorner = var2.mutable();
      this.firstTestNorthWestCorner = var2;
      this.level = var4;
      this.testTicker = var5;
      this.testsPerRow = var6;
      this.batches = (List)var1.stream().map((var2x) -> {
         Collection var3x = (Collection)var2x.getTestFunctions().stream().map((var2) -> {
            return new GameTestInfo(var2, var3, var4);
         }).collect(ImmutableList.toImmutableList());
         return Pair.of(var2x, var3x);
      }).collect(ImmutableList.toImmutableList());
      this.allTestInfos = (List)this.batches.stream().flatMap((var0) -> {
         return ((Collection)var0.getSecond()).stream();
      }).collect(ImmutableList.toImmutableList());
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
         Objects.requireNonNull(var7);
         var4.forEach(var7::addTestToTrack);
         var7.addListener(new GameTestListener() {
            private void testCompleted() {
               if (var7.isDone()) {
                  var3.runAfterBatchFunction(GameTestBatchRunner.this.level);
                  GameTestBatchRunner.this.runBatch(var1 + 1);
               }

            }

            public void testStructureLoaded(GameTestInfo var1x) {
            }

            public void testPassed(GameTestInfo var1x) {
               this.testCompleted();
            }

            public void testFailed(GameTestInfo var1x) {
               this.testCompleted();
            }
         });
         var4.forEach((var2x) -> {
            BlockPos var3 = (BlockPos)var5.get(var2x);
            GameTestRunner.runTest(var2x, var3, this.testTicker);
         });
      }
   }

   private Map<GameTestInfo, BlockPos> createStructuresForBatch(Collection<GameTestInfo> var1) {
      HashMap var2 = Maps.newHashMap();
      int var3 = 0;
      AABB var4 = new AABB(this.nextTestNorthWestCorner);
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         GameTestInfo var6 = (GameTestInfo)var5.next();
         BlockPos var7 = new BlockPos(this.nextTestNorthWestCorner);
         StructureBlockEntity var8 = StructureUtils.spawnStructure(var6.getStructureName(), var7, var6.getRotation(), 2, this.level, true);
         AABB var9 = StructureUtils.getStructureBounds(var8);
         var6.setStructureBlockPos(var8.getBlockPos());
         var2.put(var6, new BlockPos(this.nextTestNorthWestCorner));
         var4 = var4.minmax(var9);
         this.nextTestNorthWestCorner.move((int)var9.getXsize() + 5, 0, 0);
         if (var3++ % this.testsPerRow == this.testsPerRow - 1) {
            this.nextTestNorthWestCorner.move(0, 0, (int)var4.getZsize() + 6);
            this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
            var4 = new AABB(this.nextTestNorthWestCorner);
         }
      }

      return var2;
   }
}
