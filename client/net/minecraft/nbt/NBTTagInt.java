package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagInt extends NBTPrimitive {
   private int field_74748_a;

   NBTTagInt() {
      super();
   }

   public NBTTagInt(int var1) {
      super();
      this.field_74748_a = var1;
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      var1.writeInt(this.field_74748_a);
   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(96L);
      this.field_74748_a = var1.readInt();
   }

   public byte func_74732_a() {
      return 3;
   }

   public String toString() {
      return String.valueOf(this.field_74748_a);
   }

   public NBTTagInt func_74737_b() {
      return new NBTTagInt(this.field_74748_a);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagInt && this.field_74748_a == ((NBTTagInt)var1).field_74748_a;
      }
   }

   public int hashCode() {
      return this.field_74748_a;
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      return (new TextComponentString(String.valueOf(this.field_74748_a))).func_211708_a(field_197640_d);
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

   public Number func_209908_j() {
      return this.field_74748_a;
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }
}
