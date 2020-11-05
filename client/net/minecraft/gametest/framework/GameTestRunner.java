package net.minecraft.gametest.framework;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class GameTestRunner {
   public static void runTest(GameTestInfo var0, BlockPos var1, GameTestTicker var2) {
      var0.startExecution();
      var2.add(var0);
      var0.addListener(new ReportGameListener(var0, var2, var1));
      var0.spawnStructure(var1, 2);
   }

   public static Collection<GameTestInfo> runTestBatches(Collection<GameTestBatch> var0, BlockPos var1, Rotation var2, ServerLevel var3, GameTestTicker var4, int var5) {
      GameTestBatchRunner var6 = new GameTestBatchRunner(var0, var1, var2, var3, var4, var5);
      var6.start();
      return var6.getTestInfos();
   }

   public static Collection<GameTestInfo> runTests(Collection<TestFunction> var0, BlockPos var1, Rotation var2, ServerLevel var3, GameTestTicker var4, int var5) {
      return runTestBatches(groupTestsIntoBatches(var0), var1, var2, var3, var4, var5);
   }

   public static Collection<GameTestBatch> groupTestsIntoBatches(Collection<TestFunction> var0) {
      HashMap var1 = Maps.newHashMap();
      var0.forEach((var1x) -> {
         String var2 = var1x.getBatchName();
         Collection var3 = (Collection)var1.computeIfAbsent(var2, (var0) -> {
            return Lists.newArrayList();
         });
         var3.add(var1x);
      });
      return (Collection)var1.keySet().stream().flatMap((var1x) -> {
         Collection var2 = (Collection)var1.get(var1x);
         Consumer var3 = GameTestRegistry.getBeforeBatchFunction(var1x);
         Consumer var4 = GameTestRegistry.getAfterBatchFunction(var1x);
         MutableInt var5 = new MutableInt();
         return Streams.stream(Iterables.partition(var2, 100)).map((var4x) -> {
            return new GameTestBatch(var1x + ":" + var5.incrementAndGet(), var4x, var3, var4);
         });
      }).collect(Collectors.toList());
   }

   public static void clearAllTests(ServerLevel var0, BlockPos var1, GameTestTicker var2, int var3) {
      var2.clear();
      BlockPos var4 = var1.offset(-var3, 0, -var3);
      BlockPos var5 = var1.offset(var3, 0, var3);
      BlockPos.betweenClosedStream(var4, var5).filter((var1x) -> {
         return var0.getBlockState(var1x).is(Blocks.STRUCTURE_BLOCK);
      }).forEach((var1x) -> {
         StructureBlockEntity var2 = (StructureBlockEntity)var0.getBlockEntity(var1x);
         BlockPos var3 = var2.getBlockPos();
         BoundingBox var4 = StructureUtils.getStructureBoundingBox(var2);
         StructureUtils.clearSpaceForStructure(var4, var3.getY(), var0);
      });
   }

   public static void clearMarkers(ServerLevel var0) {
      DebugPackets.sendGameTestClearPacket(var0);
   }
}
