package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTBase.NBTPrimitive {
   private float field_74750_a;

   NBTTagFloat() {
      super();
   }

   public NBTTagFloat(float var1) {
      super();
      this.field_74750_a = var1;
   }

   void func_74734_a(DataOutput var1) throws IOException {
      var1.writeFloat(this.field_74750_a);
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(96L);
      this.field_74750_a = var1.readFloat();
   }

   public byte func_74732_a() {
      return 5;
   }

   public String toString() {
      return "" + this.field_74750_a + "f";
   }

   public NBTBase func_74737_b() {
      return new NBTTagFloat(this.field_74750_a);
   }

   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagFloat var2 = (NBTTagFloat)var1;
         return this.field_74750_a == var2.field_74750_a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return super.hashCode() ^ Float.floatToIntBits(this.field_74750_a);
   }

   public long func_150291_c() {
      return (long)this.field_74750_a;
   }

   public int func_150287_d() {
      return MathHelper.func_76141_d(this.field_74750_a);
   }

   public short func_150289_e() {
      return (short)(MathHelper.func_76141_d(this.field_74750_a) & '\uffff');
   }

   public byte func_150290_f() {
      return (byte)(MathHelper.func_76141_d(this.field_74750_a) & 255);
   }

   public double func_150286_g() {
      return (double)this.field_74750_a;
   }

   public float func_150288_h() {
      return this.field_74750_a;
   }
}
