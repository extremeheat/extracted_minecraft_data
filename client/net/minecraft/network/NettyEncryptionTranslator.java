package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
   private final Cipher field_150507_a;
   private byte[] field_150505_b = new byte[0];
   private byte[] field_150506_c = new byte[0];

   protected NettyEncryptionTranslator(Cipher var1) {
      super();
      this.field_150507_a = var1;
   }

   private byte[] func_150502_a(ByteBuf var1) {
      int var2 = var1.readableBytes();
      if (this.field_150505_b.length < var2) {
         this.field_150505_b = new byte[var2];
      }

      var1.readBytes(this.field_150505_b, 0, var2);
      return this.field_150505_b;
   }

   protected ByteBuf func_150503_a(ChannelHandlerContext var1, ByteBuf var2) throws ShortBufferException {
      int var3 = var2.readableBytes();
      byte[] var4 = this.func_150502_a(var2);
      ByteBuf var5 = var1.alloc().heapBuffer(this.field_150507_a.getOutputSize(var3));
      var5.writerIndex(this.field_150507_a.update(var4, 0, var3, var5.array(), var5.arrayOffset()));
      return var5;
   }

   protected void func_150504_a(ByteBuf var1, ByteBuf var2) throws ShortBufferException {
      int var3 = var1.readableBytes();
      byte[] var4 = this.func_150502_a(var1);
      int var5 = this.field_150507_a.getOutputSize(var3);
      if (this.field_150506_c.length < var5) {
         this.field_150506_c = new byte[var5];
      }

      var2.writeBytes(this.field_150506_c, 0, this.field_150507_a.update(var4, 0, var3, this.field_150506_c));
   }
}
