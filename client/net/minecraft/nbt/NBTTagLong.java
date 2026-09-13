package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagLong extends NBTBase$NBTPrimitive {
   private long field_74753_a;

   NBTTagLong() {
      super();
   }

   public NBTTagLong(long var1) {
      super();
      this.field_74753_a = var1;
   }

   @Override
   void func_74734_a(DataOutput var1) {
      var1.writeLong(this.field_74753_a);
   }

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
      var3.func_152450_a(64L);
      this.field_74753_a = var1.readLong();
   }

   @Override
   public byte func_74732_a() {
      return 4;
   }

   @Override
   public String toString() {
      return "" + this.field_74753_a + "L";
   }

   @Override
   public NBTBase func_74737_b() {
      return new NBTTagLong(this.field_74753_a);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagLong var2 = (NBTTagLong)var1;
         return this.field_74753_a == var2.field_74753_a;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ (int)(this.field_74753_a ^ this.field_74753_a >>> 32);
   }

   @Override
   public long func_150291_c() {
      return this.field_74753_a;
   }

   @Override
   public int func_150287_d() {
      return (int)(this.field_74753_a & -1L);
   }

   @Override
   public short func_150289_e() {
      return (short)((int)(this.field_74753_a & 65535L));
   }

   @Override
   public byte func_150290_f() {
      return (byte)((int)(this.field_74753_a & 255L));
   }

   @Override
   public double func_150286_g() {
      return (double)this.field_74753_a;
   }

   @Override
   public float func_150288_h() {
      return (float)this.field_74753_a;
   }
}
