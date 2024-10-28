package net.minecraft.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Field;
import net.minecraft.CharPredicate;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;

public class ParserUtils {
   private static final Field JSON_READER_POS = (Field)Util.make(() -> {
      try {
         Field var0 = JsonReader.class.getDeclaredField("pos");
         var0.setAccessible(true);
         return var0;
      } catch (NoSuchFieldException var1) {
         throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
      }
   });
   private static final Field JSON_READER_LINESTART = (Field)Util.make(() -> {
      try {
         Field var0 = JsonReader.class.getDeclaredField("lineStart");
         var0.setAccessible(true);
         return var0;
      } catch (NoSuchFieldException var1) {
         throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
      }
   });

   public ParserUtils() {
      super();
   }

   private static int getPos(JsonReader var0) {
      try {
         return JSON_READER_POS.getInt(var0) - JSON_READER_LINESTART.getInt(var0);
      } catch (IllegalAccessException var2) {
         throw new IllegalStateException("Couldn't read position of JsonReader", var2);
      }
   }

   public static <T> T parseJson(HolderLookup.Provider var0, StringReader var1, Codec<T> var2) {
      JsonReader var3 = new JsonReader(new java.io.StringReader(var1.getRemaining()));
      var3.setLenient(false);

      Object var5;
      try {
         JsonElement var4 = Streams.parse(var3);
         var5 = var2.parse(var0.createSerializationContext(JsonOps.INSTANCE), var4).getOrThrow(JsonParseException::new);
      } catch (StackOverflowError var9) {
         throw new JsonParseException(var9);
      } finally {
         var1.setCursor(var1.getCursor() + getPos(var3));
      }

      return var5;
   }

   public static String readWhile(StringReader var0, CharPredicate var1) {
      int var2 = var0.getCursor();

      while(var0.canRead() && var1.test(var0.peek())) {
         var0.skip();
      }

      return var0.getString().substring(var2, var0.getCursor());
   }
}
