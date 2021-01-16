package io.netty.handler.codec.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class HttpServerExpectContinueHandler extends ChannelInboundHandlerAdapter {
   private static final FullHttpResponse EXPECTATION_FAILED;
   private static final FullHttpResponse ACCEPT;

   public HttpServerExpectContinueHandler() {
      super();
   }

   protected HttpResponse acceptMessage(HttpRequest var1) {
      return ACCEPT.retainedDuplicate();
   }

   protected HttpResponse rejectResponse(HttpRequest var1) {
      return EXPECTATION_FAILED.retainedDuplicate();
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpRequest) {
         HttpRequest var3 = (HttpRequest)var2;
         if (HttpUtil.is100ContinueExpected(var3)) {
            HttpResponse var4 = this.acceptMessage(var3);
            if (var4 == null) {
               HttpResponse var5 = this.rejectResponse(var3);
               ReferenceCountUtil.release(var2);
               var1.writeAndFlush(var5).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
               return;
            }

            var1.writeAndFlush(var4).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            var3.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
         }
      }

      super.channelRead(var1, var2);
   }

   static {
      EXPECTATION_FAILED = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER);
      ACCEPT = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
      EXPECTATION_FAILED.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (int)0);
      ACCEPT.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (int)0);
   }
}
