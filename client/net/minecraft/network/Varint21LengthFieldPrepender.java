package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class Varint21LengthFieldPrepender extends MessageToByteEncoder<ByteBuf> {
   private static final int MAX_BYTES = 3;

   public Varint21LengthFieldPrepender() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) {
      int var4 = var2.readableBytes();
      int var5 = FriendlyByteBuf.getVarIntSize(var4);
      if (var5 > 3) {
         throw new IllegalArgumentException("unable to fit " + var4 + " into 3");
      } else {
         FriendlyByteBuf var6 = new FriendlyByteBuf(var3);
         var6.ensureWritable(var5 + var4);
         var6.writeVarInt(var4);
         var6.writeBytes(var2, var2.readerIndex(), var4);
      }
   }
}
