package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

public final class Http2FrameStreamException extends Exception {
   private static final long serialVersionUID = -4407186173493887044L;
   private final Http2Error error;
   private final Http2FrameStream stream;

   public Http2FrameStreamException(Http2FrameStream var1, Http2Error var2, Throwable var3) {
      super(var3.getMessage(), var3);
      this.stream = (Http2FrameStream)ObjectUtil.checkNotNull(var1, "stream");
      this.error = (Http2Error)ObjectUtil.checkNotNull(var2, "error");
   }

   public Http2Error error() {
      return this.error;
   }

   public Http2FrameStream stream() {
      return this.stream;
   }
}
