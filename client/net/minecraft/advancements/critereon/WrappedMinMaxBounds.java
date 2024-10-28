package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public record WrappedMinMaxBounds(@Nullable Float min, @Nullable Float max) {
   public static final WrappedMinMaxBounds ANY = new WrappedMinMaxBounds((Float)null, (Float)null);
   public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(Component.translatable("argument.range.ints"));

   public WrappedMinMaxBounds(@Nullable Float var1, @Nullable Float var2) {
      super();
      this.min = var1;
      this.max = var2;
   }

   public static WrappedMinMaxBounds exactly(float var0) {
      return new WrappedMinMaxBounds(var0, var0);
   }

   public static WrappedMinMaxBounds between(float var0, float var1) {
      return new WrappedMinMaxBounds(var0, var1);
   }

   public static WrappedMinMaxBounds atLeast(float var0) {
      return new WrappedMinMaxBounds(var0, (Float)null);
   }

   public static WrappedMinMaxBounds atMost(float var0) {
      return new WrappedMinMaxBounds((Float)null, var0);
   }

   public boolean matches(float var1) {
      if (this.min != null && this.max != null && this.min > this.max && this.min > var1 && this.max < var1) {
         return false;
      } else if (this.min != null && this.min > var1) {
         return false;
      } else {
         return this.max == null || !(this.max < var1);
      }
   }

   public boolean matchesSqr(double var1) {
      if (this.min != null && this.max != null && this.min > this.max && (double)(this.min * this.min) > var1 && (double)(this.max * this.max) < var1) {
         return false;
      } else if (this.min != null && (double)(this.min * this.min) > var1) {
         return false;
      } else {
         return this.max == null || !((double)(this.max * this.max) < var1);
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else if (this.min != null && this.max != null && this.min.equals(this.max)) {
         return new JsonPrimitive(this.min);
      } else {
         JsonObject var1 = new JsonObject();
         if (this.min != null) {
            var1.addProperty("min", this.min);
         }

         if (this.max != null) {
            var1.addProperty("max", this.min);
         }

         return var1;
      }
   }

   public static WrappedMinMaxBounds fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         if (GsonHelper.isNumberValue(var0)) {
            float var4 = GsonHelper.convertToFloat(var0, "value");
            return new WrappedMinMaxBounds(var4, var4);
         } else {
            JsonObject var1 = GsonHelper.convertToJsonObject(var0, "value");
            Float var2 = var1.has("min") ? GsonHelper.getAsFloat(var1, "min") : null;
            Float var3 = var1.has("max") ? GsonHelper.getAsFloat(var1, "max") : null;
            return new WrappedMinMaxBounds(var2, var3);
         }
      } else {
         return ANY;
      }
   }

   public static WrappedMinMaxBounds fromReader(StringReader var0, boolean var1) throws CommandSyntaxException {
      return fromReader(var0, var1, (var0x) -> {
         return var0x;
      });
   }

   public static WrappedMinMaxBounds fromReader(StringReader var0, boolean var1, Function<Float, Float> var2) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw MinMaxBounds.ERROR_EMPTY.createWithContext(var0);
      } else {
         int var3 = var0.getCursor();
         Float var4 = optionallyFormat(readNumber(var0, var1), var2);
         Float var5;
         if (var0.canRead(2) && var0.peek() == '.' && var0.peek(1) == '.') {
            var0.skip();
            var0.skip();
            var5 = optionallyFormat(readNumber(var0, var1), var2);
            if (var4 == null && var5 == null) {
               var0.setCursor(var3);
               throw MinMaxBounds.ERROR_EMPTY.createWithContext(var0);
            }
         } else {
            if (!var1 && var0.canRead() && var0.peek() == '.') {
               var0.setCursor(var3);
               throw ERROR_INTS_ONLY.createWithContext(var0);
            }

            var5 = var4;
         }

         if (var4 == null && var5 == null) {
            var0.setCursor(var3);
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(var0);
         } else {
            return new WrappedMinMaxBounds(var4, var5);
         }
      }
   }

   @Nullable
   private static Float readNumber(StringReader var0, boolean var1) throws CommandSyntaxException {
      int var2 = var0.getCursor();

      while(var0.canRead() && isAllowedNumber(var0, var1)) {
         var0.skip();
      }

      String var3 = var0.getString().substring(var2, var0.getCursor());
      if (var3.isEmpty()) {
         return null;
      } else {
         try {
            return Float.parseFloat(var3);
         } catch (NumberFormatException var5) {
            if (var1) {
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(var0, var3);
            } else {
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(var0, var3);
            }
         }
      }
   }

   private static boolean isAllowedNumber(StringReader var0, boolean var1) {
      char var2 = var0.peek();
      if ((var2 < '0' || var2 > '9') && var2 != '-') {
         if (var1 && var2 == '.') {
            return !var0.canRead(2) || var0.peek(1) != '.';
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Nullable
   private static Float optionallyFormat(@Nullable Float var0, Function<Float, Float> var1) {
      return var0 == null ? null : (Float)var1.apply(var0);
   }

   @Nullable
   public Float min() {
      return this.min;
   }

   @Nullable
   public Float max() {
      return this.max;
   }
}
