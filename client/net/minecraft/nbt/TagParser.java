package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.network.chat.TranslatableComponent;

public class TagParser {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.trailing", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.expected.key", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.expected.value", new Object[0]));
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.nbt.list.mixed", new Object[]{var0, var1});
   });
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.nbt.array.mixed", new Object[]{var0, var1});
   });
   public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("argument.nbt.array.invalid", new Object[]{var0});
   });
   private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
   private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
   private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
   private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
   private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
   private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
   private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
   private final StringReader reader;

   public static CompoundTag parseTag(String var0) throws CommandSyntaxException {
      return (new TagParser(new StringReader(var0))).readSingleStruct();
   }

   @VisibleForTesting
   CompoundTag readSingleStruct() throws CommandSyntaxException {
      CompoundTag var1 = this.readStruct();
      this.reader.skipWhitespace();
      if (this.reader.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(this.reader);
      } else {
         return var1;
      }
   }

   public TagParser(StringReader var1) {
      super();
      this.reader = var1;
   }

   protected String readKey() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
      } else {
         return this.reader.readString();
      }
   }

   protected Tag readTypedValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      int var1 = this.reader.getCursor();
      if (StringReader.isQuotedStringStart(this.reader.peek())) {
         return new StringTag(this.reader.readQuotedString());
      } else {
         String var2 = this.reader.readUnquotedString();
         if (var2.isEmpty()) {
            this.reader.setCursor(var1);
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         } else {
            return this.type(var2);
         }
      }
   }

   private Tag type(String var1) {
      try {
         if (FLOAT_PATTERN.matcher(var1).matches()) {
            return new FloatTag(Float.parseFloat(var1.substring(0, var1.length() - 1)));
         }

         if (BYTE_PATTERN.matcher(var1).matches()) {
            return new ByteTag(Byte.parseByte(var1.substring(0, var1.length() - 1)));
         }

         if (LONG_PATTERN.matcher(var1).matches()) {
            return new LongTag(Long.parseLong(var1.substring(0, var1.length() - 1)));
         }

         if (SHORT_PATTERN.matcher(var1).matches()) {
            return new ShortTag(Short.parseShort(var1.substring(0, var1.length() - 1)));
         }

         if (INT_PATTERN.matcher(var1).matches()) {
            return new IntTag(Integer.parseInt(var1));
         }

         if (DOUBLE_PATTERN.matcher(var1).matches()) {
            return new DoubleTag(Double.parseDouble(var1.substring(0, var1.length() - 1)));
         }

         if (DOUBLE_PATTERN_NOSUFFIX.matcher(var1).matches()) {
            return new DoubleTag(Double.parseDouble(var1));
         }

         if ("true".equalsIgnoreCase(var1)) {
            return new ByteTag((byte)1);
         }

         if ("false".equalsIgnoreCase(var1)) {
            return new ByteTag((byte)0);
         }
      } catch (NumberFormatException var3) {
      }

      return new StringTag(var1);
   }

   public Tag readValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         char var1 = this.reader.peek();
         if (var1 == '{') {
            return this.readStruct();
         } else {
            return var1 == '[' ? this.readList() : this.readTypedValue();
         }
      }
   }

   protected Tag readList() throws CommandSyntaxException {
      return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
   }

   public CompoundTag readStruct() throws CommandSyntaxException {
      this.expect('{');
      CompoundTag var1 = new CompoundTag();
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != '}') {
         int var2 = this.reader.getCursor();
         String var3 = this.readKey();
         if (var3.isEmpty()) {
            this.reader.setCursor(var2);
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }

         this.expect(':');
         var1.put(var3, this.readValue());
         if (!this.hasElementSeparator()) {
            break;
         }

         if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }
      }

      this.expect('}');
      return var1;
   }

   private Tag readListTag() throws CommandSyntaxException {
      this.expect('[');
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         ListTag var1 = new ListTag();
         byte var2 = -1;

         while(this.reader.peek() != ']') {
            int var3 = this.reader.getCursor();
            Tag var4 = this.readValue();
            byte var5 = var4.getId();
            if (var2 < 0) {
               var2 = var5;
            } else if (var5 != var2) {
               this.reader.setCursor(var3);
               throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, Tag.getTagTypeName(var5), Tag.getTagTypeName(var2));
            }

            var1.add(var4);
            if (!this.hasElementSeparator()) {
               break;
            }

            if (!this.reader.canRead()) {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            }
         }

         this.expect(']');
         return var1;
      }
   }

   private Tag readArrayTag() throws CommandSyntaxException {
      this.expect('[');
      int var1 = this.reader.getCursor();
      char var2 = this.reader.read();
      this.reader.read();
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else if (var2 == 'B') {
         return new ByteArrayTag(this.readArray((byte)7, (byte)1));
      } else if (var2 == 'L') {
         return new LongArrayTag(this.readArray((byte)12, (byte)4));
      } else if (var2 == 'I') {
         return new IntArrayTag(this.readArray((byte)11, (byte)3));
      } else {
         this.reader.setCursor(var1);
         throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(var2));
      }
   }

   private <T extends Number> List<T> readArray(byte var1, byte var2) throws CommandSyntaxException {
      ArrayList var3 = Lists.newArrayList();

      while(true) {
         if (this.reader.peek() != ']') {
            int var4 = this.reader.getCursor();
            Tag var5 = this.readValue();
            byte var6 = var5.getId();
            if (var6 != var2) {
               this.reader.setCursor(var4);
               throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, Tag.getTagTypeName(var6), Tag.getTagTypeName(var1));
            }

            if (var2 == 1) {
               var3.add(((NumericTag)var5).getAsByte());
            } else if (var2 == 4) {
               var3.add(((NumericTag)var5).getAsLong());
            } else {
               var3.add(((NumericTag)var5).getAsInt());
            }

            if (this.hasElementSeparator()) {
               if (!this.reader.canRead()) {
                  throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
               }
               continue;
            }
         }

         this.expect(']');
         return var3;
      }
   }

   private boolean hasElementSeparator() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == ',') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   private void expect(char var1) throws CommandSyntaxException {
      this.reader.skipWhitespace();
      this.reader.expect(var1);
   }
}
