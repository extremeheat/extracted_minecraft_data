package io.netty.handler.codec.socksx.v4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.NetUtil;
import java.util.List;

public class Socks4ClientDecoder extends ReplayingDecoder<Socks4ClientDecoder.State> {
   public Socks4ClientDecoder() {
      super(Socks4ClientDecoder.State.START);
      this.setSingleDecode(true);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch((Socks4ClientDecoder.State)this.state()) {
         case START:
            short var4 = var2.readUnsignedByte();
            if (var4 != 0) {
               throw new DecoderException("unsupported reply version: " + var4 + " (expected: 0)");
            }

            Socks4CommandStatus var5 = Socks4CommandStatus.valueOf(var2.readByte());
            int var6 = var2.readUnsignedShort();
            String var7 = NetUtil.intToIpAddress(var2.readInt());
            var3.add(new DefaultSocks4CommandResponse(var5, var7, var6));
            this.checkpoint(Socks4ClientDecoder.State.SUCCESS);
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

      DefaultSocks4CommandResponse var3 = new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED);
      var3.setDecoderResult(DecoderResult.failure((Throwable)var2));
      var1.add(var3);
      this.checkpoint(Socks4ClientDecoder.State.FAILURE);
   }

   static enum State {
      START,
      SUCCESS,
      FAILURE;

      private State() {
      }
   }
}
