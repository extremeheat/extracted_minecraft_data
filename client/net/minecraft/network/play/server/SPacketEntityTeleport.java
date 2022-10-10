package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketEntityTeleport implements Packet<INetHandlerPlayClient> {
   private int field_149458_a;
   private double field_149456_b;
   private double field_149457_c;
   private double field_149454_d;
   private byte field_149455_e;
   private byte field_149453_f;
   private boolean field_179698_g;

   public SPacketEntityTeleport() {
      super();
   }

   public SPacketEntityTeleport(Entity var1) {
      super();
      this.field_149458_a = var1.func_145782_y();
      this.field_149456_b = var1.field_70165_t;
      this.field_149457_c = var1.field_70163_u;
      this.field_149454_d = var1.field_70161_v;
      this.field_149455_e = (byte)((int)(var1.field_70177_z * 256.0F / 360.0F));
      this.field_149453_f = (byte)((int)(var1.field_70125_A * 256.0F / 360.0F));
      this.field_179698_g = var1.field_70122_E;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149458_a = var1.func_150792_a();
      this.field_149456_b = var1.readDouble();
      this.field_149457_c = var1.readDouble();
      this.field_149454_d = var1.readDouble();
      this.field_149455_e = var1.readByte();
      this.field_149453_f = var1.readByte();
      this.field_179698_g = var1.readBoolean();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149458_a);
      var1.writeDouble(this.field_149456_b);
      var1.writeDouble(this.field_149457_c);
      var1.writeDouble(this.field_149454_d);
      var1.writeByte(this.field_149455_e);
      var1.writeByte(this.field_149453_f);
      var1.writeBoolean(this.field_179698_g);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147275_a(this);
   }

   public int func_149451_c() {
      return this.field_149458_a;
   }

   public double func_186982_b() {
      return this.field_149456_b;
   }

   public double func_186983_c() {
      return this.field_149457_c;
   }

   public double func_186981_d() {
      return this.field_149454_d;
   }

   public byte func_149450_g() {
      return this.field_149455_e;
   }

   public byte func_149447_h() {
      return this.field_149453_f;
   }

   public boolean func_179697_g() {
      return this.field_179698_g;
   }
}
