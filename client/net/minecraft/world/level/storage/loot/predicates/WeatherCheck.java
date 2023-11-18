package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.loot.LootContext;

public record WeatherCheck(Optional<Boolean> b, Optional<Boolean> c) implements LootItemCondition {
   private final Optional<Boolean> isRaining;
   private final Optional<Boolean> isThundering;
   public static final Codec<WeatherCheck> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(Codec.BOOL, "raining").forGetter(WeatherCheck::isRaining),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "thundering").forGetter(WeatherCheck::isThundering)
            )
            .apply(var0, WeatherCheck::new)
   );

   public WeatherCheck(Optional<Boolean> var1, Optional<Boolean> var2) {
      super();
      this.isRaining = var1;
      this.isThundering = var2;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.WEATHER_CHECK;
   }

   public boolean test(LootContext var1) {
      ServerLevel var2 = var1.getLevel();
      if (this.isRaining.isPresent() && this.isRaining.get() != var2.isRaining()) {
         return false;
      } else {
         return !this.isThundering.isPresent() || this.isThundering.get() == var2.isThundering();
      }
   }

   public static WeatherCheck.Builder weather() {
      return new WeatherCheck.Builder();
   }

   public static class Builder implements LootItemCondition.Builder {
      private Optional<Boolean> isRaining = Optional.empty();
      private Optional<Boolean> isThundering = Optional.empty();

      public Builder() {
         super();
      }

      public WeatherCheck.Builder setRaining(boolean var1) {
         this.isRaining = Optional.of(var1);
         return this;
      }

      public WeatherCheck.Builder setThundering(boolean var1) {
         this.isThundering = Optional.of(var1);
         return this;
      }

      public WeatherCheck build() {
         return new WeatherCheck(this.isRaining, this.isThundering);
      }
   }
}
