package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

public class HttpToHttp2ConnectionHandler extends Http2ConnectionHandler {
   private final boolean validateHeaders;
   private int currentStreamId;

   protected HttpToHttp2ConnectionHandler(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2, Http2Settings var3, boolean var4) {
      super(var1, var2, var3);
      this.validateHeaders = var4;
   }

   private int getStreamId(HttpHeaders var1) throws Exception {
      return var1.getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), this.connection().local().incrementAndGetNextStreamId());
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) {
      if (!(var2 instanceof HttpMessage) && !(var2 instanceof HttpContent)) {
         var1.write(var2, var3);
      } else {
         boolean var4 = true;
         Http2CodecUtil.SimpleChannelPromiseAggregator var5 = new Http2CodecUtil.SimpleChannelPromiseAggregator(var3, var1.channel(), var1.executor());

         try {
            Http2ConnectionEncoder var6 = this.encoder();
            boolean var7 = false;
            if (var2 instanceof HttpMessage) {
               HttpMessage var8 = (HttpMessage)var2;
               this.currentStreamId = this.getStreamId(var8.headers());
               Http2Headers var9 = HttpConversionUtil.toHttp2Headers(var8, this.validateHeaders);
               var7 = var2 instanceof FullHttpMessage && !((FullHttpMessage)var2).content().isReadable();
               writeHeaders(var1, var6, this.currentStreamId, var8.headers(), var9, var7, var5);
            }

            if (!var7 && var2 instanceof HttpContent) {
               boolean var17 = false;
               Object var18 = EmptyHttpHeaders.INSTANCE;
               Object var10 = EmptyHttp2Headers.INSTANCE;
               if (var2 instanceof LastHttpContent) {
                  var17 = true;
                  LastHttpContent var11 = (LastHttpContent)var2;
                  var18 = var11.trailingHeaders();
                  var10 = HttpConversionUtil.toHttp2Headers((HttpHeaders)var18, this.validateHeaders);
               }

               ByteBuf var19 = ((HttpContent)var2).content();
               var7 = var17 && ((HttpHeaders)var18).isEmpty();
               var4 = false;
               var6.writeData(var1, this.currentStreamId, var19, 0, var7, var5.newPromise());
               if (!((HttpHeaders)var18).isEmpty()) {
                  writeHeaders(var1, var6, this.currentStreamId, (HttpHeaders)var18, (Http2Headers)var10, true, var5);
               }
            }
         } catch (Throwable var15) {
            this.onError(var1, true, var15);
            var5.setFailure(var15);
         } finally {
            if (var4) {
               ReferenceCountUtil.release(var2);
            }

            var5.doneAllocatingPromises();
         }

      }
   }

   private static void writeHeaders(ChannelHandlerContext var0, Http2ConnectionEncoder var1, int var2, HttpHeaders var3, Http2Headers var4, boolean var5, Http2CodecUtil.SimpleChannelPromiseAggregator var6) {
      int var7 = var3.getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_DEPENDENCY_ID.text(), 0);
      short var8 = var3.getShort(HttpConversionUtil.ExtensionHeaderNames.STREAM_WEIGHT.text(), (short)16);
      var1.writeHeaders(var0, var2, var4, var7, var8, false, 0, var5, var6.newPromise());
   }
}
