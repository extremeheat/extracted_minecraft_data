package net.minecraft.nbt;

import java.util.ArrayList;
import java.util.List;
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

   public void visitString(final StringTag tag) {
      this.builder.append(StringTag.quoteAndEscape(tag.value()));
   }

   public void visitByte(final ByteTag tag) {
      this.builder.append(tag.value()).append('b');
   }

   public void visitShort(final ShortTag tag) {
      this.builder.append(tag.value()).append('s');
   }

   public void visitInt(final IntTag tag) {
      this.builder.append(tag.value());
   }

   public void visitLong(final LongTag tag) {
      this.builder.append(tag.value()).append('L');
   }

   public void visitFloat(final FloatTag tag) {
      this.builder.append(tag.value()).append('f');
   }

   public void visitDouble(final DoubleTag tag) {
      this.builder.append(tag.value()).append('d');
   }

   public void visitByteArray(final ByteArrayTag tag) {
      this.builder.append("[B;");
      byte[] data = tag.getAsByteArray();

      for(int i = 0; i < data.length; ++i) {
         if (i != 0) {
            this.builder.append(',');
         }

         this.builder.append(data[i]).append('B');
      }

      this.builder.append(']');
   }

   public void visitIntArray(final IntArrayTag tag) {
      this.builder.append("[I;");
      int[] data = tag.getAsIntArray();

      for(int i = 0; i < data.length; ++i) {
         if (i != 0) {
            this.builder.append(',');
         }

         this.builder.append(data[i]);
      }

      this.builder.append(']');
   }

   public void visitLongArray(final LongArrayTag tag) {
      this.builder.append("[L;");
      long[] data = tag.getAsLongArray();

      for(int i = 0; i < data.length; ++i) {
         if (i != 0) {
            this.builder.append(',');
         }

         this.builder.append(data[i]).append('L');
      }

      this.builder.append(']');
   }

   public void visitList(final ListTag tag) {
      this.builder.append('[');

      for(int i = 0; i < tag.size(); ++i) {
         if (i != 0) {
            this.builder.append(',');
         }

         tag.get(i).accept((TagVisitor)this);
      }

      this.builder.append(']');
   }

   public void visitCompound(final CompoundTag tag) {
      this.builder.append('{');
      List<Map.Entry<String, Tag>> entries = new ArrayList(tag.entrySet());
      entries.sort(Entry.comparingByKey());

      for(int i = 0; i < entries.size(); ++i) {
         Map.Entry<String, Tag> entry = (Map.Entry)entries.get(i);
         if (i != 0) {
            this.builder.append(',');
         }

         this.handleKeyEscape((String)entry.getKey());
         this.builder.append(':');
         ((Tag)entry.getValue()).accept((TagVisitor)this);
      }

      this.builder.append('}');
   }

   private void handleKeyEscape(final String input) {
      if (!input.equalsIgnoreCase("true") && !input.equalsIgnoreCase("false") && UNQUOTED_KEY_MATCH.matcher(input).matches()) {
         this.builder.append(input);
      } else {
         StringTag.quoteAndEscape(input, this.builder);
      }

   }

   public void visitEnd(final EndTag tag) {
      this.builder.append("END");
   }
}
