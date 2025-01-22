package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class TagParser<T> {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(Component.translatable("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.key"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.value"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_COMPOUND = new SimpleCommandExceptionType(Component.translatable("argument.nbt.expected.compound"));
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("argument.nbt.array.mixed", var0, var1));
   public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("argument.nbt.array.invalid", var0));
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
   public static final Codec<CompoundTag> FLATTENED_CODEC;
   public static final Codec<CompoundTag> LENIENT_CODEC;
   private final DynamicOps<T> ops;
   private final StringReader reader;

   private static CompoundTag castToCompoundOrThrow(StringReader var0, Tag var1) throws CommandSyntaxException {
      if (var1 instanceof CompoundTag var2) {
         return var2;
      } else {
         throw ERROR_EXPECTED_COMPOUND.createWithContext(var0);
      }
   }

   public static CompoundTag parseCompoundFully(String var0) throws CommandSyntaxException {
      StringReader var1 = new StringReader(var0);
      return castToCompoundOrThrow(var1, (Tag)parseFully(NbtOps.INSTANCE, (StringReader)var1));
   }

   public static <T> T parseFully(DynamicOps<T> var0, String var1) throws CommandSyntaxException {
      return (T)parseFully(var0, new StringReader(var1));
   }

   public static <T> T parseFully(DynamicOps<T> var0, StringReader var1) throws CommandSyntaxException {
      Object var2 = (new TagParser(var0, var1)).readValue();
      var1.skipWhitespace();
      if (var1.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(var1);
      } else {
         return (T)var2;
      }
   }

   public static <T> T parseAsArgument(DynamicOps<T> var0, StringReader var1) throws CommandSyntaxException {
      return (T)(new TagParser(var0, var1)).readValue();
   }

   public static CompoundTag parseCompoundAsArgument(StringReader var0) throws CommandSyntaxException {
      Tag var1 = (Tag)parseAsArgument(NbtOps.INSTANCE, var0);
      return castToCompoundOrThrow(var0, var1);
   }

   private TagParser(DynamicOps<T> var1, StringReader var2) {
      super();
      this.ops = var1;
      this.reader = var2;
   }

   @VisibleForTesting
   public static String readKey(StringReader var0) throws CommandSyntaxException {
      var0.skipWhitespace();
      if (!var0.canRead()) {
         throw ERROR_EXPECTED_KEY.createWithContext(var0);
      } else {
         return var0.readString();
      }
   }

   private T readScalar() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      int var1 = this.reader.getCursor();
      if (StringReader.isQuotedStringStart(this.reader.peek())) {
         return (T)this.ops.createString(this.reader.readQuotedString());
      } else {
         String var2 = this.reader.readUnquotedString();
         if (var2.isEmpty()) {
            this.reader.setCursor(var1);
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         } else {
            return (T)this.parseSingleWordValue(var2);
         }
      }
   }

   @Nullable
   private static Number parseNumber(String var0) {
      try {
         char var1 = Character.toLowerCase(var0.charAt(var0.length() - 1));
         switch (var1) {
            case 'b':
               if (BYTE_PATTERN.matcher(var0).matches()) {
                  return Byte.parseByte(var0.substring(0, var0.length() - 1));
               }
               break;
            case 'd':
               if (DOUBLE_PATTERN.matcher(var0).matches()) {
                  return Double.parseDouble(var0.substring(0, var0.length() - 1));
               }
               break;
            case 'f':
               if (FLOAT_PATTERN.matcher(var0).matches()) {
                  return Float.parseFloat(var0.substring(0, var0.length() - 1));
               }
               break;
            case 'l':
               if (LONG_PATTERN.matcher(var0).matches()) {
                  return Long.parseLong(var0.substring(0, var0.length() - 1));
               }
               break;
            case 's':
               if (SHORT_PATTERN.matcher(var0).matches()) {
                  return Short.parseShort(var0.substring(0, var0.length() - 1));
               }
         }

         if (INT_PATTERN.matcher(var0).matches()) {
            return Integer.parseInt(var0);
         }

         if (DOUBLE_PATTERN_NOSUFFIX.matcher(var0).matches()) {
            return Double.parseDouble(var0);
         }
      } catch (NumberFormatException var2) {
      }

      return null;
   }

   private T parseSingleWordValue(String var1) {
      if ("true".equalsIgnoreCase(var1)) {
         return (T)this.ops.createBoolean(true);
      } else if ("false".equalsIgnoreCase(var1)) {
         return (T)this.ops.createBoolean(false);
      } else {
         Number var2 = parseNumber(var1);
         if (var2 != null) {
            Objects.requireNonNull(var2);
            byte var4 = 0;
            Object var10000;
            //$FF: var4->value
            //0->java/lang/Float
            //1->java/lang/Double
            //2->java/lang/Byte
            //3->java/lang/Short
            //4->java/lang/Integer
            //5->java/lang/Long
            switch (var2.typeSwitch<invokedynamic>(var2, var4)) {
               case 0:
                  Float var5 = (Float)var2;
                  var10000 = this.ops.createFloat(var5);
                  break;
               case 1:
                  Double var6 = (Double)var2;
                  var10000 = this.ops.createDouble(var6);
                  break;
               case 2:
                  Byte var7 = (Byte)var2;
                  var10000 = this.ops.createByte(var7);
                  break;
               case 3:
                  Short var8 = (Short)var2;
                  var10000 = this.ops.createShort(var8);
                  break;
               case 4:
                  Integer var9 = (Integer)var2;
                  var10000 = this.ops.createInt(var9);
                  break;
               case 5:
                  Long var10 = (Long)var2;
                  var10000 = this.ops.createLong(var10);
                  break;
               default:
                  throw new UnsupportedOperationException();
            }

            return (T)var10000;
         } else {
            return (T)this.ops.createString(var1);
         }
      }
   }

   @Nullable
   private Number readNumber() {
      String var1 = this.reader.readUnquotedString();
      return var1.isEmpty() ? null : parseNumber(var1);
   }

   private T readValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         char var1 = this.reader.peek();
         if (var1 == '{') {
            return (T)this.readStruct();
         } else {
            return (T)(var1 == '[' ? this.readList() : this.readScalar());
         }
      }
   }

   private T readList() throws CommandSyntaxException {
      return (T)(this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag());
   }

   private T readStruct() throws CommandSyntaxException {
      this.expect('{');
      HashMap var1 = new HashMap();
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != '}') {
         int var2 = this.reader.getCursor();
         String var3 = readKey(this.reader);
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
      return (T)this.ops.createMap(var1.entrySet().stream().map((var1x) -> Pair.of(this.ops.createString((String)var1x.getKey()), var1x.getValue())));
   }

   private T readListTag() throws CommandSyntaxException {
      this.expect('[');
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         Stream.Builder var1 = Stream.builder();

         while(this.reader.peek() != ']') {
            var1.add(this.readValue());
            if (!this.hasElementSeparator()) {
               break;
            }

            if (!this.reader.canRead()) {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            }
         }

         this.expect(']');
         return (T)this.ops.createList(var1.build());
      }
   }

   private T readArrayTag() throws CommandSyntaxException {
      this.expect('[');
      int var1 = this.reader.getCursor();
      char var2 = this.reader.read();
      this.reader.read();
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else if (var2 == 'B') {
         return (T)this.ops.createByteList(this.readByteArray());
      } else if (var2 == 'L') {
         return (T)this.ops.createLongList(this.readLongArray());
      } else if (var2 == 'I') {
         return (T)this.ops.createIntList(this.readIntArray());
      } else {
         this.reader.setCursor(var1);
         throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(var2));
      }
   }

   private ByteBuffer readByteArray() throws CommandSyntaxException {
      ByteArrayList var1 = new ByteArrayList();

      while(this.reader.peek() != ']') {
         int var2 = this.reader.getCursor();
         Number var3 = this.readNumber();
         if (!(var3 instanceof Byte)) {
            this.reader.setCursor(var2);
            throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, var3, "byte array");
         }

         Byte var4 = (Byte)var3;
         var1.add(var4);
         if (!this.hasElementSeparator()) {
            break;
         }

         if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         }
      }

      this.expect(']');
      return ByteBuffer.wrap(var1.toByteArray());
   }

   private IntStream readIntArray() throws CommandSyntaxException {
      IntArrayList var1 = new IntArrayList();

      while(this.reader.peek() != ']') {
         int var2 = this.reader.getCursor();
         Number var3 = this.readNumber();
         if (!(var3 instanceof Integer)) {
            this.reader.setCursor(var2);
            throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, var3, "int array");
         }

         Integer var4 = (Integer)var3;
         var1.add(var4);
         if (!this.hasElementSeparator()) {
            break;
         }

         if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         }
      }

      this.expect(']');
      return var1.intStream();
   }

   private LongStream readLongArray() throws CommandSyntaxException {
      LongArrayList var1 = new LongArrayList();

      while(this.reader.peek() != ']') {
         int var2 = this.reader.getCursor();
         Number var3 = this.readNumber();
         if (!(var3 instanceof Long)) {
            this.reader.setCursor(var2);
            throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, var3, "long array");
         }

         Long var4 = (Long)var3;
         var1.add(var4);
         if (!this.hasElementSeparator()) {
            break;
         }

         if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         }
      }

      this.expect(']');
      return var1.longStream();
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
      FLATTENED_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         try {
            Tag var1 = (Tag)parseFully(NbtOps.INSTANCE, (String)var0);
            if (var1 instanceof CompoundTag var2) {
               return DataResult.success(var2, Lifecycle.stable());
            } else {
               return DataResult.error(() -> "Expected compound tag, got " + String.valueOf(var1));
            }
         } catch (CommandSyntaxException var3) {
            Objects.requireNonNull(var3);
            return DataResult.error(var3::getMessage);
         }
      }, CompoundTag::toString);
      LENIENT_CODEC = Codec.withAlternative(FLATTENED_CODEC, CompoundTag.CODEC);
   }
}
