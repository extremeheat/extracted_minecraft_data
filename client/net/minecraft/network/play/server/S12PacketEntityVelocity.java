package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S12PacketEntityVelocity implements Packet<INetHandlerPlayClient> {
   private int field_149417_a;
   private int field_149415_b;
   private int field_149416_c;
   private int field_149414_d;

   public S12PacketEntityVelocity() {
      super();
   }

   public S12PacketEntityVelocity(Entity var1) {
      this(var1.func_145782_y(), var1.field_70159_w, var1.field_70181_x, var1.field_70179_y);
   }

   public S12PacketEntityVelocity(int var1, double var2, double var4, double var6) {
      super();
      this.field_149417_a = var1;
      double var8 = 3.9D;
      if (var2 < -var8) {
         var2 = -var8;
      }

      if (var4 < -var8) {
         var4 = -var8;
      }

      if (var6 < -var8) {
         var6 = -var8;
      }

      if (var2 > var8) {
         var2 = var8;
      }

      if (var4 > var8) {
         var4 = var8;
      }

      if (var6 > var8) {
         var6 = var8;
      }

      this.field_149415_b = (int)(var2 * 8000.0D);
      this.field_149416_c = (int)(var4 * 8000.0D);
      this.field_149414_d = (int)(var6 * 8000.0D);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149417_a = var1.func_150792_a();
      this.field_149415_b = var1.readShort();
      this.field_149416_c = var1.readShort();
      this.field_149414_d = var1.readShort();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149417_a);
      var1.writeShort(this.field_149415_b);
      var1.writeShort(this.field_149416_c);
      var1.writeShort(this.field_149414_d);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147244_a(this);
   }

   public int func_149412_c() {
      return this.field_149417_a;
   }

   public int func_149411_d() {
      return this.field_149415_b;
   }

   public int func_149410_e() {
      return this.field_149416_c;
   }

   public int func_149409_f() {
      return this.field_149414_d;
   }
}
