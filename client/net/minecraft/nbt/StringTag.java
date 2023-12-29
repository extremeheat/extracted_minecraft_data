package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class StringTag implements Tag {
   private static final int SELF_SIZE_IN_BYTES = 36;
   public static final TagType<StringTag> TYPE = new TagType.VariableSize<StringTag>() {
      public StringTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return StringTag.valueOf(readAccounted(var1, var2));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static String readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(36L);
         String var2 = var0.readUTF();
         var1.accountBytes(2L, (long)var2.length());
         return var2;
      }

      @Override
      public void skip(DataInput var1, NbtAccounter var2) throws IOException {
         StringTag.skipString(var1);
      }

      @Override
      public String getName() {
         return "STRING";
      }

      @Override
      public String getPrettyName() {
         return "TAG_String";
      }

      @Override
      public boolean isValue() {
         return true;
      }
   };
   private static final StringTag EMPTY = new StringTag("");
   private static final char DOUBLE_QUOTE = '"';
   private static final char SINGLE_QUOTE = '\'';
   private static final char ESCAPE = '\\';
   private static final char NOT_SET = '\u0000';
   private final String data;

   public static void skipString(DataInput var0) throws IOException {
      var0.skipBytes(var0.readUnsignedShort());
   }

   private StringTag(String var1) {
      super();
      Objects.requireNonNull(var1, "Null string not allowed");
      this.data = var1;
   }

   public static StringTag valueOf(String var0) {
      return var0.isEmpty() ? EMPTY : new StringTag(var0);
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeUTF(this.data);
   }

   @Override
   public int sizeInBytes() {
      return 36 + 2 * this.data.length();
   }

   @Override
   public byte getId() {
      return 8;
   }

   @Override
   public TagType<StringTag> getType() {
      return TYPE;
   }

   @Override
   public String toString() {
      return Tag.super.getAsString();
   }

   public StringTag copy() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof StringTag && Objects.equals(this.data, ((StringTag)var1).data);
      }
   }

   @Override
   public int hashCode() {
      return this.data.hashCode();
   }

   @Override
   public String getAsString() {
      return this.data;
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitString(this);
   }

   public static String quoteAndEscape(String var0) {
      StringBuilder var1 = new StringBuilder(" ");
      int var2 = 0;

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         char var4 = var0.charAt(var3);
         if (var4 == '\\') {
            var1.append('\\');
         } else if (var4 == '"' || var4 == '\'') {
            if (var2 == 0) {
               var2 = var4 == '"' ? 39 : 34;
            }

            if (var2 == var4) {
               var1.append('\\');
            }
         }

         var1.append(var4);
      }

      if (var2 == 0) {
         var2 = 34;
      }

      var1.setCharAt(0, (char)var2);
      var1.append((char)var2);
      return var1.toString();
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.data);
   }
}
