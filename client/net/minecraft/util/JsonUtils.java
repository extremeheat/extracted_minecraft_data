package net.minecraft.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
   public static boolean func_151205_a(JsonObject var0, String var1) {
      return !func_151201_f(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isString();
   }

   public static boolean func_151211_a(JsonElement var0) {
      return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isString();
   }

   public static boolean func_180199_c(JsonObject var0, String var1) {
      return !func_151201_f(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isBoolean();
   }

   public static boolean func_151202_d(JsonObject var0, String var1) {
      if (!func_151204_g(var0, var1)) {
         return false;
      } else {
         return var0.get(var1).isJsonArray();
      }
   }

   public static boolean func_151201_f(JsonObject var0, String var1) {
      if (!func_151204_g(var0, var1)) {
         return false;
      } else {
         return var0.get(var1).isJsonPrimitive();
      }
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

   public static JsonArray func_151213_a(JsonObject var0, String var1, JsonArray var2) {
      return var0.has(var1) ? func_151207_m(var0.get(var1), var1) : var2;
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
}
