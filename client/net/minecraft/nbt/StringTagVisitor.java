package net.minecraft.nbt;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class StringTagVisitor implements TagVisitor {
   private static final Pattern UNQUOTED_KEY_MATCH = Pattern.compile("[A-Za-z._]+[A-Za-z0-9._+-]*");
   private final StringBuilder builder = new StringBuilder();

   public StringTagVisitor() {
      super();
   }

   public String build() {
      return this.builder.toString();
   }

   public void visitString(StringTag var1) {
      this.builder.append(StringTag.quoteAndEscape(var1.value()));
   }

   public void visitByte(ByteTag var1) {
      this.builder.append(var1.value()).append('b');
   }

   public void visitShort(ShortTag var1) {
      this.builder.append(var1.value()).append('s');
   }

   public void visitInt(IntTag var1) {
      this.builder.append(var1.value());
   }

   public void visitLong(LongTag var1) {
      this.builder.append(var1.value()).append('L');
   }

   public void visitFloat(FloatTag var1) {
      this.builder.append(var1.value()).append('f');
   }

   public void visitDouble(DoubleTag var1) {
      this.builder.append(var1.value()).append('d');
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

         var1.get(var2).accept((TagVisitor)this);
      }

      this.builder.append(']');
   }

   public void visitCompound(CompoundTag var1) {
      this.builder.append('{');
      ArrayList var2 = new ArrayList(var1.entrySet());
      var2.sort(Entry.comparingByKey());

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         Map.Entry var4 = (Map.Entry)var2.get(var3);
         if (var3 != 0) {
            this.builder.append(',');
         }

         this.handleKeyEscape((String)var4.getKey());
         this.builder.append(':');
         ((Tag)var4.getValue()).accept((TagVisitor)this);
      }

      this.builder.append('}');
   }

   private void handleKeyEscape(String var1) {
      if (!var1.equalsIgnoreCase("true") && !var1.equalsIgnoreCase("false") && UNQUOTED_KEY_MATCH.matcher(var1).matches()) {
         this.builder.append(var1);
      } else {
         StringTag.quoteAndEscape(var1, this.builder);
      }

   }

   public void visitEnd(EndTag var1) {
      this.builder.append("END");
   }
}
