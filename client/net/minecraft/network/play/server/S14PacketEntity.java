package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S14PacketEntity extends Packet {
   protected int field_149074_a;
   protected byte field_149072_b;
   protected byte field_149073_c;
   protected byte field_149070_d;
   protected byte field_149071_e;
   protected byte field_149068_f;
   protected boolean field_149069_g;

   public S14PacketEntity() {
      super();
   }

   public S14PacketEntity(int var1) {
      super();
      this.field_149074_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149074_a = var1.readInt();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149074_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147259_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format("id=%d", this.field_149074_a);
   }

   @Override
   public String toString() {
      return "Entity_" + super.toString();
   }

   public Entity func_149065_a(World var1) {
      return var1.func_73045_a(this.field_149074_a);
   }

   public byte func_149062_c() {
      return this.field_149072_b;
   }

   public byte func_149061_d() {
      return this.field_149073_c;
   }

   public byte func_149064_e() {
      return this.field_149070_d;
   }

   public byte func_149066_f() {
      return this.field_149071_e;
   }

   public byte func_149063_g() {
      return this.field_149068_f;
   }

   public boolean func_149060_h() {
      return this.field_149069_g;
   }
}
