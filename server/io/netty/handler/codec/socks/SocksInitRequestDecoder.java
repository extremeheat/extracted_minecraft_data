package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SocksInitRequestDecoder extends ReplayingDecoder<SocksInitRequestDecoder.State> {
   public SocksInitRequestDecoder() {
      super(SocksInitRequestDecoder.State.CHECK_PROTOCOL_VERSION);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      switch((SocksInitRequestDecoder.State)this.state()) {
      case CHECK_PROTOCOL_VERSION:
         if (var2.readByte() != SocksProtocolVersion.SOCKS5.byteValue()) {
            var3.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
            break;
         } else {
            this.checkpoint(SocksInitRequestDecoder.State.READ_AUTH_SCHEMES);
         }
      case READ_AUTH_SCHEMES:
         byte var4 = var2.readByte();
         Object var5;
         if (var4 > 0) {
            var5 = new ArrayList(var4);

            for(int var6 = 0; var6 < var4; ++var6) {
               ((List)var5).add(SocksAuthScheme.valueOf(var2.readByte()));
            }
         } else {
            var5 = Collections.emptyList();
         }

         var3.add(new SocksInitRequest((List)var5));
         break;
      default:
         throw new Error();
      }

      var1.pipeline().remove((ChannelHandler)this);
   }

   static enum State {
      CHECK_PROTOCOL_VERSION,
      READ_AUTH_SCHEMES;

      private State() {
      }
   }
}
