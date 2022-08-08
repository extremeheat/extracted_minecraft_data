package com.mojang.realmsclient.exception;

public class RetryCallException extends RealmsServiceException {
   public static final int DEFAULT_DELAY = 5;
   public final int delaySeconds;

   public RetryCallException(int var1, int var2) {
      super(var2, "Retry operation");
      if (var1 >= 0 && var1 <= 120) {
         this.delaySeconds = var1;
      } else {
         this.delaySeconds = 5;
      }

   }
}
