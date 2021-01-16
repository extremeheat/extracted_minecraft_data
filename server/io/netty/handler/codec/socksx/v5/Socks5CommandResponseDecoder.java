package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import java.util.List;

public class Socks5CommandResponseDecoder extends ReplayingDecoder<Socks5CommandResponseDecoder.State> {
   private final Socks5AddressDecoder addressDecoder;

   public Socks5CommandResponseDecoder() {
      this(Socks5AddressDecoder.DEFAULT);
   }

   public Socks5CommandResponseDecoder(Socks5AddressDecoder var1) {
      super(Socks5CommandResponseDecoder.State.INIT);
      if (var1 == null) {
         throw new NullPointerException("addressDecoder");
      } else {
         this.addressDecoder = var1;
      }
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch((Socks5CommandResponseDecoder.State)this.state()) {
         case INIT:
            byte var4 = var2.readByte();
            if (var4 != SocksVersion.SOCKS5.byteValue()) {
               throw new DecoderException("unsupported version: " + var4 + " (expected: " + SocksVersion.SOCKS5.byteValue() + ')');
            }

            Socks5CommandStatus var5 = Socks5CommandStatus.valueOf(var2.readByte());
            var2.skipBytes(1);
            Socks5AddressType var6 = Socks5AddressType.valueOf(var2.readByte());
            String var7 = this.addressDecoder.decodeAddress(var6, var2);
            int var8 = var2.readUnsignedShort();
            var3.add(new DefaultSocks5CommandResponse(var5, var6, var7, var8));
            this.checkpoint(Socks5CommandResponseDecoder.State.SUCCESS);
         case SUCCESS:
            int var10 = this.actualReadableBytes();
            if (var10 > 0) {
               var3.add(var2.readRetainedSlice(var10));
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

      this.checkpoint(Socks5CommandResponseDecoder.State.FAILURE);
      DefaultSocks5CommandResponse var3 = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4, (String)null, 0);
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
