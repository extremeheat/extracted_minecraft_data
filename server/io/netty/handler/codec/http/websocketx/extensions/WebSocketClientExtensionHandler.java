package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WebSocketClientExtensionHandler extends ChannelDuplexHandler {
   private final List<WebSocketClientExtensionHandshaker> extensionHandshakers;

   public WebSocketClientExtensionHandler(WebSocketClientExtensionHandshaker... var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("extensionHandshakers");
      } else if (var1.length == 0) {
         throw new IllegalArgumentException("extensionHandshakers must contains at least one handshaker");
      } else {
         this.extensionHandshakers = Arrays.asList(var1);
      }
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (var2 instanceof HttpRequest && WebSocketExtensionUtil.isWebsocketUpgrade(((HttpRequest)var2).headers())) {
         HttpRequest var4 = (HttpRequest)var2;
         String var5 = var4.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);

         WebSocketExtensionData var8;
         for(Iterator var6 = this.extensionHandshakers.iterator(); var6.hasNext(); var5 = WebSocketExtensionUtil.appendExtension(var5, var8.name(), var8.parameters())) {
            WebSocketClientExtensionHandshaker var7 = (WebSocketClientExtensionHandshaker)var6.next();
            var8 = var7.newRequestData();
         }

         var4.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, (Object)var5);
      }

      super.write(var1, var2, var3);
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpResponse) {
         HttpResponse var3 = (HttpResponse)var2;
         if (WebSocketExtensionUtil.isWebsocketUpgrade(var3.headers())) {
            String var4 = var3.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            if (var4 != null) {
               List var5 = WebSocketExtensionUtil.extractExtensions(var4);
               ArrayList var6 = new ArrayList(var5.size());
               int var7 = 0;
               Iterator var8 = var5.iterator();

               label51:
               while(true) {
                  if (!var8.hasNext()) {
                     var8 = var6.iterator();

                     while(true) {
                        if (!var8.hasNext()) {
                           break label51;
                        }

                        WebSocketClientExtension var13 = (WebSocketClientExtension)var8.next();
                        WebSocketExtensionDecoder var14 = var13.newExtensionDecoder();
                        WebSocketExtensionEncoder var15 = var13.newExtensionEncoder();
                        var1.pipeline().addAfter(var1.name(), var14.getClass().getName(), var14);
                        var1.pipeline().addAfter(var1.name(), var15.getClass().getName(), var15);
                     }
                  }

                  WebSocketExtensionData var9 = (WebSocketExtensionData)var8.next();
                  Iterator var10 = this.extensionHandshakers.iterator();

                  WebSocketClientExtension var11;
                  WebSocketClientExtensionHandshaker var12;
                  for(var11 = null; var11 == null && var10.hasNext(); var11 = var12.handshakeExtension(var9)) {
                     var12 = (WebSocketClientExtensionHandshaker)var10.next();
                  }

                  if (var11 == null || (var11.rsv() & var7) != 0) {
                     throw new CodecException("invalid WebSocket Extension handshake for \"" + var4 + '"');
                  }

                  var7 |= var11.rsv();
                  var6.add(var11);
               }
            }

            var1.pipeline().remove(var1.name());
         }
      }

      super.channelRead(var1, var2);
   }
}
