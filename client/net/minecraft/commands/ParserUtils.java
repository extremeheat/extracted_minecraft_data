package net.minecraft.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Field;
import net.minecraft.Util;

public class ParserUtils {
   private static final Field JSON_READER_POS = Util.make(() -> {
      try {
         Field var0 = JsonReader.class.getDeclaredField("pos");
         var0.setAccessible(true);
         return var0;
      } catch (NoSuchFieldException var1) {
         throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
      }
   });
   private static final Field JSON_READER_LINESTART = Util.make(() -> {
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
         return JSON_READER_POS.getInt(var0) - JSON_READER_LINESTART.getInt(var0) + 1;
      } catch (IllegalAccessException var2) {
         throw new IllegalStateException("Couldn't read position of JsonReader", var2);
      }
   }

   public static <T> T parseJson(StringReader var0, Codec<T> var1) {
      JsonReader var2 = new JsonReader(new java.io.StringReader(var0.getRemaining()));
      var2.setLenient(false);

      Object var4;
      try {
         JsonElement var3 = Streams.parse(var2);
         var4 = Util.getOrThrow(var1.parse(JsonOps.INSTANCE, var3), JsonParseException::new);
      } catch (StackOverflowError var8) {
         throw new JsonParseException(var8);
      } finally {
         var0.setCursor(var0.getCursor() + getPos(var2));
      }

      return (T)var4;
   }
}
