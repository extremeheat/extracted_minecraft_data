package net.minecraft.world.level.storage.loot;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jspecify.annotations.Nullable;

public class IntRange implements LootContextUser {
   private static final Codec<IntRange> RECORD_CODEC = RecordCodecBuilder.create((i) -> i.group(NumberProviders.DIRECT_CODEC.optionalFieldOf("min").forGetter((r) -> Optional.ofNullable(r.min)), NumberProviders.DIRECT_CODEC.optionalFieldOf("max").forGetter((r) -> Optional.ofNullable(r.max))).apply(i, IntRange::new));
   public static final Codec<IntRange> CODEC;
   private final @Nullable NumberProvider min;
   private final @Nullable NumberProvider max;
   private final IntLimiter limiter;
   private final IntChecker predicate;

   public void validate(final ValidationContext context) {
      LootContextUser.super.validate(context);
      if (this.min != null) {
         Validatable.validate(context, "min", this.min);
      }

      if (this.max != null) {
         Validatable.validate(context, "max", this.max);
      }

   }

   private IntRange(final Optional<NumberProvider> min, final Optional<NumberProvider> max) {
      this((NumberProvider)min.orElse((Object)null), (NumberProvider)max.orElse((Object)null));
   }

   private IntRange(final @Nullable NumberProvider min, final @Nullable NumberProvider max) {
      super();
      this.min = min;
      this.max = max;
      if (min == null) {
         if (max == null) {
            this.limiter = (context, value) -> value;
            this.predicate = (context, value) -> true;
         } else {
            this.limiter = (context, value) -> Math.min(max.getInt(context), value);
            this.predicate = (context, value) -> value <= max.getInt(context);
         }
      } else if (max == null) {
         this.limiter = (context, value) -> Math.max(min.getInt(context), value);
         this.predicate = (context, value) -> value >= min.getInt(context);
      } else {
         this.limiter = (context, value) -> Mth.clamp(value, min.getInt(context), max.getInt(context));
         this.predicate = (context, value) -> value >= min.getInt(context) && value <= max.getInt(context);
      }

   }

   public static IntRange exact(final int value) {
      ConstantValue c = ConstantValue.exactly((float)value);
      return new IntRange(Optional.of(c), Optional.of(c));
   }

   public static IntRange range(final int min, final int max) {
      return new IntRange(Optional.of(ConstantValue.exactly((float)min)), Optional.of(ConstantValue.exactly((float)max)));
   }

   public static IntRange lowerBound(final int value) {
      return new IntRange(Optional.of(ConstantValue.exactly((float)value)), Optional.empty());
   }

   public static IntRange upperBound(final int value) {
      return new IntRange(Optional.empty(), Optional.of(ConstantValue.exactly((float)value)));
   }

   public int clamp(final LootContext context, final int value) {
      return this.limiter.apply(context, value);
   }

   public boolean test(final LootContext context, final int value) {
      return this.predicate.test(context, value);
   }

   private OptionalInt unpackExact() {
      if (Objects.equals(this.min, this.max)) {
         NumberProvider var2 = this.min;
         if (var2 instanceof ConstantValue) {
            ConstantValue constant = (ConstantValue)var2;
            if (Math.floor((double)constant.value()) == (double)constant.value()) {
               return OptionalInt.of((int)constant.value());
            }
         }
      }

      return OptionalInt.empty();
   }

   static {
      CODEC = Codec.either(Codec.INT, RECORD_CODEC).xmap((e) -> (IntRange)e.map(IntRange::exact, Function.identity()), (range) -> {
         OptionalInt exact = range.unpackExact();
         return exact.isPresent() ? Either.left(exact.getAsInt()) : Either.right(range);
      });
   }

   @FunctionalInterface
   private interface IntChecker {
      boolean test(LootContext context, int value);
   }

   @FunctionalInterface
   private interface IntLimiter {
      int apply(LootContext context, int value);
   }
}
