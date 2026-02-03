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

   protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) {
      int bodyLength = msg.readableBytes();
      int headerLength = VarInt.getByteSize(bodyLength);
      if (headerLength > 3) {
         throw new EncoderException("Packet too large: size " + bodyLength + " is over 8");
      } else {
         out.ensureWritable(headerLength + bodyLength);
         VarInt.write(out, bodyLength);
         out.writeBytes(msg, msg.readerIndex(), bodyLength);
      }
   }
}
