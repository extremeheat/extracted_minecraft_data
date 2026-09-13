package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {
   private int[] field_74749_a;

   NBTTagIntArray() {
      super();
   }

   public NBTTagIntArray(int[] var1) {
      super();
      this.field_74749_a = var1;
   }

   @Override
   void func_74734_a(DataOutput var1) {
      var1.writeInt(this.field_74749_a.length);

      for(int var2 = 0; var2 < this.field_74749_a.length; ++var2) {
         var1.writeInt(this.field_74749_a[var2]);
      }
   }

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
      int var4 = var1.readInt();
      var3.func_152450_a((long)(32 * var4));
      this.field_74749_a = new int[var4];

      for(int var5 = 0; var5 < var4; ++var5) {
         this.field_74749_a[var5] = var1.readInt();
      }
   }

   @Override
   public byte func_74732_a() {
      return 11;
   }

   @Override
   public String toString() {
      String var1 = "[";

      for(int var5 : this.field_74749_a) {
         var1 = var1 + var5 + ",";
      }

      return var1 + "]";
   }

   @Override
   public NBTBase func_74737_b() {
      int[] var1 = new int[this.field_74749_a.length];
      System.arraycopy(this.field_74749_a, 0, var1, 0, this.field_74749_a.length);
      return new NBTTagIntArray(var1);
   }

   @Override
   public boolean equals(Object var1) {
      return super.equals(var1) ? Arrays.equals(this.field_74749_a, ((NBTTagIntArray)var1).field_74749_a) : false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.field_74749_a);
   }

   public int[] func_150302_c() {
      return this.field_74749_a;
   }
}
