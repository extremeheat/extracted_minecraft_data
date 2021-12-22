package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.Util;

public interface StatFormatter {
   DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("########0.00"), (var0) -> {
      var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   StatFormatter DEFAULT;
   StatFormatter DIVIDE_BY_TEN;
   StatFormatter DISTANCE;
   StatFormatter TIME;

   String format(int var1);

   static {
      NumberFormat var10000 = NumberFormat.getIntegerInstance(Locale.US);
      Objects.requireNonNull(var10000);
      DEFAULT = var10000::format;
      DIVIDE_BY_TEN = (var0) -> {
         return DECIMAL_FORMAT.format((double)var0 * 0.1D);
      };
      DISTANCE = (var0) -> {
         double var1 = (double)var0 / 100.0D;
         double var3 = var1 / 1000.0D;
         if (var3 > 0.5D) {
            return DECIMAL_FORMAT.format(var3) + " km";
         } else {
            return var1 > 0.5D ? DECIMAL_FORMAT.format(var1) + " m" : var0 + " cm";
         }
      };
      TIME = (var0) -> {
         double var1 = (double)var0 / 20.0D;
         double var3 = var1 / 60.0D;
         double var5 = var3 / 60.0D;
         double var7 = var5 / 24.0D;
         double var9 = var7 / 365.0D;
         if (var9 > 0.5D) {
            return DECIMAL_FORMAT.format(var9) + " y";
         } else if (var7 > 0.5D) {
            return DECIMAL_FORMAT.format(var7) + " d";
         } else if (var5 > 0.5D) {
            return DECIMAL_FORMAT.format(var5) + " h";
         } else {
            return var3 > 0.5D ? DECIMAL_FORMAT.format(var3) + " m" : var1 + " s";
         }
      };
   }
}
