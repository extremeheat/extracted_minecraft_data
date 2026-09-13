package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S19PacketEntityHeadLook extends Packet {
   private int field_149384_a;
   private byte field_149383_b;

   public S19PacketEntityHeadLook() {
      super();
   }

   public S19PacketEntityHeadLook(Entity var1, byte var2) {
      super();
      this.field_149384_a = var1.func_145782_y();
      this.field_149383_b = var2;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149384_a = var1.readInt();
      this.field_149383_b = var1.readByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149384_a);
      var1.writeByte(this.field_149383_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147267_a(this);
   }

   @Override
   public String func_148835_b() {
      return String.format("id=%d, rot=%d", this.field_149384_a, this.field_149383_b);
   }

   public Entity func_149381_a(World var1) {
      return var1.func_73045_a(this.field_149384_a);
   }

   public byte func_149380_c() {
      return this.field_149383_b;
   }
}
