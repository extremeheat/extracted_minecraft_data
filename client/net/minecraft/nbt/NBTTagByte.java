package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagByte extends NBTPrimitive {
   private byte field_74756_a;

   NBTTagByte() {
      super();
   }

   public NBTTagByte(byte var1) {
      super();
      this.field_74756_a = var1;
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      var1.writeByte(this.field_74756_a);
   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(72L);
      this.field_74756_a = var1.readByte();
   }

   public byte func_74732_a() {
      return 1;
   }

   public String toString() {
      return this.field_74756_a + "b";
   }

   public NBTTagByte func_74737_b() {
      return new NBTTagByte(this.field_74756_a);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagByte && this.field_74756_a == ((NBTTagByte)var1).field_74756_a;
      }
   }

   public int hashCode() {
      return this.field_74756_a;
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      ITextComponent var3 = (new TextComponentString("b")).func_211708_a(field_197641_e);
      return (new TextComponentString(String.valueOf(this.field_74756_a))).func_150257_a(var3).func_211708_a(field_197640_d);
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

   public Number func_209908_j() {
      return this.field_74756_a;
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }
}
