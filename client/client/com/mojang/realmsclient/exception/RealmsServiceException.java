package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;

public class RealmsServiceException extends Exception {
   public final RealmsError realmsError;

   public RealmsServiceException(RealmsError var1) {
      super();
      this.realmsError = var1;
   }

   @Override
   public String getMessage() {
      return this.realmsError.logMessage();
   }
}
