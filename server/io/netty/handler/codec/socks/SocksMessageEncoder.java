package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class SocksMessageEncoder extends MessageToByteEncoder<SocksMessage> {
   public SocksMessageEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, SocksMessage var2, ByteBuf var3) throws Exception {
      var2.encodeAsByteBuf(var3);
   }
}
