package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase.NBTPrimitive {
   private int field_74748_a;

   NBTTagInt() {
      super();
   }

   public NBTTagInt(int var1) {
      super();
      this.field_74748_a = var1;
   }

   void func_74734_a(DataOutput var1) throws IOException {
      var1.writeInt(this.field_74748_a);
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(96L);
      this.field_74748_a = var1.readInt();
   }

   public byte func_74732_a() {
      return 3;
   }

   public String toString() {
      return "" + this.field_74748_a;
   }

   public NBTBase func_74737_b() {
      return new NBTTagInt(this.field_74748_a);
   }

   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagInt var2 = (NBTTagInt)var1;
         return this.field_74748_a == var2.field_74748_a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return super.hashCode() ^ this.field_74748_a;
   }

   public long func_150291_c() {
      return (long)this.field_74748_a;
   }

   public int func_150287_d() {
      return this.field_74748_a;
   }

   public short func_150289_e() {
      return (short)(this.field_74748_a & '\uffff');
   }

   public byte func_150290_f() {
      return (byte)(this.field_74748_a & 255);
   }

   public double func_150286_g() {
      return (double)this.field_74748_a;
   }

   public float func_150288_h() {
      return (float)this.field_74748_a;
   }
}
