package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class PerMessageDeflateServerExtensionHandshaker implements WebSocketServerExtensionHandshaker {
   public static final int MIN_WINDOW_SIZE = 8;
   public static final int MAX_WINDOW_SIZE = 15;
   static final String PERMESSAGE_DEFLATE_EXTENSION = "permessage-deflate";
   static final String CLIENT_MAX_WINDOW = "client_max_window_bits";
   static final String SERVER_MAX_WINDOW = "server_max_window_bits";
   static final String CLIENT_NO_CONTEXT = "client_no_context_takeover";
   static final String SERVER_NO_CONTEXT = "server_no_context_takeover";
   private final int compressionLevel;
   private final boolean allowServerWindowSize;
   private final int preferredClientWindowSize;
   private final boolean allowServerNoContext;
   private final boolean preferredClientNoContext;

   public PerMessageDeflateServerExtensionHandshaker() {
      this(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), 15, false, false);
   }

   public PerMessageDeflateServerExtensionHandshaker(int var1, boolean var2, int var3, boolean var4, boolean var5) {
      super();
      if (var3 <= 15 && var3 >= 8) {
         if (var1 >= 0 && var1 <= 9) {
            this.compressionLevel = var1;
            this.allowServerWindowSize = var2;
            this.preferredClientWindowSize = var3;
            this.allowServerNoContext = var4;
            this.preferredClientNoContext = var5;
         } else {
            throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
         }
      } else {
         throw new IllegalArgumentException("preferredServerWindowSize: " + var3 + " (expected: 8-15)");
      }
   }

   public WebSocketServerExtension handshakeExtension(WebSocketExtensionData var1) {
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
               var3 = this.preferredClientWindowSize;
            } else if ("server_max_window_bits".equalsIgnoreCase((String)var8.getKey())) {
               if (this.allowServerWindowSize) {
                  var4 = Integer.parseInt((String)var8.getValue());
                  if (var4 > 15 || var4 < 8) {
                     var2 = false;
                  }
               } else {
                  var2 = false;
               }
            } else if ("client_no_context_takeover".equalsIgnoreCase((String)var8.getKey())) {
               var6 = this.preferredClientNoContext;
            } else if ("server_no_context_takeover".equalsIgnoreCase((String)var8.getKey())) {
               if (this.allowServerNoContext) {
                  var5 = true;
               } else {
                  var2 = false;
               }
            } else {
               var2 = false;
            }
         }

         return var2 ? new PerMessageDeflateServerExtensionHandshaker.PermessageDeflateExtension(this.compressionLevel, var5, var4, var6, var3) : null;
      }
   }

   private static class PermessageDeflateExtension implements WebSocketServerExtension {
      private final int compressionLevel;
      private final boolean serverNoContext;
      private final int serverWindowSize;
      private final boolean clientNoContext;
      private final int clientWindowSize;

      public PermessageDeflateExtension(int var1, boolean var2, int var3, boolean var4, int var5) {
         super();
         this.compressionLevel = var1;
         this.serverNoContext = var2;
         this.serverWindowSize = var3;
         this.clientNoContext = var4;
         this.clientWindowSize = var5;
      }

      public int rsv() {
         return 4;
      }

      public WebSocketExtensionEncoder newExtensionEncoder() {
         return new PerMessageDeflateEncoder(this.compressionLevel, this.clientWindowSize, this.clientNoContext);
      }

      public WebSocketExtensionDecoder newExtensionDecoder() {
         return new PerMessageDeflateDecoder(this.serverNoContext);
      }

      public WebSocketExtensionData newReponseData() {
         HashMap var1 = new HashMap(4);
         if (this.serverNoContext) {
            var1.put("server_no_context_takeover", (Object)null);
         }

         if (this.clientNoContext) {
            var1.put("client_no_context_takeover", (Object)null);
         }

         if (this.serverWindowSize != 15) {
            var1.put("server_max_window_bits", Integer.toString(this.serverWindowSize));
         }

         if (this.clientWindowSize != 15) {
            var1.put("client_max_window_bits", Integer.toString(this.clientWindowSize));
         }

         return new WebSocketExtensionData("permessage-deflate", var1);
      }
   }
}
