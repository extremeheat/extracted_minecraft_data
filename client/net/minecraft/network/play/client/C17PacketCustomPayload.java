package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload implements Packet<INetHandlerPlayServer> {
   private String field_149562_a;
   private PacketBuffer field_149561_c;

   public C17PacketCustomPayload() {
      super();
   }

   public C17PacketCustomPayload(String var1, PacketBuffer var2) {
      super();
      this.field_149562_a = var1;
      this.field_149561_c = var2;
      if (var2.writerIndex() > 32767) {
         throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
      }
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149562_a = var1.func_150789_c(20);
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 32767) {
         this.field_149561_c = new PacketBuffer(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_149562_a);
      var1.writeBytes((ByteBuf)this.field_149561_c);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147349_a(this);
   }

   public String func_149559_c() {
      return this.field_149562_a;
   }

   public PacketBuffer func_180760_b() {
      return this.field_149561_c;
   }
}
