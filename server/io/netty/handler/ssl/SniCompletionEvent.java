package io.netty.handler.ssl;

public final class SniCompletionEvent extends SslCompletionEvent {
   private final String hostname;

   SniCompletionEvent(String var1) {
      super();
      this.hostname = var1;
   }

   SniCompletionEvent(String var1, Throwable var2) {
      super(var2);
      this.hostname = var1;
   }

   SniCompletionEvent(Throwable var1) {
      this((String)null, var1);
   }

   public String hostname() {
      return this.hostname;
   }

   public String toString() {
      Throwable var1 = this.cause();
      return var1 == null ? this.getClass().getSimpleName() + "(SUCCESS='" + this.hostname + "'\")" : this.getClass().getSimpleName() + '(' + var1 + ')';
   }
}
