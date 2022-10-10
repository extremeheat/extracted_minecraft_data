package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagString implements INBTBase {
   private String field_74751_a;

   public NBTTagString() {
      this("");
   }

   public NBTTagString(String var1) {
      super();
      Objects.requireNonNull(var1, "Null string not allowed");
      this.field_74751_a = var1;
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      var1.writeUTF(this.field_74751_a);
   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(288L);
      this.field_74751_a = var1.readUTF();
      var3.func_152450_a((long)(16 * this.field_74751_a.length()));
   }

   public byte func_74732_a() {
      return 8;
   }

   public String toString() {
      return func_197654_a(this.field_74751_a, true);
   }

   public NBTTagString func_74737_b() {
      return new NBTTagString(this.field_74751_a);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagString && Objects.equals(this.field_74751_a, ((NBTTagString)var1).field_74751_a);
      }
   }

   public int hashCode() {
      return this.field_74751_a.hashCode();
   }

   public String func_150285_a_() {
      return this.field_74751_a;
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      ITextComponent var3 = (new TextComponentString(func_197654_a(this.field_74751_a, false))).func_211708_a(field_197639_c);
      return (new TextComponentString("\"")).func_150257_a(var3).func_150258_a("\"");
   }

   public static String func_197654_a(String var0, boolean var1) {
      StringBuilder var2 = new StringBuilder();
      if (var1) {
         var2.append('"');
      }

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         char var4 = var0.charAt(var3);
         if (var4 == '\\' || var4 == '"') {
            var2.append('\\');
         }

         var2.append(var4);
      }

      if (var1) {
         var2.append('"');
      }

      return var2.toString();
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }
}
