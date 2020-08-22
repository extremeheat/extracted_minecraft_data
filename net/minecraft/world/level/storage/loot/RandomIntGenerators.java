package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class RandomIntGenerators {
   private static final Map GENERATORS = Maps.newHashMap();

   public static RandomIntGenerator deserialize(JsonElement var0, JsonDeserializationContext var1) throws JsonParseException {
      if (var0.isJsonPrimitive()) {
         return (RandomIntGenerator)var1.deserialize(var0, ConstantIntValue.class);
      } else {
         JsonObject var2 = var0.getAsJsonObject();
         String var3 = GsonHelper.getAsString(var2, "type", RandomIntGenerator.UNIFORM.toString());
         Class var4 = (Class)GENERATORS.get(new ResourceLocation(var3));
         if (var4 == null) {
            throw new JsonParseException("Unknown generator: " + var3);
         } else {
            return (RandomIntGenerator)var1.deserialize(var2, var4);
         }
      }
   }

   public static JsonElement serialize(RandomIntGenerator var0, JsonSerializationContext var1) {
      JsonElement var2 = var1.serialize(var0);
      if (var2.isJsonObject()) {
         var2.getAsJsonObject().addProperty("type", var0.getType().toString());
      }

      return var2;
   }

   static {
      GENERATORS.put(RandomIntGenerator.UNIFORM, RandomValueBounds.class);
      GENERATORS.put(RandomIntGenerator.BINOMIAL, BinomialDistributionGenerator.class);
      GENERATORS.put(RandomIntGenerator.CONSTANT, ConstantIntValue.class);
   }
}
