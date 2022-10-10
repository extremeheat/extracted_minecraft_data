package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public interface INBTBase {
   String[] field_82578_b = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]"};
   TextFormatting field_197638_b = TextFormatting.AQUA;
   TextFormatting field_197639_c = TextFormatting.GREEN;
   TextFormatting field_197640_d = TextFormatting.GOLD;
   TextFormatting field_197641_e = TextFormatting.RED;

   void func_74734_a(DataOutput var1) throws IOException;

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException;

   String toString();

   byte func_74732_a();

   static INBTBase func_150284_a(byte var0) {
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
      case 12:
         return new NBTTagLongArray();
      default:
         return null;
      }
   }

   static String func_193581_j(int var0) {
      switch(var0) {
      case 0:
         return "TAG_End";
      case 1:
         return "TAG_Byte";
      case 2:
         return "TAG_Short";
      case 3:
         return "TAG_Int";
      case 4:
         return "TAG_Long";
      case 5:
         return "TAG_Float";
      case 6:
         return "TAG_Double";
      case 7:
         return "TAG_Byte_Array";
      case 8:
         return "TAG_String";
      case 9:
         return "TAG_List";
      case 10:
         return "TAG_Compound";
      case 11:
         return "TAG_Int_Array";
      case 12:
         return "TAG_Long_Array";
      case 99:
         return "Any Numeric Tag";
      default:
         return "UNKNOWN";
      }
   }

   INBTBase func_74737_b();

   default String func_150285_a_() {
      return this.toString();
   }

   default ITextComponent func_197637_c() {
      return this.func_199850_a("", 0);
   }

   ITextComponent func_199850_a(String var1, int var2);
}
