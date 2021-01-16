package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CorruptedFrameException;

public class Utf8FrameValidator extends ChannelInboundHandlerAdapter {
   private int fragmentedFramesCount;
   private Utf8Validator utf8Validator;

   public Utf8FrameValidator() {
      super();
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof WebSocketFrame) {
         WebSocketFrame var3 = (WebSocketFrame)var2;
         if (((WebSocketFrame)var2).isFinalFragment()) {
            if (!(var3 instanceof PingWebSocketFrame)) {
               this.fragmentedFramesCount = 0;
               if (var3 instanceof TextWebSocketFrame || this.utf8Validator != null && this.utf8Validator.isChecking()) {
                  this.checkUTF8String(var1, var3.content());
                  this.utf8Validator.finish();
               }
            }
         } else {
            if (this.fragmentedFramesCount == 0) {
               if (var3 instanceof TextWebSocketFrame) {
                  this.checkUTF8String(var1, var3.content());
               }
            } else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
               this.checkUTF8String(var1, var3.content());
            }

            ++this.fragmentedFramesCount;
         }
      }

      super.channelRead(var1, var2);
   }

   private void checkUTF8String(ChannelHandlerContext var1, ByteBuf var2) {
      try {
         if (this.utf8Validator == null) {
            this.utf8Validator = new Utf8Validator();
         }

         this.utf8Validator.check(var2);
      } catch (CorruptedFrameException var4) {
         if (var1.channel().isActive()) {
            var1.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
         }
      }

   }
}
