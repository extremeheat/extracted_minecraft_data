package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase {
   NBTTagEnd() {
      super();
   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(64L);
   }

   void func_74734_a(DataOutput var1) throws IOException {
   }

   public byte func_74732_a() {
      return 0;
   }

   public String toString() {
      return "END";
   }

   public NBTBase func_74737_b() {
      return new NBTTagEnd();
   }
}
