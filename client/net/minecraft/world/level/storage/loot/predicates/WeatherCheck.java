package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;

public record WeatherCheck(Optional<Boolean> isRaining, Optional<Boolean> isThundering) implements LootItemCondition {
   public static final MapCodec<WeatherCheck> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.BOOL.optionalFieldOf("raining").forGetter(WeatherCheck::isRaining), Codec.BOOL.optionalFieldOf("thundering").forGetter(WeatherCheck::isThundering)).apply(var0, WeatherCheck::new);
   });

   public WeatherCheck(Optional<Boolean> var1, Optional<Boolean> var2) {
      super();
      this.isRaining = var1;
      this.isThundering = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.WEATHER_CHECK;
   }

   public boolean test(LootContext var1) {
      ServerLevel var2 = var1.getLevel();
      if (this.isRaining.isPresent() && (Boolean)this.isRaining.get() != var2.isRaining()) {
         return false;
      } else {
         return !this.isThundering.isPresent() || (Boolean)this.isThundering.get() == var2.isThundering();
      }
   }

   public static Builder weather() {
      return new Builder();
   }

   public Optional<Boolean> isRaining() {
      return this.isRaining;
   }

   public Optional<Boolean> isThundering() {
      return this.isThundering;
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Builder implements LootItemCondition.Builder {
      private Optional<Boolean> isRaining = Optional.empty();
      private Optional<Boolean> isThundering = Optional.empty();

      public Builder() {
         super();
      }

      public Builder setRaining(boolean var1) {
         this.isRaining = Optional.of(var1);
         return this;
      }

      public Builder setThundering(boolean var1) {
         this.isThundering = Optional.of(var1);
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
}
