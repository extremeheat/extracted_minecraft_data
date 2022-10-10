package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketPlayerPosLook implements Packet<INetHandlerPlayClient> {
   private double field_148940_a;
   private double field_148938_b;
   private double field_148939_c;
   private float field_148936_d;
   private float field_148937_e;
   private Set<SPacketPlayerPosLook.EnumFlags> field_179835_f;
   private int field_186966_g;

   public SPacketPlayerPosLook() {
      super();
   }

   public SPacketPlayerPosLook(double var1, double var3, double var5, float var7, float var8, Set<SPacketPlayerPosLook.EnumFlags> var9, int var10) {
      super();
      this.field_148940_a = var1;
      this.field_148938_b = var3;
      this.field_148939_c = var5;
      this.field_148936_d = var7;
      this.field_148937_e = var8;
      this.field_179835_f = var9;
      this.field_186966_g = var10;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148940_a = var1.readDouble();
      this.field_148938_b = var1.readDouble();
      this.field_148939_c = var1.readDouble();
      this.field_148936_d = var1.readFloat();
      this.field_148937_e = var1.readFloat();
      this.field_179835_f = SPacketPlayerPosLook.EnumFlags.func_187044_a(var1.readUnsignedByte());
      this.field_186966_g = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeDouble(this.field_148940_a);
      var1.writeDouble(this.field_148938_b);
      var1.writeDouble(this.field_148939_c);
      var1.writeFloat(this.field_148936_d);
      var1.writeFloat(this.field_148937_e);
      var1.writeByte(SPacketPlayerPosLook.EnumFlags.func_187040_a(this.field_179835_f));
      var1.func_150787_b(this.field_186966_g);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_184330_a(this);
   }

   public double func_148932_c() {
      return this.field_148940_a;
   }

   public double func_148928_d() {
      return this.field_148938_b;
   }

   public double func_148933_e() {
      return this.field_148939_c;
   }

   public float func_148931_f() {
      return this.field_148936_d;
   }

   public float func_148930_g() {
      return this.field_148937_e;
   }

   public int func_186965_f() {
      return this.field_186966_g;
   }

   public Set<SPacketPlayerPosLook.EnumFlags> func_179834_f() {
      return this.field_179835_f;
   }

   public static enum EnumFlags {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      private final int field_187050_f;

      private EnumFlags(int var3) {
         this.field_187050_f = var3;
      }

      private int func_187042_a() {
         return 1 << this.field_187050_f;
      }

      private boolean func_187043_b(int var1) {
         return (var1 & this.func_187042_a()) == this.func_187042_a();
      }

      public static Set<SPacketPlayerPosLook.EnumFlags> func_187044_a(int var0) {
         EnumSet var1 = EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class);
         SPacketPlayerPosLook.EnumFlags[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            SPacketPlayerPosLook.EnumFlags var5 = var2[var4];
            if (var5.func_187043_b(var0)) {
               var1.add(var5);
            }
         }

         return var1;
      }

      public static int func_187040_a(Set<SPacketPlayerPosLook.EnumFlags> var0) {
         int var1 = 0;

         SPacketPlayerPosLook.EnumFlags var3;
         for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 |= var3.func_187042_a()) {
            var3 = (SPacketPlayerPosLook.EnumFlags)var2.next();
         }

         return var1;
      }
   }
}
