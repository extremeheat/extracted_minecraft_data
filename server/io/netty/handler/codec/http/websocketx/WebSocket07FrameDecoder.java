package io.netty.handler.codec.http.websocketx;

public class WebSocket07FrameDecoder extends WebSocket08FrameDecoder {
   public WebSocket07FrameDecoder(boolean var1, boolean var2, int var3) {
      this(var1, var2, var3, false);
   }

   public WebSocket07FrameDecoder(boolean var1, boolean var2, int var3, boolean var4) {
      super(var1, var2, var3, var4);
   }
}
