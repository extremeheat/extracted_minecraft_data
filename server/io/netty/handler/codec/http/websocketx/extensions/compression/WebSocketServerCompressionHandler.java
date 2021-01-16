package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandler;

public class WebSocketServerCompressionHandler extends WebSocketServerExtensionHandler {
   public WebSocketServerCompressionHandler() {
      super(new PerMessageDeflateServerExtensionHandshaker(), new DeflateFrameServerExtensionHandshaker());
   }
}
