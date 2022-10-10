package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSpawnGlobalEntity implements Packet<INetHandlerPlayClient> {
   private int field_149059_a;
   private double field_149057_b;
   private double field_149058_c;
   private double field_149055_d;
   private int field_149056_e;

   public SPacketSpawnGlobalEntity() {
      super();
   }

   public SPacketSpawnGlobalEntity(Entity var1) {
      super();
      this.field_149059_a = var1.func_145782_y();
      this.field_149057_b = var1.field_70165_t;
      this.field_149058_c = var1.field_70163_u;
      this.field_149055_d = var1.field_70161_v;
      if (var1 instanceof EntityLightningBolt) {
         this.field_149056_e = 1;
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149059_a = var1.func_150792_a();
      this.field_149056_e = var1.readByte();
      this.field_149057_b = var1.readDouble();
      this.field_149058_c = var1.readDouble();
      this.field_149055_d = var1.readDouble();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149059_a);
      var1.writeByte(this.field_149056_e);
      var1.writeDouble(this.field_149057_b);
      var1.writeDouble(this.field_149058_c);
      var1.writeDouble(this.field_149055_d);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147292_a(this);
   }

   public int func_149052_c() {
      return this.field_149059_a;
   }

   public double func_186888_b() {
      return this.field_149057_b;
   }

   public double func_186889_c() {
      return this.field_149058_c;
   }

   public double func_186887_d() {
      return this.field_149055_d;
   }

   public int func_149053_g() {
      return this.field_149056_e;
   }
}
