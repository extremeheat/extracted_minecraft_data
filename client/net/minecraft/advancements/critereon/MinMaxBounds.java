package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;

public abstract class MinMaxBounds<T extends Number> {
   public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.empty"));
   public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.swapped"));
   @Nullable
   protected final T min;
   @Nullable
   protected final T max;

   protected MinMaxBounds(@Nullable T var1, @Nullable T var2) {
      super();
      this.min = var1;
      this.max = var2;
   }

   @Nullable
   public T getMin() {
      return this.min;
   }

   @Nullable
   public T getMax() {
      return this.max;
   }

   public boolean isAny() {
      return this.min == null && this.max == null;
   }

   public JsonElement serializeToJson() {
      if (this.isAny()) {
         return JsonNull.INSTANCE;
      } else if (this.min != null && this.min.equals(this.max)) {
         return new JsonPrimitive(this.min);
      } else {
         JsonObject var1 = new JsonObject();
         if (this.min != null) {
            var1.addProperty("min", this.min);
         }

         if (this.max != null) {
            var1.addProperty("max", this.max);
         }

         return var1;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(@Nullable JsonElement var0, R var1, BiFunction<JsonElement, String, T> var2, MinMaxBounds.BoundsFactory<T, R> var3) {
      if (var0 != null && !var0.isJsonNull()) {
         if (GsonHelper.isNumberValue(var0)) {
            Number var7 = (Number)var2.apply(var0, "value");
            return var3.create(var7, var7);
         } else {
            JsonObject var4 = GsonHelper.convertToJsonObject(var0, "value");
            Number var5 = var4.has("min") ? (Number)var2.apply(var4.get("min"), "min") : null;
            Number var6 = var4.has("max") ? (Number)var2.apply(var4.get("max"), "max") : null;
            return var3.create(var5, var6);
         }
      } else {
         return var1;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader var0, MinMaxBounds.BoundsFromReaderFactory<T, R> var1, Function<String, T> var2, Supplier<DynamicCommandExceptionType> var3, Function<T, T> var4) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw ERROR_EMPTY.createWithContext(var0);
      } else {
         int var5 = var0.getCursor();

         try {
            Number var6 = (Number)optionallyFormat(readNumber(var0, var2, var3), var4);
            Number var7;
            if (var0.canRead(2) && var0.peek() == '.' && var0.peek(1) == '.') {
               var0.skip();
               var0.skip();
               var7 = (Number)optionallyFormat(readNumber(var0, var2, var3), var4);
               if (var6 == null && var7 == null) {
                  throw ERROR_EMPTY.createWithContext(var0);
               }
            } else {
               var7 = var6;
            }

            if (var6 == null && var7 == null) {
               throw ERROR_EMPTY.createWithContext(var0);
            } else {
               return var1.create(var0, var6, var7);
            }
         } catch (CommandSyntaxException var8) {
            var0.setCursor(var5);
            throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), var5);
         }
      }
   }

   @Nullable
   private static <T extends Number> T readNumber(StringReader var0, Function<String, T> var1, Supplier<DynamicCommandExceptionType> var2) throws CommandSyntaxException {
      int var3 = var0.getCursor();

      while(var0.canRead() && isAllowedInputChat(var0)) {
         var0.skip();
      }

      String var4 = var0.getString().substring(var3, var0.getCursor());
      if (var4.isEmpty()) {
         return null;
      } else {
         try {
            return (Number)var1.apply(var4);
         } catch (NumberFormatException var6) {
            throw ((DynamicCommandExceptionType)var2.get()).createWithContext(var0, var4);
         }
      }
   }

   private static boolean isAllowedInputChat(StringReader var0) {
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

   @Nullable
   private static <T> T optionallyFormat(@Nullable T var0, Function<T, T> var1) {
      return var0 == null ? null : var1.apply(var0);
   }

   @FunctionalInterface
   protected interface BoundsFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(@Nullable T var1, @Nullable T var2);
   }

   @FunctionalInterface
   protected interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader var1, @Nullable T var2, @Nullable T var3) throws CommandSyntaxException;
   }

   public static class Doubles extends MinMaxBounds<Double> {
      public static final MinMaxBounds.Doubles ANY = new MinMaxBounds.Doubles((Double)null, (Double)null);
      @Nullable
      private final Double minSq;
      @Nullable
      private final Double maxSq;

      private static MinMaxBounds.Doubles create(StringReader var0, @Nullable Double var1, @Nullable Double var2) throws CommandSyntaxException {
         if (var1 != null && var2 != null && var1 > var2) {
            throw ERROR_SWAPPED.createWithContext(var0);
         } else {
            return new MinMaxBounds.Doubles(var1, var2);
         }
      }

      @Nullable
      private static Double squareOpt(@Nullable Double var0) {
         return var0 == null ? null : var0 * var0;
      }

      private Doubles(@Nullable Double var1, @Nullable Double var2) {
         super(var1, var2);
         this.minSq = squareOpt(var1);
         this.maxSq = squareOpt(var2);
      }

      public static MinMaxBounds.Doubles exactly(double var0) {
         return new MinMaxBounds.Doubles(var0, var0);
      }

      public static MinMaxBounds.Doubles between(double var0, double var2) {
         return new MinMaxBounds.Doubles(var0, var2);
      }

      public static MinMaxBounds.Doubles atLeast(double var0) {
         return new MinMaxBounds.Doubles(var0, (Double)null);
      }

      public static MinMaxBounds.Doubles atMost(double var0) {
         return new MinMaxBounds.Doubles((Double)null, var0);
      }

      public boolean matches(double var1) {
         if (this.min != null && (Double)this.min > var1) {
            return false;
         } else {
            return this.max == null || !((Double)this.max < var1);
         }
      }

      public boolean matchesSqr(double var1) {
         if (this.minSq != null && this.minSq > var1) {
            return false;
         } else {
            return this.maxSq == null || !(this.maxSq < var1);
         }
      }

      public static MinMaxBounds.Doubles fromJson(@Nullable JsonElement var0) {
         return (MinMaxBounds.Doubles)fromJson(var0, ANY, GsonHelper::convertToDouble, MinMaxBounds.Doubles::new);
      }

      public static MinMaxBounds.Doubles fromReader(StringReader var0) throws CommandSyntaxException {
         return fromReader(var0, (var0x) -> {
            return var0x;
         });
      }

      public static MinMaxBounds.Doubles fromReader(StringReader var0, Function<Double, Double> var1) throws CommandSyntaxException {
         MinMaxBounds.BoundsFromReaderFactory var10001 = MinMaxBounds.Doubles::create;
         Function var10002 = Double::parseDouble;
         BuiltInExceptionProvider var10003 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10003);
         return (MinMaxBounds.Doubles)fromReader(var0, var10001, var10002, var10003::readerInvalidDouble, var1);
      }
   }

   public static class Ints extends MinMaxBounds<Integer> {
      public static final MinMaxBounds.Ints ANY = new MinMaxBounds.Ints((Integer)null, (Integer)null);
      @Nullable
      private final Long minSq;
      @Nullable
      private final Long maxSq;

      private static MinMaxBounds.Ints create(StringReader var0, @Nullable Integer var1, @Nullable Integer var2) throws CommandSyntaxException {
         if (var1 != null && var2 != null && var1 > var2) {
            throw ERROR_SWAPPED.createWithContext(var0);
         } else {
            return new MinMaxBounds.Ints(var1, var2);
         }
      }

      @Nullable
      private static Long squareOpt(@Nullable Integer var0) {
         return var0 == null ? null : var0.longValue() * var0.longValue();
      }

      private Ints(@Nullable Integer var1, @Nullable Integer var2) {
         super(var1, var2);
         this.minSq = squareOpt(var1);
         this.maxSq = squareOpt(var2);
      }

      public static MinMaxBounds.Ints exactly(int var0) {
         return new MinMaxBounds.Ints(var0, var0);
      }

      public static MinMaxBounds.Ints between(int var0, int var1) {
         return new MinMaxBounds.Ints(var0, var1);
      }

      public static MinMaxBounds.Ints atLeast(int var0) {
         return new MinMaxBounds.Ints(var0, (Integer)null);
      }

      public static MinMaxBounds.Ints atMost(int var0) {
         return new MinMaxBounds.Ints((Integer)null, var0);
      }

      public boolean matches(int var1) {
         if (this.min != null && (Integer)this.min > var1) {
            return false;
         } else {
            return this.max == null || (Integer)this.max >= var1;
         }
      }

      public boolean matchesSqr(long var1) {
         if (this.minSq != null && this.minSq > var1) {
            return false;
         } else {
            return this.maxSq == null || this.maxSq >= var1;
         }
      }

      public static MinMaxBounds.Ints fromJson(@Nullable JsonElement var0) {
         return (MinMaxBounds.Ints)fromJson(var0, ANY, GsonHelper::convertToInt, MinMaxBounds.Ints::new);
      }

      public static MinMaxBounds.Ints fromReader(StringReader var0) throws CommandSyntaxException {
         return fromReader(var0, (var0x) -> {
            return var0x;
         });
      }

      public static MinMaxBounds.Ints fromReader(StringReader var0, Function<Integer, Integer> var1) throws CommandSyntaxException {
         MinMaxBounds.BoundsFromReaderFactory var10001 = MinMaxBounds.Ints::create;
         Function var10002 = Integer::parseInt;
         BuiltInExceptionProvider var10003 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10003);
         return (MinMaxBounds.Ints)fromReader(var0, var10001, var10002, var10003::readerInvalidInt, var1);
      }
   }
}
