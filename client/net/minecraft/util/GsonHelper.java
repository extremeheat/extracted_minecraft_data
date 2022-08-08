package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;

public class GsonHelper {
   private static final Gson GSON = (new GsonBuilder()).create();

   public GsonHelper() {
      super();
   }

   public static boolean isStringValue(JsonObject var0, String var1) {
      return !isValidPrimitive(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isString();
   }

   public static boolean isStringValue(JsonElement var0) {
      return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isString();
   }

   public static boolean isNumberValue(JsonObject var0, String var1) {
      return !isValidPrimitive(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isNumber();
   }

   public static boolean isNumberValue(JsonElement var0) {
      return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isNumber();
   }

   public static boolean isBooleanValue(JsonObject var0, String var1) {
      return !isValidPrimitive(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isBoolean();
   }

   public static boolean isBooleanValue(JsonElement var0) {
      return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isBoolean();
   }

   public static boolean isArrayNode(JsonObject var0, String var1) {
      return !isValidNode(var0, var1) ? false : var0.get(var1).isJsonArray();
   }

   public static boolean isObjectNode(JsonObject var0, String var1) {
      return !isValidNode(var0, var1) ? false : var0.get(var1).isJsonObject();
   }

   public static boolean isValidPrimitive(JsonObject var0, String var1) {
      return !isValidNode(var0, var1) ? false : var0.get(var1).isJsonPrimitive();
   }

   public static boolean isValidNode(JsonObject var0, String var1) {
      if (var0 == null) {
         return false;
      } else {
         return var0.get(var1) != null;
      }
   }

   public static String convertToString(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive()) {
         return var0.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a string, was " + getType(var0));
      }
   }

   public static String getAsString(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToString(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a string");
      }
   }

   @Nullable
   @Contract("_,_,!null->!null;_,_,null->_")
   public static String getAsString(JsonObject var0, String var1, @Nullable String var2) {
      return var0.has(var1) ? convertToString(var0.get(var1), var1) : var2;
   }

   public static Item convertToItem(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive()) {
         String var2 = var0.getAsString();
         return (Item)Registry.ITEM.getOptional(new ResourceLocation(var2)).orElseThrow(() -> {
            return new JsonSyntaxException("Expected " + var1 + " to be an item, was unknown string '" + var2 + "'");
         });
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be an item, was " + getType(var0));
      }
   }

   public static Item getAsItem(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToItem(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find an item");
      }
   }

   @Nullable
   @Contract("_,_,!null->!null;_,_,null->_")
   public static Item getAsItem(JsonObject var0, String var1, @Nullable Item var2) {
      return var0.has(var1) ? convertToItem(var0.get(var1), var1) : var2;
   }

   public static boolean convertToBoolean(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive()) {
         return var0.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Boolean, was " + getType(var0));
      }
   }

   public static boolean getAsBoolean(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToBoolean(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Boolean");
      }
   }

   public static boolean getAsBoolean(JsonObject var0, String var1, boolean var2) {
      return var0.has(var1) ? convertToBoolean(var0.get(var1), var1) : var2;
   }

   public static double convertToDouble(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsDouble();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Double, was " + getType(var0));
      }
   }

   public static double getAsDouble(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToDouble(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Double");
      }
   }

   public static double getAsDouble(JsonObject var0, String var1, double var2) {
      return var0.has(var1) ? convertToDouble(var0.get(var1), var1) : var2;
   }

   public static float convertToFloat(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Float, was " + getType(var0));
      }
   }

   public static float getAsFloat(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToFloat(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Float");
      }
   }

   public static float getAsFloat(JsonObject var0, String var1, float var2) {
      return var0.has(var1) ? convertToFloat(var0.get(var1), var1) : var2;
   }

   public static long convertToLong(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsLong();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Long, was " + getType(var0));
      }
   }

   public static long getAsLong(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToLong(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Long");
      }
   }

   public static long getAsLong(JsonObject var0, String var1, long var2) {
      return var0.has(var1) ? convertToLong(var0.get(var1), var1) : var2;
   }

   public static int convertToInt(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Int, was " + getType(var0));
      }
   }

   public static int getAsInt(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToInt(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Int");
      }
   }

   public static int getAsInt(JsonObject var0, String var1, int var2) {
      return var0.has(var1) ? convertToInt(var0.get(var1), var1) : var2;
   }

   public static byte convertToByte(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsByte();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Byte, was " + getType(var0));
      }
   }

   public static byte getAsByte(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToByte(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Byte");
      }
   }

   public static byte getAsByte(JsonObject var0, String var1, byte var2) {
      return var0.has(var1) ? convertToByte(var0.get(var1), var1) : var2;
   }

   public static char convertToCharacter(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsCharacter();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Character, was " + getType(var0));
      }
   }

   public static char getAsCharacter(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToCharacter(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Character");
      }
   }

   public static char getAsCharacter(JsonObject var0, String var1, char var2) {
      return var0.has(var1) ? convertToCharacter(var0.get(var1), var1) : var2;
   }

   public static BigDecimal convertToBigDecimal(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsBigDecimal();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a BigDecimal, was " + getType(var0));
      }
   }

   public static BigDecimal getAsBigDecimal(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToBigDecimal(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a BigDecimal");
      }
   }

   public static BigDecimal getAsBigDecimal(JsonObject var0, String var1, BigDecimal var2) {
      return var0.has(var1) ? convertToBigDecimal(var0.get(var1), var1) : var2;
   }

   public static BigInteger convertToBigInteger(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsBigInteger();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a BigInteger, was " + getType(var0));
      }
   }

   public static BigInteger getAsBigInteger(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToBigInteger(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a BigInteger");
      }
   }

   public static BigInteger getAsBigInteger(JsonObject var0, String var1, BigInteger var2) {
      return var0.has(var1) ? convertToBigInteger(var0.get(var1), var1) : var2;
   }

   public static short convertToShort(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsShort();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Short, was " + getType(var0));
      }
   }

   public static short getAsShort(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToShort(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Short");
      }
   }

   public static short getAsShort(JsonObject var0, String var1, short var2) {
      return var0.has(var1) ? convertToShort(var0.get(var1), var1) : var2;
   }

   public static JsonObject convertToJsonObject(JsonElement var0, String var1) {
      if (var0.isJsonObject()) {
         return var0.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a JsonObject, was " + getType(var0));
      }
   }

   public static JsonObject getAsJsonObject(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToJsonObject(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a JsonObject");
      }
   }

   @Nullable
   @Contract("_,_,!null->!null;_,_,null->_")
   public static JsonObject getAsJsonObject(JsonObject var0, String var1, @Nullable JsonObject var2) {
      return var0.has(var1) ? convertToJsonObject(var0.get(var1), var1) : var2;
   }

   public static JsonArray convertToJsonArray(JsonElement var0, String var1) {
      if (var0.isJsonArray()) {
         return var0.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a JsonArray, was " + getType(var0));
      }
   }

   public static JsonArray getAsJsonArray(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return convertToJsonArray(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a JsonArray");
      }
   }

   @Nullable
   @Contract("_,_,!null->!null;_,_,null->_")
   public static JsonArray getAsJsonArray(JsonObject var0, String var1, @Nullable JsonArray var2) {
      return var0.has(var1) ? convertToJsonArray(var0.get(var1), var1) : var2;
   }

   public static <T> T convertToObject(@Nullable JsonElement var0, String var1, JsonDeserializationContext var2, Class<? extends T> var3) {
      if (var0 != null) {
         return var2.deserialize(var0, var3);
      } else {
         throw new JsonSyntaxException("Missing " + var1);
      }
   }

   public static <T> T getAsObject(JsonObject var0, String var1, JsonDeserializationContext var2, Class<? extends T> var3) {
      if (var0.has(var1)) {
         return convertToObject(var0.get(var1), var1, var2, var3);
      } else {
         throw new JsonSyntaxException("Missing " + var1);
      }
   }

   @Nullable
   @Contract("_,_,!null,_,_->!null;_,_,null,_,_->_")
   public static <T> T getAsObject(JsonObject var0, String var1, @Nullable T var2, JsonDeserializationContext var3, Class<? extends T> var4) {
      return var0.has(var1) ? convertToObject(var0.get(var1), var1, var3, var4) : var2;
   }

   public static String getType(@Nullable JsonElement var0) {
      String var1 = StringUtils.abbreviateMiddle(String.valueOf(var0), "...", 10);
      if (var0 == null) {
         return "null (missing)";
      } else if (var0.isJsonNull()) {
         return "null (json)";
      } else if (var0.isJsonArray()) {
         return "an array (" + var1 + ")";
      } else if (var0.isJsonObject()) {
         return "an object (" + var1 + ")";
      } else {
         if (var0.isJsonPrimitive()) {
            JsonPrimitive var2 = var0.getAsJsonPrimitive();
            if (var2.isNumber()) {
               return "a number (" + var1 + ")";
            }

            if (var2.isBoolean()) {
               return "a boolean (" + var1 + ")";
            }
         }

         return var1;
      }
   }

   @Nullable
   public static <T> T fromJson(Gson var0, Reader var1, Class<T> var2, boolean var3) {
      try {
         JsonReader var4 = new JsonReader(var1);
         var4.setLenient(var3);
         return var0.getAdapter(var2).read(var4);
      } catch (IOException var5) {
         throw new JsonParseException(var5);
      }
   }

   @Nullable
   public static <T> T fromJson(Gson var0, Reader var1, TypeToken<T> var2, boolean var3) {
      try {
         JsonReader var4 = new JsonReader(var1);
         var4.setLenient(var3);
         return var0.getAdapter(var2).read(var4);
      } catch (IOException var5) {
         throw new JsonParseException(var5);
      }
   }

   @Nullable
   public static <T> T fromJson(Gson var0, String var1, TypeToken<T> var2, boolean var3) {
      return fromJson(var0, (Reader)(new StringReader(var1)), (TypeToken)var2, var3);
   }

   @Nullable
   public static <T> T fromJson(Gson var0, String var1, Class<T> var2, boolean var3) {
      return fromJson(var0, (Reader)(new StringReader(var1)), (Class)var2, var3);
   }

   @Nullable
   public static <T> T fromJson(Gson var0, Reader var1, TypeToken<T> var2) {
      return fromJson(var0, var1, var2, false);
   }

   @Nullable
   public static <T> T fromJson(Gson var0, String var1, TypeToken<T> var2) {
      return fromJson(var0, var1, var2, false);
   }

   @Nullable
   public static <T> T fromJson(Gson var0, Reader var1, Class<T> var2) {
      return fromJson(var0, var1, var2, false);
   }

   @Nullable
   public static <T> T fromJson(Gson var0, String var1, Class<T> var2) {
      return fromJson(var0, var1, var2, false);
   }

   public static JsonObject parse(String var0, boolean var1) {
      return parse((Reader)(new StringReader(var0)), var1);
   }

   public static JsonObject parse(Reader var0, boolean var1) {
      return (JsonObject)fromJson(GSON, var0, JsonObject.class, var1);
   }

   public static JsonObject parse(String var0) {
      return parse(var0, false);
   }

   public static JsonObject parse(Reader var0) {
      return parse(var0, false);
   }

   public static JsonArray parseArray(String var0) {
      return parseArray((Reader)(new StringReader(var0)));
   }

   public static JsonArray parseArray(Reader var0) {
      return (JsonArray)fromJson(GSON, var0, JsonArray.class, false);
   }

   public static String toStableString(JsonElement var0) {
      StringWriter var1 = new StringWriter();
      JsonWriter var2 = new JsonWriter(var1);

      try {
         writeValue(var2, var0, Comparator.naturalOrder());
      } catch (IOException var4) {
         throw new AssertionError(var4);
      }

      return var1.toString();
   }

   public static void writeValue(JsonWriter var0, @Nullable JsonElement var1, @Nullable Comparator<String> var2) throws IOException {
      if (var1 != null && !var1.isJsonNull()) {
         if (var1.isJsonPrimitive()) {
            JsonPrimitive var3 = var1.getAsJsonPrimitive();
            if (var3.isNumber()) {
               var0.value(var3.getAsNumber());
            } else if (var3.isBoolean()) {
               var0.value(var3.getAsBoolean());
            } else {
               var0.value(var3.getAsString());
            }
         } else {
            Iterator var5;
            if (var1.isJsonArray()) {
               var0.beginArray();
               var5 = var1.getAsJsonArray().iterator();

               while(var5.hasNext()) {
                  JsonElement var4 = (JsonElement)var5.next();
                  writeValue(var0, var4, var2);
               }

               var0.endArray();
            } else {
               if (!var1.isJsonObject()) {
                  throw new IllegalArgumentException("Couldn't write " + var1.getClass());
               }

               var0.beginObject();
               var5 = sortByKeyIfNeeded(var1.getAsJsonObject().entrySet(), var2).iterator();

               while(var5.hasNext()) {
                  Map.Entry var6 = (Map.Entry)var5.next();
                  var0.name((String)var6.getKey());
                  writeValue(var0, (JsonElement)var6.getValue(), var2);
               }

               var0.endObject();
            }
         }
      } else {
         var0.nullValue();
      }

   }

   private static Collection<Map.Entry<String, JsonElement>> sortByKeyIfNeeded(Collection<Map.Entry<String, JsonElement>> var0, @Nullable Comparator<String> var1) {
      if (var1 == null) {
         return var0;
      } else {
         ArrayList var2 = new ArrayList(var0);
         var2.sort(Entry.comparingByKey(var1));
         return var2;
      }
   }
}
