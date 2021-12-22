package net.minecraft.gametest.framework;

class ExhaustedAttemptsException extends Throwable {
   public ExhaustedAttemptsException(int var1, int var2, GameTestInfo var3) {
      super("Not enough successes: " + var2 + " out of " + var1 + " attempts. Required successes: " + var3.requiredSuccesses() + ". max attempts: " + var3.maxAttempts() + ".", var3.getError());
   }
}
