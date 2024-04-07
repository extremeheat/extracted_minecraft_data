package net.minecraft.gametest.framework;

public class GlobalTestReporter {
   private static TestReporter DELEGATE = new LogTestReporter();

   public GlobalTestReporter() {
      super();
   }

   public static void replaceWith(TestReporter var0) {
      DELEGATE = var0;
   }

   public static void onTestFailed(GameTestInfo var0) {
      DELEGATE.onTestFailed(var0);
   }

   public static void onTestSuccess(GameTestInfo var0) {
      DELEGATE.onTestSuccess(var0);
   }

   public static void finish() {
      DELEGATE.finish();
   }
}
