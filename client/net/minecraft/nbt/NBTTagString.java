package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagString extends NBTBase {
   private String field_74751_a;

   public NBTTagString() {
      super();
      this.field_74751_a = "";
   }

   public NBTTagString(String var1) {
      super();
      this.field_74751_a = var1;
      if (var1 == null) {
         throw new IllegalArgumentException("Empty string not allowed");
      }
   }

   @Override
   void func_74734_a(DataOutput var1) {
      var1.writeUTF(this.field_74751_a);
   }

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
      this.field_74751_a = var1.readUTF();
      var3.func_152450_a((long)(16 * this.field_74751_a.length()));
   }

   @Override
   public byte func_74732_a() {
      return 8;
   }

   @Override
   public String toString() {
      return "\"" + this.field_74751_a + "\"";
   }

   @Override
   public NBTBase func_74737_b() {
      return new NBTTagString(this.field_74751_a);
   }

   @Override
   public boolean equals(Object var1) {
      if (!super.equals(var1)) {
         return false;
      } else {
         NBTTagString var2 = (NBTTagString)var1;
         return this.field_74751_a == null && var2.field_74751_a == null || this.field_74751_a != null && this.field_74751_a.equals(var2.field_74751_a);
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.field_74751_a.hashCode();
   }

   @Override
   public String func_150285_a_() {
      return this.field_74751_a;
   }
}
