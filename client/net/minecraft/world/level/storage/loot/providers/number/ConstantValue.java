package net.minecraft.world.level.storage.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;

public final class ConstantValue implements NumberProvider {
   final float value;

   ConstantValue(float var1) {
      super();
      this.value = var1;
   }

   @Override
   public LootNumberProviderType getType() {
      return NumberProviders.CONSTANT;
   }

   @Override
   public float getFloat(LootContext var1) {
      return this.value;
   }

   public static ConstantValue exactly(float var0) {
      return new ConstantValue(var0);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         return Float.compare(((ConstantValue)var1).value, this.value) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.value != 0.0F ? Float.floatToIntBits(this.value) : 0;
   }

   public static class InlineSerializer implements GsonAdapterFactory.InlineSerializer<ConstantValue> {
      public InlineSerializer() {
         super();
      }

      public JsonElement serialize(ConstantValue var1, JsonSerializationContext var2) {
         return new JsonPrimitive(var1.value);
      }

      public ConstantValue deserialize(JsonElement var1, JsonDeserializationContext var2) {
         return new ConstantValue(GsonHelper.convertToFloat(var1, "value"));
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ConstantValue> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, ConstantValue var2, JsonSerializationContext var3) {
         var1.addProperty("value", var2.value);
      }

      public ConstantValue deserialize(JsonObject var1, JsonDeserializationContext var2) {
         float var3 = GsonHelper.getAsFloat(var1, "value");
         return new ConstantValue(var3);
      }
   }
}
