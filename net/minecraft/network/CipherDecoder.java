package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class CipherDecoder extends MessageToMessageDecoder {
   private final CipherBase cipher;

   public CipherDecoder(Cipher var1) {
      this.cipher = new CipherBase(var1);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List var3) throws Exception {
      var3.add(this.cipher.decipher(var1, var2));
   }

   // $FF: synthetic method
   protected void decode(ChannelHandlerContext var1, Object var2, List var3) throws Exception {
      this.decode(var1, (ByteBuf)var2, var3);
   }
}
