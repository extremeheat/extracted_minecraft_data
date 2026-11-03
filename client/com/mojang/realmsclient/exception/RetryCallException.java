package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;
import org.jspecify.annotations.Nullable;

public class RetryCallException extends RealmsServiceException {
   public static final int DEFAULT_DELAY = 5;
   public final int delaySeconds;

   public RetryCallException(final int delaySeconds, final int statusCode) {
      this(delaySeconds, statusCode, (String)null);
   }

   public RetryCallException(final int delaySeconds, final int statusCode, final @Nullable String retryAfterHeader) {
      super(RealmsError.CustomError.retry(statusCode), retryAfterHeader);
      if (delaySeconds >= 0 && delaySeconds <= 120) {
         this.delaySeconds = delaySeconds;
      } else {
         this.delaySeconds = 5;
      }

   }
}
