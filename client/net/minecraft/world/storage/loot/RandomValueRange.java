package net.minecraft.world.storage.loot;

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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;

public class RandomValueRange {
   private final float field_186514_a;
   private final float field_186515_b;

   public RandomValueRange(float var1, float var2) {
      super();
      this.field_186514_a = var1;
      this.field_186515_b = var2;
   }

   public RandomValueRange(float var1) {
      super();
      this.field_186514_a = var1;
      this.field_186515_b = var1;
   }

   public float func_186509_a() {
      return this.field_186514_a;
   }

   public float func_186512_b() {
      return this.field_186515_b;
   }

   public int func_186511_a(Random var1) {
      return MathHelper.func_76136_a(var1, MathHelper.func_76141_d(this.field_186514_a), MathHelper.func_76141_d(this.field_186515_b));
   }

   public float func_186507_b(Random var1) {
      return MathHelper.func_151240_a(var1, this.field_186514_a, this.field_186515_b);
   }

   public boolean func_186510_a(int var1) {
      return (float)var1 <= this.field_186515_b && (float)var1 >= this.field_186514_a;
   }

   public static class Serializer implements JsonDeserializer<RandomValueRange>, JsonSerializer<RandomValueRange> {
      public Serializer() {
         super();
      }

      public RandomValueRange deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (JsonUtils.func_188175_b(var1)) {
            return new RandomValueRange(JsonUtils.func_151220_d(var1, "value"));
         } else {
            JsonObject var4 = JsonUtils.func_151210_l(var1, "value");
            float var5 = JsonUtils.func_151217_k(var4, "min");
            float var6 = JsonUtils.func_151217_k(var4, "max");
            return new RandomValueRange(var5, var6);
         }
      }

      public JsonElement serialize(RandomValueRange var1, Type var2, JsonSerializationContext var3) {
         if (var1.field_186514_a == var1.field_186515_b) {
            return new JsonPrimitive(var1.field_186514_a);
         } else {
            JsonObject var4 = new JsonObject();
            var4.addProperty("min", var1.field_186514_a);
            var4.addProperty("max", var1.field_186515_b);
            return var4;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((RandomValueRange)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
