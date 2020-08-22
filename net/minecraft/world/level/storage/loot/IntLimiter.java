package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class IntLimiter implements IntUnaryOperator {
   private final Integer min;
   private final Integer max;
   private final IntUnaryOperator op;

   private IntLimiter(@Nullable Integer var1, @Nullable Integer var2) {
      this.min = var1;
      this.max = var2;
      int var3;
      if (var1 == null) {
         if (var2 == null) {
            this.op = (var0) -> {
               return var0;
            };
         } else {
            var3 = var2;
            this.op = (var1x) -> {
               return Math.min(var3, var1x);
            };
         }
      } else {
         var3 = var1;
         if (var2 == null) {
            this.op = (var1x) -> {
               return Math.max(var3, var1x);
            };
         } else {
            int var4 = var2;
            this.op = (var2x) -> {
               return Mth.clamp(var2x, var3, var4);
            };
         }
      }

   }

   public static IntLimiter clamp(int var0, int var1) {
      return new IntLimiter(var0, var1);
   }

   public static IntLimiter lowerBound(int var0) {
      return new IntLimiter(var0, (Integer)null);
   }

   public static IntLimiter upperBound(int var0) {
      return new IntLimiter((Integer)null, var0);
   }

   public int applyAsInt(int var1) {
      return this.op.applyAsInt(var1);
   }

   // $FF: synthetic method
   IntLimiter(Integer var1, Integer var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public IntLimiter deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "value");
         Integer var5 = var4.has("min") ? GsonHelper.getAsInt(var4, "min") : null;
         Integer var6 = var4.has("max") ? GsonHelper.getAsInt(var4, "max") : null;
         return new IntLimiter(var5, var6);
      }

      public JsonElement serialize(IntLimiter var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (var1.max != null) {
            var4.addProperty("max", var1.max);
         }

         if (var1.min != null) {
            var4.addProperty("min", var1.min);
         }

         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((IntLimiter)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
