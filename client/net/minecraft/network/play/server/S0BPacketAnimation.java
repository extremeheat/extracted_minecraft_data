package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S0BPacketAnimation extends Packet {
   private int field_148981_a;
   private int field_148980_b;

   public S0BPacketAnimation() {
      super();
   }

   public S0BPacketAnimation(Entity var1, int var2) {
      super();
      this.field_148981_a = var1.func_145782_y();
      this.field_148980_b = var2;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_148981_a = var1.func_150792_a();
      this.field_148980_b = var1.readUnsignedByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150787_b(this.field_148981_a);
      var1.writeByte(this.field_148980_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147279_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format("id=%d, type=%d", this.field_148981_a, this.field_148980_b);
   }

   public int func_148978_c() {
      return this.field_148981_a;
   }

   public int func_148977_d() {
      return this.field_148980_b;
   }
}
