package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public record LightPredicate(MinMaxBounds.Ints b) {
   private final MinMaxBounds.Ints composite;
   public static final Codec<LightPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("light", MinMaxBounds.Ints.ANY).forGetter(LightPredicate::composite))
            .apply(var0, LightPredicate::new)
   );

   public LightPredicate(MinMaxBounds.Ints var1) {
      super();
      this.composite = var1;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (!var1.isLoaded(var2)) {
         return false;
      } else {
         return this.composite.matches(var1.getMaxLocalRawBrightness(var2));
      }
   }

   public static class Builder {
      private MinMaxBounds.Ints composite = MinMaxBounds.Ints.ANY;

      public Builder() {
         super();
      }

      public static LightPredicate.Builder light() {
         return new LightPredicate.Builder();
      }

      public LightPredicate.Builder setComposite(MinMaxBounds.Ints var1) {
         this.composite = var1;
         return this;
      }

      public LightPredicate build() {
         return new LightPredicate(this.composite);
      }
   }
}
