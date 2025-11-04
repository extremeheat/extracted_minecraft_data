package net.minecraft.advancements.criterion;

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

   public static record Ints(Bounds<Integer> bounds, Bounds<Long> boundsSqr) implements MinMaxBounds<Integer> {
      public static final Ints ANY = new Ints(MinMaxBounds.Bounds.any());
      public static final Codec<Ints> CODEC;
      public static final StreamCodec<ByteBuf, Ints> STREAM_CODEC;

      private Ints(Bounds<Integer> var1) {
         this(var1, var1.map((var0) -> Mth.square(var0.longValue())));
      }

      public Ints(Bounds<Integer> var1, Bounds<Long> var2) {
         super();
         this.bounds = var1;
         this.boundsSqr = var2;
      }

      public static Ints exactly(int var0) {
         return new Ints(MinMaxBounds.Bounds.exactly(var0));
      }

      public static Ints between(int var0, int var1) {
         return new Ints(MinMaxBounds.Bounds.between(var0, var1));
      }

      public static Ints atLeast(int var0) {
         return new Ints(MinMaxBounds.Bounds.atLeast(var0));
      }

      public static Ints atMost(int var0) {
         return new Ints(MinMaxBounds.Bounds.atMost(var0));
      }

      public boolean matches(int var1) {
         if (this.bounds.min.isPresent() && (Integer)this.bounds.min.get() > var1) {
            return false;
         } else {
            return this.bounds.max.isEmpty() || (Integer)this.bounds.max.get() >= var1;
         }
      }

      public boolean matchesSqr(long var1) {
         if (this.boundsSqr.min.isPresent() && (Long)this.boundsSqr.min.get() > var1) {
            return false;
         } else {
            return this.boundsSqr.max.isEmpty() || (Long)this.boundsSqr.max.get() >= var1;
         }
      }

      public static Ints fromReader(StringReader var0) throws CommandSyntaxException {
         int var1 = var0.getCursor();
         Function var10001 = Integer::parseInt;
         BuiltInExceptionProvider var10002 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10002);
         Bounds var2 = MinMaxBounds.Bounds.fromReader(var0, var10001, var10002::readerInvalidInt);
         if (var2.areSwapped()) {
            var0.setCursor(var1);
            throw ERROR_SWAPPED.createWithContext(var0);
         } else {
            return new Ints(var2);
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

      private Doubles(Bounds<Double> var1) {
         this(var1, var1.map(Mth::square));
      }

      public Doubles(Bounds<Double> var1, Bounds<Double> var2) {
         super();
         this.bounds = var1;
         this.boundsSqr = var2;
      }

      public static Doubles exactly(double var0) {
         return new Doubles(MinMaxBounds.Bounds.exactly(var0));
      }

      public static Doubles between(double var0, double var2) {
         return new Doubles(MinMaxBounds.Bounds.between(var0, var2));
      }

      public static Doubles atLeast(double var0) {
         return new Doubles(MinMaxBounds.Bounds.atLeast(var0));
      }

      public static Doubles atMost(double var0) {
         return new Doubles(MinMaxBounds.Bounds.atMost(var0));
      }

      public boolean matches(double var1) {
         if (this.bounds.min.isPresent() && (Double)this.bounds.min.get() > var1) {
            return false;
         } else {
            return this.bounds.max.isEmpty() || !((Double)this.bounds.max.get() < var1);
         }
      }

      public boolean matchesSqr(double var1) {
         if (this.boundsSqr.min.isPresent() && (Double)this.boundsSqr.min.get() > var1) {
            return false;
         } else {
            return this.boundsSqr.max.isEmpty() || !((Double)this.boundsSqr.max.get() < var1);
         }
      }

      public static Doubles fromReader(StringReader var0) throws CommandSyntaxException {
         int var1 = var0.getCursor();
         Function var10001 = Double::parseDouble;
         BuiltInExceptionProvider var10002 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10002);
         Bounds var2 = MinMaxBounds.Bounds.fromReader(var0, var10001, var10002::readerInvalidDouble);
         if (var2.areSwapped()) {
            var0.setCursor(var1);
            throw ERROR_SWAPPED.createWithContext(var0);
         } else {
            return new Doubles(var2);
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

      public FloatDegrees(Bounds<Float> var1) {
         super();
         this.bounds = var1;
      }

      public static FloatDegrees fromReader(StringReader var0) throws CommandSyntaxException {
         Function var10001 = Float::parseFloat;
         BuiltInExceptionProvider var10002 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10002);
         Bounds var1 = MinMaxBounds.Bounds.fromReader(var0, var10001, var10002::readerInvalidFloat);
         return new FloatDegrees(var1);
      }

      static {
         CODEC = MinMaxBounds.Bounds.createCodec(Codec.FLOAT).xmap(FloatDegrees::new, FloatDegrees::bounds);
         STREAM_CODEC = MinMaxBounds.Bounds.createStreamCodec(ByteBufCodecs.FLOAT).map(FloatDegrees::new, FloatDegrees::bounds);
      }
   }

   public static record Bounds<T extends Number & Comparable<T>>(Optional<T> min, Optional<T> max) {
      final Optional<T> min;
      final Optional<T> max;

      public Bounds(Optional<T> var1, Optional<T> var2) {
         super();
         this.min = var1;
         this.max = var2;
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

      public Optional<T> asPoint() {
         Optional var1 = this.min();
         Optional var2 = this.max();
         return var1.equals(var2) ? var1 : Optional.empty();
      }

      public static <T extends Number & Comparable<T>> Bounds<T> any() {
         return new Bounds<T>(Optional.empty(), Optional.empty());
      }

      public static <T extends Number & Comparable<T>> Bounds<T> exactly(T var0) {
         Optional var1 = Optional.of(var0);
         return new Bounds<T>(var1, var1);
      }

      public static <T extends Number & Comparable<T>> Bounds<T> between(T var0, T var1) {
         return new Bounds<T>(Optional.of(var0), Optional.of(var1));
      }

      public static <T extends Number & Comparable<T>> Bounds<T> atLeast(T var0) {
         return new Bounds<T>(Optional.of(var0), Optional.empty());
      }

      public static <T extends Number & Comparable<T>> Bounds<T> atMost(T var0) {
         return new Bounds<T>(Optional.empty(), Optional.of(var0));
      }

      public <U extends Number & Comparable<U>> Bounds<U> map(Function<T, U> var1) {
         return new Bounds<U>(this.min.map(var1), this.max.map(var1));
      }

      static <T extends Number & Comparable<T>> Codec<Bounds<T>> createCodec(Codec<T> var0) {
         Codec var1 = RecordCodecBuilder.create((var1x) -> var1x.group(var0.optionalFieldOf("min").forGetter(Bounds::min), var0.optionalFieldOf("max").forGetter(Bounds::max)).apply(var1x, Bounds::new));
         return Codec.either(var1, var0).xmap((var0x) -> (Bounds)var0x.map((var0) -> var0, (var0) -> exactly((Number)var0)), (var0x) -> {
            Optional var1 = var0x.asPoint();
            return var1.isPresent() ? Either.right((Number)var1.get()) : Either.left(var0x);
         });
      }

      static <B extends ByteBuf, T extends Number & Comparable<T>> StreamCodec<B, Bounds<T>> createStreamCodec(final StreamCodec<B, T> var0) {
         return new StreamCodec<B, Bounds<T>>() {
            private static final int MIN_FLAG = 1;
            private static final int MAX_FLAG = 2;

            public Bounds<T> decode(B var1) {
               byte var2 = var1.readByte();
               Optional var3 = (var2 & 1) != 0 ? Optional.of((Number)var0.decode(var1)) : Optional.empty();
               Optional var4 = (var2 & 2) != 0 ? Optional.of((Number)var0.decode(var1)) : Optional.empty();
               return new Bounds<T>(var3, var4);
            }

            public void encode(B var1, Bounds<T> var2) {
               Optional var3 = var2.min();
               Optional var4 = var2.max();
               var1.writeByte((var3.isPresent() ? 1 : 0) | (var4.isPresent() ? 2 : 0));
               var3.ifPresent((var2x) -> var0.encode(var1, var2x));
               var4.ifPresent((var2x) -> var0.encode(var1, var2x));
            }

            // $FF: synthetic method
            public void encode(final Object var1, final Object var2) {
               this.encode((ByteBuf)var1, (Bounds)var2);
            }

            // $FF: synthetic method
            public Object decode(final Object var1) {
               return this.decode((ByteBuf)var1);
            }
         };
      }

      public static <T extends Number & Comparable<T>> Bounds<T> fromReader(StringReader var0, Function<String, T> var1, Supplier<DynamicCommandExceptionType> var2) throws CommandSyntaxException {
         if (!var0.canRead()) {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(var0);
         } else {
            int var3 = var0.getCursor();

            try {
               Optional var4 = readNumber(var0, var1, var2);
               Optional var5;
               if (var0.canRead(2) && var0.peek() == '.' && var0.peek(1) == '.') {
                  var0.skip();
                  var0.skip();
                  var5 = readNumber(var0, var1, var2);
               } else {
                  var5 = var4;
               }

               if (var4.isEmpty() && var5.isEmpty()) {
                  throw MinMaxBounds.ERROR_EMPTY.createWithContext(var0);
               } else {
                  return new Bounds<T>(var4, var5);
               }
            } catch (CommandSyntaxException var6) {
               var0.setCursor(var3);
               throw new CommandSyntaxException(var6.getType(), var6.getRawMessage(), var6.getInput(), var3);
            }
         }
      }

      private static <T extends Number> Optional<T> readNumber(StringReader var0, Function<String, T> var1, Supplier<DynamicCommandExceptionType> var2) throws CommandSyntaxException {
         int var3 = var0.getCursor();

         while(var0.canRead() && isAllowedInputChar(var0)) {
            var0.skip();
         }

         String var4 = var0.getString().substring(var3, var0.getCursor());
         if (var4.isEmpty()) {
            return Optional.empty();
         } else {
            try {
               return Optional.of((Number)var1.apply(var4));
            } catch (NumberFormatException var6) {
               throw ((DynamicCommandExceptionType)var2.get()).createWithContext(var0, var4);
            }
         }
      }

      private static boolean isAllowedInputChar(StringReader var0) {
         char var1 = var0.peek();
         if ((var1 < '0' || var1 > '9') && var1 != '-') {
            if (var1 != '.') {
               return false;
            } else {
               return !var0.canRead(2) || var0.peek(1) != '.';
            }
         } else {
            return true;
         }
      }
   }
}
