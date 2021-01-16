package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import java.util.Collections;

public final class DeflateFrameClientExtensionHandshaker implements WebSocketClientExtensionHandshaker {
   private final int compressionLevel;
   private final boolean useWebkitExtensionName;

   public DeflateFrameClientExtensionHandshaker(boolean var1) {
      this(6, var1);
   }

   public DeflateFrameClientExtensionHandshaker(int var1, boolean var2) {
      super();
      if (var1 >= 0 && var1 <= 9) {
         this.compressionLevel = var1;
         this.useWebkitExtensionName = var2;
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
      }
   }

   public WebSocketExtensionData newRequestData() {
      return new WebSocketExtensionData(this.useWebkitExtensionName ? "x-webkit-deflate-frame" : "deflate-frame", Collections.emptyMap());
   }

   public WebSocketClientExtension handshakeExtension(WebSocketExtensionData var1) {
      if (!"x-webkit-deflate-frame".equals(var1.name()) && !"deflate-frame".equals(var1.name())) {
         return null;
      } else {
         return var1.parameters().isEmpty() ? new DeflateFrameClientExtensionHandshaker.DeflateFrameClientExtension(this.compressionLevel) : null;
      }
   }

   private static class DeflateFrameClientExtension implements WebSocketClientExtension {
      private final int compressionLevel;

      public DeflateFrameClientExtension(int var1) {
         super();
         this.compressionLevel = var1;
      }

      public int rsv() {
         return 4;
      }

      public WebSocketExtensionEncoder newExtensionEncoder() {
         return new PerFrameDeflateEncoder(this.compressionLevel, 15, false);
      }

      public WebSocketExtensionDecoder newExtensionDecoder() {
         return new PerFrameDeflateDecoder(false);
      }
   }
}
