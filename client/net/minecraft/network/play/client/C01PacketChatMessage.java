package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C01PacketChatMessage extends Packet {
   private String field_149440_a;

   public C01PacketChatMessage() {
      super();
   }

   public C01PacketChatMessage(String var1) {
      super();
      if (var1.length() > 100) {
         var1 = var1.substring(0, 100);
      }

      this.field_149440_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149440_a = var1.func_150789_c(100);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149440_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147354_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format("message='%s'", this.field_149440_a);
   }

   public String func_149439_c() {
      return this.field_149440_a;
   }
}
