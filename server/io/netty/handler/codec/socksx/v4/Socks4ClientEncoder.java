package io.netty.handler.codec.socksx.v4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.NetUtil;

@ChannelHandler.Sharable
public final class Socks4ClientEncoder extends MessageToByteEncoder<Socks4CommandRequest> {
   public static final Socks4ClientEncoder INSTANCE = new Socks4ClientEncoder();
   private static final byte[] IPv4_DOMAIN_MARKER = new byte[]{0, 0, 0, 1};

   private Socks4ClientEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, Socks4CommandRequest var2, ByteBuf var3) throws Exception {
      var3.writeByte(var2.version().byteValue());
      var3.writeByte(var2.type().byteValue());
      var3.writeShort(var2.dstPort());
      if (NetUtil.isValidIpV4Address(var2.dstAddr())) {
         var3.writeBytes(NetUtil.createByteArrayFromIpAddressString(var2.dstAddr()));
         ByteBufUtil.writeAscii((ByteBuf)var3, var2.userId());
         var3.writeByte(0);
      } else {
         var3.writeBytes(IPv4_DOMAIN_MARKER);
         ByteBufUtil.writeAscii((ByteBuf)var3, var2.userId());
         var3.writeByte(0);
         ByteBufUtil.writeAscii((ByteBuf)var3, var2.dstAddr());
         var3.writeByte(0);
      }

   }
}
