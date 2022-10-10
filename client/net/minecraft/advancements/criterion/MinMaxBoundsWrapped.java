package net.minecraft.advancements.criterion;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextComponentTranslation;

public class MinMaxBoundsWrapped {
   public static final MinMaxBoundsWrapped field_207926_a = new MinMaxBoundsWrapped((Float)null, (Float)null);
   public static final SimpleCommandExceptionType field_211362_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.range.ints", new Object[0]));
   private final Float field_207929_d;
   private final Float field_207930_e;

   public MinMaxBoundsWrapped(@Nullable Float var1, @Nullable Float var2) {
      super();
      this.field_207929_d = var1;
      this.field_207930_e = var2;
   }

   @Nullable
   public Float func_207923_a() {
      return this.field_207929_d;
   }

   @Nullable
   public Float func_207925_b() {
      return this.field_207930_e;
   }

   public static MinMaxBoundsWrapped func_207921_a(StringReader var0, boolean var1, Function<Float, Float> var2) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw MinMaxBounds.field_196978_b.createWithContext(var0);
      } else {
         int var3 = var0.getCursor();
         Float var4 = func_207922_a(func_207924_b(var0, var1), var2);
         Float var5;
         if (var0.canRead(2) && var0.peek() == '.' && var0.peek(1) == '.') {
            var0.skip();
            var0.skip();
            var5 = func_207922_a(func_207924_b(var0, var1), var2);
            if (var4 == null && var5 == null) {
               var0.setCursor(var3);
               throw MinMaxBounds.field_196978_b.createWithContext(var0);
            }
         } else {
            if (!var1 && var0.canRead() && var0.peek() == '.') {
               var0.setCursor(var3);
               throw field_211362_b.createWithContext(var0);
            }

            var5 = var4;
         }

         if (var4 == null && var5 == null) {
            var0.setCursor(var3);
            throw MinMaxBounds.field_196978_b.createWithContext(var0);
         } else {
            return new MinMaxBoundsWrapped(var4, var5);
         }
      }
   }

   @Nullable
   private static Float func_207924_b(StringReader var0, boolean var1) throws CommandSyntaxException {
      int var2 = var0.getCursor();

      while(var0.canRead() && func_207920_c(var0, var1)) {
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

   private static boolean func_207920_c(StringReader var0, boolean var1) {
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
   private static Float func_207922_a(@Nullable Float var0, Function<Float, Float> var1) {
      return var0 == null ? null : (Float)var1.apply(var0);
   }
}
