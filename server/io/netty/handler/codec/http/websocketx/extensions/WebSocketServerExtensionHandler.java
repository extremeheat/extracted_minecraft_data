package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WebSocketServerExtensionHandler extends ChannelDuplexHandler {
   private final List<WebSocketServerExtensionHandshaker> extensionHandshakers;
   private List<WebSocketServerExtension> validExtensions;

   public WebSocketServerExtensionHandler(WebSocketServerExtensionHandshaker... var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("extensionHandshakers");
      } else if (var1.length == 0) {
         throw new IllegalArgumentException("extensionHandshakers must contains at least one handshaker");
      } else {
         this.extensionHandshakers = Arrays.asList(var1);
      }
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpRequest) {
         HttpRequest var3 = (HttpRequest)var2;
         if (WebSocketExtensionUtil.isWebsocketUpgrade(var3.headers())) {
            String var4 = var3.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
            if (var4 != null) {
               List var5 = WebSocketExtensionUtil.extractExtensions(var4);
               int var6 = 0;
               Iterator var7 = var5.iterator();

               while(var7.hasNext()) {
                  WebSocketExtensionData var8 = (WebSocketExtensionData)var7.next();
                  Iterator var9 = this.extensionHandshakers.iterator();

                  WebSocketServerExtension var10;
                  WebSocketServerExtensionHandshaker var11;
                  for(var10 = null; var10 == null && var9.hasNext(); var10 = var11.handshakeExtension(var8)) {
                     var11 = (WebSocketServerExtensionHandshaker)var9.next();
                  }

                  if (var10 != null && (var10.rsv() & var6) == 0) {
                     if (this.validExtensions == null) {
                        this.validExtensions = new ArrayList(1);
                     }

                     var6 |= var10.rsv();
                     this.validExtensions.add(var10);
                  }
               }
            }
         }
      }

      super.channelRead(var1, var2);
   }

   public void write(final ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (var2 instanceof HttpResponse && WebSocketExtensionUtil.isWebsocketUpgrade(((HttpResponse)var2).headers()) && this.validExtensions != null) {
         HttpResponse var4 = (HttpResponse)var2;
         String var5 = var4.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);

         WebSocketExtensionData var8;
         for(Iterator var6 = this.validExtensions.iterator(); var6.hasNext(); var5 = WebSocketExtensionUtil.appendExtension(var5, var8.name(), var8.parameters())) {
            WebSocketServerExtension var7 = (WebSocketServerExtension)var6.next();
            var8 = var7.newReponseData();
         }

         var3.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               if (var1x.isSuccess()) {
                  Iterator var2 = WebSocketServerExtensionHandler.this.validExtensions.iterator();

                  while(var2.hasNext()) {
                     WebSocketServerExtension var3 = (WebSocketServerExtension)var2.next();
                     WebSocketExtensionDecoder var4 = var3.newExtensionDecoder();
                     WebSocketExtensionEncoder var5 = var3.newExtensionEncoder();
                     var1.pipeline().addAfter(var1.name(), var4.getClass().getName(), var4);
                     var1.pipeline().addAfter(var1.name(), var5.getClass().getName(), var5);
                  }
               }

               var1.pipeline().remove(var1.name());
            }
         });
         if (var5 != null) {
            var4.headers().set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, (Object)var5);
         }
      }

      super.write(var1, var2, var3);
   }
}
