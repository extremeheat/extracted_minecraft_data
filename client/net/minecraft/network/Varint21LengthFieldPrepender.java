package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class Varint21LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {
   public static final int MAX_VARINT21_BYTES = 3;

   public Varint21LengthFieldPrepender() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) {
      int var4 = var2.readableBytes();
      int var5 = VarInt.getByteSize(var4);
      if (var5 > 3) {
         throw new EncoderException("Packet too large: size " + var4 + " is over 8");
      } else {
         var3.ensureWritable(var5 + var4);
         VarInt.write(var3, var4);
         var3.writeBytes(var2, var2.readerIndex(), var4);
      }
   }

   // $FF: synthetic method
   protected void encode(final ChannelHandlerContext var1, final Object var2, final ByteBuf var3) throws Exception {
      this.encode(var1, (ByteBuf)var2, var3);
   }
}
