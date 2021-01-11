package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
   private byte[] field_74754_a;

   NBTTagByteArray() {
      super();
   }

   public NBTTagByteArray(byte[] var1) {
      super();
      this.field_74754_a = var1;
   }

   void func_74734_a(DataOutput var1) throws IOException {
      var1.writeInt(this.field_74754_a.length);
      var1.write(this.field_74754_a);
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
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
      return "[" + this.field_74754_a.length + " bytes]";
   }

   public NBTBase func_74737_b() {
      byte[] var1 = new byte[this.field_74754_a.length];
      System.arraycopy(this.field_74754_a, 0, var1, 0, this.field_74754_a.length);
      return new NBTTagByteArray(var1);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) ? Arrays.equals(this.field_74754_a, ((NBTTagByteArray)var1).field_74754_a) : false;
   }

   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.field_74754_a);
   }

   public byte[] func_150292_c() {
      return this.field_74754_a;
   }
}
