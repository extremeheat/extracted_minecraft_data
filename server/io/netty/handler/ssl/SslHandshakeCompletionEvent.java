package io.netty.handler.ssl;

public final class SslHandshakeCompletionEvent extends SslCompletionEvent {
   public static final SslHandshakeCompletionEvent SUCCESS = new SslHandshakeCompletionEvent();

   private SslHandshakeCompletionEvent() {
      super();
   }

   public SslHandshakeCompletionEvent(Throwable var1) {
      super(var1);
   }
}
