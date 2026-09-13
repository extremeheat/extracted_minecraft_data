package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTBase$NBTPrimitive {
   private float field_74750_a;

   NBTTagFloat() {
      super();
   }

   public NBTTagFloat(float var1) {
      super();
      this.field_74750_a = var1;
   }

   @Override
   void func_74734_a(DataOutput var1) {
      var1.writeFloat(this.field_74750_a);
   }

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
      var3.func_152450_a(32L);
      this.field_74750_a = var1.readFloat();
   }

   @Override
   public byte func_74732_a() {
      return 5;
   }

   @Override
   public String toString() {
      return "" + this.field_74750_a + "f";
   }

   @Override
   public NBTBase func_74737_b() {
      return new NBTTagFloat(this.field_74750_a);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagFloat var2 = (NBTTagFloat)var1;
         return this.field_74750_a == var2.field_74750_a;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Float.floatToIntBits(this.field_74750_a);
   }

   @Override
   public long func_150291_c() {
      return (long)this.field_74750_a;
   }

   @Override
   public int func_150287_d() {
      return MathHelper.func_76141_d(this.field_74750_a);
   }

   @Override
   public short func_150289_e() {
      return (short)(MathHelper.func_76141_d(this.field_74750_a) & 65535);
   }

   @Override
   public byte func_150290_f() {
      return (byte)(MathHelper.func_76141_d(this.field_74750_a) & 0xFF);
   }

   @Override
   public double func_150286_g() {
      return (double)this.field_74750_a;
   }

   @Override
   public float func_150288_h() {
      return this.field_74750_a;
   }
}
