package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagLongArray extends NBTTagCollection<NBTTagLong> {
   private long[] field_193587_b;

   NBTTagLongArray() {
      super();
   }

   public NBTTagLongArray(long[] var1) {
      super();
      this.field_193587_b = var1;
   }

   public NBTTagLongArray(LongSet var1) {
      super();
      this.field_193587_b = var1.toLongArray();
   }

   public NBTTagLongArray(List<Long> var1) {
      this(func_193586_a(var1));
   }

   private static long[] func_193586_a(List<Long> var0) {
      long[] var1 = new long[var0.size()];

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         Long var3 = (Long)var0.get(var2);
         var1[var2] = var3 == null ? 0L : var3;
      }

      return var1;
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      var1.writeInt(this.field_193587_b.length);
      long[] var2 = this.field_193587_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long var5 = var2[var4];
         var1.writeLong(var5);
      }

   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(192L);
      int var4 = var1.readInt();
      var3.func_152450_a((long)(64 * var4));
      this.field_193587_b = new long[var4];

      for(int var5 = 0; var5 < var4; ++var5) {
         this.field_193587_b[var5] = var1.readLong();
      }

   }

   public byte func_74732_a() {
      return 12;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[L;");

      for(int var2 = 0; var2 < this.field_193587_b.length; ++var2) {
         if (var2 != 0) {
            var1.append(',');
         }

         var1.append(this.field_193587_b[var2]).append('L');
      }

      return var1.append(']').toString();
   }

   public NBTTagLongArray func_74737_b() {
      long[] var1 = new long[this.field_193587_b.length];
      System.arraycopy(this.field_193587_b, 0, var1, 0, this.field_193587_b.length);
      return new NBTTagLongArray(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagLongArray && Arrays.equals(this.field_193587_b, ((NBTTagLongArray)var1).field_193587_b);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.field_193587_b);
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      ITextComponent var3 = (new TextComponentString("L")).func_211708_a(field_197641_e);
      ITextComponent var4 = (new TextComponentString("[")).func_150257_a(var3).func_150258_a(";");

      for(int var5 = 0; var5 < this.field_193587_b.length; ++var5) {
         ITextComponent var6 = (new TextComponentString(String.valueOf(this.field_193587_b[var5]))).func_211708_a(field_197640_d);
         var4.func_150258_a(" ").func_150257_a(var6).func_150257_a(var3);
         if (var5 != this.field_193587_b.length - 1) {
            var4.func_150258_a(",");
         }
      }

      var4.func_150258_a("]");
      return var4;
   }

   public long[] func_197652_h() {
      return this.field_193587_b;
   }

   public int size() {
      return this.field_193587_b.length;
   }

   public NBTTagLong func_197647_c(int var1) {
      return new NBTTagLong(this.field_193587_b[var1]);
   }

   public void func_197648_a(int var1, INBTBase var2) {
      this.field_193587_b[var1] = ((NBTPrimitive)var2).func_150291_c();
   }

   public void func_197649_b(int var1) {
      this.field_193587_b = ArrayUtils.remove(this.field_193587_b, var1);
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
