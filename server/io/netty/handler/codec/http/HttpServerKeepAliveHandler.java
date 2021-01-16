package io.netty.handler.codec.http;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class HttpServerKeepAliveHandler extends ChannelDuplexHandler {
   private static final String MULTIPART_PREFIX = "multipart";
   private boolean persistentConnection = true;
   private int pendingResponses;

   public HttpServerKeepAliveHandler() {
      super();
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpRequest) {
         HttpRequest var3 = (HttpRequest)var2;
         if (this.persistentConnection) {
            ++this.pendingResponses;
            this.persistentConnection = HttpUtil.isKeepAlive(var3);
         }
      }

      super.channelRead(var1, var2);
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (var2 instanceof HttpResponse) {
         HttpResponse var4 = (HttpResponse)var2;
         this.trackResponse(var4);
         if (!HttpUtil.isKeepAlive(var4) || !isSelfDefinedMessageLength(var4)) {
            this.pendingResponses = 0;
            this.persistentConnection = false;
         }

         if (!this.shouldKeepAlive()) {
            HttpUtil.setKeepAlive(var4, false);
         }
      }

      if (var2 instanceof LastHttpContent && !this.shouldKeepAlive()) {
         var3 = var3.unvoid().addListener(ChannelFutureListener.CLOSE);
      }

      super.write(var1, var2, var3);
   }

   private void trackResponse(HttpResponse var1) {
      if (!isInformational(var1)) {
         --this.pendingResponses;
      }

   }

   private boolean shouldKeepAlive() {
      return this.pendingResponses != 0 || this.persistentConnection;
   }

   private static boolean isSelfDefinedMessageLength(HttpResponse var0) {
      return HttpUtil.isContentLengthSet(var0) || HttpUtil.isTransferEncodingChunked(var0) || isMultipart(var0) || isInformational(var0) || var0.status().code() == HttpResponseStatus.NO_CONTENT.code();
   }

   private static boolean isInformational(HttpResponse var0) {
      return var0.status().codeClass() == HttpStatusClass.INFORMATIONAL;
   }

   private static boolean isMultipart(HttpResponse var0) {
      String var1 = var0.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE);
      return var1 != null && var1.regionMatches(true, 0, "multipart", 0, "multipart".length());
   }
}
