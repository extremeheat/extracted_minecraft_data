package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import org.jspecify.annotations.Nullable;

public class IntLimit implements Validatable {
   public static final Codec<IntLimit> CODEC = RecordCodecBuilder.create((i) -> i.group(ContextIntProviders.CODEC.optionalFieldOf("min").forGetter((r) -> r.min), ContextIntProviders.CODEC.optionalFieldOf("max").forGetter((r) -> r.max)).apply(i, IntLimit::new));
   private final Optional<Holder<ContextIntProvider>> min;
   private final Optional<Holder<ContextIntProvider>> max;
   private final IntLimiter limiter;

   private IntLimit(final Optional<Holder<ContextIntProvider>> min, final Optional<Holder<ContextIntProvider>> max) {
      super();
      this.min = min;
      this.max = max;
      this.limiter = createLimiter((Holder)min.orElse((Object)null), (Holder)max.orElse((Object)null));
   }

   private static IntLimiter createLimiter(final @Nullable Holder<ContextIntProvider> min, final @Nullable Holder<ContextIntProvider> max) {
      if (min == null) {
         return max == null ? (var0, input) -> input : (context, input) -> Math.min(((ContextIntProvider)max.value()).getInt(context), input);
      } else {
         return max == null ? (context, input) -> Math.max(((ContextIntProvider)min.value()).getInt(context), input) : (context, input) -> Mth.clamp(input, ((ContextIntProvider)min.value()).getInt(context), ((ContextIntProvider)max.value()).getInt(context));
      }
   }

   public static IntLimit range(final int min, final int max) {
      return new IntLimit(Optional.of(ContextIntProviders.exactly(min)), Optional.of(ContextIntProviders.exactly(max)));
   }

   public static IntLimit lowerBound(final int value) {
      return new IntLimit(Optional.of(ContextIntProviders.exactly(value)), Optional.empty());
   }

   public static IntLimit upperBound(final int value) {
      return new IntLimit(Optional.empty(), Optional.of(ContextIntProviders.exactly(value)));
   }

   public void validate(final ValidationContext context) {
      Validatable.validateHolder(context, "min", this.min);
      Validatable.validateHolder(context, "max", this.max);
   }

   public int clamp(final LootContext context, final int input) {
      return this.limiter.apply(context, input);
   }

   @FunctionalInterface
   private interface IntLimiter {
      int apply(LootContext context, int value);
   }
}
