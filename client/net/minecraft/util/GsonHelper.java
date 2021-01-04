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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;

public class GsonHelper {
   private static final Gson GSON = (new GsonBuilder()).create();

   public static boolean isStringValue(JsonObject var0, String var1) {
      return !isValidPrimitive(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isString();
   }

   public static boolean isStringValue(JsonElement var0) {
      return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isString();
   }

   public static boolean isNumberValue(JsonElement var0) {
      return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isNumber();
   }

   public static boolean isBooleanValue(JsonObject var0, String var1) {
      return !isValidPrimitive(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isBoolean();
   }

   public static boolean isArrayNode(JsonObject var0, String var1) {
      return !isValidNode(var0, var1) ? false : var0.get(var1).isJsonArray();
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

   public static String getAsString(JsonObject var0, String var1, String var2) {
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

   public static byte getAsByte(JsonObject var0, String var1, byte var2) {
      return var0.has(var1) ? convertToByte(var0.get(var1), var1) : var2;
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

   public static JsonObject getAsJsonObject(JsonObject var0, String var1, JsonObject var2) {
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

   public static <T> T getAsObject(JsonObject var0, String var1, T var2, JsonDeserializationContext var3, Class<? extends T> var4) {
      return var0.has(var1) ? convertToObject(var0.get(var1), var1, var3, var4) : var2;
   }

   public static String getType(JsonElement var0) {
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
   public static <T> T fromJson(Gson var0, Reader var1, Type var2, boolean var3) {
      try {
         JsonReader var4 = new JsonReader(var1);
         var4.setLenient(var3);
         return var0.getAdapter(TypeToken.get(var2)).read(var4);
      } catch (IOException var5) {
         throw new JsonParseException(var5);
      }
   }

   @Nullable
   public static <T> T fromJson(Gson var0, String var1, Type var2, boolean var3) {
      return fromJson(var0, (Reader)(new StringReader(var1)), (Type)var2, var3);
   }

   @Nullable
   public static <T> T fromJson(Gson var0, String var1, Class<T> var2, boolean var3) {
      return fromJson(var0, (Reader)(new StringReader(var1)), (Class)var2, var3);
   }

   @Nullable
   public static <T> T fromJson(Gson var0, Reader var1, Type var2) {
      return fromJson(var0, var1, var2, false);
   }

   @Nullable
   public static <T> T fromJson(Gson var0, String var1, Type var2) {
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
}
