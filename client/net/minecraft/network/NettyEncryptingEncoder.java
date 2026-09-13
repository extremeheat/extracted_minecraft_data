package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class NettyEncryptingEncoder extends MessageToByteEncoder {
   private final NettyEncryptionTranslator field_150750_a;

   public NettyEncryptingEncoder(Cipher var1) {
      super();
      this.field_150750_a = new NettyEncryptionTranslator(var1);
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) {
      this.field_150750_a.func_150504_a(var2, var3);
   }
}
