package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagInt extends NBTBase$NBTPrimitive {
   private int field_74748_a;

   NBTTagInt() {
      super();
   }

   public NBTTagInt(int var1) {
      super();
      this.field_74748_a = var1;
   }

   @Override
   void func_74734_a(DataOutput var1) {
      var1.writeInt(this.field_74748_a);
   }

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
      var3.func_152450_a(32L);
      this.field_74748_a = var1.readInt();
   }

   @Override
   public byte func_74732_a() {
      return 3;
   }

   @Override
   public String toString() {
      return "" + this.field_74748_a;
   }

   @Override
   public NBTBase func_74737_b() {
      return new NBTTagInt(this.field_74748_a);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagInt var2 = (NBTTagInt)var1;
         return this.field_74748_a == var2.field_74748_a;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.field_74748_a;
   }

   @Override
   public long func_150291_c() {
      return (long)this.field_74748_a;
   }

   @Override
   public int func_150287_d() {
      return this.field_74748_a;
   }

   @Override
   public short func_150289_e() {
      return (short)(this.field_74748_a & 65535);
   }

   @Override
   public byte func_150290_f() {
      return (byte)(this.field_74748_a & 0xFF);
   }

   @Override
   public double func_150286_g() {
      return (double)this.field_74748_a;
   }

   @Override
   public float func_150288_h() {
      return (float)this.field_74748_a;
   }
}
