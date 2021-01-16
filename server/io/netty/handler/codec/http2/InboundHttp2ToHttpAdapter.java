package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.internal.ObjectUtil;

public class InboundHttp2ToHttpAdapter extends Http2EventAdapter {
   private static final InboundHttp2ToHttpAdapter.ImmediateSendDetector DEFAULT_SEND_DETECTOR = new InboundHttp2ToHttpAdapter.ImmediateSendDetector() {
      public boolean mustSendImmediately(FullHttpMessage var1) {
         if (var1 instanceof FullHttpResponse) {
            return ((FullHttpResponse)var1).status().codeClass() == HttpStatusClass.INFORMATIONAL;
         } else {
            return var1 instanceof FullHttpRequest ? var1.headers().contains((CharSequence)HttpHeaderNames.EXPECT) : false;
         }
      }

      public FullHttpMessage copyIfNeeded(FullHttpMessage var1) {
         if (var1 instanceof FullHttpRequest) {
            FullHttpRequest var2 = ((FullHttpRequest)var1).replace(Unpooled.buffer(0));
            var2.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
            return var2;
         } else {
            return null;
         }
      }
   };
   private final int maxContentLength;
   private final InboundHttp2ToHttpAdapter.ImmediateSendDetector sendDetector;
   private final Http2Connection.PropertyKey messageKey;
   private final boolean propagateSettings;
   protected final Http2Connection connection;
   protected final boolean validateHttpHeaders;

   protected InboundHttp2ToHttpAdapter(Http2Connection var1, int var2, boolean var3, boolean var4) {
      super();
      ObjectUtil.checkNotNull(var1, "connection");
      if (var2 <= 0) {
         throw new IllegalArgumentException("maxContentLength: " + var2 + " (expected: > 0)");
      } else {
         this.connection = var1;
         this.maxContentLength = var2;
         this.validateHttpHeaders = var3;
         this.propagateSettings = var4;
         this.sendDetector = DEFAULT_SEND_DETECTOR;
         this.messageKey = var1.newKey();
      }
   }

   protected final void removeMessage(Http2Stream var1, boolean var2) {
      FullHttpMessage var3 = (FullHttpMessage)var1.removeProperty(this.messageKey);
      if (var2 && var3 != null) {
         var3.release();
      }

   }

   protected final FullHttpMessage getMessage(Http2Stream var1) {
      return (FullHttpMessage)var1.getProperty(this.messageKey);
   }

   protected final void putMessage(Http2Stream var1, FullHttpMessage var2) {
      FullHttpMessage var3 = (FullHttpMessage)var1.setProperty(this.messageKey, var2);
      if (var3 != var2 && var3 != null) {
         var3.release();
      }

   }

   public void onStreamRemoved(Http2Stream var1) {
      this.removeMessage(var1, true);
   }

   protected void fireChannelRead(ChannelHandlerContext var1, FullHttpMessage var2, boolean var3, Http2Stream var4) {
      this.removeMessage(var4, var3);
      HttpUtil.setContentLength(var2, (long)var2.content().readableBytes());
      var1.fireChannelRead(var2);
   }

   protected FullHttpMessage newMessage(Http2Stream var1, Http2Headers var2, boolean var3, ByteBufAllocator var4) throws Http2Exception {
      return (FullHttpMessage)(this.connection.isServer() ? HttpConversionUtil.toFullHttpRequest(var1.id(), var2, var4, var3) : HttpConversionUtil.toFullHttpResponse(var1.id(), var2, var4, var3));
   }

   protected FullHttpMessage processHeadersBegin(ChannelHandlerContext var1, Http2Stream var2, Http2Headers var3, boolean var4, boolean var5, boolean var6) throws Http2Exception {
      FullHttpMessage var7 = this.getMessage(var2);
      boolean var8 = true;
      if (var7 == null) {
         var7 = this.newMessage(var2, var3, this.validateHttpHeaders, var1.alloc());
      } else if (var5) {
         var8 = false;
         HttpConversionUtil.addHttp2ToHttpHeaders(var2.id(), var3, var7, var6);
      } else {
         var8 = false;
         var7 = null;
      }

      if (this.sendDetector.mustSendImmediately(var7)) {
         FullHttpMessage var9 = var4 ? null : this.sendDetector.copyIfNeeded(var7);
         this.fireChannelRead(var1, var7, var8, var2);
         return var9;
      } else {
         return var7;
      }
   }

   private void processHeadersEnd(ChannelHandlerContext var1, Http2Stream var2, FullHttpMessage var3, boolean var4) {
      if (var4) {
         this.fireChannelRead(var1, var3, this.getMessage(var2) != var3, var2);
      } else {
         this.putMessage(var2, var3);
      }

   }

   public int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5) throws Http2Exception {
      Http2Stream var6 = this.connection.stream(var2);
      FullHttpMessage var7 = this.getMessage(var6);
      if (var7 == null) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Data Frame received for unknown stream id %d", var2);
      } else {
         ByteBuf var8 = var7.content();
         int var9 = var3.readableBytes();
         if (var8.readableBytes() > this.maxContentLength - var9) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "Content length exceeded max of %d for stream id %d", this.maxContentLength, var2);
         } else {
            var8.writeBytes(var3, var3.readerIndex(), var9);
            if (var5) {
               this.fireChannelRead(var1, var7, false, var6);
            }

            return var9 + var4;
         }
      }
   }

   public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5) throws Http2Exception {
      Http2Stream var6 = this.connection.stream(var2);
      FullHttpMessage var7 = this.processHeadersBegin(var1, var6, var3, var5, true, true);
      if (var7 != null) {
         this.processHeadersEnd(var1, var6, var7, var5);
      }

   }

   public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8) throws Http2Exception {
      Http2Stream var9 = this.connection.stream(var2);
      FullHttpMessage var10 = this.processHeadersBegin(var1, var9, var3, var8, true, true);
      if (var10 != null) {
         if (var4 != 0) {
            var10.headers().setInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_DEPENDENCY_ID.text(), var4);
         }

         var10.headers().setShort(HttpConversionUtil.ExtensionHeaderNames.STREAM_WEIGHT.text(), var5);
         this.processHeadersEnd(var1, var9, var10, var8);
      }

   }

   public void onRstStreamRead(ChannelHandlerContext var1, int var2, long var3) throws Http2Exception {
      Http2Stream var5 = this.connection.stream(var2);
      FullHttpMessage var6 = this.getMessage(var5);
      if (var6 != null) {
         this.onRstStreamRead(var5, var6);
      }

      var1.fireExceptionCaught(Http2Exception.streamError(var2, Http2Error.valueOf(var3), "HTTP/2 to HTTP layer caught stream reset"));
   }

   public void onPushPromiseRead(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5) throws Http2Exception {
      Http2Stream var6 = this.connection.stream(var3);
      if (var4.status() == null) {
         var4.status(HttpResponseStatus.OK.codeAsText());
      }

      FullHttpMessage var7 = this.processHeadersBegin(var1, var6, var4, false, false, false);
      if (var7 == null) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Push Promise Frame received for pre-existing stream id %d", var3);
      } else {
         var7.headers().setInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_PROMISE_ID.text(), var2);
         var7.headers().setShort(HttpConversionUtil.ExtensionHeaderNames.STREAM_WEIGHT.text(), (short)16);
         this.processHeadersEnd(var1, var6, var7, false);
      }
   }

   public void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) throws Http2Exception {
      if (this.propagateSettings) {
         var1.fireChannelRead(var2);
      }

   }

   protected void onRstStreamRead(Http2Stream var1, FullHttpMessage var2) {
      this.removeMessage(var1, true);
   }

   private interface ImmediateSendDetector {
      boolean mustSendImmediately(FullHttpMessage var1);

      FullHttpMessage copyIfNeeded(FullHttpMessage var1);
   }
}
