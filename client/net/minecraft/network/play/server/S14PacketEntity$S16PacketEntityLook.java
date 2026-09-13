package net.minecraft.network.play.server;

import net.minecraft.network.PacketBuffer;

public class S14PacketEntity$S16PacketEntityLook extends S14PacketEntity {
   public S14PacketEntity$S16PacketEntityLook() {
      super();
      this.field_149069_g = true;
   }

   public S14PacketEntity$S16PacketEntityLook(int var1, byte var2, byte var3) {
      super(var1);
      this.field_149071_e = var2;
      this.field_149068_f = var3;
      this.field_149069_g = true;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      super.func_148837_a(var1);
      this.field_149071_e = var1.readByte();
      this.field_149068_f = var1.readByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      super.func_148840_b(var1);
      var1.writeByte(this.field_149071_e);
      var1.writeByte(this.field_149068_f);
   }

   @Override
   public String func_148835_b() {
      return super.func_148835_b() + String.format(", yRot=%d, xRot=%d", this.field_149071_e, this.field_149068_f);
   }
}
