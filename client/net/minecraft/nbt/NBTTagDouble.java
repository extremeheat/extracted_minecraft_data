package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTBase.NBTPrimitive {
   private double field_74755_a;

   NBTTagDouble() {
      super();
   }

   public NBTTagDouble(double var1) {
      super();
      this.field_74755_a = var1;
   }

   void func_74734_a(DataOutput var1) throws IOException {
      var1.writeDouble(this.field_74755_a);
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(128L);
      this.field_74755_a = var1.readDouble();
   }

   public byte func_74732_a() {
      return 6;
   }

   public String toString() {
      return "" + this.field_74755_a + "d";
   }

   public NBTBase func_74737_b() {
      return new NBTTagDouble(this.field_74755_a);
   }

   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagDouble var2 = (NBTTagDouble)var1;
         return this.field_74755_a == var2.field_74755_a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.field_74755_a);
      return super.hashCode() ^ (int)(var1 ^ var1 >>> 32);
   }

   public long func_150291_c() {
      return (long)Math.floor(this.field_74755_a);
   }

   public int func_150287_d() {
      return MathHelper.func_76128_c(this.field_74755_a);
   }

   public short func_150289_e() {
      return (short)(MathHelper.func_76128_c(this.field_74755_a) & '\uffff');
   }

   public byte func_150290_f() {
      return (byte)(MathHelper.func_76128_c(this.field_74755_a) & 255);
   }

   public double func_150286_g() {
      return this.field_74755_a;
   }

   public float func_150288_h() {
      return (float)this.field_74755_a;
   }
}
