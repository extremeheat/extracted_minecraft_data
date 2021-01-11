package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S06PacketUpdateHealth implements Packet<INetHandlerPlayClient> {
   private float field_149336_a;
   private int field_149334_b;
   private float field_149335_c;

   public S06PacketUpdateHealth() {
      super();
   }

   public S06PacketUpdateHealth(float var1, int var2, float var3) {
      super();
      this.field_149336_a = var1;
      this.field_149334_b = var2;
      this.field_149335_c = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149336_a = var1.readFloat();
      this.field_149334_b = var1.func_150792_a();
      this.field_149335_c = var1.readFloat();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeFloat(this.field_149336_a);
      var1.func_150787_b(this.field_149334_b);
      var1.writeFloat(this.field_149335_c);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147249_a(this);
   }

   public float func_149332_c() {
      return this.field_149336_a;
   }

   public int func_149330_d() {
      return this.field_149334_b;
   }

   public float func_149331_e() {
      return this.field_149335_c;
   }
}
