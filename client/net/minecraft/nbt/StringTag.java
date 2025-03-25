package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public record StringTag(String value) implements PrimitiveTag {
   private static final int SELF_SIZE_IN_BYTES = 36;
   public static final TagType<StringTag> TYPE = new TagType.VariableSize<StringTag>() {
      public StringTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return StringTag.valueOf(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static String readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(36L);
         String var2 = var0.readUTF();
         var1.accountBytes(2L, (long)var2.length());
         return var2;
      }

      public void skip(DataInput var1, NbtAccounter var2) throws IOException {
         StringTag.skipString(var1);
      }

      public String getName() {
         return "STRING";
      }

      public String getPrettyName() {
         return "TAG_String";
      }

      // $FF: synthetic method
      public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
      }
   };
   private static final StringTag EMPTY = new StringTag("");
   private static final char DOUBLE_QUOTE = '"';
   private static final char SINGLE_QUOTE = '\'';
   private static final char ESCAPE = '\\';
   private static final char NOT_SET = '\u0000';

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public StringTag(String var1) {
      super();
      this.value = var1;
   }

   public static void skipString(DataInput var0) throws IOException {
      var0.skipBytes(var0.readUnsignedShort());
   }

   public static StringTag valueOf(String var0) {
      return var0.isEmpty() ? EMPTY : new StringTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeUTF(this.value);
   }

   public int sizeInBytes() {
      return 36 + 2 * this.value.length();
   }

   public byte getId() {
      return 8;
   }

   public TagType<StringTag> getType() {
      return TYPE;
   }

   public String toString() {
      StringTagVisitor var1 = new StringTagVisitor();
      var1.visitString(this);
      return var1.build();
   }

   public StringTag copy() {
      return this;
   }

   public Optional<String> asString() {
      return Optional.of(this.value);
   }

   public void accept(TagVisitor var1) {
      var1.visitString(this);
   }

   public static String quoteAndEscape(String var0) {
      StringBuilder var1 = new StringBuilder();
      quoteAndEscape(var0, var1);
      return var1.toString();
   }

   public static void quoteAndEscape(String var0, StringBuilder var1) {
      int var2 = var1.length();
      var1.append(' ');
      int var3 = 0;

      for(int var4 = 0; var4 < var0.length(); ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 == '\\') {
            var1.append("\\\\");
         } else if (var5 != '"' && var5 != '\'') {
            String var6 = SnbtGrammar.escapeControlCharacters(var5);
            if (var6 != null) {
               var1.append('\\');
               var1.append(var6);
            } else {
               var1.append(var5);
            }
         } else {
            if (var3 == 0) {
               var3 = var5 == '"' ? 39 : 34;
            }

            if (var3 == var5) {
               var1.append('\\');
            }

            var1.append(var5);
         }
      }

      if (var3 == 0) {
         var3 = 34;
      }

      var1.setCharAt(var2, (char)var3);
      var1.append((char)var3);
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.value);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
