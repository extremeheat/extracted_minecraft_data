package io.netty.handler.codec.http2;

public class Http2NoMoreStreamIdsException extends Http2Exception {
   private static final long serialVersionUID = -7756236161274851110L;
   private static final String ERROR_MESSAGE = "No more streams can be created on this connection";

   public Http2NoMoreStreamIdsException() {
      super(Http2Error.PROTOCOL_ERROR, "No more streams can be created on this connection", Http2Exception.ShutdownHint.GRACEFUL_SHUTDOWN);
   }

   public Http2NoMoreStreamIdsException(Throwable var1) {
      super(Http2Error.PROTOCOL_ERROR, "No more streams can be created on this connection", var1, Http2Exception.ShutdownHint.GRACEFUL_SHUTDOWN);
   }
}
