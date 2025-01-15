package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import org.apache.commons.lang3.exception.ExceptionUtils;

class ReportGameListener implements GameTestListener {
   private int attempts = 0;
   private int successes = 0;

   public ReportGameListener() {
      super();
   }

   public void testStructureLoaded(GameTestInfo var1) {
      ++this.attempts;
   }

   private void handleRetry(GameTestInfo var1, GameTestRunner var2, boolean var3) {
      RetryOptions var4 = var1.retryOptions();
      String var5 = String.format("[Run: %4d, Ok: %4d, Fail: %4d", this.attempts, this.successes, this.attempts - this.successes);
      if (!var4.unlimitedTries()) {
         var5 = var5 + String.format(", Left: %4d", var4.numberOfTries() - this.attempts);
      }

      var5 = var5 + "]";
      String var10000 = String.valueOf(var1.id());
      String var6 = var10000 + " " + (var3 ? "passed" : "failed") + "! " + var1.getRunTime() + "ms";
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

   public void testPassed(GameTestInfo var1, GameTestRunner var2) {
      ++this.successes;
      if (var1.retryOptions().hasRetries()) {
         this.handleRetry(var1, var2, true);
      } else if (!var1.isFlaky()) {
         String var4 = String.valueOf(var1.id());
         reportPassed(var1, var4 + " passed! (" + var1.getRunTime() + "ms)");
      } else {
         if (this.successes >= var1.requiredSuccesses()) {
            String var10001 = String.valueOf(var1);
            reportPassed(var1, var10001 + " passed " + this.successes + " times of " + this.attempts + " attempts.");
         } else {
            ServerLevel var10000 = var1.getLevel();
            ChatFormatting var3 = ChatFormatting.GREEN;
            String var10002 = String.valueOf(var1);
            say(var10000, var3, "Flaky test " + var10002 + " succeeded, attempt: " + this.attempts + " successes: " + this.successes);
            var2.rerunTest(var1);
         }

      }
   }

   public void testFailed(GameTestInfo var1, GameTestRunner var2) {
      if (!var1.isFlaky()) {
         reportFailure(var1, var1.getError());
         if (var1.retryOptions().hasRetries()) {
            this.handleRetry(var1, var2, false);
         }

      } else {
         GameTestInstance var3 = var1.getTest();
         String var10000 = String.valueOf(var1);
         String var4 = "Flaky test " + var10000 + " failed, attempt: " + this.attempts + "/" + var3.maxAttempts();
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

   public void testAddedForRerun(GameTestInfo var1, GameTestInfo var2, GameTestRunner var3) {
      var2.addListener(this);
   }

   public static void reportPassed(GameTestInfo var0, String var1) {
      getTestInstanceBlockEntity(var0).ifPresent((var0x) -> var0x.setSuccess());
      visualizePassedTest(var0, var1);
   }

   private static void visualizePassedTest(GameTestInfo var0, String var1) {
      say(var0.getLevel(), ChatFormatting.GREEN, var1);
      GlobalTestReporter.onTestSuccess(var0);
   }

   protected static void reportFailure(GameTestInfo var0, Throwable var1) {
      Object var2;
      if (var1 instanceof GameTestAssertException var3) {
         var2 = var3.getDescription();
      } else {
         var2 = Component.literal(Util.describeError(var1));
      }

      getTestInstanceBlockEntity(var0).ifPresent((var1x) -> var1x.setErrorMessage(var2));
      visualizeFailedTest(var0, var1);
   }

   protected static void visualizeFailedTest(GameTestInfo var0, Throwable var1) {
      String var10000 = var1.getMessage();
      String var2 = var10000 + (var1.getCause() == null ? "" : " cause: " + Util.describeError(var1.getCause()));
      var10000 = var0.isRequired() ? "" : "(optional) ";
      String var3 = var10000 + String.valueOf(var0.id()) + " failed! " + var2;
      say(var0.getLevel(), var0.isRequired() ? ChatFormatting.RED : ChatFormatting.YELLOW, var3);
      Throwable var4 = (Throwable)MoreObjects.firstNonNull(ExceptionUtils.getRootCause(var1), var1);
      if (var4 instanceof GameTestAssertPosException var5) {
         showRedBox(var0.getLevel(), var5.getAbsolutePos(), var5.getMessageToShowAtBlock());
      }

      GlobalTestReporter.onTestFailed(var0);
   }

   private static Optional<TestInstanceBlockEntity> getTestInstanceBlockEntity(GameTestInfo var0) {
      ServerLevel var1 = var0.getLevel();
      Optional var2 = Optional.ofNullable(var0.getTestBlockPos());
      Optional var3 = var2.flatMap((var1x) -> var1.getBlockEntity(var1x, BlockEntityType.TEST_INSTANCE_BLOCK));
      return var3;
   }

   protected static void say(ServerLevel var0, ChatFormatting var1, String var2) {
      var0.getPlayers((var0x) -> true).forEach((var2x) -> var2x.sendSystemMessage(Component.literal(var2).withStyle(var1)));
   }

   private static void showRedBox(ServerLevel var0, BlockPos var1, String var2) {
      DebugPackets.sendGameTestAddMarker(var0, var1, var2, -2130771968, 2147483647);
   }
}
