package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;

public class RealmsServiceException extends Exception {
   public final RealmsError realmsError;

   public RealmsServiceException(final RealmsError error) {
      super();
      this.realmsError = error;
   }

   public String getMessage() {
      return this.realmsError.logMessage();
   }
}
