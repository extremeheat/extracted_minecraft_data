package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class SocksAuthResponseDecoder extends ReplayingDecoder<SocksAuthResponseDecoder.State> {
   public SocksAuthResponseDecoder() {
      super(SocksAuthResponseDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      switch((SocksAuthResponseDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (var2.readByte() != SocksSubnegotiationVersion.AUTH_PASSWORD.byteValue()) {
            var3.add(SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE);
            break;
         } else {
            this.checkpoint(SocksAuthResponseDecoder.State.READ_AUTH_RESPONSE);
         }
      case READ_AUTH_RESPONSE:
         SocksAuthStatus var4 = SocksAuthStatus.valueOf(var2.readByte());
         var3.add(new SocksAuthResponse(var4));
         break;
      default:
         throw new Error();
      }

      var1.pipeline().remove((ChannelHandler)this);
   }

   static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_AUTH_RESPONSE;

      private State() {
      }
   }
}
