package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.Map;
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
   private static final int MAX_TESTS_PER_BATCH = 100;
   public static final int PADDING_AROUND_EACH_STRUCTURE = 2;
   public static final int SPACE_BETWEEN_COLUMNS = 5;
   public static final int SPACE_BETWEEN_ROWS = 6;
   public static final int DEFAULT_TESTS_PER_ROW = 8;

   public GameTestRunner() {
      super();
   }

   public static void runTest(GameTestInfo var0, BlockPos var1, GameTestTicker var2) {
      var0.startExecution();
      var2.add(var0);
      var0.addListener(new ReportGameListener(var0, var2, var1));
      var0.spawnStructure(var1, 2);
   }

   public static Collection<GameTestInfo> runTestBatches(
      Collection<GameTestBatch> var0, BlockPos var1, Rotation var2, ServerLevel var3, GameTestTicker var4, int var5
   ) {
      GameTestBatchRunner var6 = new GameTestBatchRunner(var0, var1, var2, var3, var4, var5);
      var6.start();
      return var6.getTestInfos();
   }

   public static Collection<GameTestInfo> runTests(
      Collection<TestFunction> var0, BlockPos var1, Rotation var2, ServerLevel var3, GameTestTicker var4, int var5
   ) {
      return runTestBatches(groupTestsIntoBatches(var0), var1, var2, var3, var4, var5);
   }

   public static Collection<GameTestBatch> groupTestsIntoBatches(Collection<TestFunction> var0) {
      Map var1 = var0.stream().collect(Collectors.groupingBy(TestFunction::getBatchName));
      return var1.entrySet()
         .stream()
         .flatMap(
            var0x -> {
               String var1x = (String)var0x.getKey();
               Consumer var2 = GameTestRegistry.getBeforeBatchFunction(var1x);
               Consumer var3 = GameTestRegistry.getAfterBatchFunction(var1x);
               MutableInt var4 = new MutableInt();
               Collection var5 = (Collection)var0x.getValue();
               return Streams.stream(Iterables.partition(var5, 100))
                  .map(var4x -> new GameTestBatch(var1x + ":" + var4.incrementAndGet(), ImmutableList.copyOf(var4x), var2, var3));
            }
         )
         .collect(ImmutableList.toImmutableList());
   }

   public static void clearAllTests(ServerLevel var0, BlockPos var1, GameTestTicker var2, int var3) {
      var2.clear();
      BlockPos var4 = var1.offset(-var3, 0, -var3);
      BlockPos var5 = var1.offset(var3, 0, var3);
      BlockPos.betweenClosedStream(var4, var5).filter(var1x -> var0.getBlockState(var1x).is(Blocks.STRUCTURE_BLOCK)).forEach(var1x -> {
         StructureBlockEntity var2x = (StructureBlockEntity)var0.getBlockEntity(var1x);
         BlockPos var3x = var2x.getBlockPos();
         BoundingBox var4x = StructureUtils.getStructureBoundingBox(var2x);
         StructureUtils.clearSpaceForStructure(var4x, var3x.getY(), var0);
      });
   }

   public static void clearMarkers(ServerLevel var0) {
      DebugPackets.sendGameTestClearPacket(var0);
   }
}
