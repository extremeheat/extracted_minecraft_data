package net.minecraft.pathfinding;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;

public class PathPoint {
   public final int field_75839_a;
   public final int field_75837_b;
   public final int field_75838_c;
   private final int field_75840_j;
   public int field_75835_d = -1;
   public float field_75836_e;
   public float field_75833_f;
   public float field_75834_g;
   public PathPoint field_75841_h;
   public boolean field_75842_i;
   public float field_186284_j;
   public float field_186285_k;
   public float field_186286_l;
   public PathNodeType field_186287_m;

   public PathPoint(int var1, int var2, int var3) {
      super();
      this.field_186287_m = PathNodeType.BLOCKED;
      this.field_75839_a = var1;
      this.field_75837_b = var2;
      this.field_75838_c = var3;
      this.field_75840_j = func_75830_a(var1, var2, var3);
   }

   public PathPoint func_186283_a(int var1, int var2, int var3) {
      PathPoint var4 = new PathPoint(var1, var2, var3);
      var4.field_75835_d = this.field_75835_d;
      var4.field_75836_e = this.field_75836_e;
      var4.field_75833_f = this.field_75833_f;
      var4.field_75834_g = this.field_75834_g;
      var4.field_75841_h = this.field_75841_h;
      var4.field_75842_i = this.field_75842_i;
      var4.field_186284_j = this.field_186284_j;
      var4.field_186285_k = this.field_186285_k;
      var4.field_186286_l = this.field_186286_l;
      var4.field_186287_m = this.field_186287_m;
      return var4;
   }

   public static int func_75830_a(int var0, int var1, int var2) {
      return var1 & 255 | (var0 & 32767) << 8 | (var2 & 32767) << 24 | (var0 < 0 ? -2147483648 : 0) | (var2 < 0 ? '\u8000' : 0);
   }

   public float func_75829_a(PathPoint var1) {
      float var2 = (float)(var1.field_75839_a - this.field_75839_a);
      float var3 = (float)(var1.field_75837_b - this.field_75837_b);
      float var4 = (float)(var1.field_75838_c - this.field_75838_c);
      return MathHelper.func_76129_c(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float func_75832_b(PathPoint var1) {
      float var2 = (float)(var1.field_75839_a - this.field_75839_a);
      float var3 = (float)(var1.field_75837_b - this.field_75837_b);
      float var4 = (float)(var1.field_75838_c - this.field_75838_c);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public float func_186281_c(PathPoint var1) {
      float var2 = (float)Math.abs(var1.field_75839_a - this.field_75839_a);
      float var3 = (float)Math.abs(var1.field_75837_b - this.field_75837_b);
      float var4 = (float)Math.abs(var1.field_75838_c - this.field_75838_c);
      return var2 + var3 + var4;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof PathPoint)) {
         return false;
      } else {
         PathPoint var2 = (PathPoint)var1;
         return this.field_75840_j == var2.field_75840_j && this.field_75839_a == var2.field_75839_a && this.field_75837_b == var2.field_75837_b && this.field_75838_c == var2.field_75838_c;
      }
   }

   public int hashCode() {
      return this.field_75840_j;
   }

   public boolean func_75831_a() {
      return this.field_75835_d >= 0;
   }

   public String toString() {
      return this.field_75839_a + ", " + this.field_75837_b + ", " + this.field_75838_c;
   }

   public static PathPoint func_186282_b(PacketBuffer var0) {
      PathPoint var1 = new PathPoint(var0.readInt(), var0.readInt(), var0.readInt());
      var1.field_186284_j = var0.readFloat();
      var1.field_186285_k = var0.readFloat();
      var1.field_186286_l = var0.readFloat();
      var1.field_75842_i = var0.readBoolean();
      var1.field_186287_m = PathNodeType.values()[var0.readInt()];
      var1.field_75834_g = var0.readFloat();
      return var1;
   }
}
