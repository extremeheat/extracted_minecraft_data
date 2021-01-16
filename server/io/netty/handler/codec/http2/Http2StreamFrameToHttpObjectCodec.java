package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import java.util.List;

@ChannelHandler.Sharable
public class Http2StreamFrameToHttpObjectCodec extends MessageToMessageCodec<Http2StreamFrame, HttpObject> {
   private final boolean isServer;
   private final boolean validateHeaders;
   private HttpScheme scheme;

   public Http2StreamFrameToHttpObjectCodec(boolean var1, boolean var2) {
      super();
      this.isServer = var1;
      this.validateHeaders = var2;
      this.scheme = HttpScheme.HTTP;
   }

   public Http2StreamFrameToHttpObjectCodec(boolean var1) {
      this(var1, true);
   }

   public boolean acceptInboundMessage(Object var1) throws Exception {
      return var1 instanceof Http2HeadersFrame || var1 instanceof Http2DataFrame;
   }

   protected void decode(ChannelHandlerContext var1, Http2StreamFrame var2, List<Object> var3) throws Exception {
      if (var2 instanceof Http2HeadersFrame) {
         Http2HeadersFrame var4 = (Http2HeadersFrame)var2;
         Http2Headers var5 = var4.headers();
         Http2FrameStream var6 = var4.stream();
         int var7 = var6 == null ? 0 : var6.id();
         CharSequence var8 = var5.status();
         FullHttpMessage var9;
         if (null != var8 && HttpResponseStatus.CONTINUE.codeAsText().contentEquals(var8)) {
            var9 = this.newFullMessage(var7, var5, var1.alloc());
            var3.add(var9);
            return;
         }

         if (var4.isEndStream()) {
            if (var5.method() == null && var8 == null) {
               DefaultLastHttpContent var11 = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
               HttpConversionUtil.addHttp2ToHttpHeaders(var7, var5, var11.trailingHeaders(), HttpVersion.HTTP_1_1, true, true);
               var3.add(var11);
            } else {
               var9 = this.newFullMessage(var7, var5, var1.alloc());
               var3.add(var9);
            }
         } else {
            HttpMessage var12 = this.newMessage(var7, var5);
            if (!HttpUtil.isContentLengthSet(var12)) {
               var12.headers().add((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
            }

            var3.add(var12);
         }
      } else if (var2 instanceof Http2DataFrame) {
         Http2DataFrame var10 = (Http2DataFrame)var2;
         if (var10.isEndStream()) {
            var3.add(new DefaultLastHttpContent(var10.content().retain(), this.validateHeaders));
         } else {
            var3.add(new DefaultHttpContent(var10.content().retain()));
         }
      }

   }

   private void encodeLastContent(LastHttpContent var1, List<Object> var2) {
      boolean var3 = !(var1 instanceof FullHttpMessage) && var1.trailingHeaders().isEmpty();
      if (var1.content().isReadable() || var3) {
         var2.add(new DefaultHttp2DataFrame(var1.content().retain(), var1.trailingHeaders().isEmpty()));
      }

      if (!var1.trailingHeaders().isEmpty()) {
         Http2Headers var4 = HttpConversionUtil.toHttp2Headers(var1.trailingHeaders(), this.validateHeaders);
         var2.add(new DefaultHttp2HeadersFrame(var4, true));
      }

   }

   protected void encode(ChannelHandlerContext var1, HttpObject var2, List<Object> var3) throws Exception {
      if (var2 instanceof HttpResponse) {
         HttpResponse var4 = (HttpResponse)var2;
         if (var4.status().equals(HttpResponseStatus.CONTINUE)) {
            if (var4 instanceof FullHttpResponse) {
               Http2Headers var10 = this.toHttp2Headers(var4);
               var3.add(new DefaultHttp2HeadersFrame(var10, false));
               return;
            }

            throw new EncoderException(HttpResponseStatus.CONTINUE.toString() + " must be a FullHttpResponse");
         }
      }

      if (var2 instanceof HttpMessage) {
         Http2Headers var7 = this.toHttp2Headers((HttpMessage)var2);
         boolean var5 = false;
         if (var2 instanceof FullHttpMessage) {
            FullHttpMessage var6 = (FullHttpMessage)var2;
            var5 = !var6.content().isReadable() && var6.trailingHeaders().isEmpty();
         }

         var3.add(new DefaultHttp2HeadersFrame(var7, var5));
      }

      if (var2 instanceof LastHttpContent) {
         LastHttpContent var8 = (LastHttpContent)var2;
         this.encodeLastContent(var8, var3);
      } else if (var2 instanceof HttpContent) {
         HttpContent var9 = (HttpContent)var2;
         var3.add(new DefaultHttp2DataFrame(var9.content().retain(), false));
      }

   }

   private Http2Headers toHttp2Headers(HttpMessage var1) {
      if (var1 instanceof HttpRequest) {
         var1.headers().set((CharSequence)HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), (Object)this.scheme.name());
      }

      return HttpConversionUtil.toHttp2Headers(var1, this.validateHeaders);
   }

   private HttpMessage newMessage(int var1, Http2Headers var2) throws Http2Exception {
      return (HttpMessage)(this.isServer ? HttpConversionUtil.toHttpRequest(var1, var2, this.validateHeaders) : HttpConversionUtil.toHttpResponse(var1, var2, this.validateHeaders));
   }

   private FullHttpMessage newFullMessage(int var1, Http2Headers var2, ByteBufAllocator var3) throws Http2Exception {
      return (FullHttpMessage)(this.isServer ? HttpConversionUtil.toFullHttpRequest(var1, var2, var3, this.validateHeaders) : HttpConversionUtil.toFullHttpResponse(var1, var2, var3, this.validateHeaders));
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      super.handlerAdded(var1);
      this.scheme = this.isSsl(var1) ? HttpScheme.HTTPS : HttpScheme.HTTP;
   }

   protected boolean isSsl(ChannelHandlerContext var1) {
      Channel var2 = var1.channel();
      Channel var3 = var2 instanceof Http2StreamChannel ? var2.parent() : var2;
      return null != var3.pipeline().get(SslHandler.class);
   }
}
