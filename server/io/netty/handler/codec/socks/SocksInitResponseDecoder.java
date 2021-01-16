package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class SocksInitResponseDecoder extends ReplayingDecoder<SocksInitResponseDecoder.State> {
   public SocksInitResponseDecoder() {
      super(SocksInitResponseDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      switch((SocksInitResponseDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (var2.readByte() != SocksProtocolVersion.SOCKS5.byteValue()) {
            var3.add(SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE);
            break;
         } else {
            this.checkpoint(SocksInitResponseDecoder.State.READ_PREFERRED_AUTH_TYPE);
         }
      case READ_PREFERRED_AUTH_TYPE:
         SocksAuthScheme var4 = SocksAuthScheme.valueOf(var2.readByte());
         var3.add(new SocksInitResponse(var4));
         break;
      default:
         throw new Error();
      }

      var1.pipeline().remove((ChannelHandler)this);
   }

   static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_PREFERRED_AUTH_TYPE;

      private State() {
      }
   }
}
