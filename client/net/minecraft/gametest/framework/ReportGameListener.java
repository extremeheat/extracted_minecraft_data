package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;

class ReportGameListener implements GameTestListener {
   private final GameTestInfo originalTestInfo;
   private final GameTestTicker testTicker;
   private final BlockPos structurePos;
   int attempts;
   int successes;

   public ReportGameListener(GameTestInfo var1, GameTestTicker var2, BlockPos var3) {
      super();
      this.originalTestInfo = var1;
      this.testTicker = var2;
      this.structurePos = var3;
      this.attempts = 0;
      this.successes = 0;
   }

   @Override
   public void testStructureLoaded(GameTestInfo var1) {
      spawnBeacon(this.originalTestInfo, Blocks.LIGHT_GRAY_STAINED_GLASS);
      ++this.attempts;
   }

   @Override
   public void testPassed(GameTestInfo var1) {
      ++this.successes;
      if (var1.rerunUntilFailed()) {
         reportPassed(var1, var1.getTestName() + " passed! (" + var1.getRunTime() + "ms). Rerunning until failed.");
         this.rerunTest();
      } else if (!var1.isFlaky()) {
         reportPassed(var1, var1.getTestName() + " passed! (" + var1.getRunTime() + "ms)");
      } else {
         if (this.successes >= var1.requiredSuccesses()) {
            reportPassed(var1, var1 + " passed " + this.successes + " times of " + this.attempts + " attempts.");
         } else {
            say(
               this.originalTestInfo.getLevel(),
               ChatFormatting.GREEN,
               "Flaky test " + this.originalTestInfo + " succeeded, attempt: " + this.attempts + " successes: " + this.successes
            );
            this.rerunTest();
         }
      }
   }

   @Override
   public void testFailed(GameTestInfo var1) {
      if (!var1.isFlaky()) {
         reportFailure(var1, var1.getError());
      } else {
         TestFunction var2 = this.originalTestInfo.getTestFunction();
         String var3 = "Flaky test " + this.originalTestInfo + " failed, attempt: " + this.attempts + "/" + var2.getMaxAttempts();
         if (var2.getRequiredSuccesses() > 1) {
            var3 = var3 + ", successes: " + this.successes + " (" + var2.getRequiredSuccesses() + " required)";
         }

         say(this.originalTestInfo.getLevel(), ChatFormatting.YELLOW, var3);
         if (var1.maxAttempts() - this.attempts + this.successes >= var1.requiredSuccesses()) {
            this.rerunTest();
         } else {
            reportFailure(var1, new ExhaustedAttemptsException(this.attempts, this.successes, var1));
         }
      }
   }

   public static void reportPassed(GameTestInfo var0, String var1) {
      spawnBeacon(var0, Blocks.LIME_STAINED_GLASS);
      visualizePassedTest(var0, var1);
   }

   private static void visualizePassedTest(GameTestInfo var0, String var1) {
      say(var0.getLevel(), ChatFormatting.GREEN, var1);
      GlobalTestReporter.onTestSuccess(var0);
   }

   protected static void reportFailure(GameTestInfo var0, Throwable var1) {
      spawnBeacon(var0, var0.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
      spawnLectern(var0, Util.describeError(var1));
      visualizeFailedTest(var0, var1);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected static void visualizeFailedTest(GameTestInfo var0, Throwable var1) {
      String var2 = var1.getMessage() + (var1.getCause() == null ? "" : " cause: " + Util.describeError(var1.getCause()));
      String var3 = (var0.isRequired() ? "" : "(optional) ") + var0.getTestName() + " failed! " + var2;
      say(var0.getLevel(), var0.isRequired() ? ChatFormatting.RED : ChatFormatting.YELLOW, var3);
      Throwable var4 = (Throwable)MoreObjects.firstNonNull(ExceptionUtils.getRootCause(var1), var1);
      if (var4 instanceof GameTestAssertPosException var5) {
         showRedBox(var0.getLevel(), var5.getAbsolutePos(), var5.getMessageToShowAtBlock());
      }

      GlobalTestReporter.onTestFailed(var0);
   }

   private void rerunTest() {
      this.originalTestInfo.clearStructure();
      GameTestInfo var1 = new GameTestInfo(this.originalTestInfo.getTestFunction(), this.originalTestInfo.getRotation(), this.originalTestInfo.getLevel());
      var1.setRerunUntilFailed(this.originalTestInfo.rerunUntilFailed());
      this.testTicker.add(var1);
      var1.addListener(this);
      var1.prepareTestStructure(this.structurePos);
   }

   protected static void spawnBeacon(GameTestInfo var0, Block var1) {
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
      LecternBlock.tryPlaceBook(null, var2, var5, var6, var7);
   }

   private static ItemStack createBook(String var0, boolean var1, String var2) {
      ItemStack var3 = new ItemStack(Items.WRITABLE_BOOK);
      ListTag var4 = new ListTag();
      StringBuffer var5 = new StringBuffer();
      Arrays.stream(var0.split("\\.")).forEach(var1x -> var5.append(var1x).append('\n'));
      if (!var1) {
         var5.append("(optional)\n");
      }

      var5.append("-------------------\n");
      var4.add(StringTag.valueOf(var5 + var2));
      var3.addTagElement("pages", var4);
      return var3;
   }

   protected static void say(ServerLevel var0, ChatFormatting var1, String var2) {
      var0.getPlayers(var0x -> true).forEach(var2x -> var2x.sendSystemMessage(Component.literal(var2).withStyle(var1)));
   }

   private static void showRedBox(ServerLevel var0, BlockPos var1, String var2) {
      DebugPackets.sendGameTestAddMarker(var0, var1, var2, -2130771968, 2147483647);
   }
}
