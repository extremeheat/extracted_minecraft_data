package io.netty.handler.codec.http2;

public final class Http2FrameStreamEvent {
   private final Http2FrameStream stream;
   private final Http2FrameStreamEvent.Type type;

   private Http2FrameStreamEvent(Http2FrameStream var1, Http2FrameStreamEvent.Type var2) {
      super();
      this.stream = var1;
      this.type = var2;
   }

   public Http2FrameStream stream() {
      return this.stream;
   }

   public Http2FrameStreamEvent.Type type() {
      return this.type;
   }

   static Http2FrameStreamEvent stateChanged(Http2FrameStream var0) {
      return new Http2FrameStreamEvent(var0, Http2FrameStreamEvent.Type.State);
   }

   static Http2FrameStreamEvent writabilityChanged(Http2FrameStream var0) {
      return new Http2FrameStreamEvent(var0, Http2FrameStreamEvent.Type.Writability);
   }

   static enum Type {
      State,
      Writability;

      private Type() {
      }
   }
}
