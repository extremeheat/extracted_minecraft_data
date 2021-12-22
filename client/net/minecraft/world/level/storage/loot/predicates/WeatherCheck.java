package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;

public class WeatherCheck implements LootItemCondition {
   @Nullable
   final Boolean isRaining;
   @Nullable
   final Boolean isThundering;

   WeatherCheck(@Nullable Boolean var1, @Nullable Boolean var2) {
      super();
      this.isRaining = var1;
      this.isThundering = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.WEATHER_CHECK;
   }

   public boolean test(LootContext var1) {
      ServerLevel var2 = var1.getLevel();
      if (this.isRaining != null && this.isRaining != var2.isRaining()) {
         return false;
      } else {
         return this.isThundering == null || this.isThundering == var2.isThundering();
      }
   }

   public static WeatherCheck.Builder weather() {
      return new WeatherCheck.Builder();
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Builder implements LootItemCondition.Builder {
      @Nullable
      private Boolean isRaining;
      @Nullable
      private Boolean isThundering;

      public Builder() {
         super();
      }

      public WeatherCheck.Builder setRaining(@Nullable Boolean var1) {
         this.isRaining = var1;
         return this;
      }

      public WeatherCheck.Builder setThundering(@Nullable Boolean var1) {
         this.isThundering = var1;
         return this;
      }

      public WeatherCheck build() {
         return new WeatherCheck(this.isRaining, this.isThundering);
      }

      // $FF: synthetic method
      public LootItemCondition build() {
         return this.build();
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<WeatherCheck> {
      public Serializer() {
         super();
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
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
