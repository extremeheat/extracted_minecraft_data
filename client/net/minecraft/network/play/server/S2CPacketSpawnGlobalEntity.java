package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S2CPacketSpawnGlobalEntity extends Packet {
   private int field_149059_a;
   private int field_149057_b;
   private int field_149058_c;
   private int field_149055_d;
   private int field_149056_e;

   public S2CPacketSpawnGlobalEntity() {
      super();
   }

   public S2CPacketSpawnGlobalEntity(Entity var1) {
      super();
      this.field_149059_a = var1.func_145782_y();
      this.field_149057_b = MathHelper.func_76128_c(var1.field_70165_t * 32.0);
      this.field_149058_c = MathHelper.func_76128_c(var1.field_70163_u * 32.0);
      this.field_149055_d = MathHelper.func_76128_c(var1.field_70161_v * 32.0);
      if (var1 instanceof EntityLightningBolt) {
         this.field_149056_e = 1;
      }
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149059_a = var1.func_150792_a();
      this.field_149056_e = var1.readByte();
      this.field_149057_b = var1.readInt();
      this.field_149058_c = var1.readInt();
      this.field_149055_d = var1.readInt();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150787_b(this.field_149059_a);
      var1.writeByte(this.field_149056_e);
      var1.writeInt(this.field_149057_b);
      var1.writeInt(this.field_149058_c);
      var1.writeInt(this.field_149055_d);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147292_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format(
         "id=%d, type=%d, x=%.2f, y=%.2f, z=%.2f",
         this.field_149059_a,
         this.field_149056_e,
         (float)this.field_149057_b / 32.0F,
         (float)this.field_149058_c / 32.0F,
         (float)this.field_149055_d / 32.0F
      );
   }

   public int func_149052_c() {
      return this.field_149059_a;
   }

   public int func_149051_d() {
      return this.field_149057_b;
   }

   public int func_149050_e() {
      return this.field_149058_c;
   }

   public int func_149049_f() {
      return this.field_149055_d;
   }

   public int func_149053_g() {
      return this.field_149056_e;
   }
}
