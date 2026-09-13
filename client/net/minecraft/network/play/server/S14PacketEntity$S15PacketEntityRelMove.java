package net.minecraft.network.play.server;

import net.minecraft.network.PacketBuffer;

public class S14PacketEntity$S15PacketEntityRelMove extends S14PacketEntity {
   public S14PacketEntity$S15PacketEntityRelMove() {
      super();
   }

   public S14PacketEntity$S15PacketEntityRelMove(int var1, byte var2, byte var3, byte var4) {
      super(var1);
      this.field_149072_b = var2;
      this.field_149073_c = var3;
      this.field_149070_d = var4;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      super.func_148837_a(var1);
      this.field_149072_b = var1.readByte();
      this.field_149073_c = var1.readByte();
      this.field_149070_d = var1.readByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      super.func_148840_b(var1);
      var1.writeByte(this.field_149072_b);
      var1.writeByte(this.field_149073_c);
      var1.writeByte(this.field_149070_d);
   }

   @Override
   public String func_148835_b() {
      return super.func_148835_b() + String.format(", xa=%d, ya=%d, za=%d", this.field_149072_b, this.field_149073_c, this.field_149070_d);
   }
}
