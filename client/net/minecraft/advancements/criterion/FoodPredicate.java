package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.food.FoodData;

public record FoodPredicate(MinMaxBounds.Ints level, MinMaxBounds.Doubles saturation) {
   public static final FoodPredicate ANY;
   public static final Codec<FoodPredicate> CODEC;

   public FoodPredicate {
      super();
   }

   public boolean matches(final FoodData food) {
      if (!this.level.matches(food.getFoodLevel())) {
         return false;
      } else {
         return this.saturation.matches((double)food.getSaturationLevel());
      }
   }

   static {
      ANY = new FoodPredicate(MinMaxBounds.Ints.ANY, MinMaxBounds.Doubles.ANY);
      CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(FoodPredicate::level), MinMaxBounds.Doubles.CODEC.optionalFieldOf("saturation", MinMaxBounds.Doubles.ANY).forGetter(FoodPredicate::saturation)).apply(i, FoodPredicate::new));
   }

   public static class Builder {
      private MinMaxBounds.Ints level;
      private MinMaxBounds.Doubles saturation;

      public Builder() {
         super();
         this.level = MinMaxBounds.Ints.ANY;
         this.saturation = MinMaxBounds.Doubles.ANY;
      }

      public Builder withLevel(final MinMaxBounds.Ints level) {
         this.level = level;
         return this;
      }

      public Builder withSaturation(final MinMaxBounds.Doubles saturation) {
         this.saturation = saturation;
         return this;
      }

      public static Builder food() {
         return new Builder();
      }

      public FoodPredicate build() {
         return new FoodPredicate(this.level, this.saturation);
      }
   }
}
