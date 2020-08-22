package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestBatchRunner {
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockPos startPos;
   private final ServerLevel level;
   private final GameTestTicker testTicker;
   private final List allTestInfos = Lists.newArrayList();
   private final List batches = Lists.newArrayList();
   private MultipleTestTracker currentBatchTracker;
   private int currentBatchIndex = 0;
   private BlockPos.MutableBlockPos nextTestPos;
   private int maxDepthOnThisRow = 0;

   public GameTestBatchRunner(Collection var1, BlockPos var2, ServerLevel var3, GameTestTicker var4) {
      this.nextTestPos = new BlockPos.MutableBlockPos(var2);
      this.startPos = var2;
      this.level = var3;
      this.testTicker = var4;
      var1.forEach((var2x) -> {
         ArrayList var3x = Lists.newArrayList();
         Collection var4 = var2x.getTestFunctions();
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            TestFunction var6 = (TestFunction)var5.next();
            GameTestInfo var7 = new GameTestInfo(var6, var3);
            var3x.add(var7);
            this.allTestInfos.add(var7);
         }

         this.batches.add(Pair.of(var2x, var3x));
      });
   }

   public List getTestInfos() {
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
            this.currentBatchTracker.add(var1x);
            this.currentBatchTracker.setListener(new GameTestListener() {
               public void testStructureLoaded(GameTestInfo var1) {
               }

               public void testFailed(GameTestInfo var1) {
                  GameTestBatchRunner.this.testCompleted(var1);
               }
            });
            GameTestRunner.runTest(var1x, this.testTicker);
         });
      }
   }

   private void testCompleted(GameTestInfo var1) {
      if (this.currentBatchTracker.isDone()) {
         this.runBatch(this.currentBatchIndex + 1);
      }

   }

   private void createStructuresForBatch(Collection var1) {
      int var2 = 0;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         GameTestInfo var4 = (GameTestInfo)var3.next();
         BlockPos var5 = new BlockPos(this.nextTestPos);
         var4.assignPosition(var5);
         StructureUtils.spawnStructure(var4.getStructureName(), var5, 2, this.level, true);
         BlockPos var6 = var4.getStructureSize();
         int var7 = var6 == null ? 1 : var6.getX();
         int var8 = var6 == null ? 1 : var6.getZ();
         this.maxDepthOnThisRow = Math.max(this.maxDepthOnThisRow, var8);
         this.nextTestPos.move(var7 + 4, 0, 0);
         if (var2++ % 8 == 0) {
            this.nextTestPos.move(0, 0, this.maxDepthOnThisRow + 5);
            this.nextTestPos.setX(this.startPos.getX());
            this.maxDepthOnThisRow = 0;
         }
      }

   }
}
