package net.minecraft.network.play.server;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload implements Packet<INetHandlerPlayClient> {
   private String field_149172_a;
   private PacketBuffer field_149171_b;

   public S3FPacketCustomPayload() {
      super();
   }

   public S3FPacketCustomPayload(String var1, PacketBuffer var2) {
      super();
      this.field_149172_a = var1;
      this.field_149171_b = var2;
      if (var2.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149172_a = var1.func_150789_c(20);
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         this.field_149171_b = new PacketBuffer(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_149172_a);
      var1.writeBytes((ByteBuf)this.field_149171_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147240_a(this);
   }

   public String func_149169_c() {
      return this.field_149172_a;
   }

   public PacketBuffer func_180735_b() {
      return this.field_149171_b;
   }
}
