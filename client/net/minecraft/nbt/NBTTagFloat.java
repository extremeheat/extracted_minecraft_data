package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagFloat extends NBTPrimitive {
   private float field_74750_a;

   NBTTagFloat() {
      super();
   }

   public NBTTagFloat(float var1) {
      super();
      this.field_74750_a = var1;
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      var1.writeFloat(this.field_74750_a);
   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(96L);
      this.field_74750_a = var1.readFloat();
   }

   public byte func_74732_a() {
      return 5;
   }

   public String toString() {
      return this.field_74750_a + "f";
   }

   public NBTTagFloat func_74737_b() {
      return new NBTTagFloat(this.field_74750_a);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagFloat && this.field_74750_a == ((NBTTagFloat)var1).field_74750_a;
      }
   }

   public int hashCode() {
      return Float.floatToIntBits(this.field_74750_a);
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      ITextComponent var3 = (new TextComponentString("f")).func_211708_a(field_197641_e);
      return (new TextComponentString(String.valueOf(this.field_74750_a))).func_150257_a(var3).func_211708_a(field_197640_d);
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

   public Number func_209908_j() {
      return this.field_74750_a;
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }
}
