package net.minecraft.network.play.server;

import net.minecraft.network.PacketBuffer;

public class S14PacketEntity$S17PacketEntityLookMove extends S14PacketEntity {
   public S14PacketEntity$S17PacketEntityLookMove() {
      super();
      this.field_149069_g = true;
   }

   public S14PacketEntity$S17PacketEntityLookMove(int var1, byte var2, byte var3, byte var4, byte var5, byte var6) {
      super(var1);
      this.field_149072_b = var2;
      this.field_149073_c = var3;
      this.field_149070_d = var4;
      this.field_149071_e = var5;
      this.field_149068_f = var6;
      this.field_149069_g = true;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      super.func_148837_a(var1);
      this.field_149072_b = var1.readByte();
      this.field_149073_c = var1.readByte();
      this.field_149070_d = var1.readByte();
      this.field_149071_e = var1.readByte();
      this.field_149068_f = var1.readByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      super.func_148840_b(var1);
      var1.writeByte(this.field_149072_b);
      var1.writeByte(this.field_149073_c);
      var1.writeByte(this.field_149070_d);
      var1.writeByte(this.field_149071_e);
      var1.writeByte(this.field_149068_f);
   }

   @Override
   public String func_148835_b() {
      return super.func_148835_b()
         + String.format(
            ", xa=%d, ya=%d, za=%d, yRot=%d, xRot=%d", this.field_149072_b, this.field_149073_c, this.field_149070_d, this.field_149071_e, this.field_149068_f
         );
   }
}
