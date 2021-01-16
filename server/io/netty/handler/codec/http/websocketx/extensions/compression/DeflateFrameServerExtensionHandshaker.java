package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import java.util.Collections;

public final class DeflateFrameServerExtensionHandshaker implements WebSocketServerExtensionHandshaker {
   static final String X_WEBKIT_DEFLATE_FRAME_EXTENSION = "x-webkit-deflate-frame";
   static final String DEFLATE_FRAME_EXTENSION = "deflate-frame";
   private final int compressionLevel;

   public DeflateFrameServerExtensionHandshaker() {
      this(6);
   }

   public DeflateFrameServerExtensionHandshaker(int var1) {
      super();
      if (var1 >= 0 && var1 <= 9) {
         this.compressionLevel = var1;
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
      }
   }

   public WebSocketServerExtension handshakeExtension(WebSocketExtensionData var1) {
      if (!"x-webkit-deflate-frame".equals(var1.name()) && !"deflate-frame".equals(var1.name())) {
         return null;
      } else {
         return var1.parameters().isEmpty() ? new DeflateFrameServerExtensionHandshaker.DeflateFrameServerExtension(this.compressionLevel, var1.name()) : null;
      }
   }

   private static class DeflateFrameServerExtension implements WebSocketServerExtension {
      private final String extensionName;
      private final int compressionLevel;

      public DeflateFrameServerExtension(int var1, String var2) {
         super();
         this.extensionName = var2;
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

      public WebSocketExtensionData newReponseData() {
         return new WebSocketExtensionData(this.extensionName, Collections.emptyMap());
      }
   }
}
