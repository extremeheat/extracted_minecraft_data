package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;

public abstract class MinMaxBounds {
   public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.empty", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.swapped", new Object[0]));
   protected final Number min;
   protected final Number max;

   protected MinMaxBounds(@Nullable Number var1, @Nullable Number var2) {
      this.min = var1;
      this.max = var2;
   }

   @Nullable
   public Number getMin() {
      return this.min;
   }

   @Nullable
   public Number getMax() {
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

   protected static MinMaxBounds fromJson(@Nullable JsonElement var0, MinMaxBounds var1, BiFunction var2, MinMaxBounds.BoundsFactory var3) {
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

   protected static MinMaxBounds fromReader(StringReader var0, MinMaxBounds.BoundsFromReaderFactory var1, Function var2, Supplier var3, Function var4) throws CommandSyntaxException {
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
   private static Number readNumber(StringReader var0, Function var1, Supplier var2) throws CommandSyntaxException {
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
   private static Object optionallyFormat(@Nullable Object var0, Function var1) {
      return var0 == null ? null : var1.apply(var0);
   }

   @FunctionalInterface
   public interface BoundsFromReaderFactory {
      MinMaxBounds create(StringReader var1, @Nullable Number var2, @Nullable Number var3) throws CommandSyntaxException;
   }

   @FunctionalInterface
   public interface BoundsFactory {
      MinMaxBounds create(@Nullable Number var1, @Nullable Number var2);
   }

   public static class Floats extends MinMaxBounds {
      public static final MinMaxBounds.Floats ANY = new MinMaxBounds.Floats((Float)null, (Float)null);
      private final Double minSq;
      private final Double maxSq;

      private static MinMaxBounds.Floats create(StringReader var0, @Nullable Float var1, @Nullable Float var2) throws CommandSyntaxException {
         if (var1 != null && var2 != null && var1 > var2) {
            throw ERROR_SWAPPED.createWithContext(var0);
         } else {
            return new MinMaxBounds.Floats(var1, var2);
         }
      }

      @Nullable
      private static Double squareOpt(@Nullable Float var0) {
         return var0 == null ? null : var0.doubleValue() * var0.doubleValue();
      }

      private Floats(@Nullable Float var1, @Nullable Float var2) {
         super(var1, var2);
         this.minSq = squareOpt(var1);
         this.maxSq = squareOpt(var2);
      }

      public static MinMaxBounds.Floats atLeast(float var0) {
         return new MinMaxBounds.Floats(var0, (Float)null);
      }

      public boolean matches(float var1) {
         if (this.min != null && (Float)this.min > var1) {
            return false;
         } else {
            return this.max == null || (Float)this.max >= var1;
         }
      }

      public boolean matchesSqr(double var1) {
         if (this.minSq != null && this.minSq > var1) {
            return false;
         } else {
            return this.maxSq == null || this.maxSq >= var1;
         }
      }

      public static MinMaxBounds.Floats fromJson(@Nullable JsonElement var0) {
         return (MinMaxBounds.Floats)fromJson(var0, ANY, GsonHelper::convertToFloat, MinMaxBounds.Floats::new);
      }

      public static MinMaxBounds.Floats fromReader(StringReader var0) throws CommandSyntaxException {
         return fromReader(var0, (var0x) -> {
            return var0x;
         });
      }

      public static MinMaxBounds.Floats fromReader(StringReader var0, Function var1) throws CommandSyntaxException {
         return (MinMaxBounds.Floats)fromReader(var0, MinMaxBounds.Floats::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, var1);
      }
   }

   public static class Ints extends MinMaxBounds {
      public static final MinMaxBounds.Ints ANY = new MinMaxBounds.Ints((Integer)null, (Integer)null);
      private final Long minSq;
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

      public static MinMaxBounds.Ints atLeast(int var0) {
         return new MinMaxBounds.Ints(var0, (Integer)null);
      }

      public boolean matches(int var1) {
         if (this.min != null && (Integer)this.min > var1) {
            return false;
         } else {
            return this.max == null || (Integer)this.max >= var1;
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

      public static MinMaxBounds.Ints fromReader(StringReader var0, Function var1) throws CommandSyntaxException {
         return (MinMaxBounds.Ints)fromReader(var0, MinMaxBounds.Ints::create, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, var1);
      }
   }
}
