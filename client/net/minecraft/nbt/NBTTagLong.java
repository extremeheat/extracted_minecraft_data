package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase.NBTPrimitive {
   private long field_74753_a;

   NBTTagLong() {
      super();
   }

   public NBTTagLong(long var1) {
      super();
      this.field_74753_a = var1;
   }

   void func_74734_a(DataOutput var1) throws IOException {
      var1.writeLong(this.field_74753_a);
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(128L);
      this.field_74753_a = var1.readLong();
   }

   public byte func_74732_a() {
      return 4;
   }

   public String toString() {
      return "" + this.field_74753_a + "L";
   }

   public NBTBase func_74737_b() {
      return new NBTTagLong(this.field_74753_a);
   }

   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagLong var2 = (NBTTagLong)var1;
         return this.field_74753_a == var2.field_74753_a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return super.hashCode() ^ (int)(this.field_74753_a ^ this.field_74753_a >>> 32);
   }

   public long func_150291_c() {
      return this.field_74753_a;
   }

   public int func_150287_d() {
      return (int)(this.field_74753_a & -1L);
   }

   public short func_150289_e() {
      return (short)((int)(this.field_74753_a & 65535L));
   }

   public byte func_150290_f() {
      return (byte)((int)(this.field_74753_a & 255L));
   }

   public double func_150286_g() {
      return (double)this.field_74753_a;
   }

   public float func_150288_h() {
      return (float)this.field_74753_a;
   }
}
