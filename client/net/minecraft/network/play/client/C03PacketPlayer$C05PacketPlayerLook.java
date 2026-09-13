package net.minecraft.network.play.client;

import net.minecraft.network.PacketBuffer;

public class C03PacketPlayer$C05PacketPlayerLook extends C03PacketPlayer {
   public C03PacketPlayer$C05PacketPlayerLook() {
      super();
      this.field_149481_i = true;
   }

   public C03PacketPlayer$C05PacketPlayerLook(float var1, float var2, boolean var3) {
      super();
      this.field_149476_e = var1;
      this.field_149473_f = var2;
      this.field_149474_g = var3;
      this.field_149481_i = true;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149476_e = var1.readFloat();
      this.field_149473_f = var1.readFloat();
      super.func_148837_a(var1);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeFloat(this.field_149476_e);
      var1.writeFloat(this.field_149473_f);
      super.func_148840_b(var1);
   }
}
