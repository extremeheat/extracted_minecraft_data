package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public record LightPredicate(MinMaxBounds.Ints composite) {
   public static final Codec<LightPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("light", MinMaxBounds.Ints.ANY).forGetter(LightPredicate::composite)).apply(var0, LightPredicate::new);
   });

   public LightPredicate(MinMaxBounds.Ints composite) {
      super();
      this.composite = composite;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (!var1.isLoaded(var2)) {
         return false;
      } else {
         return this.composite.matches(var1.getMaxLocalRawBrightness(var2));
      }
   }

   public MinMaxBounds.Ints composite() {
      return this.composite;
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

      public Builder setComposite(MinMaxBounds.Ints var1) {
         this.composite = var1;
         return this;
      }

      public LightPredicate build() {
         return new LightPredicate(this.composite);
      }
   }
}
