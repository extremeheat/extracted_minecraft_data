package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketSetExperience implements Packet<INetHandlerPlayClient> {
   private float field_149401_a;
   private int field_149399_b;
   private int field_149400_c;

   public SPacketSetExperience() {
      super();
   }

   public SPacketSetExperience(float var1, int var2, int var3) {
      super();
      this.field_149401_a = var1;
      this.field_149399_b = var2;
      this.field_149400_c = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149401_a = var1.readFloat();
      this.field_149400_c = var1.func_150792_a();
      this.field_149399_b = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeFloat(this.field_149401_a);
      var1.func_150787_b(this.field_149400_c);
      var1.func_150787_b(this.field_149399_b);
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
