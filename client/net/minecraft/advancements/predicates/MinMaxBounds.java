package net.minecraft.advancements.predicates;

import com.google.common.collect.Range;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public interface MinMaxBounds<T extends Number & Comparable<T>> {
   SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(Component.translatable("argument.range.empty"));
   SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(Component.translatable("argument.range.swapped"));

   Bounds<T> bounds();

   default Optional<T> min() {
      return this.bounds().min;
   }

   default Optional<T> max() {
      return this.bounds().max;
   }

   default boolean isAny() {
      return this.bounds().isAny();
   }

   static <V extends Number & Comparable<V>, B extends MinMaxBounds<V>> Function<B, DataResult<B>> validateContainedInRange(final MinMaxBounds<V> allowed) {
      Range<V> allowedRange = allowed.bounds().asRange();
      return (target) -> {
         Range<V> selfAsRange = target.bounds().asRange();
         return !allowedRange.encloses(selfAsRange) ? DataResult.error(() -> {
            String var10000 = String.valueOf(allowedRange);
            return "Range must be within " + var10000 + ", but was " + String.valueOf(selfAsRange);
         }) : DataResult.success(target);
      };
   }

   public static record Ints(Bounds<Integer> bounds, Bounds<Long> boundsSqr) implements MinMaxBounds<Integer> {
      public static final Ints ANY = new Ints(MinMaxBounds.Bounds.any());
      public static final Codec<Ints> CODEC;
      public static final StreamCodec<ByteBuf, Ints> STREAM_CODEC;

      private Ints(final Bounds<Integer> bounds) {
         this(bounds, bounds.map((i) -> Mth.square(i.longValue())));
      }

      public Ints {
         super();
      }

      public static Ints exactly(final int value) {
         return new Ints(MinMaxBounds.Bounds.exactly(value));
      }

      public static Ints between(final int min, final int max) {
         return new Ints(MinMaxBounds.Bounds.between(min, max));
      }

      public static Ints atLeast(final int value) {
         return new Ints(MinMaxBounds.Bounds.atLeast(value));
      }

      public static Ints atMost(final int value) {
         return new Ints(MinMaxBounds.Bounds.atMost(value));
      }

      public boolean matches(final int value) {
         if (this.bounds.min.isPresent() && (Integer)this.bounds.min.get() > value) {
            return false;
         } else {
            return this.bounds.max.isEmpty() || (Integer)this.bounds.max.get() >= value;
         }
      }

      public boolean matchesSqr(final long valueSqr) {
         if (this.boundsSqr.min.isPresent() && (Long)this.boundsSqr.min.get() > valueSqr) {
            return false;
         } else {
            return this.boundsSqr.max.isEmpty() || (Long)this.boundsSqr.max.get() >= valueSqr;
         }
      }

      public static Ints fromReader(final StringReader reader) throws CommandSyntaxException {
         int start = reader.getCursor();
         Function var10001 = Integer::parseInt;
         BuiltInExceptionProvider var10002 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10002);
         Bounds<Integer> bounds = MinMaxBounds.Bounds.<Integer>fromReader(reader, var10001, var10002::readerInvalidInt);
         if (bounds.areSwapped()) {
            reader.setCursor(start);
            throw ERROR_SWAPPED.createWithContext(reader);
         } else {
            return new Ints(bounds);
         }
      }

      static {
         CODEC = MinMaxBounds.Bounds.createCodec(Codec.INT).validate(Bounds::validateSwappedBoundsInCodec).xmap(Ints::new, Ints::bounds);
         STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.INT).map(Ints::new, Ints::bounds);
      }
   }

   public static record Doubles(Bounds<Double> bounds, Bounds<Double> boundsSqr) implements MinMaxBounds<Double> {
      public static final Doubles ANY = new Doubles(MinMaxBounds.Bounds.any());
      public static final Codec<Doubles> CODEC;
      public static final StreamCodec<ByteBuf, Doubles> STREAM_CODEC;

      private Doubles(final Bounds<Double> bounds) {
         this(bounds, bounds.map(Mth::square));
      }

      public Doubles {
         super();
      }

      public static Doubles exactly(final double value) {
         return new Doubles(MinMaxBounds.Bounds.exactly(value));
      }

      public static Doubles between(final double min, final double max) {
         return new Doubles(MinMaxBounds.Bounds.between(min, max));
      }

      public static Doubles atLeast(final double value) {
         return new Doubles(MinMaxBounds.Bounds.atLeast(value));
      }

      public static Doubles atMost(final double value) {
         return new Doubles(MinMaxBounds.Bounds.atMost(value));
      }

      public boolean matches(final double value) {
         if (this.bounds.min.isPresent() && (Double)this.bounds.min.get() > value) {
            return false;
         } else {
            return this.bounds.max.isEmpty() || !((Double)this.bounds.max.get() < value);
         }
      }

      public boolean matchesSqr(final double valueSqr) {
         if (this.boundsSqr.min.isPresent() && (Double)this.boundsSqr.min.get() > valueSqr) {
            return false;
         } else {
            return this.boundsSqr.max.isEmpty() || !((Double)this.boundsSqr.max.get() < valueSqr);
         }
      }

      public static Doubles fromReader(final StringReader reader) throws CommandSyntaxException {
         int start = reader.getCursor();
         Function var10001 = Double::parseDouble;
         BuiltInExceptionProvider var10002 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10002);
         Bounds<Double> bounds = MinMaxBounds.Bounds.<Double>fromReader(reader, var10001, var10002::readerInvalidDouble);
         if (bounds.areSwapped()) {
            reader.setCursor(start);
            throw ERROR_SWAPPED.createWithContext(reader);
         } else {
            return new Doubles(bounds);
         }
      }

      static {
         CODEC = MinMaxBounds.Bounds.createCodec(Codec.DOUBLE).validate(Bounds::validateSwappedBoundsInCodec).xmap(Doubles::new, Doubles::bounds);
         STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.DOUBLE).map(Doubles::new, Doubles::bounds);
      }
   }

   public static record FloatDegrees(Bounds<Float> bounds) implements MinMaxBounds<Float> {
      public static final FloatDegrees ANY = new FloatDegrees(MinMaxBounds.Bounds.any());
      public static final Codec<FloatDegrees> CODEC;
      public static final StreamCodec<ByteBuf, FloatDegrees> STREAM_CODEC;

      public FloatDegrees {
         super();
      }

      public static FloatDegrees fromReader(final StringReader reader) throws CommandSyntaxException {
         Function var10001 = Float::parseFloat;
         BuiltInExceptionProvider var10002 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10002);
         Bounds<Float> bounds = MinMaxBounds.Bounds.<Float>fromReader(reader, var10001, var10002::readerInvalidFloat);
         return new FloatDegrees(bounds);
      }

      static {
         CODEC = MinMaxBounds.Bounds.createCodec(Codec.FLOAT).xmap(FloatDegrees::new, FloatDegrees::bounds);
         STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.FLOAT).map(FloatDegrees::new, FloatDegrees::bounds);
      }
   }

   public static record Bounds<T extends Number & Comparable<T>>(Optional<T> min, Optional<T> max) {
      public Bounds {
         super();
      }

      public boolean isAny() {
         return this.min().isEmpty() && this.max().isEmpty();
      }

      public DataResult<Bounds<T>> validateSwappedBoundsInCodec() {
         return this.areSwapped() ? DataResult.error(() -> {
            String var10000 = String.valueOf(this.min());
            return "Swapped bounds in range: " + var10000 + " is higher than " + String.valueOf(this.max());
         }) : DataResult.success(this);
      }

      public boolean areSwapped() {
         return this.min.isPresent() && this.max.isPresent() && ((Comparable)((Number)this.min.get())).compareTo((Number)this.max.get()) > 0;
      }

      public Range<T> asRange() {
         if (this.min.isPresent()) {
            return this.max.isPresent() ? Range.closed((Number)this.min.get(), (Number)this.max.get()) : Range.atLeast((Number)this.min.get());
         } else {
            return this.max.isPresent() ? Range.atMost((Number)this.max.get()) : Range.all();
         }
      }

      public Optional<T> asPoint() {
         Optional<T> min = this.min();
         Optional<T> max = this.max();
         return min.equals(max) ? min : Optional.empty();
      }

      public static <T extends Number & Comparable<T>> Bounds<T> any() {
         return new Bounds<T>(Optional.empty(), Optional.empty());
      }

      public static <T extends Number & Comparable<T>> Bounds<T> exactly(final T value) {
         Optional<T> wrapped = Optional.of(value);
         return new Bounds<T>(wrapped, wrapped);
      }

      public static <T extends Number & Comparable<T>> Bounds<T> between(final T min, final T max) {
         return new Bounds<T>(Optional.of(min), Optional.of(max));
      }

      public static <T extends Number & Comparable<T>> Bounds<T> atLeast(final T value) {
         return new Bounds<T>(Optional.of(value), Optional.empty());
      }

      public static <T extends Number & Comparable<T>> Bounds<T> atMost(final T value) {
         return new Bounds<T>(Optional.empty(), Optional.of(value));
      }

      public <U extends Number & Comparable<U>> Bounds<U> map(final Function<T, U> mapper) {
         return new Bounds<U>(this.min.map(mapper), this.max.map(mapper));
      }

      static <T extends Number & Comparable<T>> Codec<Bounds<T>> createCodec(final Codec<T> numberCodec) {
         Codec<Bounds<T>> rangeCodec = RecordCodecBuilder.create((i) -> i.group(numberCodec.optionalFieldOf("min").forGetter(Bounds::min), numberCodec.optionalFieldOf("max").forGetter(Bounds::max)).apply(i, Bounds::new));
         return Codec.either(rangeCodec, numberCodec).xmap((either) -> (Bounds)either.map((v) -> v, (x$0) -> exactly(x$0)), (bounds) -> {
            Optional<T> point = bounds.asPoint();
            return point.isPresent() ? Either.right((Number)point.get()) : Either.left(bounds);
         });
      }

      static <B extends ByteBuf, T extends Number & Comparable<T>> StreamCodec<B, Bounds<T>> createStreamCodec(final StreamCodec<B, T> numberCodec) {
         return new StreamCodec<B, Bounds<T>>() {
            private static final int MIN_FLAG = 1;
            private static final int MAX_FLAG = 2;

            public Bounds<T> decode(final B input) {
               byte flags = input.readByte();
               Optional<T> min = (flags & 1) != 0 ? Optional.of((Number)numberCodec.decode(input)) : Optional.empty();
               Optional<T> max = (flags & 2) != 0 ? Optional.of((Number)numberCodec.decode(input)) : Optional.empty();
               return new Bounds<T>(min, max);
            }

            public void encode(final B output, final Bounds<T> value) {
               Optional<T> min = value.min();
               Optional<T> max = value.max();
               output.writeByte((min.isPresent() ? 1 : 0) | (max.isPresent() ? 2 : 0));
               min.ifPresent((v) -> numberCodec.encode(output, v));
               max.ifPresent((v) -> numberCodec.encode(output, v));
            }
         };
      }

      public static <T extends Number & Comparable<T>> Bounds<T> fromReader(final StringReader reader, final Function<String, T> converter, final Supplier<DynamicCommandExceptionType> parseExc) throws CommandSyntaxException {
         if (!reader.canRead()) {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
         } else {
            int start = reader.getCursor();

            try {
               Optional<T> min = readNumber(reader, converter, parseExc);
               Optional<T> max;
               if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
                  reader.skip();
                  reader.skip();
                  max = readNumber(reader, converter, parseExc);
               } else {
                  max = min;
               }

               if (min.isEmpty() && max.isEmpty()) {
                  throw MinMaxBounds.ERROR_EMPTY.createWithContext(reader);
               } else {
                  return new Bounds<T>(min, max);
               }
            } catch (CommandSyntaxException e) {
               reader.setCursor(start);
               throw new CommandSyntaxException(e.getType(), e.getRawMessage(), e.getInput(), start);
            }
         }
      }

      private static <T extends Number> Optional<T> readNumber(final StringReader reader, final Function<String, T> converter, final Supplier<DynamicCommandExceptionType> parseExc) throws CommandSyntaxException {
         int start = reader.getCursor();

         while(reader.canRead() && isAllowedInputChar(reader)) {
            reader.skip();
         }

         String number = reader.getString().substring(start, reader.getCursor());
         if (number.isEmpty()) {
            return Optional.empty();
         } else {
            try {
               return Optional.of((Number)converter.apply(number));
            } catch (NumberFormatException var6) {
               throw ((DynamicCommandExceptionType)parseExc.get()).createWithContext(reader, number);
            }
         }
      }

      private static boolean isAllowedInputChar(final StringReader reader) {
         char c = reader.peek();
         if ((c < '0' || c > '9') && c != '-') {
            if (c != '.') {
               return false;
            } else {
               return !reader.canRead(2) || reader.peek(1) != '.';
            }
         } else {
            return true;
         }
      }
   }
}
