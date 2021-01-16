package org.apache.commons.lang3.concurrent;

public class CircuitBreakingException extends RuntimeException {
   private static final long serialVersionUID = 1408176654686913340L;

   public CircuitBreakingException() {
      super();
   }

   public CircuitBreakingException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public CircuitBreakingException(String var1) {
      super(var1);
   }

   public CircuitBreakingException(Throwable var1) {
      super(var1);
   }
}
