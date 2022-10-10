package net.minecraft.advancements.criterion;

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
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class MinMaxBounds<T extends Number> {
   public static final SimpleCommandExceptionType field_196978_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.range.empty", new Object[0]));
   public static final SimpleCommandExceptionType field_196980_d = new SimpleCommandExceptionType(new TextComponentTranslation("argument.range.swapped", new Object[0]));
   protected final T field_192517_b;
   protected final T field_192518_c;

   protected MinMaxBounds(@Nullable T var1, @Nullable T var2) {
      super();
      this.field_192517_b = var1;
      this.field_192518_c = var2;
   }

   @Nullable
   public T func_196973_a() {
      return this.field_192517_b;
   }

   @Nullable
   public T func_196977_b() {
      return this.field_192518_c;
   }

   public boolean func_211335_c() {
      return this.field_192517_b == null && this.field_192518_c == null;
   }

   public JsonElement func_200321_c() {
      if (this.func_211335_c()) {
         return JsonNull.INSTANCE;
      } else if (this.field_192517_b != null && this.field_192517_b.equals(this.field_192518_c)) {
         return new JsonPrimitive(this.field_192517_b);
      } else {
         JsonObject var1 = new JsonObject();
         if (this.field_192517_b != null) {
            var1.addProperty("min", this.field_192517_b);
         }

         if (this.field_192518_c != null) {
            var1.addProperty("max", this.field_192517_b);
         }

         return var1;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R func_211331_a(@Nullable JsonElement var0, R var1, BiFunction<JsonElement, String, T> var2, MinMaxBounds.IBoundFactory<T, R> var3) {
      if (var0 != null && !var0.isJsonNull()) {
         if (JsonUtils.func_188175_b(var0)) {
            Number var7 = (Number)var2.apply(var0, "value");
            return var3.create(var7, var7);
         } else {
            JsonObject var4 = JsonUtils.func_151210_l(var0, "value");
            Number var5 = var4.has("min") ? (Number)var2.apply(var4.get("min"), "min") : null;
            Number var6 = var4.has("max") ? (Number)var2.apply(var4.get("max"), "max") : null;
            return var3.create(var5, var6);
         }
      } else {
         return var1;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R func_211337_a(StringReader var0, MinMaxBounds.IBoundReader<T, R> var1, Function<String, T> var2, Supplier<DynamicCommandExceptionType> var3, Function<T, T> var4) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw field_196978_b.createWithContext(var0);
      } else {
         int var5 = var0.getCursor();

         try {
            Number var6 = (Number)func_196972_a(func_196975_b(var0, var2, var3), var4);
            Number var7;
            if (var0.canRead(2) && var0.peek() == '.' && var0.peek(1) == '.') {
               var0.skip();
               var0.skip();
               var7 = (Number)func_196972_a(func_196975_b(var0, var2, var3), var4);
               if (var6 == null && var7 == null) {
                  throw field_196978_b.createWithContext(var0);
               }
            } else {
               var7 = var6;
            }

            if (var6 == null && var7 == null) {
               throw field_196978_b.createWithContext(var0);
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
   private static <T extends Number> T func_196975_b(StringReader var0, Function<String, T> var1, Supplier<DynamicCommandExceptionType> var2) throws CommandSyntaxException {
      int var3 = var0.getCursor();

      while(var0.canRead() && func_196970_c(var0)) {
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

   private static boolean func_196970_c(StringReader var0) {
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
   private static <T> T func_196972_a(@Nullable T var0, Function<T, T> var1) {
      return var0 == null ? null : var1.apply(var0);
   }

   @FunctionalInterface
   public interface IBoundReader<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader var1, @Nullable T var2, @Nullable T var3) throws CommandSyntaxException;
   }

   @FunctionalInterface
   public interface IBoundFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(@Nullable T var1, @Nullable T var2);
   }

   public static class FloatBound extends MinMaxBounds<Float> {
      public static final MinMaxBounds.FloatBound field_211359_e = new MinMaxBounds.FloatBound((Float)null, (Float)null);
      private final Double field_211360_f;
      private final Double field_211361_g;

      private static MinMaxBounds.FloatBound func_211352_a(StringReader var0, @Nullable Float var1, @Nullable Float var2) throws CommandSyntaxException {
         if (var1 != null && var2 != null && var1 > var2) {
            throw field_196980_d.createWithContext(var0);
         } else {
            return new MinMaxBounds.FloatBound(var1, var2);
         }
      }

      @Nullable
      private static Double func_211350_a(@Nullable Float var0) {
         return var0 == null ? null : var0.doubleValue() * var0.doubleValue();
      }

      private FloatBound(@Nullable Float var1, @Nullable Float var2) {
         super(var1, var2);
         this.field_211360_f = func_211350_a(var1);
         this.field_211361_g = func_211350_a(var2);
      }

      public static MinMaxBounds.FloatBound func_211355_b(float var0) {
         return new MinMaxBounds.FloatBound(var0, (Float)null);
      }

      public boolean func_211354_d(float var1) {
         if (this.field_192517_b != null && (Float)this.field_192517_b > var1) {
            return false;
         } else {
            return this.field_192518_c == null || (Float)this.field_192518_c >= var1;
         }
      }

      public boolean func_211351_a(double var1) {
         if (this.field_211360_f != null && this.field_211360_f > var1) {
            return false;
         } else {
            return this.field_211361_g == null || this.field_211361_g >= var1;
         }
      }

      public static MinMaxBounds.FloatBound func_211356_a(@Nullable JsonElement var0) {
         return (MinMaxBounds.FloatBound)func_211331_a(var0, field_211359_e, JsonUtils::func_151220_d, MinMaxBounds.FloatBound::new);
      }

      public static MinMaxBounds.FloatBound func_211357_a(StringReader var0) throws CommandSyntaxException {
         return func_211353_a(var0, (var0x) -> {
            return var0x;
         });
      }

      public static MinMaxBounds.FloatBound func_211353_a(StringReader var0, Function<Float, Float> var1) throws CommandSyntaxException {
         return (MinMaxBounds.FloatBound)func_211337_a(var0, MinMaxBounds.FloatBound::func_211352_a, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, var1);
      }
   }

   public static class IntBound extends MinMaxBounds<Integer> {
      public static final MinMaxBounds.IntBound field_211347_e = new MinMaxBounds.IntBound((Integer)null, (Integer)null);
      private final Long field_211348_f;
      private final Long field_211349_g;

      private static MinMaxBounds.IntBound func_211338_a(StringReader var0, @Nullable Integer var1, @Nullable Integer var2) throws CommandSyntaxException {
         if (var1 != null && var2 != null && var1 > var2) {
            throw field_196980_d.createWithContext(var0);
         } else {
            return new MinMaxBounds.IntBound(var1, var2);
         }
      }

      @Nullable
      private static Long func_211343_a(@Nullable Integer var0) {
         return var0 == null ? null : var0.longValue() * var0.longValue();
      }

      private IntBound(@Nullable Integer var1, @Nullable Integer var2) {
         super(var1, var2);
         this.field_211348_f = func_211343_a(var1);
         this.field_211349_g = func_211343_a(var2);
      }

      public static MinMaxBounds.IntBound func_211345_a(int var0) {
         return new MinMaxBounds.IntBound(var0, var0);
      }

      public static MinMaxBounds.IntBound func_211340_b(int var0) {
         return new MinMaxBounds.IntBound(var0, (Integer)null);
      }

      public boolean func_211339_d(int var1) {
         if (this.field_192517_b != null && (Integer)this.field_192517_b > var1) {
            return false;
         } else {
            return this.field_192518_c == null || (Integer)this.field_192518_c >= var1;
         }
      }

      public static MinMaxBounds.IntBound func_211344_a(@Nullable JsonElement var0) {
         return (MinMaxBounds.IntBound)func_211331_a(var0, field_211347_e, JsonUtils::func_151215_f, MinMaxBounds.IntBound::new);
      }

      public static MinMaxBounds.IntBound func_211342_a(StringReader var0) throws CommandSyntaxException {
         return func_211341_a(var0, (var0x) -> {
            return var0x;
         });
      }

      public static MinMaxBounds.IntBound func_211341_a(StringReader var0, Function<Integer, Integer> var1) throws CommandSyntaxException {
         return (MinMaxBounds.IntBound)func_211337_a(var0, MinMaxBounds.IntBound::func_211338_a, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, var1);
      }
   }
}
