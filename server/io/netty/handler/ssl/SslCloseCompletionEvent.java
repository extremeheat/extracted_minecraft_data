package io.netty.handler.ssl;

public final class SslCloseCompletionEvent extends SslCompletionEvent {
   public static final SslCloseCompletionEvent SUCCESS = new SslCloseCompletionEvent();

   private SslCloseCompletionEvent() {
      super();
   }

   public SslCloseCompletionEvent(Throwable var1) {
      super(var1);
   }
}
