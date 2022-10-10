package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSpawnExperienceOrb implements Packet<INetHandlerPlayClient> {
   private int field_148992_a;
   private double field_148990_b;
   private double field_148991_c;
   private double field_148988_d;
   private int field_148989_e;

   public SPacketSpawnExperienceOrb() {
      super();
   }

   public SPacketSpawnExperienceOrb(EntityXPOrb var1) {
      super();
      this.field_148992_a = var1.func_145782_y();
      this.field_148990_b = var1.field_70165_t;
      this.field_148991_c = var1.field_70163_u;
      this.field_148988_d = var1.field_70161_v;
      this.field_148989_e = var1.func_70526_d();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148992_a = var1.func_150792_a();
      this.field_148990_b = var1.readDouble();
      this.field_148991_c = var1.readDouble();
      this.field_148988_d = var1.readDouble();
      this.field_148989_e = var1.readShort();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148992_a);
      var1.writeDouble(this.field_148990_b);
      var1.writeDouble(this.field_148991_c);
      var1.writeDouble(this.field_148988_d);
      var1.writeShort(this.field_148989_e);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147286_a(this);
   }

   public int func_148985_c() {
      return this.field_148992_a;
   }

   public double func_186885_b() {
      return this.field_148990_b;
   }

   public double func_186886_c() {
      return this.field_148991_c;
   }

   public double func_186884_d() {
      return this.field_148988_d;
   }

   public int func_148986_g() {
      return this.field_148989_e;
   }
}
