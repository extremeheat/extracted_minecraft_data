package net.minecraft.gametest.framework;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.mutable.MutableInt;

public class GameTestRunner {
   public static TestReporter TEST_REPORTER = new LogTestReporter();

   public static void runTest(GameTestInfo var0, BlockPos var1, GameTestTicker var2) {
      var0.startExecution();
      var2.add(var0);
      var0.addListener(new GameTestListener() {
         public void testStructureLoaded(GameTestInfo var1) {
            GameTestRunner.spawnBeacon(var1, Blocks.LIGHT_GRAY_STAINED_GLASS);
         }

         public void testFailed(GameTestInfo var1) {
            GameTestRunner.spawnBeacon(var1, var1.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
            GameTestRunner.spawnLectern(var1, Util.describeError(var1.getError()));
            GameTestRunner.visualizeFailedTest(var1);
         }
      });
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
         MutableInt var4 = new MutableInt();
         return Streams.stream(Iterables.partition(var2, 100)).map((var4x) -> {
            return new GameTestBatch(var1x + ":" + var4.incrementAndGet(), var2, var3);
         });
      }).collect(Collectors.toList());
   }

   private static void visualizeFailedTest(GameTestInfo var0) {
      Throwable var1 = var0.getError();
      String var2 = (var0.isRequired() ? "" : "(optional) ") + var0.getTestName() + " failed! " + Util.describeError(var1);
      say(var0.getLevel(), var0.isRequired() ? ChatFormatting.RED : ChatFormatting.YELLOW, var2);
      if (var1 instanceof GameTestAssertPosException) {
         GameTestAssertPosException var3 = (GameTestAssertPosException)var1;
         showRedBox(var0.getLevel(), var3.getAbsolutePos(), var3.getMessageToShowAtBlock());
      }

      TEST_REPORTER.onTestFailed(var0);
   }

   private static void spawnBeacon(GameTestInfo var0, Block var1) {
      ServerLevel var2 = var0.getLevel();
      BlockPos var3 = var0.getStructureBlockPos();
      BlockPos var4 = new BlockPos(-1, -1, -1);
      BlockPos var5 = StructureTemplate.transform(var3.offset(var4), Mirror.NONE, var0.getRotation(), var3);
      var2.setBlockAndUpdate(var5, Blocks.BEACON.defaultBlockState().rotate(var0.getRotation()));
      BlockPos var6 = var5.offset(0, 1, 0);
      var2.setBlockAndUpdate(var6, var1.defaultBlockState());

      for(int var7 = -1; var7 <= 1; ++var7) {
         for(int var8 = -1; var8 <= 1; ++var8) {
            BlockPos var9 = var5.offset(var7, -1, var8);
            var2.setBlockAndUpdate(var9, Blocks.IRON_BLOCK.defaultBlockState());
         }
      }

   }

   private static void spawnLectern(GameTestInfo var0, String var1) {
      ServerLevel var2 = var0.getLevel();
      BlockPos var3 = var0.getStructureBlockPos();
      BlockPos var4 = new BlockPos(-1, 1, -1);
      BlockPos var5 = StructureTemplate.transform(var3.offset(var4), Mirror.NONE, var0.getRotation(), var3);
      var2.setBlockAndUpdate(var5, Blocks.LECTERN.defaultBlockState().rotate(var0.getRotation()));
      BlockState var6 = var2.getBlockState(var5);
      ItemStack var7 = createBook(var0.getTestName(), var0.isRequired(), var1);
      LecternBlock.tryPlaceBook(var2, var5, var6, var7);
   }

   private static ItemStack createBook(String var0, boolean var1, String var2) {
      ItemStack var3 = new ItemStack(Items.WRITABLE_BOOK);
      ListTag var4 = new ListTag();
      StringBuffer var5 = new StringBuffer();
      Arrays.stream(var0.split("\\.")).forEach((var1x) -> {
         var5.append(var1x).append('\n');
      });
      if (!var1) {
         var5.append("(optional)\n");
      }

      var5.append("-------------------\n");
      var4.add(StringTag.valueOf(var5.toString() + var2));
      var3.addTagElement("pages", var4);
      return var3;
   }

   private static void say(ServerLevel var0, ChatFormatting var1, String var2) {
      var0.getPlayers((var0x) -> {
         return true;
      }).forEach((var2x) -> {
         var2x.sendMessage((new TextComponent(var2)).withStyle(var1), Util.NIL_UUID);
      });
   }

   public static void clearMarkers(ServerLevel var0) {
      DebugPackets.sendGameTestClearPacket(var0);
   }

   private static void showRedBox(ServerLevel var0, BlockPos var1, String var2) {
      DebugPackets.sendGameTestAddMarker(var0, var1, var2, -2130771968, 2147483647);
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
}
