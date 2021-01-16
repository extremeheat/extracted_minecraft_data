package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;

public class Http2ConnectionAdapter implements Http2Connection.Listener {
   public Http2ConnectionAdapter() {
      super();
   }

   public void onStreamAdded(Http2Stream var1) {
   }

   public void onStreamActive(Http2Stream var1) {
   }

   public void onStreamHalfClosed(Http2Stream var1) {
   }

   public void onStreamClosed(Http2Stream var1) {
   }

   public void onStreamRemoved(Http2Stream var1) {
   }

   public void onGoAwaySent(int var1, long var2, ByteBuf var4) {
   }

   public void onGoAwayReceived(int var1, long var2, ByteBuf var4) {
   }
}
