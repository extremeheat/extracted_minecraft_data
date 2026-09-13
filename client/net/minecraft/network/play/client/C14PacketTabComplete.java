package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import org.apache.commons.lang3.StringUtils;

public class C14PacketTabComplete extends Packet {
   private String field_149420_a;

   public C14PacketTabComplete() {
      super();
   }

   public C14PacketTabComplete(String var1) {
      super();
      this.field_149420_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149420_a = var1.func_150789_c(32767);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(StringUtils.substring(this.field_149420_a, 0, 32767));
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147341_a(this);
   }

   public String func_149419_c() {
      return this.field_149420_a;
   }

   @Override
   public String func_148835_b() {
      return String.format("message='%s'", this.field_149420_a);
   }
}
