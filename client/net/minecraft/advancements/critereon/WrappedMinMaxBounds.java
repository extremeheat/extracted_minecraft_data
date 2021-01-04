package net.minecraft.advancements.critereon;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TranslatableComponent;

public class WrappedMinMaxBounds {
   public static final WrappedMinMaxBounds ANY = new WrappedMinMaxBounds((Float)null, (Float)null);
   public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(new TranslatableComponent("argument.range.ints", new Object[0]));
   private final Float min;
   private final Float max;

   public WrappedMinMaxBounds(@Nullable Float var1, @Nullable Float var2) {
      super();
      this.min = var1;
      this.max = var2;
   }

   @Nullable
   public Float getMin() {
      return this.min;
   }

   @Nullable
   public Float getMax() {
      return this.max;
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
}
