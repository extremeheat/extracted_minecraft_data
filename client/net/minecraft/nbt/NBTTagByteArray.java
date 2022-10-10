package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagByteArray extends NBTTagCollection<NBTTagByte> {
   private byte[] field_74754_a;

   NBTTagByteArray() {
      super();
   }

   public NBTTagByteArray(byte[] var1) {
      super();
      this.field_74754_a = var1;
   }

   public NBTTagByteArray(List<Byte> var1) {
      this(func_193589_a(var1));
   }

   private static byte[] func_193589_a(List<Byte> var0) {
      byte[] var1 = new byte[var0.size()];

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         Byte var3 = (Byte)var0.get(var2);
         var1[var2] = var3 == null ? 0 : var3;
      }

      return var1;
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      var1.writeInt(this.field_74754_a.length);
      var1.write(this.field_74754_a);
   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(192L);
      int var4 = var1.readInt();
      var3.func_152450_a((long)(8 * var4));
      this.field_74754_a = new byte[var4];
      var1.readFully(this.field_74754_a);
   }

   public byte func_74732_a() {
      return 7;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[B;");

      for(int var2 = 0; var2 < this.field_74754_a.length; ++var2) {
         if (var2 != 0) {
            var1.append(',');
         }

         var1.append(this.field_74754_a[var2]).append('B');
      }

      return var1.append(']').toString();
   }

   public INBTBase func_74737_b() {
      byte[] var1 = new byte[this.field_74754_a.length];
      System.arraycopy(this.field_74754_a, 0, var1, 0, this.field_74754_a.length);
      return new NBTTagByteArray(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagByteArray && Arrays.equals(this.field_74754_a, ((NBTTagByteArray)var1).field_74754_a);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.field_74754_a);
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      ITextComponent var3 = (new TextComponentString("B")).func_211708_a(field_197641_e);
      ITextComponent var4 = (new TextComponentString("[")).func_150257_a(var3).func_150258_a(";");

      for(int var5 = 0; var5 < this.field_74754_a.length; ++var5) {
         ITextComponent var6 = (new TextComponentString(String.valueOf(this.field_74754_a[var5]))).func_211708_a(field_197640_d);
         var4.func_150258_a(" ").func_150257_a(var6).func_150257_a(var3);
         if (var5 != this.field_74754_a.length - 1) {
            var4.func_150258_a(",");
         }
      }

      var4.func_150258_a("]");
      return var4;
   }

   public byte[] func_150292_c() {
      return this.field_74754_a;
   }

   public int size() {
      return this.field_74754_a.length;
   }

   public NBTTagByte func_197647_c(int var1) {
      return new NBTTagByte(this.field_74754_a[var1]);
   }

   public void func_197648_a(int var1, INBTBase var2) {
      this.field_74754_a[var1] = ((NBTPrimitive)var2).func_150290_f();
   }

   public void func_197649_b(int var1) {
      this.field_74754_a = ArrayUtils.remove(this.field_74754_a, var1);
   }

   // $FF: synthetic method
   public INBTBase func_197647_c(int var1) {
      return this.func_197647_c(var1);
   }
}
