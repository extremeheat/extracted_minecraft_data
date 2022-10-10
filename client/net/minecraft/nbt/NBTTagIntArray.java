package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagIntArray extends NBTTagCollection<NBTTagInt> {
   private int[] field_74749_a;

   NBTTagIntArray() {
      super();
   }

   public NBTTagIntArray(int[] var1) {
      super();
      this.field_74749_a = var1;
   }

   public NBTTagIntArray(List<Integer> var1) {
      this(func_193584_a(var1));
   }

   private static int[] func_193584_a(List<Integer> var0) {
      int[] var1 = new int[var0.size()];

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         Integer var3 = (Integer)var0.get(var2);
         var1[var2] = var3 == null ? 0 : var3;
      }

      return var1;
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      var1.writeInt(this.field_74749_a.length);
      int[] var2 = this.field_74749_a;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var1.writeInt(var5);
      }

   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(192L);
      int var4 = var1.readInt();
      var3.func_152450_a((long)(32 * var4));
      this.field_74749_a = new int[var4];

      for(int var5 = 0; var5 < var4; ++var5) {
         this.field_74749_a[var5] = var1.readInt();
      }

   }

   public byte func_74732_a() {
      return 11;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[I;");

      for(int var2 = 0; var2 < this.field_74749_a.length; ++var2) {
         if (var2 != 0) {
            var1.append(',');
         }

         var1.append(this.field_74749_a[var2]);
      }

      return var1.append(']').toString();
   }

   public NBTTagIntArray func_74737_b() {
      int[] var1 = new int[this.field_74749_a.length];
      System.arraycopy(this.field_74749_a, 0, var1, 0, this.field_74749_a.length);
      return new NBTTagIntArray(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagIntArray && Arrays.equals(this.field_74749_a, ((NBTTagIntArray)var1).field_74749_a);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.field_74749_a);
   }

   public int[] func_150302_c() {
      return this.field_74749_a;
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      ITextComponent var3 = (new TextComponentString("I")).func_211708_a(field_197641_e);
      ITextComponent var4 = (new TextComponentString("[")).func_150257_a(var3).func_150258_a(";");

      for(int var5 = 0; var5 < this.field_74749_a.length; ++var5) {
         var4.func_150258_a(" ").func_150257_a((new TextComponentString(String.valueOf(this.field_74749_a[var5]))).func_211708_a(field_197640_d));
         if (var5 != this.field_74749_a.length - 1) {
            var4.func_150258_a(",");
         }
      }

      var4.func_150258_a("]");
      return var4;
   }

   public int size() {
      return this.field_74749_a.length;
   }

   public NBTTagInt func_197647_c(int var1) {
      return new NBTTagInt(this.field_74749_a[var1]);
   }

   public void func_197648_a(int var1, INBTBase var2) {
      this.field_74749_a[var1] = ((NBTPrimitive)var2).func_150287_d();
   }

   public void func_197649_b(int var1) {
      this.field_74749_a = ArrayUtils.remove(this.field_74749_a, var1);
   }

   // $FF: synthetic method
   public INBTBase func_197647_c(int var1) {
      return this.func_197647_c(var1);
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }
}
