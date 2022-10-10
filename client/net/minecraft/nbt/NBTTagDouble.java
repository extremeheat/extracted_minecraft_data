package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagDouble extends NBTPrimitive {
   private double field_74755_a;

   NBTTagDouble() {
      super();
   }

   public NBTTagDouble(double var1) {
      super();
      this.field_74755_a = var1;
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      var1.writeDouble(this.field_74755_a);
   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(128L);
      this.field_74755_a = var1.readDouble();
   }

   public byte func_74732_a() {
      return 6;
   }

   public String toString() {
      return this.field_74755_a + "d";
   }

   public NBTTagDouble func_74737_b() {
      return new NBTTagDouble(this.field_74755_a);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagDouble && this.field_74755_a == ((NBTTagDouble)var1).field_74755_a;
      }
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.field_74755_a);
      return (int)(var1 ^ var1 >>> 32);
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      ITextComponent var3 = (new TextComponentString("d")).func_211708_a(field_197641_e);
      return (new TextComponentString(String.valueOf(this.field_74755_a))).func_150257_a(var3).func_211708_a(field_197640_d);
   }

   public long func_150291_c() {
      return (long)Math.floor(this.field_74755_a);
   }

   public int func_150287_d() {
      return MathHelper.func_76128_c(this.field_74755_a);
   }

   public short func_150289_e() {
      return (short)(MathHelper.func_76128_c(this.field_74755_a) & '\uffff');
   }

   public byte func_150290_f() {
      return (byte)(MathHelper.func_76128_c(this.field_74755_a) & 255);
   }

   public double func_150286_g() {
      return this.field_74755_a;
   }

   public float func_150288_h() {
      return (float)this.field_74755_a;
   }

   public Number func_209908_j() {
      return this.field_74755_a;
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }
}
