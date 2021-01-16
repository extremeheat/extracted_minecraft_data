package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class SocksAuthRequestDecoder extends ReplayingDecoder<SocksAuthRequestDecoder.State> {
   private String username;

   public SocksAuthRequestDecoder() {
      super(SocksAuthRequestDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      byte var4;
      switch((SocksAuthRequestDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (var2.readByte() != SocksSubnegotiationVersion.AUTH_PASSWORD.byteValue()) {
            var3.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
            break;
         } else {
            this.checkpoint(SocksAuthRequestDecoder.State.READ_USERNAME);
         }
      case READ_USERNAME:
         var4 = var2.readByte();
         this.username = SocksCommonUtils.readUsAscii(var2, var4);
         this.checkpoint(SocksAuthRequestDecoder.State.READ_PASSWORD);
      case READ_PASSWORD:
         var4 = var2.readByte();
         String var5 = SocksCommonUtils.readUsAscii(var2, var4);
         var3.add(new SocksAuthRequest(this.username, var5));
         break;
      default:
         throw new Error();
      }

      var1.pipeline().remove((ChannelHandler)this);
   }

   static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_USERNAME,
      READ_PASSWORD;

      private State() {
      }
   }
}
