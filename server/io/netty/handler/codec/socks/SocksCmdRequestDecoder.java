package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.NetUtil;
import java.util.List;

public class SocksCmdRequestDecoder extends ReplayingDecoder<SocksCmdRequestDecoder.State> {
   private SocksCmdType cmdType;
   private SocksAddressType addressType;

   public SocksCmdRequestDecoder() {
      super(SocksCmdRequestDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      label22:
      switch((SocksCmdRequestDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (var2.readByte() != SocksProtocolVersion.SOCKS5.byteValue()) {
            var3.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
            break;
         } else {
            this.checkpoint(SocksCmdRequestDecoder.State.READ_CMD_HEADER);
         }
      case READ_CMD_HEADER:
         this.cmdType = SocksCmdType.valueOf(var2.readByte());
         var2.skipBytes(1);
         this.addressType = SocksAddressType.valueOf(var2.readByte());
         this.checkpoint(SocksCmdRequestDecoder.State.READ_CMD_ADDRESS);
      case READ_CMD_ADDRESS:
         String var5;
         int var6;
         switch(this.addressType) {
         case IPv4:
            String var8 = NetUtil.intToIpAddress(var2.readInt());
            int var9 = var2.readUnsignedShort();
            var3.add(new SocksCmdRequest(this.cmdType, this.addressType, var8, var9));
            break label22;
         case DOMAIN:
            byte var7 = var2.readByte();
            var5 = SocksCommonUtils.readUsAscii(var2, var7);
            var6 = var2.readUnsignedShort();
            var3.add(new SocksCmdRequest(this.cmdType, this.addressType, var5, var6));
            break label22;
         case IPv6:
            byte[] var4 = new byte[16];
            var2.readBytes(var4);
            var5 = SocksCommonUtils.ipv6toStr(var4);
            var6 = var2.readUnsignedShort();
            var3.add(new SocksCmdRequest(this.cmdType, this.addressType, var5, var6));
            break label22;
         case UNKNOWN:
            var3.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
            break label22;
         default:
            throw new Error();
         }
      default:
         throw new Error();
      }

      var1.pipeline().remove((ChannelHandler)this);
   }

   static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_CMD_HEADER,
      READ_CMD_ADDRESS;

      private State() {
      }
   }
}
