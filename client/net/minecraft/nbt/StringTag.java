package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class StringTag implements Tag {
   public static final TagType<StringTag> TYPE = new TagType<StringTag>() {
      public StringTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(288L);
         String var4 = var1.readUTF();
         var3.accountBits((long)(16 * var4.length()));
         return StringTag.valueOf(var4);
      }

      public String getName() {
         return "STRING";
      }

      public String getPrettyName() {
         return "TAG_String";
      }

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   private static final StringTag EMPTY = new StringTag("");
   private final String data;

   private StringTag(String var1) {
      super();
      Objects.requireNonNull(var1, "Null string not allowed");
      this.data = var1;
   }

   public static StringTag valueOf(String var0) {
      return var0.isEmpty() ? EMPTY : new StringTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeUTF(this.data);
   }

   public byte getId() {
      return 8;
   }

   public TagType<StringTag> getType() {
      return TYPE;
   }

   public String toString() {
      return quoteAndEscape(this.data);
   }

   public StringTag copy() {
      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof StringTag && Objects.equals(this.data, ((StringTag)var1).data);
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public String getAsString() {
      return this.data;
   }

   public Component getPrettyDisplay(String var1, int var2) {
      String var3 = quoteAndEscape(this.data);
      String var4 = var3.substring(0, 1);
      MutableComponent var5 = (new TextComponent(var3.substring(1, var3.length() - 1))).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      return (new TextComponent(var4)).append(var5).append(var4);
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

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
