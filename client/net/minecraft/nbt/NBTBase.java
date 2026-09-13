package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public abstract class NBTBase {
   public static final String[] field_82578_b = new String[]{
      "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"
   };

   abstract void func_74734_a(DataOutput var1);

   abstract void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3);

   @Override
   public abstract String toString();

   public abstract byte func_74732_a();

   protected NBTBase() {
      super();
   }

   protected static NBTBase func_150284_a(byte var0) {
      switch(var0) {
         case 0:
            return new NBTTagEnd();
         case 1:
            return new NBTTagByte();
         case 2:
            return new NBTTagShort();
         case 3:
            return new NBTTagInt();
         case 4:
            return new NBTTagLong();
         case 5:
            return new NBTTagFloat();
         case 6:
            return new NBTTagDouble();
         case 7:
            return new NBTTagByteArray();
         case 8:
            return new NBTTagString();
         case 9:
            return new NBTTagList();
         case 10:
            return new NBTTagCompound();
         case 11:
            return new NBTTagIntArray();
         default:
            return null;
      }
   }

   public abstract NBTBase func_74737_b();

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof NBTBase)) {
         return false;
      } else {
         NBTBase var2 = (NBTBase)var1;
         return this.func_74732_a() == var2.func_74732_a();
      }
   }

   @Override
   public int hashCode() {
      return this.func_74732_a();
   }

   protected String func_150285_a_() {
      return this.toString();
   }
}
