package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;

public class WeatherCheck implements LootItemCondition {
   @Nullable
   private final Boolean isRaining;
   @Nullable
   private final Boolean isThundering;

   private WeatherCheck(@Nullable Boolean var1, @Nullable Boolean var2) {
      super();
      this.isRaining = var1;
      this.isThundering = var2;
   }

   public boolean test(LootContext var1) {
      ServerLevel var2 = var1.getLevel();
      if (this.isRaining != null && this.isRaining != var2.isRaining()) {
         return false;
      } else {
         return this.isThundering == null || this.isThundering == var2.isThundering();
      }
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   WeatherCheck(Boolean var1, Boolean var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemCondition.Serializer<WeatherCheck> {
      public Serializer() {
         super(new ResourceLocation("weather_check"), WeatherCheck.class);
      }

      public void serialize(JsonObject var1, WeatherCheck var2, JsonSerializationContext var3) {
         var1.addProperty("raining", var2.isRaining);
         var1.addProperty("thundering", var2.isThundering);
      }

      public WeatherCheck deserialize(JsonObject var1, JsonDeserializationContext var2) {
         Boolean var3 = var1.has("raining") ? GsonHelper.getAsBoolean(var1, "raining") : null;
         Boolean var4 = var1.has("thundering") ? GsonHelper.getAsBoolean(var1, "thundering") : null;
         return new WeatherCheck(var3, var4);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
