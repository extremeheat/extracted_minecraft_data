package io.netty.handler.codec.bytes;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ByteArrayEncoder extends MessageToMessageEncoder<byte[]> {
   public ByteArrayEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, byte[] var2, List<Object> var3) throws Exception {
      var3.add(Unpooled.wrappedBuffer(var2));
   }
}
