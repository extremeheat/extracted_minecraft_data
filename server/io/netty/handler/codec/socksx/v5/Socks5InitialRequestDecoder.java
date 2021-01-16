package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import java.util.List;

public class Socks5InitialRequestDecoder extends ReplayingDecoder<Socks5InitialRequestDecoder.State> {
   public Socks5InitialRequestDecoder() {
      super(Socks5InitialRequestDecoder.State.INIT);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch((Socks5InitialRequestDecoder.State)this.state()) {
         case INIT:
            byte var4 = var2.readByte();
            if (var4 != SocksVersion.SOCKS5.byteValue()) {
               throw new DecoderException("unsupported version: " + var4 + " (expected: " + SocksVersion.SOCKS5.byteValue() + ')');
            }

            short var5 = var2.readUnsignedByte();
            if (this.actualReadableBytes() < var5) {
               break;
            }

            Socks5AuthMethod[] var6 = new Socks5AuthMethod[var5];

            for(int var7 = 0; var7 < var5; ++var7) {
               var6[var7] = Socks5AuthMethod.valueOf(var2.readByte());
            }

            var3.add(new DefaultSocks5InitialRequest(var6));
            this.checkpoint(Socks5InitialRequestDecoder.State.SUCCESS);
         case SUCCESS:
            int var9 = this.actualReadableBytes();
            if (var9 > 0) {
               var3.add(var2.readRetainedSlice(var9));
            }
            break;
         case FAILURE:
            var2.skipBytes(this.actualReadableBytes());
         }
      } catch (Exception var8) {
         this.fail(var3, var8);
      }

   }

   private void fail(List<Object> var1, Exception var2) {
      if (!(var2 instanceof DecoderException)) {
         var2 = new DecoderException((Throwable)var2);
      }

      this.checkpoint(Socks5InitialRequestDecoder.State.FAILURE);
      DefaultSocks5InitialRequest var3 = new DefaultSocks5InitialRequest(new Socks5AuthMethod[]{Socks5AuthMethod.NO_AUTH});
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
