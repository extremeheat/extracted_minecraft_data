package net.minecraft.world.level.storage.loot;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import org.jspecify.annotations.Nullable;

public sealed interface IntRangePredicate extends Validatable {
   Codec<Point> POINT_CODEC = ContextIntProviders.CODEC.xmap(Point::new, Point::value);
   Codec<Line> LINE_CODEC = RecordCodecBuilder.create((i) -> i.group(ContextIntProviders.CODEC.optionalFieldOf("min").forGetter((r) -> r.min), ContextIntProviders.CODEC.optionalFieldOf("max").forGetter((r) -> r.max)).apply(i, Line::new));
   Codec<IntRangePredicate> CODEC = Codec.either(POINT_CODEC, LINE_CODEC).xmap(Either::unwrap, (range) -> {
      Objects.requireNonNull(range);
      int index$1 = 0;
      Either var10000;
      //$FF: index$1->value
      //0->net/minecraft/world/level/storage/loot/IntRangePredicate$Point
      //1->net/minecraft/world/level/storage/loot/IntRangePredicate$Line
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

   static IntRangePredicate exact(final int value) {
      return new Point(ContextIntProviders.exactly(value));
   }

   static IntRangePredicate range(final int min, final int max) {
      return new Line(Optional.of(ContextIntProviders.exactly(min)), Optional.of(ContextIntProviders.exactly(max)));
   }

   static IntRangePredicate lowerBound(final int value) {
      return new Line(Optional.of(ContextIntProviders.exactly(value)), Optional.empty());
   }

   static IntRangePredicate upperBound(final int value) {
      return new Line(Optional.empty(), Optional.of(ContextIntProviders.exactly(value)));
   }

   boolean test(LootContext context, int input);

   default boolean test(final LootContext context, final ContextIntProvider input) {
      return this.test(context, input.getInt(context));
   }

   public static record Point(Holder<ContextIntProvider> value) implements IntRangePredicate {
      public Point {
         super();
      }

      public void validate(final ValidationContext context) {
         Validatable.validateHolder(context, this.value);
      }

      private int computeValue(final LootContext context) {
         return ((ContextIntProvider)this.value.value()).getInt(context);
      }

      public boolean test(final LootContext context, final int input) {
         return this.computeValue(context) == input;
      }
   }

   public static final class Line implements IntRangePredicate {
      private final Optional<Holder<ContextIntProvider>> min;
      private final Optional<Holder<ContextIntProvider>> max;
      private final @Nullable IntChecker predicate;

      private Line(final Optional<Holder<ContextIntProvider>> min, final Optional<Holder<ContextIntProvider>> max) {
         super();
         this.min = min;
         this.max = max;
         this.predicate = createPredicate((Holder)min.orElse((Object)null), (Holder)max.orElse((Object)null));
      }

      private static @Nullable IntChecker createPredicate(final @Nullable Holder<ContextIntProvider> min, final @Nullable Holder<ContextIntProvider> max) {
         if (min == null) {
            return max == null ? null : (context, input) -> input <= ((ContextIntProvider)max.value()).getInt(context);
         } else {
            return max == null ? (context, input) -> input >= ((ContextIntProvider)min.value()).getInt(context) : (context, input) -> input >= ((ContextIntProvider)min.value()).getInt(context) && input <= ((ContextIntProvider)max.value()).getInt(context);
         }
      }

      public void validate(final ValidationContext context) {
         Validatable.validateHolder(context, "min", this.min);
         Validatable.validateHolder(context, "max", this.max);
      }

      public boolean test(final LootContext context, final int input) {
         return this.predicate == null || this.predicate.test(context, input);
      }

      public boolean test(final LootContext context, final ContextIntProvider input) {
         return this.predicate == null || this.predicate.test(context, input.getInt(context));
      }

      @FunctionalInterface
      private interface IntChecker {
         boolean test(LootContext context, int value);
      }
   }
}
