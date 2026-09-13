package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagByte extends NBTBase$NBTPrimitive {
   private byte field_74756_a;

   NBTTagByte() {
      super();
   }

   public NBTTagByte(byte var1) {
      super();
      this.field_74756_a = var1;
   }

   @Override
   void func_74734_a(DataOutput var1) {
      var1.writeByte(this.field_74756_a);
   }

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
      var3.func_152450_a(8L);
      this.field_74756_a = var1.readByte();
   }

   @Override
   public byte func_74732_a() {
      return 1;
   }

   @Override
   public String toString() {
      return "" + this.field_74756_a + "b";
   }

   @Override
   public NBTBase func_74737_b() {
      return new NBTTagByte(this.field_74756_a);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagByte var2 = (NBTTagByte)var1;
         return this.field_74756_a == var2.field_74756_a;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.field_74756_a;
   }

   @Override
   public long func_150291_c() {
      return (long)this.field_74756_a;
   }

   @Override
   public int func_150287_d() {
      return this.field_74756_a;
   }

   @Override
   public short func_150289_e() {
      return (short)this.field_74756_a;
   }

   @Override
   public byte func_150290_f() {
      return this.field_74756_a;
   }

   @Override
   public double func_150286_g() {
      return (double)this.field_74756_a;
   }

   @Override
   public float func_150288_h() {
      return (float)this.field_74756_a;
   }
}
