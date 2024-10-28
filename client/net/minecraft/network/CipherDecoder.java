package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class CipherDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final CipherBase cipher;

   public CipherDecoder(Cipher var1) {
      super();
      this.cipher = new CipherBase(var1);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var3.add(this.cipher.decipher(var1, var2));
   }

   // $FF: synthetic method
   protected void decode(final ChannelHandlerContext var1, final Object var2, final List var3) throws Exception {
      this.decode(var1, (ByteBuf)var2, var3);
   }
}
