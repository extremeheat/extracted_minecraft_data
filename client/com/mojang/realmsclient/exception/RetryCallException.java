package com.mojang.realmsclient.exception;

public class RetryCallException extends RealmsServiceException {
   public final int delaySeconds;

   public RetryCallException(int var1, int var2) {
      super(var2, "Retry operation", -1, "");
      if (var1 >= 0 && var1 <= 120) {
         this.delaySeconds = var1;
      } else {
         this.delaySeconds = 5;
      }

   }
}
