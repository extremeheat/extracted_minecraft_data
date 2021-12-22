package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;

public class StringTagVisitor implements TagVisitor {
   private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
   private final StringBuilder builder = new StringBuilder();

   public StringTagVisitor() {
      super();
   }

   public String visit(Tag var1) {
      var1.accept((TagVisitor)this);
      return this.builder.toString();
   }

   public void visitString(StringTag var1) {
      this.builder.append(StringTag.quoteAndEscape(var1.getAsString()));
   }

   public void visitByte(ByteTag var1) {
      this.builder.append(var1.getAsNumber()).append('b');
   }

   public void visitShort(ShortTag var1) {
      this.builder.append(var1.getAsNumber()).append('s');
   }

   public void visitInt(IntTag var1) {
      this.builder.append(var1.getAsNumber());
   }

   public void visitLong(LongTag var1) {
      this.builder.append(var1.getAsNumber()).append('L');
   }

   public void visitFloat(FloatTag var1) {
      this.builder.append(var1.getAsFloat()).append('f');
   }

   public void visitDouble(DoubleTag var1) {
      this.builder.append(var1.getAsDouble()).append('d');
   }

   public void visitByteArray(ByteArrayTag var1) {
      this.builder.append("[B;");
      byte[] var2 = var1.getAsByteArray();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var3 != 0) {
            this.builder.append(',');
         }

         this.builder.append(var2[var3]).append('B');
      }

      this.builder.append(']');
   }

   public void visitIntArray(IntArrayTag var1) {
      this.builder.append("[I;");
      int[] var2 = var1.getAsIntArray();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var3 != 0) {
            this.builder.append(',');
         }

         this.builder.append(var2[var3]);
      }

      this.builder.append(']');
   }

   public void visitLongArray(LongArrayTag var1) {
      this.builder.append("[L;");
      long[] var2 = var1.getAsLongArray();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var3 != 0) {
            this.builder.append(',');
         }

         this.builder.append(var2[var3]).append('L');
      }

      this.builder.append(']');
   }

   public void visitList(ListTag var1) {
      this.builder.append('[');

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         if (var2 != 0) {
            this.builder.append(',');
         }

         this.builder.append((new StringTagVisitor()).visit(var1.get(var2)));
      }

      this.builder.append(']');
   }

   public void visitCompound(CompoundTag var1) {
      this.builder.append('{');
      ArrayList var2 = Lists.newArrayList(var1.getAllKeys());
      Collections.sort(var2);

      String var4;
      for(Iterator var3 = var2.iterator(); var3.hasNext(); this.builder.append(handleEscape(var4)).append(':').append((new StringTagVisitor()).visit(var1.get(var4)))) {
         var4 = (String)var3.next();
         if (this.builder.length() != 1) {
            this.builder.append(',');
         }
      }

      this.builder.append('}');
   }

   protected static String handleEscape(String var0) {
      return SIMPLE_VALUE.matcher(var0).matches() ? var0 : StringTag.quoteAndEscape(var0);
   }

   public void visitEnd(EndTag var1) {
      this.builder.append("END");
   }
}
