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
import net.minecraft.item.Item;
import net.minecraft.util.registry.IRegistry;

public class JsonUtils {
   private static final Gson field_212747_a = (new GsonBuilder()).create();

   public static boolean func_151205_a(JsonObject var0, String var1) {
      return !func_151201_f(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isString();
   }

   public static boolean func_151211_a(JsonElement var0) {
      return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isString();
   }

   public static boolean func_188175_b(JsonElement var0) {
      return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isNumber();
   }

   public static boolean func_180199_c(JsonObject var0, String var1) {
      return !func_151201_f(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isBoolean();
   }

   public static boolean func_151202_d(JsonObject var0, String var1) {
      return !func_151204_g(var0, var1) ? false : var0.get(var1).isJsonArray();
   }

   public static boolean func_151201_f(JsonObject var0, String var1) {
      return !func_151204_g(var0, var1) ? false : var0.get(var1).isJsonPrimitive();
   }

   public static boolean func_151204_g(JsonObject var0, String var1) {
      if (var0 == null) {
         return false;
      } else {
         return var0.get(var1) != null;
      }
   }

   public static String func_151206_a(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive()) {
         return var0.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a string, was " + func_151222_d(var0));
      }
   }

   public static String func_151200_h(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return func_151206_a(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a string");
      }
   }

   public static String func_151219_a(JsonObject var0, String var1, String var2) {
      return var0.has(var1) ? func_151206_a(var0.get(var1), var1) : var2;
   }

   public static Item func_188172_b(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive()) {
         String var2 = var0.getAsString();
         Item var3 = (Item)IRegistry.field_212630_s.func_212608_b(new ResourceLocation(var2));
         if (var3 == null) {
            throw new JsonSyntaxException("Expected " + var1 + " to be an item, was unknown string '" + var2 + "'");
         } else {
            return var3;
         }
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be an item, was " + func_151222_d(var0));
      }
   }

   public static Item func_188180_i(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return func_188172_b(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find an item");
      }
   }

   public static boolean func_151216_b(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive()) {
         return var0.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Boolean, was " + func_151222_d(var0));
      }
   }

   public static boolean func_151212_i(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return func_151216_b(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Boolean");
      }
   }

   public static boolean func_151209_a(JsonObject var0, String var1, boolean var2) {
      return var0.has(var1) ? func_151216_b(var0.get(var1), var1) : var2;
   }

   public static float func_151220_d(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Float, was " + func_151222_d(var0));
      }
   }

   public static float func_151217_k(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return func_151220_d(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Float");
      }
   }

   public static float func_151221_a(JsonObject var0, String var1, float var2) {
      return var0.has(var1) ? func_151220_d(var0.get(var1), var1) : var2;
   }

   public static int func_151215_f(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Int, was " + func_151222_d(var0));
      }
   }

   public static int func_151203_m(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return func_151215_f(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Int");
      }
   }

   public static int func_151208_a(JsonObject var0, String var1, int var2) {
      return var0.has(var1) ? func_151215_f(var0.get(var1), var1) : var2;
   }

   public static byte func_204332_h(JsonElement var0, String var1) {
      if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
         return var0.getAsByte();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a Byte, was " + func_151222_d(var0));
      }
   }

   public static byte func_204331_o(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return func_204332_h(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a Byte");
      }
   }

   public static JsonObject func_151210_l(JsonElement var0, String var1) {
      if (var0.isJsonObject()) {
         return var0.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a JsonObject, was " + func_151222_d(var0));
      }
   }

   public static JsonObject func_152754_s(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return func_151210_l(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a JsonObject");
      }
   }

   public static JsonObject func_151218_a(JsonObject var0, String var1, JsonObject var2) {
      return var0.has(var1) ? func_151210_l(var0.get(var1), var1) : var2;
   }

   public static JsonArray func_151207_m(JsonElement var0, String var1) {
      if (var0.isJsonArray()) {
         return var0.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a JsonArray, was " + func_151222_d(var0));
      }
   }

   public static JsonArray func_151214_t(JsonObject var0, String var1) {
      if (var0.has(var1)) {
         return func_151207_m(var0.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a JsonArray");
      }
   }

   public static JsonArray func_151213_a(JsonObject var0, String var1, @Nullable JsonArray var2) {
      return var0.has(var1) ? func_151207_m(var0.get(var1), var1) : var2;
   }

   public static <T> T func_188179_a(@Nullable JsonElement var0, String var1, JsonDeserializationContext var2, Class<? extends T> var3) {
      if (var0 != null) {
         return var2.deserialize(var0, var3);
      } else {
         throw new JsonSyntaxException("Missing " + var1);
      }
   }

   public static <T> T func_188174_a(JsonObject var0, String var1, JsonDeserializationContext var2, Class<? extends T> var3) {
      if (var0.has(var1)) {
         return func_188179_a(var0.get(var1), var1, var2, var3);
      } else {
         throw new JsonSyntaxException("Missing " + var1);
      }
   }

   public static <T> T func_188177_a(JsonObject var0, String var1, T var2, JsonDeserializationContext var3, Class<? extends T> var4) {
      return var0.has(var1) ? func_188179_a(var0.get(var1), var1, var3, var4) : var2;
   }

   public static String func_151222_d(JsonElement var0) {
      String var1 = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(var0), "...", 10);
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
   public static <T> T func_188173_a(Gson var0, Reader var1, Class<T> var2, boolean var3) {
      try {
         JsonReader var4 = new JsonReader(var1);
         var4.setLenient(var3);
         return var0.getAdapter(var2).read(var4);
      } catch (IOException var5) {
         throw new JsonParseException(var5);
      }
   }

   @Nullable
   public static <T> T func_193838_a(Gson var0, Reader var1, Type var2, boolean var3) {
      try {
         JsonReader var4 = new JsonReader(var1);
         var4.setLenient(var3);
         return var0.getAdapter(TypeToken.get(var2)).read(var4);
      } catch (IOException var5) {
         throw new JsonParseException(var5);
      }
   }

   @Nullable
   public static <T> T func_193837_a(Gson var0, String var1, Type var2, boolean var3) {
      return func_193838_a(var0, new StringReader(var1), var2, var3);
   }

   @Nullable
   public static <T> T func_188176_a(Gson var0, String var1, Class<T> var2, boolean var3) {
      return func_188173_a(var0, new StringReader(var1), var2, var3);
   }

   @Nullable
   public static <T> T func_193841_a(Gson var0, Reader var1, Type var2) {
      return func_193838_a(var0, var1, var2, false);
   }

   @Nullable
   public static <T> T func_193840_a(Gson var0, String var1, Type var2) {
      return func_193837_a(var0, var1, var2, false);
   }

   @Nullable
   public static <T> T func_193839_a(Gson var0, Reader var1, Class<T> var2) {
      return func_188173_a(var0, var1, var2, false);
   }

   @Nullable
   public static <T> T func_188178_a(Gson var0, String var1, Class<T> var2) {
      return func_188176_a(var0, var1, var2, false);
   }

   public static JsonObject func_212746_a(String var0, boolean var1) {
      return func_212744_a(new StringReader(var0), var1);
   }

   public static JsonObject func_212744_a(Reader var0, boolean var1) {
      return (JsonObject)func_188173_a(field_212747_a, var0, JsonObject.class, var1);
   }

   public static JsonObject func_212745_a(String var0) {
      return func_212746_a(var0, false);
   }

   public static JsonObject func_212743_a(Reader var0) {
      return func_212744_a(var0, false);
   }
}
