package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class NBTTagEnd extends NBTBase {
   NBTTagEnd() {
      super();
   }

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
   }

   @Override
   void func_74734_a(DataOutput var1) {
   }

   @Override
   public byte func_74732_a() {
      return 0;
   }

   @Override
   public String toString() {
      return "END";
   }

   @Override
   public NBTBase func_74737_b() {
      return new NBTTagEnd();
   }
}
