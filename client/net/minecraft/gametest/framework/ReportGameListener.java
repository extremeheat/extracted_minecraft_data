package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;

class ReportGameListener implements GameTestListener {
   private int attempts = 0;
   private int successes = 0;

   public ReportGameListener() {
      super();
   }

   @Override
   public void testStructureLoaded(GameTestInfo var1) {
      spawnBeacon(var1, Blocks.LIGHT_GRAY_STAINED_GLASS);
      this.attempts++;
   }

   private void handleRetry(GameTestInfo var1, GameTestRunner var2, boolean var3) {
      RetryOptions var4 = var1.retryOptions();
      String var5 = String.format("[Run: %4d, Ok: %4d, Fail: %4d", this.attempts, this.successes, this.attempts - this.successes);
      if (!var4.unlimitedTries()) {
         var5 = var5 + String.format(", Left: %4d", var4.numberOfTries() - this.attempts);
      }

      var5 = var5 + "]";
      String var6 = var1.getTestName() + " " + (var3 ? "passed" : "failed") + "! " + var1.getRunTime() + "ms";
      String var7 = String.format("%-53s%s", var5, var6);
      if (var3) {
         reportPassed(var1, var7);
      } else {
         say(var1.getLevel(), ChatFormatting.RED, var7);
      }

      if (var4.hasTriesLeft(this.attempts, this.successes)) {
         var2.rerunTest(var1);
      }
   }

   @Override
   public void testPassed(GameTestInfo var1, GameTestRunner var2) {
      this.successes++;
      if (var1.retryOptions().hasRetries()) {
         this.handleRetry(var1, var2, true);
      } else if (!var1.isFlaky()) {
         reportPassed(var1, var1.getTestName() + " passed! (" + var1.getRunTime() + "ms)");
      } else {
         if (this.successes >= var1.requiredSuccesses()) {
            reportPassed(var1, var1 + " passed " + this.successes + " times of " + this.attempts + " attempts.");
         } else {
            say(var1.getLevel(), ChatFormatting.GREEN, "Flaky test " + var1 + " succeeded, attempt: " + this.attempts + " successes: " + this.successes);
            var2.rerunTest(var1);
         }
      }
   }

   @Override
   public void testFailed(GameTestInfo var1, GameTestRunner var2) {
      if (!var1.isFlaky()) {
         reportFailure(var1, var1.getError());
         if (var1.retryOptions().hasRetries()) {
            this.handleRetry(var1, var2, false);
         }
      } else {
         TestFunction var3 = var1.getTestFunction();
         String var4 = "Flaky test " + var1 + " failed, attempt: " + this.attempts + "/" + var3.maxAttempts();
         if (var3.requiredSuccesses() > 1) {
            var4 = var4 + ", successes: " + this.successes + " (" + var3.requiredSuccesses() + " required)";
         }

         say(var1.getLevel(), ChatFormatting.YELLOW, var4);
         if (var1.maxAttempts() - this.attempts + this.successes >= var1.requiredSuccesses()) {
            var2.rerunTest(var1);
         } else {
            reportFailure(var1, new ExhaustedAttemptsException(this.attempts, this.successes, var1));
         }
      }
   }

   @Override
   public void testAddedForRerun(GameTestInfo var1, GameTestInfo var2, GameTestRunner var3) {
      var2.addListener(this);
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

   protected static void spawnBeacon(GameTestInfo var0, Block var1) {
      ServerLevel var2 = var0.getLevel();
      BlockPos var3 = var0.getStructureBlockPos();
      BlockPos var4 = new BlockPos(-1, -2, -1);
      BlockPos var5 = StructureTemplate.transform(var3.offset(var4), Mirror.NONE, var0.getRotation(), var3);
      var2.setBlockAndUpdate(var5, Blocks.BEACON.defaultBlockState().rotate(var0.getRotation()));
      BlockPos var6 = var5.offset(0, 1, 0);
      var2.setBlockAndUpdate(var6, var1.defaultBlockState());

      for (int var7 = -1; var7 <= 1; var7++) {
         for (int var8 = -1; var8 <= 1; var8++) {
            BlockPos var9 = var5.offset(var7, -1, var8);
            var2.setBlockAndUpdate(var9, Blocks.IRON_BLOCK.defaultBlockState());
         }
      }
   }

   private static void spawnLectern(GameTestInfo var0, String var1) {
      ServerLevel var2 = var0.getLevel();
      BlockPos var3 = var0.getStructureBlockPos();
      BlockPos var4 = new BlockPos(-1, 0, -1);
      BlockPos var5 = StructureTemplate.transform(var3.offset(var4), Mirror.NONE, var0.getRotation(), var3);
      var2.setBlockAndUpdate(var5, Blocks.LECTERN.defaultBlockState().rotate(var0.getRotation()));
      BlockState var6 = var2.getBlockState(var5);
      ItemStack var7 = createBook(var0.getTestName(), var0.isRequired(), var1);
      LecternBlock.tryPlaceBook(null, var2, var5, var6, var7);
   }

   private static ItemStack createBook(String var0, boolean var1, String var2) {
      StringBuffer var3 = new StringBuffer();
      Arrays.stream(var0.split("\\.")).forEach(var1x -> var3.append(var1x).append('\n'));
      if (!var1) {
         var3.append("(optional)\n");
      }

      var3.append("-------------------\n");
      ItemStack var4 = new ItemStack(Items.WRITABLE_BOOK);
      var4.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(List.of(Filterable.passThrough(var3 + var2))));
      return var4;
   }

   protected static void say(ServerLevel var0, ChatFormatting var1, String var2) {
      var0.getPlayers(var0x -> true).forEach(var2x -> var2x.sendSystemMessage(Component.literal(var2).withStyle(var1)));
   }

   private static void showRedBox(ServerLevel var0, BlockPos var1, String var2) {
      DebugPackets.sendGameTestAddMarker(var0, var1, var2, -2130771968, 2147483647);
   }
}
