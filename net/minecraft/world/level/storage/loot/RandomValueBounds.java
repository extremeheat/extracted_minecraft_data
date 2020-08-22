package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class RandomValueBounds implements RandomIntGenerator {
   private final float min;
   private final float max;

   public RandomValueBounds(float var1, float var2) {
      this.min = var1;
      this.max = var2;
   }

   public RandomValueBounds(float var1) {
      this.min = var1;
      this.max = var1;
   }

   public static RandomValueBounds between(float var0, float var1) {
      return new RandomValueBounds(var0, var1);
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public int getInt(Random var1) {
      return Mth.nextInt(var1, Mth.floor(this.min), Mth.floor(this.max));
   }

   public float getFloat(Random var1) {
      return Mth.nextFloat(var1, this.min, this.max);
   }

   public boolean matchesValue(int var1) {
      return (float)var1 <= this.max && (float)var1 >= this.min;
   }

   public ResourceLocation getType() {
      return UNIFORM;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public RandomValueBounds deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (GsonHelper.isNumberValue(var1)) {
            return new RandomValueBounds(GsonHelper.convertToFloat(var1, "value"));
         } else {
            JsonObject var4 = GsonHelper.convertToJsonObject(var1, "value");
            float var5 = GsonHelper.getAsFloat(var4, "min");
            float var6 = GsonHelper.getAsFloat(var4, "max");
            return new RandomValueBounds(var5, var6);
         }
      }

      public JsonElement serialize(RandomValueBounds var1, Type var2, JsonSerializationContext var3) {
         if (var1.min == var1.max) {
            return new JsonPrimitive(var1.min);
         } else {
            JsonObject var4 = new JsonObject();
            var4.addProperty("min", var1.min);
            var4.addProperty("max", var1.max);
            return var4;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((RandomValueBounds)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
