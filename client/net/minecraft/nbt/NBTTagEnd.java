package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class NBTTagEnd implements INBTBase {
   public NBTTagEnd() {
      super();
   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(64L);
   }

   public void func_74734_a(DataOutput var1) throws IOException {
   }

   public byte func_74732_a() {
      return 0;
   }

   public String toString() {
      return "END";
   }

   public NBTTagEnd func_74737_b() {
      return new NBTTagEnd();
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      return new TextComponentString("");
   }

   public boolean equals(Object var1) {
      return var1 instanceof NBTTagEnd;
   }

   public int hashCode() {
      return this.func_74732_a();
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }
}
