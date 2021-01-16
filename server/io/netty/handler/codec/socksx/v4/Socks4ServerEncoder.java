package io.netty.handler.codec.socksx.v4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.NetUtil;

@ChannelHandler.Sharable
public final class Socks4ServerEncoder extends MessageToByteEncoder<Socks4CommandResponse> {
   public static final Socks4ServerEncoder INSTANCE = new Socks4ServerEncoder();
   private static final byte[] IPv4_HOSTNAME_ZEROED = new byte[]{0, 0, 0, 0};

   private Socks4ServerEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, Socks4CommandResponse var2, ByteBuf var3) throws Exception {
      var3.writeByte(0);
      var3.writeByte(var2.status().byteValue());
      var3.writeShort(var2.dstPort());
      var3.writeBytes(var2.dstAddr() == null ? IPv4_HOSTNAME_ZEROED : NetUtil.createByteArrayFromIpAddressString(var2.dstAddr()));
   }
}
