package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public record LightPredicate(MinMaxBounds.Ints composite) {
   public static final Codec<LightPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("light", MinMaxBounds.Ints.ANY).forGetter(LightPredicate::composite)).apply(i, LightPredicate::new));

   public LightPredicate {
      super();
   }

   public boolean matches(final ServerLevel level, final BlockPos pos) {
      if (!level.isLoaded(pos)) {
         return false;
      } else {
         return this.composite.matches(level.getMaxLocalRawBrightness(pos));
      }
   }

   public static class Builder {
      private MinMaxBounds.Ints composite;

      public Builder() {
         super();
         this.composite = MinMaxBounds.Ints.ANY;
      }

      public static Builder light() {
         return new Builder();
      }

      public Builder setComposite(final MinMaxBounds.Ints composite) {
         this.composite = composite;
         return this;
      }

      public LightPredicate build() {
         return new LightPredicate(this.composite);
      }
   }
}
