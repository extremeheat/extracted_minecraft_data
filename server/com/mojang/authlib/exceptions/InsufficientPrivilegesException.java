package com.mojang.authlib.exceptions;

public class InsufficientPrivilegesException extends AuthenticationException {
   public InsufficientPrivilegesException() {
      super();
   }

   public InsufficientPrivilegesException(String var1) {
      super(var1);
   }

   public InsufficientPrivilegesException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public InsufficientPrivilegesException(Throwable var1) {
      super(var1);
   }
}
