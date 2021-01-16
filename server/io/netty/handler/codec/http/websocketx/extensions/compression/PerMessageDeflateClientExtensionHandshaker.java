package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class PerMessageDeflateClientExtensionHandshaker implements WebSocketClientExtensionHandshaker {
   private final int compressionLevel;
   private final boolean allowClientWindowSize;
   private final int requestedServerWindowSize;
   private final boolean allowClientNoContext;
   private final boolean requestedServerNoContext;

   public PerMessageDeflateClientExtensionHandshaker() {
      this(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), 15, false, false);
   }

   public PerMessageDeflateClientExtensionHandshaker(int var1, boolean var2, int var3, boolean var4, boolean var5) {
      super();
      if (var3 <= 15 && var3 >= 8) {
         if (var1 >= 0 && var1 <= 9) {
            this.compressionLevel = var1;
            this.allowClientWindowSize = var2;
            this.requestedServerWindowSize = var3;
            this.allowClientNoContext = var4;
            this.requestedServerNoContext = var5;
         } else {
            throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
         }
      } else {
         throw new IllegalArgumentException("requestedServerWindowSize: " + var3 + " (expected: 8-15)");
      }
   }

   public WebSocketExtensionData newRequestData() {
      HashMap var1 = new HashMap(4);
      if (this.requestedServerWindowSize != 15) {
         var1.put("server_no_context_takeover", (Object)null);
      }

      if (this.allowClientNoContext) {
         var1.put("client_no_context_takeover", (Object)null);
      }

      if (this.requestedServerWindowSize != 15) {
         var1.put("server_max_window_bits", Integer.toString(this.requestedServerWindowSize));
      }

      if (this.allowClientWindowSize) {
         var1.put("client_max_window_bits", (Object)null);
      }

      return new WebSocketExtensionData("permessage-deflate", var1);
   }

   public WebSocketClientExtension handshakeExtension(WebSocketExtensionData var1) {
      if (!"permessage-deflate".equals(var1.name())) {
         return null;
      } else {
         boolean var2 = true;
         int var3 = 15;
         int var4 = 15;
         boolean var5 = false;
         boolean var6 = false;
         Iterator var7 = var1.parameters().entrySet().iterator();

         while(var2 && var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            if ("client_max_window_bits".equalsIgnoreCase((String)var8.getKey())) {
               if (this.allowClientWindowSize) {
                  var3 = Integer.parseInt((String)var8.getValue());
               } else {
                  var2 = false;
               }
            } else if ("server_max_window_bits".equalsIgnoreCase((String)var8.getKey())) {
               var4 = Integer.parseInt((String)var8.getValue());
               if (var3 > 15 || var3 < 8) {
                  var2 = false;
               }
            } else if ("client_no_context_takeover".equalsIgnoreCase((String)var8.getKey())) {
               if (this.allowClientNoContext) {
                  var6 = true;
               } else {
                  var2 = false;
               }
            } else if ("server_no_context_takeover".equalsIgnoreCase((String)var8.getKey())) {
               if (this.requestedServerNoContext) {
                  var5 = true;
               } else {
                  var2 = false;
               }
            } else {
               var2 = false;
            }
         }

         if (this.requestedServerNoContext && !var5 || this.requestedServerWindowSize != var4) {
            var2 = false;
         }

         return var2 ? new PerMessageDeflateClientExtensionHandshaker.PermessageDeflateExtension(var5, var4, var6, var3) : null;
      }
   }

   private final class PermessageDeflateExtension implements WebSocketClientExtension {
      private final boolean serverNoContext;
      private final int serverWindowSize;
      private final boolean clientNoContext;
      private final int clientWindowSize;

      public int rsv() {
         return 4;
      }

      public PermessageDeflateExtension(boolean var2, int var3, boolean var4, int var5) {
         super();
         this.serverNoContext = var2;
         this.serverWindowSize = var3;
         this.clientNoContext = var4;
         this.clientWindowSize = var5;
      }

      public WebSocketExtensionEncoder newExtensionEncoder() {
         return new PerMessageDeflateEncoder(PerMessageDeflateClientExtensionHandshaker.this.compressionLevel, this.serverWindowSize, this.serverNoContext);
      }

      public WebSocketExtensionDecoder newExtensionDecoder() {
         return new PerMessageDeflateDecoder(this.clientNoContext);
      }
   }
}
