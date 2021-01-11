package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase.NBTPrimitive {
   private byte field_74756_a;

   NBTTagByte() {
      super();
   }

   public NBTTagByte(byte var1) {
      super();
      this.field_74756_a = var1;
   }

   void func_74734_a(DataOutput var1) throws IOException {
      var1.writeByte(this.field_74756_a);
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(72L);
      this.field_74756_a = var1.readByte();
   }

   public byte func_74732_a() {
      return 1;
   }

   public String toString() {
      return "" + this.field_74756_a + "b";
   }

   public NBTBase func_74737_b() {
      return new NBTTagByte(this.field_74756_a);
   }

   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagByte var2 = (NBTTagByte)var1;
         return this.field_74756_a == var2.field_74756_a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return super.hashCode() ^ this.field_74756_a;
   }

   public long func_150291_c() {
      return (long)this.field_74756_a;
   }

   public int func_150287_d() {
      return this.field_74756_a;
   }

   public short func_150289_e() {
      return (short)this.field_74756_a;
   }

   public byte func_150290_f() {
      return this.field_74756_a;
   }

   public double func_150286_g() {
      return (double)this.field_74756_a;
   }

   public float func_150288_h() {
      return (float)this.field_74756_a;
   }
}
