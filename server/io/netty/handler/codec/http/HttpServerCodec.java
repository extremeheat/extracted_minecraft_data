package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public final class HttpServerCodec extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder> implements HttpServerUpgradeHandler.SourceCodec {
   private final Queue<HttpMethod> queue;

   public HttpServerCodec() {
      this(4096, 8192, 8192);
   }

   public HttpServerCodec(int var1, int var2, int var3) {
      super();
      this.queue = new ArrayDeque();
      this.init(new HttpServerCodec.HttpServerRequestDecoder(var1, var2, var3), new HttpServerCodec.HttpServerResponseEncoder());
   }

   public HttpServerCodec(int var1, int var2, int var3, boolean var4) {
      super();
      this.queue = new ArrayDeque();
      this.init(new HttpServerCodec.HttpServerRequestDecoder(var1, var2, var3, var4), new HttpServerCodec.HttpServerResponseEncoder());
   }

   public HttpServerCodec(int var1, int var2, int var3, boolean var4, int var5) {
      super();
      this.queue = new ArrayDeque();
      this.init(new HttpServerCodec.HttpServerRequestDecoder(var1, var2, var3, var4, var5), new HttpServerCodec.HttpServerResponseEncoder());
   }

   public void upgradeFrom(ChannelHandlerContext var1) {
      var1.pipeline().remove((ChannelHandler)this);
   }

   private final class HttpServerResponseEncoder extends HttpResponseEncoder {
      private HttpMethod method;

      private HttpServerResponseEncoder() {
         super();
      }

      protected void sanitizeHeadersBeforeEncode(HttpResponse var1, boolean var2) {
         if (!var2 && this.method == HttpMethod.CONNECT && var1.status().codeClass() == HttpStatusClass.SUCCESS) {
            var1.headers().remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
         } else {
            super.sanitizeHeadersBeforeEncode(var1, var2);
         }
      }

      protected boolean isContentAlwaysEmpty(HttpResponse var1) {
         this.method = (HttpMethod)HttpServerCodec.this.queue.poll();
         return HttpMethod.HEAD.equals(this.method) || super.isContentAlwaysEmpty(var1);
      }

      // $FF: synthetic method
      HttpServerResponseEncoder(Object var2) {
         this();
      }
   }

   private final class HttpServerRequestDecoder extends HttpRequestDecoder {
      public HttpServerRequestDecoder(int var2, int var3, int var4) {
         super(var2, var3, var4);
      }

      public HttpServerRequestDecoder(int var2, int var3, int var4, boolean var5) {
         super(var2, var3, var4, var5);
      }

      public HttpServerRequestDecoder(int var2, int var3, int var4, boolean var5, int var6) {
         super(var2, var3, var4, var5, var6);
      }

      protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
         int var4 = var3.size();
         super.decode(var1, var2, var3);
         int var5 = var3.size();

         for(int var6 = var4; var6 < var5; ++var6) {
            Object var7 = var3.get(var6);
            if (var7 instanceof HttpRequest) {
               HttpServerCodec.this.queue.add(((HttpRequest)var7).method());
            }
         }

      }
   }
}
