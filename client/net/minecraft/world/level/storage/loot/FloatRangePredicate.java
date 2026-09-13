package net.minecraft.world.level.storage.loot;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import org.jspecify.annotations.Nullable;

public sealed interface FloatRangePredicate extends Validatable {
   Codec<Point> POINT_CODEC = ContextFloatProviders.CODEC.xmap(Point::new, Point::value);
   Codec<Line> LINE_CODEC = RecordCodecBuilder.create((i) -> i.group(ContextFloatProviders.CODEC.optionalFieldOf("min").forGetter((r) -> r.min), ContextFloatProviders.CODEC.optionalFieldOf("max").forGetter((r) -> r.max)).apply(i, Line::new));
   Codec<FloatRangePredicate> CODEC = Codec.either(POINT_CODEC, LINE_CODEC).xmap(Either::unwrap, (range) -> {
      Objects.requireNonNull(range);
      int index$1 = 0;
      Either var10000;
      //$FF: index$1->value
      //0->net/minecraft/world/level/storage/loot/FloatRangePredicate$Point
      //1->net/minecraft/world/level/storage/loot/FloatRangePredicate$Line
      switch (range.typeSwitch<invokedynamic>(range, index$1)) {
         case 0:
            Point point = (Point)range;
            var10000 = Either.left(point);
            break;
         case 1:
            Line line = (Line)range;
            var10000 = Either.right(line);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   });

   static FloatRangePredicate exact(final float value) {
      return new Point(ContextFloatProviders.exactly(value));
   }

   static FloatRangePredicate range(final float min, final float max) {
      return new Line(Optional.of(ContextFloatProviders.exactly(min)), Optional.of(ContextFloatProviders.exactly(max)));
   }

   static FloatRangePredicate lowerBound(final float value) {
      return new Line(Optional.of(ContextFloatProviders.exactly(value)), Optional.empty());
   }

   static FloatRangePredicate upperBound(final float value) {
      return new Line(Optional.empty(), Optional.of(ContextFloatProviders.exactly(value)));
   }

   boolean test(LootContext context, float input);

   default boolean test(final LootContext context, final ContextFloatProvider input) {
      return this.test(context, input.getFloat(context));
   }

   public static record Point(Holder<ContextFloatProvider> value) implements FloatRangePredicate {
      public Point {
         super();
      }

      public void validate(final ValidationContext context) {
         Validatable.validateHolder(context, this.value);
      }

      private float computeValue(final LootContext context) {
         return ((ContextFloatProvider)this.value.value()).getFloat(context);
      }

      public boolean test(final LootContext context, final float input) {
         return this.computeValue(context) == input;
      }
   }

   public static final class Line implements FloatRangePredicate {
      private final Optional<Holder<ContextFloatProvider>> min;
      private final Optional<Holder<ContextFloatProvider>> max;
      private final @Nullable FloatChecker predicate;

      private Line(final Optional<Holder<ContextFloatProvider>> min, final Optional<Holder<ContextFloatProvider>> max) {
         super();
         this.min = min;
         this.max = max;
         this.predicate = createPredicate((Holder)min.orElse((Object)null), (Holder)max.orElse((Object)null));
      }

      private static @Nullable FloatChecker createPredicate(final @Nullable Holder<ContextFloatProvider> min, final @Nullable Holder<ContextFloatProvider> max) {
         if (min == null) {
            return max == null ? null : (context, input) -> input <= ((ContextFloatProvider)max.value()).getFloat(context);
         } else {
            return max == null ? (context, input) -> input >= ((ContextFloatProvider)min.value()).getFloat(context) : (context, input) -> input >= ((ContextFloatProvider)min.value()).getFloat(context) && input <= ((ContextFloatProvider)max.value()).getFloat(context);
         }
      }

      public void validate(final ValidationContext context) {
         Validatable.validateHolder(context, "min", this.min);
         Validatable.validateHolder(context, "max", this.max);
      }

      public boolean test(final LootContext context, final float input) {
         return this.predicate == null || this.predicate.test(context, input);
      }

      public boolean test(final LootContext context, final ContextFloatProvider input) {
         return this.predicate == null || this.predicate.test(context, input.getFloat(context));
      }

      @FunctionalInterface
      private interface FloatChecker {
         boolean test(LootContext context, float value);
      }
   }
}
