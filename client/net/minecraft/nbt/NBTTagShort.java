package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase.NBTPrimitive {
   private short field_74752_a;

   public NBTTagShort() {
      super();
   }

   public NBTTagShort(short var1) {
      super();
      this.field_74752_a = var1;
   }

   void func_74734_a(DataOutput var1) throws IOException {
      var1.writeShort(this.field_74752_a);
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(80L);
      this.field_74752_a = var1.readShort();
   }

   public byte func_74732_a() {
      return 2;
   }

   public String toString() {
      return "" + this.field_74752_a + "s";
   }

   public NBTBase func_74737_b() {
      return new NBTTagShort(this.field_74752_a);
   }

   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagShort var2 = (NBTTagShort)var1;
         return this.field_74752_a == var2.field_74752_a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return super.hashCode() ^ this.field_74752_a;
   }

   public long func_150291_c() {
      return (long)this.field_74752_a;
   }

   public int func_150287_d() {
      return this.field_74752_a;
   }

   public short func_150289_e() {
      return this.field_74752_a;
   }

   public byte func_150290_f() {
      return (byte)(this.field_74752_a & 255);
   }

   public double func_150286_g() {
      return (double)this.field_74752_a;
   }

   public float func_150288_h() {
      return (float)this.field_74752_a;
   }
}
