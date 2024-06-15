package net.minecraft.gametest.framework;

public record RetryOptions(int numberOfTries, boolean haltOnFailure) {
   private static final RetryOptions NO_RETRIES = new RetryOptions(1, true);

   public RetryOptions(int numberOfTries, boolean haltOnFailure) {
      super();
      this.numberOfTries = numberOfTries;
      this.haltOnFailure = haltOnFailure;
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
