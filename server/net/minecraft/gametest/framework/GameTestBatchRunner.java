package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
   private final ServerLevel level;
   private final GameTestTicker testTicker;
   private final int testsPerRow;
   private final List<GameTestInfo> allTestInfos = Lists.newArrayList();
   private final Map<GameTestInfo, BlockPos> northWestCorners = Maps.newHashMap();
   private final List<Pair<GameTestBatch, Collection<GameTestInfo>>> batches = Lists.newArrayList();
   private MultipleTestTracker currentBatchTracker;
   private int currentBatchIndex = 0;
   private BlockPos.MutableBlockPos nextTestNorthWestCorner;

   public GameTestBatchRunner(Collection<GameTestBatch> var1, BlockPos var2, Rotation var3, ServerLevel var4, GameTestTicker var5, int var6) {
      super();
      this.nextTestNorthWestCorner = var2.mutable();
      this.firstTestNorthWestCorner = var2;
      this.level = var4;
      this.testTicker = var5;
      this.testsPerRow = var6;
      var1.forEach((var3x) -> {
         ArrayList var4x = Lists.newArrayList();
         Collection var5 = var3x.getTestFunctions();
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            TestFunction var7 = (TestFunction)var6.next();
            GameTestInfo var8 = new GameTestInfo(var7, var3, var4);
            var4x.add(var8);
            this.allTestInfos.add(var8);
         }

         this.batches.add(Pair.of(var3x, var4x));
      });
   }

   public List<GameTestInfo> getTestInfos() {
      return this.allTestInfos;
   }

   public void start() {
      this.runBatch(0);
   }

   private void runBatch(int var1) {
      this.currentBatchIndex = var1;
      this.currentBatchTracker = new MultipleTestTracker();
      if (var1 < this.batches.size()) {
         Pair var2 = (Pair)this.batches.get(this.currentBatchIndex);
         GameTestBatch var3 = (GameTestBatch)var2.getFirst();
         Collection var4 = (Collection)var2.getSecond();
         this.createStructuresForBatch(var4);
         var3.runBeforeBatchFunction(this.level);
         String var5 = var3.getName();
         LOGGER.info("Running test batch '" + var5 + "' (" + var4.size() + " tests)...");
         var4.forEach((var1x) -> {
            this.currentBatchTracker.addTestToTrack(var1x);
            this.currentBatchTracker.addListener(new GameTestListener() {
               public void testStructureLoaded(GameTestInfo var1) {
               }

               public void testFailed(GameTestInfo var1) {
                  GameTestBatchRunner.this.testCompleted(var1);
               }
            });
            BlockPos var2 = (BlockPos)this.northWestCorners.get(var1x);
            GameTestRunner.runTest(var1x, var2, this.testTicker);
         });
      }
   }

   private void testCompleted(GameTestInfo var1) {
      if (this.currentBatchTracker.isDone()) {
         this.runBatch(this.currentBatchIndex + 1);
      }

   }

   private void createStructuresForBatch(Collection<GameTestInfo> var1) {
      int var2 = 0;
      AABB var3 = new AABB(this.nextTestNorthWestCorner);
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GameTestInfo var5 = (GameTestInfo)var4.next();
         BlockPos var6 = new BlockPos(this.nextTestNorthWestCorner);
         StructureBlockEntity var7 = StructureUtils.spawnStructure(var5.getStructureName(), var6, var5.getRotation(), 2, this.level, true);
         AABB var8 = StructureUtils.getStructureBounds(var7);
         var5.setStructureBlockPos(var7.getBlockPos());
         this.northWestCorners.put(var5, new BlockPos(this.nextTestNorthWestCorner));
         var3 = var3.minmax(var8);
         this.nextTestNorthWestCorner.move((int)var8.getXsize() + 5, 0, 0);
         if (var2++ % this.testsPerRow == this.testsPerRow - 1) {
            this.nextTestNorthWestCorner.move(0, 0, (int)var3.getZsize() + 6);
            this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
            var3 = new AABB(this.nextTestNorthWestCorner);
         }
      }

   }
}
