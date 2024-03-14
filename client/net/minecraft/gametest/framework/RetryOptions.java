package net.minecraft.gametest.framework;

public record RetryOptions(int a, boolean b) {
   private final int numberOfTries;
   private final boolean haltOnFailure;
   private static final RetryOptions NO_RETRIES = new RetryOptions(1, true);

   public RetryOptions(int var1, boolean var2) {
      super();
      this.numberOfTries = var1;
      this.haltOnFailure = var2;
   }

   public static RetryOptions noRetries() {
      return NO_RETRIES;
   }

   public boolean unlimitedTries() {
      return this.numberOfTries < 1;
   }

   public boolean hasTriesLeft(int var1, int var2) {
      boolean var3 = var1 != var2;
      boolean var4 = this.unlimitedTries() || var1 < this.numberOfTries;
      return var4 && (!var3 || !this.haltOnFailure);
   }

   public boolean hasRetries() {
      return this.numberOfTries != 1;
   }
}
