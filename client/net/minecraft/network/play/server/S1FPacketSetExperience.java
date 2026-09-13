package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1FPacketSetExperience extends Packet {
   private float field_149401_a;
   private int field_149399_b;
   private int field_149400_c;

   public S1FPacketSetExperience() {
      super();
   }

   public S1FPacketSetExperience(float var1, int var2, int var3) {
      super();
      this.field_149401_a = var1;
      this.field_149399_b = var2;
      this.field_149400_c = var3;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149401_a = var1.readFloat();
      this.field_149400_c = var1.readShort();
      this.field_149399_b = var1.readShort();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeFloat(this.field_149401_a);
      var1.writeShort(this.field_149400_c);
      var1.writeShort(this.field_149399_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147295_a(this);
   }

   public float func_149397_c() {
      return this.field_149401_a;
   }

   public int func_149396_d() {
      return this.field_149399_b;
   }

   public int func_149395_e() {
      return this.field_149400_c;
   }
}
