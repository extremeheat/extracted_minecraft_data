package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

public class Socks5PasswordAuthResponseDecoder extends ReplayingDecoder<Socks5PasswordAuthResponseDecoder.State> {
   public Socks5PasswordAuthResponseDecoder() {
      super(Socks5PasswordAuthResponseDecoder.State.INIT);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch((Socks5PasswordAuthResponseDecoder.State)this.state()) {
         case INIT:
            byte var4 = var2.readByte();
            if (var4 != 1) {
               throw new DecoderException("unsupported subnegotiation version: " + var4 + " (expected: 1)");
            }

            var3.add(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.valueOf(var2.readByte())));
            this.checkpoint(Socks5PasswordAuthResponseDecoder.State.SUCCESS);
         case SUCCESS:
            int var6 = this.actualReadableBytes();
            if (var6 > 0) {
               var3.add(var2.readRetainedSlice(var6));
            }
            break;
         case FAILURE:
            var2.skipBytes(this.actualReadableBytes());
         }
      } catch (Exception var5) {
         this.fail(var3, var5);
      }

   }

   private void fail(List<Object> var1, Exception var2) {
      if (!(var2 instanceof DecoderException)) {
         var2 = new DecoderException((Throwable)var2);
      }

      this.checkpoint(Socks5PasswordAuthResponseDecoder.State.FAILURE);
      DefaultSocks5PasswordAuthResponse var3 = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE);
      var3.setDecoderResult(DecoderResult.failure((Throwable)var2));
      var1.add(var3);
   }

   static enum State {
      INIT,
      SUCCESS,
      FAILURE;

      private State() {
      }
   }
}
