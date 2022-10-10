package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketMoveVehicle implements Packet<INetHandlerPlayClient> {
   private double field_186960_a;
   private double field_186961_b;
   private double field_186962_c;
   private float field_186963_d;
   private float field_186964_e;

   public SPacketMoveVehicle() {
      super();
   }

   public SPacketMoveVehicle(Entity var1) {
      super();
      this.field_186960_a = var1.field_70165_t;
      this.field_186961_b = var1.field_70163_u;
      this.field_186962_c = var1.field_70161_v;
      this.field_186963_d = var1.field_70177_z;
      this.field_186964_e = var1.field_70125_A;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_186960_a = var1.readDouble();
      this.field_186961_b = var1.readDouble();
      this.field_186962_c = var1.readDouble();
      this.field_186963_d = var1.readFloat();
      this.field_186964_e = var1.readFloat();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeDouble(this.field_186960_a);
      var1.writeDouble(this.field_186961_b);
      var1.writeDouble(this.field_186962_c);
      var1.writeFloat(this.field_186963_d);
      var1.writeFloat(this.field_186964_e);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_184323_a(this);
   }

   public double func_186957_a() {
      return this.field_186960_a;
   }

   public double func_186955_b() {
      return this.field_186961_b;
   }

   public double func_186956_c() {
      return this.field_186962_c;
   }

   public float func_186959_d() {
      return this.field_186963_d;
   }

   public float func_186958_e() {
      return this.field_186964_e;
   }
}
