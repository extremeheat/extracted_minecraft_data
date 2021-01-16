package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;

public class Socks5PasswordAuthRequestDecoder extends ReplayingDecoder<Socks5PasswordAuthRequestDecoder.State> {
   public Socks5PasswordAuthRequestDecoder() {
      super(Socks5PasswordAuthRequestDecoder.State.INIT);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         int var4;
         switch((Socks5PasswordAuthRequestDecoder.State)this.state()) {
         case INIT:
            var4 = var2.readerIndex();
            byte var5 = var2.getByte(var4);
            if (var5 != 1) {
               throw new DecoderException("unsupported subnegotiation version: " + var5 + " (expected: 1)");
            }

            short var6 = var2.getUnsignedByte(var4 + 1);
            short var7 = var2.getUnsignedByte(var4 + 2 + var6);
            int var8 = var6 + var7 + 3;
            var2.skipBytes(var8);
            var3.add(new DefaultSocks5PasswordAuthRequest(var2.toString(var4 + 2, var6, CharsetUtil.US_ASCII), var2.toString(var4 + 3 + var6, var7, CharsetUtil.US_ASCII)));
            this.checkpoint(Socks5PasswordAuthRequestDecoder.State.SUCCESS);
         case SUCCESS:
            var4 = this.actualReadableBytes();
            if (var4 > 0) {
               var3.add(var2.readRetainedSlice(var4));
            }
            break;
         case FAILURE:
            var2.skipBytes(this.actualReadableBytes());
         }
      } catch (Exception var9) {
         this.fail(var3, var9);
      }

   }

   private void fail(List<Object> var1, Exception var2) {
      if (!(var2 instanceof DecoderException)) {
         var2 = new DecoderException((Throwable)var2);
      }

      this.checkpoint(Socks5PasswordAuthRequestDecoder.State.FAILURE);
      DefaultSocks5PasswordAuthRequest var3 = new DefaultSocks5PasswordAuthRequest("", "");
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
