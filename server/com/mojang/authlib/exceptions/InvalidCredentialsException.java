package com.mojang.authlib.exceptions;

public class InvalidCredentialsException extends AuthenticationException {
   public InvalidCredentialsException() {
      super();
   }

   public InvalidCredentialsException(String var1) {
      super(var1);
   }

   public InvalidCredentialsException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public InvalidCredentialsException(Throwable var1) {
      super(var1);
   }
}
