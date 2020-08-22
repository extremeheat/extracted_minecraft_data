package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public final class BinomialDistributionGenerator implements RandomIntGenerator {
   private final int n;
   private final float p;

   public BinomialDistributionGenerator(int var1, float var2) {
      this.n = var1;
      this.p = var2;
   }

   public int getInt(Random var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.n; ++var3) {
         if (var1.nextFloat() < this.p) {
            ++var2;
         }
      }

      return var2;
   }

   public static BinomialDistributionGenerator binomial(int var0, float var1) {
      return new BinomialDistributionGenerator(var0, var1);
   }

   public ResourceLocation getType() {
      return BINOMIAL;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public BinomialDistributionGenerator deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "value");
         int var5 = GsonHelper.getAsInt(var4, "n");
         float var6 = GsonHelper.getAsFloat(var4, "p");
         return new BinomialDistributionGenerator(var5, var6);
      }

      public JsonElement serialize(BinomialDistributionGenerator var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("n", var1.n);
         var4.addProperty("p", var1.p);
         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((BinomialDistributionGenerator)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
