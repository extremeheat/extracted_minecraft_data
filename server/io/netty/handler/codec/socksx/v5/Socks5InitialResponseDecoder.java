package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import java.util.List;

public class Socks5InitialResponseDecoder extends ReplayingDecoder<Socks5InitialResponseDecoder.State> {
   public Socks5InitialResponseDecoder() {
      super(Socks5InitialResponseDecoder.State.INIT);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch((Socks5InitialResponseDecoder.State)this.state()) {
         case INIT:
            byte var4 = var2.readByte();
            if (var4 != SocksVersion.SOCKS5.byteValue()) {
               throw new DecoderException("unsupported version: " + var4 + " (expected: " + SocksVersion.SOCKS5.byteValue() + ')');
            }

            Socks5AuthMethod var5 = Socks5AuthMethod.valueOf(var2.readByte());
            var3.add(new DefaultSocks5InitialResponse(var5));
            this.checkpoint(Socks5InitialResponseDecoder.State.SUCCESS);
         case SUCCESS:
            int var7 = this.actualReadableBytes();
            if (var7 > 0) {
               var3.add(var2.readRetainedSlice(var7));
            }
            break;
         case FAILURE:
            var2.skipBytes(this.actualReadableBytes());
         }
      } catch (Exception var6) {
         this.fail(var3, var6);
      }

   }

   private void fail(List<Object> var1, Exception var2) {
      if (!(var2 instanceof DecoderException)) {
         var2 = new DecoderException((Throwable)var2);
      }

      this.checkpoint(Socks5InitialResponseDecoder.State.FAILURE);
      DefaultSocks5InitialResponse var3 = new DefaultSocks5InitialResponse(Socks5AuthMethod.UNACCEPTED);
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
