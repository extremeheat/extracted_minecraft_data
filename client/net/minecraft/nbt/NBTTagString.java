package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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

   void func_74734_a(DataOutput var1) throws IOException {
      var1.writeUTF(this.field_74751_a);
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(288L);
      this.field_74751_a = var1.readUTF();
      var3.func_152450_a((long)(16 * this.field_74751_a.length()));
   }

   public byte func_74732_a() {
      return 8;
   }

   public String toString() {
      return "\"" + this.field_74751_a.replace("\"", "\\\"") + "\"";
   }

   public NBTBase func_74737_b() {
      return new NBTTagString(this.field_74751_a);
   }

   public boolean func_82582_d() {
      return this.field_74751_a.isEmpty();
   }

   public boolean equals(Object var1) {
      if (!super.equals(var1)) {
         return false;
      } else {
         NBTTagString var2 = (NBTTagString)var1;
         return this.field_74751_a == null && var2.field_74751_a == null || this.field_74751_a != null && this.field_74751_a.equals(var2.field_74751_a);
      }
   }

   public int hashCode() {
      return super.hashCode() ^ this.field_74751_a.hashCode();
   }

   public String func_150285_a_() {
      return this.field_74751_a;
   }
}
