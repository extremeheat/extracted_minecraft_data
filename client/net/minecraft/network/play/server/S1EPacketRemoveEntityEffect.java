package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1EPacketRemoveEntityEffect extends Packet {
   private int field_149079_a;
   private int field_149078_b;

   public S1EPacketRemoveEntityEffect() {
      super();
   }

   public S1EPacketRemoveEntityEffect(int var1, PotionEffect var2) {
      super();
      this.field_149079_a = var1;
      this.field_149078_b = var2.func_76456_a();
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149079_a = var1.readInt();
      this.field_149078_b = var1.readUnsignedByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149079_a);
      var1.writeByte(this.field_149078_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147262_a(this);
   }

   public int func_149076_c() {
      return this.field_149079_a;
   }

   public int func_149075_d() {
      return this.field_149078_b;
   }
}
