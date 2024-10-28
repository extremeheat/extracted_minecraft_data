package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;

public class RetryCallException extends RealmsServiceException {
   public static final int DEFAULT_DELAY = 5;
   public final int delaySeconds;

   public RetryCallException(int var1, int var2) {
      super(RealmsError.CustomError.retry(var2));
      if (var1 >= 0 && var1 <= 120) {
         this.delaySeconds = var1;
      } else {
         this.delaySeconds = 5;
      }

   }
}
