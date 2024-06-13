package net.minecraft.advancements.critereon;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public interface MinMaxBounds<T extends Number> {
   SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(Component.translatable("argument.range.empty"));
   SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(Component.translatable("argument.range.swapped"));

   Optional<T> min();

   Optional<T> max();

   default boolean isAny() {
      return this.min().isEmpty() && this.max().isEmpty();
   }

   default Optional<T> unwrapPoint() {
      Optional var1 = this.min();
      Optional var2 = this.max();
      return var1.equals(var2) ? var1 : Optional.empty();
   }

   static <T extends Number, R extends MinMaxBounds<T>> Codec<R> createCodec(Codec<T> var0, MinMaxBounds.BoundsFactory<T, R> var1) {
      Codec var2 = RecordCodecBuilder.create(
         var2x -> var2x.group(var0.optionalFieldOf("min").forGetter(MinMaxBounds::min), var0.optionalFieldOf("max").forGetter(MinMaxBounds::max))
               .apply(var2x, var1::create)
      );
      return Codec.either(var2, var0)
         .xmap(var1x -> (MinMaxBounds)var1x.map(var0xx -> var0xx, var1xx -> var1.create(Optional.of(var1xx), Optional.of(var1xx))), var0x -> {
            Optional var1x = var0x.unwrapPoint();
            return var1x.isPresent() ? Either.right((Number)var1x.get()) : Either.left(var0x);
         });
   }

   static <T extends Number, R extends MinMaxBounds<T>> R fromReader(
      StringReader var0,
      MinMaxBounds.BoundsFromReaderFactory<T, R> var1,
      Function<String, T> var2,
      Supplier<DynamicCommandExceptionType> var3,
      Function<T, T> var4
   ) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw ERROR_EMPTY.createWithContext(var0);
      } else {
         int var5 = var0.getCursor();

         try {
            Optional var6 = readNumber(var0, var2, var3).map(var4);
            Optional var7;
            if (var0.canRead(2) && var0.peek() == '.' && var0.peek(1) == '.') {
               var0.skip();
               var0.skip();
               var7 = readNumber(var0, var2, var3).map(var4);
               if (var6.isEmpty() && var7.isEmpty()) {
                  throw ERROR_EMPTY.createWithContext(var0);
               }
            } else {
               var7 = var6;
            }

            if (var6.isEmpty() && var7.isEmpty()) {
               throw ERROR_EMPTY.createWithContext(var0);
            } else {
               return (R)var1.create(var0, var6, var7);
            }
         } catch (CommandSyntaxException var8) {
            var0.setCursor(var5);
            throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), var5);
         }
      }
   }

   private static <T extends Number> Optional<T> readNumber(StringReader var0, Function<String, T> var1, Supplier<DynamicCommandExceptionType> var2) throws CommandSyntaxException {
      int var3 = var0.getCursor();

      while (var0.canRead() && isAllowedInputChat(var0)) {
         var0.skip();
      }

      String var4 = var0.getString().substring(var3, var0.getCursor());
      if (var4.isEmpty()) {
         return Optional.empty();
      } else {
         try {
            return Optional.of((T)var1.apply(var4));
         } catch (NumberFormatException var6) {
            throw ((DynamicCommandExceptionType)var2.get()).createWithContext(var0, var4);
         }
      }
   }

   private static boolean isAllowedInputChat(StringReader var0) {
      char var1 = var0.peek();
      if ((var1 < '0' || var1 > '9') && var1 != '-') {
         return var1 != '.' ? false : !var0.canRead(2) || var0.peek(1) != '.';
      } else {
         return true;
      }
   }

   @FunctionalInterface
   public interface BoundsFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(Optional<T> var1, Optional<T> var2);
   }

   @FunctionalInterface
   public interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader var1, Optional<T> var2, Optional<T> var3) throws CommandSyntaxException;
   }

   public static record Doubles(Optional<Double> min, Optional<Double> max, Optional<Double> minSq, Optional<Double> maxSq) implements MinMaxBounds<Double> {
      public static final MinMaxBounds.Doubles ANY = new MinMaxBounds.Doubles(Optional.empty(), Optional.empty());
      public static final Codec<MinMaxBounds.Doubles> CODEC = MinMaxBounds.createCodec(Codec.DOUBLE, MinMaxBounds.Doubles::new);

      private Doubles(Optional<Double> var1, Optional<Double> var2) {
         this(var1, var2, squareOpt(var1), squareOpt(var2));
      }

      public Doubles(Optional<Double> min, Optional<Double> max, Optional<Double> minSq, Optional<Double> maxSq) {
         super();
         this.min = min;
         this.max = max;
         this.minSq = minSq;
         this.maxSq = maxSq;
      }

      private static MinMaxBounds.Doubles create(StringReader var0, Optional<Double> var1, Optional<Double> var2) throws CommandSyntaxException {
         if (var1.isPresent() && var2.isPresent() && (Double)var1.get() > (Double)var2.get()) {
            throw ERROR_SWAPPED.createWithContext(var0);
         } else {
            return new MinMaxBounds.Doubles(var1, var2);
         }
      }

      private static Optional<Double> squareOpt(Optional<Double> var0) {
         return var0.map(var0x -> var0x * var0x);
      }

      public static MinMaxBounds.Doubles exactly(double var0) {
         return new MinMaxBounds.Doubles(Optional.of(var0), Optional.of(var0));
      }

      public static MinMaxBounds.Doubles between(double var0, double var2) {
         return new MinMaxBounds.Doubles(Optional.of(var0), Optional.of(var2));
      }

      public static MinMaxBounds.Doubles atLeast(double var0) {
         return new MinMaxBounds.Doubles(Optional.of(var0), Optional.empty());
      }

      public static MinMaxBounds.Doubles atMost(double var0) {
         return new MinMaxBounds.Doubles(Optional.empty(), Optional.of(var0));
      }

      public boolean matches(double var1) {
         return this.min.isPresent() && this.min.get() > var1 ? false : this.max.isEmpty() || !(this.max.get() < var1);
      }

      public boolean matchesSqr(double var1) {
         return this.minSq.isPresent() && this.minSq.get() > var1 ? false : this.maxSq.isEmpty() || !(this.maxSq.get() < var1);
      }

      public static MinMaxBounds.Doubles fromReader(StringReader var0) throws CommandSyntaxException {
         return fromReader(var0, var0x -> var0x);
      }

      public static MinMaxBounds.Doubles fromReader(StringReader var0, Function<Double, Double> var1) throws CommandSyntaxException {
         return MinMaxBounds.fromReader(
            var0, MinMaxBounds.Doubles::create, Double::parseDouble, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidDouble, var1
         );
      }
   }

   public static record Ints(Optional<Integer> min, Optional<Integer> max, Optional<Long> minSq, Optional<Long> maxSq) implements MinMaxBounds<Integer> {
      public static final MinMaxBounds.Ints ANY = new MinMaxBounds.Ints(Optional.empty(), Optional.empty());
      public static final Codec<MinMaxBounds.Ints> CODEC = MinMaxBounds.createCodec(Codec.INT, MinMaxBounds.Ints::new);

      private Ints(Optional<Integer> var1, Optional<Integer> var2) {
         this(var1, var2, var1.map(var0 -> var0.longValue() * var0.longValue()), squareOpt(var2));
      }

      public Ints(Optional<Integer> min, Optional<Integer> max, Optional<Long> minSq, Optional<Long> maxSq) {
         super();
         this.min = min;
         this.max = max;
         this.minSq = minSq;
         this.maxSq = maxSq;
      }

      private static MinMaxBounds.Ints create(StringReader var0, Optional<Integer> var1, Optional<Integer> var2) throws CommandSyntaxException {
         if (var1.isPresent() && var2.isPresent() && (Integer)var1.get() > (Integer)var2.get()) {
            throw ERROR_SWAPPED.createWithContext(var0);
         } else {
            return new MinMaxBounds.Ints(var1, var2);
         }
      }

      private static Optional<Long> squareOpt(Optional<Integer> var0) {
         return var0.map(var0x -> var0x.longValue() * var0x.longValue());
      }

      public static MinMaxBounds.Ints exactly(int var0) {
         return new MinMaxBounds.Ints(Optional.of(var0), Optional.of(var0));
      }

      public static MinMaxBounds.Ints between(int var0, int var1) {
         return new MinMaxBounds.Ints(Optional.of(var0), Optional.of(var1));
      }

      public static MinMaxBounds.Ints atLeast(int var0) {
         return new MinMaxBounds.Ints(Optional.of(var0), Optional.empty());
      }

      public static MinMaxBounds.Ints atMost(int var0) {
         return new MinMaxBounds.Ints(Optional.empty(), Optional.of(var0));
      }

      public boolean matches(int var1) {
         return this.min.isPresent() && this.min.get() > var1 ? false : this.max.isEmpty() || this.max.get() >= var1;
      }

      public boolean matchesSqr(long var1) {
         return this.minSq.isPresent() && this.minSq.get() > var1 ? false : this.maxSq.isEmpty() || this.maxSq.get() >= var1;
      }

      public static MinMaxBounds.Ints fromReader(StringReader var0) throws CommandSyntaxException {
         return fromReader(var0, var0x -> var0x);
      }

      public static MinMaxBounds.Ints fromReader(StringReader var0, Function<Integer, Integer> var1) throws CommandSyntaxException {
         return MinMaxBounds.fromReader(var0, MinMaxBounds.Ints::create, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, var1);
      }
   }
}
