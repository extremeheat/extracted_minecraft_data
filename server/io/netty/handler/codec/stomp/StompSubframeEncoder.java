package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.AsciiHeadersEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class StompSubframeEncoder extends MessageToMessageEncoder<StompSubframe> {
   public StompSubframeEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, StompSubframe var2, List<Object> var3) throws Exception {
      ByteBuf var5;
      if (var2 instanceof StompFrame) {
         StompFrame var4 = (StompFrame)var2;
         var5 = encodeFrame(var4, var1);
         var3.add(var5);
         ByteBuf var6 = encodeContent(var4, var1);
         var3.add(var6);
      } else if (var2 instanceof StompHeadersSubframe) {
         StompHeadersSubframe var7 = (StompHeadersSubframe)var2;
         var5 = encodeFrame(var7, var1);
         var3.add(var5);
      } else if (var2 instanceof StompContentSubframe) {
         StompContentSubframe var8 = (StompContentSubframe)var2;
         var5 = encodeContent(var8, var1);
         var3.add(var5);
      }

   }

   private static ByteBuf encodeContent(StompContentSubframe var0, ChannelHandlerContext var1) {
      if (var0 instanceof LastStompContentSubframe) {
         ByteBuf var2 = var1.alloc().buffer(var0.content().readableBytes() + 1);
         var2.writeBytes(var0.content());
         var2.writeByte(0);
         return var2;
      } else {
         return var0.content().retain();
      }
   }

   private static ByteBuf encodeFrame(StompHeadersSubframe var0, ChannelHandlerContext var1) {
      ByteBuf var2 = var1.alloc().buffer();
      var2.writeCharSequence(var0.command().toString(), CharsetUtil.US_ASCII);
      var2.writeByte(10);
      AsciiHeadersEncoder var3 = new AsciiHeadersEncoder(var2, AsciiHeadersEncoder.SeparatorType.COLON, AsciiHeadersEncoder.NewlineType.LF);
      Iterator var4 = var0.headers().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var3.encode(var5);
      }

      var2.writeByte(10);
      return var2;
   }
}
