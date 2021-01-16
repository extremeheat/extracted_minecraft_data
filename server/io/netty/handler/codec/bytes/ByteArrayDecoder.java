package io.netty.handler.codec.bytes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class ByteArrayDecoder extends MessageToMessageDecoder<ByteBuf> {
   public ByteArrayDecoder() {
      super();
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      byte[] var4 = new byte[var2.readableBytes()];
      var2.getBytes(0, (byte[])var4);
      var3.add(var4);
   }
}
