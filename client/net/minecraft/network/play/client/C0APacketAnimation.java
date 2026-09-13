package net.minecraft.network.play.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0APacketAnimation extends Packet {
   private int field_149424_a;
   private int field_149423_b;

   public C0APacketAnimation() {
      super();
   }

   public C0APacketAnimation(Entity var1, int var2) {
      super();
      this.field_149424_a = var1.func_145782_y();
      this.field_149423_b = var2;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149424_a = var1.readInt();
      this.field_149423_b = var1.readByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149424_a);
      var1.writeByte(this.field_149423_b);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147350_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format("id=%d, type=%d", this.field_149424_a, this.field_149423_b);
   }

   public int func_149421_d() {
      return this.field_149423_b;
   }
}
