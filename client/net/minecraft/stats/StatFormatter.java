package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.Util;
import org.apache.commons.lang3.time.DurationFormatUtils;

public interface StatFormatter {
   DecimalFormat DECIMAL_FORMAT = Util.make(
      new DecimalFormat("########0.00"), var0 -> var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
   );
   StatFormatter DEFAULT = NumberFormat.getIntegerInstance(Locale.US)::format;
   StatFormatter DIVIDE_BY_TEN = var0 -> DECIMAL_FORMAT.format((double)var0 * 0.1);
   StatFormatter DISTANCE = var0 -> {
      double var1 = (double)var0 / 100.0;
      double var3 = var1 / 1000.0;
      if (var3 > 0.5) {
         return DECIMAL_FORMAT.format(var3) + " km";
      } else {
         return var1 > 0.5 ? DECIMAL_FORMAT.format(var1) + " m" : var0 + " cm";
      }
   };
   StatFormatter TIME = var0 -> {
      double var1 = (double)var0 / 20.0;
      double var3 = var1 / 60.0;
      double var5 = var3 / 60.0;
      double var7 = var5 / 24.0;
      double var9 = var7 / 365.0;
      if (var9 > 0.5) {
         return DECIMAL_FORMAT.format(var9) + " y";
      } else if (var7 > 0.5) {
         return DECIMAL_FORMAT.format(var7) + " d";
      } else if (var5 > 0.5) {
         return DECIMAL_FORMAT.format(var5) + " h";
      } else {
         return var3 > 0.5 ? DECIMAL_FORMAT.format(var3) + " m" : var1 + " s";
      }
   };
   StatFormatter HUMAN_TIME = var0 -> DurationFormatUtils.formatDurationHMS(Math.round((double)var0 * 50.0));

   String format(int var1);
}
