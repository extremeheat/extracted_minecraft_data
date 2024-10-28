package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;

public class TagParser {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(Component.translatable("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.key"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.value"));
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("argument.nbt.list.mixed", var0, var1);
   });
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("argument.nbt.array.mixed", var0, var1);
   });
   public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.nbt.array.invalid", var0);
   });
   public static final char ELEMENT_SEPARATOR = ',';
   public static final char NAME_VALUE_SEPARATOR = ':';
   private static final char LIST_OPEN = '[';
   private static final char LIST_CLOSE = ']';
   private static final char STRUCT_CLOSE = '}';
   private static final char STRUCT_OPEN = '{';
   private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
   private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
   private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
   private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
   private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
   private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
   private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
   public static final Codec<CompoundTag> AS_CODEC;
   public static final Codec<CompoundTag> LENIENT_CODEC;
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
         return StringTag.valueOf(this.reader.readQuotedString());
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
            return FloatTag.valueOf(Float.parseFloat(var1.substring(0, var1.length() - 1)));
         }

         if (BYTE_PATTERN.matcher(var1).matches()) {
            return ByteTag.valueOf(Byte.parseByte(var1.substring(0, var1.length() - 1)));
         }

         if (LONG_PATTERN.matcher(var1).matches()) {
            return LongTag.valueOf(Long.parseLong(var1.substring(0, var1.length() - 1)));
         }

         if (SHORT_PATTERN.matcher(var1).matches()) {
            return ShortTag.valueOf(Short.parseShort(var1.substring(0, var1.length() - 1)));
         }

         if (INT_PATTERN.matcher(var1).matches()) {
            return IntTag.valueOf(Integer.parseInt(var1));
         }

         if (DOUBLE_PATTERN.matcher(var1).matches()) {
            return DoubleTag.valueOf(Double.parseDouble(var1.substring(0, var1.length() - 1)));
         }

         if (DOUBLE_PATTERN_NOSUFFIX.matcher(var1).matches()) {
            return DoubleTag.valueOf(Double.parseDouble(var1));
         }

         if ("true".equalsIgnoreCase(var1)) {
            return ByteTag.ONE;
         }

         if ("false".equalsIgnoreCase(var1)) {
            return ByteTag.ZERO;
         }
      } catch (NumberFormatException var3) {
      }

      return StringTag.valueOf(var1);
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
         TagType var2 = null;

         while(this.reader.peek() != ']') {
            int var3 = this.reader.getCursor();
            Tag var4 = this.readValue();
            TagType var5 = var4.getType();
            if (var2 == null) {
               var2 = var5;
            } else if (var5 != var2) {
               this.reader.setCursor(var3);
               throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, var5.getPrettyName(), var2.getPrettyName());
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
         return new ByteArrayTag(this.readArray(ByteArrayTag.TYPE, ByteTag.TYPE));
      } else if (var2 == 'L') {
         return new LongArrayTag(this.readArray(LongArrayTag.TYPE, LongTag.TYPE));
      } else if (var2 == 'I') {
         return new IntArrayTag(this.readArray(IntArrayTag.TYPE, IntTag.TYPE));
      } else {
         this.reader.setCursor(var1);
         throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(var2));
      }
   }

   private <T extends Number> List<T> readArray(TagType<?> var1, TagType<?> var2) throws CommandSyntaxException {
      ArrayList var3 = Lists.newArrayList();

      while(true) {
         if (this.reader.peek() != ']') {
            int var4 = this.reader.getCursor();
            Tag var5 = this.readValue();
            TagType var6 = var5.getType();
            if (var6 != var2) {
               this.reader.setCursor(var4);
               throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, var6.getPrettyName(), var1.getPrettyName());
            }

            if (var2 == ByteTag.TYPE) {
               var3.add(((NumericTag)var5).getAsByte());
            } else if (var2 == LongTag.TYPE) {
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

   static {
      AS_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success((new TagParser(new StringReader(var0))).readSingleStruct(), Lifecycle.stable());
         } catch (CommandSyntaxException var2) {
            Objects.requireNonNull(var2);
            return DataResult.error(var2::getMessage);
         }
      }, CompoundTag::toString);
      LENIENT_CODEC = Codec.withAlternative(AS_CODEC, CompoundTag.CODEC);
   }
}
