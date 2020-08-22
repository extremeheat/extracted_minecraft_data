package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public final class ConstantIntValue implements RandomIntGenerator {
   private final int value;

   public ConstantIntValue(int var1) {
      this.value = var1;
   }

   public int getInt(Random var1) {
      return this.value;
   }

   public ResourceLocation getType() {
      return CONSTANT;
   }

   public static ConstantIntValue exactly(int var0) {
      return new ConstantIntValue(var0);
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public ConstantIntValue deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return new ConstantIntValue(GsonHelper.convertToInt(var1, "value"));
      }

      public JsonElement serialize(ConstantIntValue var1, Type var2, JsonSerializationContext var3) {
         return new JsonPrimitive(var1.value);
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((ConstantIntValue)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
