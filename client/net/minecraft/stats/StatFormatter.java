package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public interface StatFormatter {
   DecimalFormat DECIMAL_FORMAT = new DecimalFormat("########0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
   StatFormatter DEFAULT;
   StatFormatter DIVIDE_BY_TEN;
   StatFormatter DISTANCE;
   StatFormatter TIME;

   String format(int value);

   static {
      NumberFormat var10000 = NumberFormat.getIntegerInstance(Locale.US);
      Objects.requireNonNull(var10000);
      DEFAULT = var10000::format;
      DIVIDE_BY_TEN = (value) -> DECIMAL_FORMAT.format((double)value * 0.1);
      DISTANCE = (cm) -> {
         double meters = (double)cm / 100.0;
         double kilometers = meters / 1000.0;
         if (kilometers > 0.5) {
            return DECIMAL_FORMAT.format(kilometers) + " km";
         } else {
            return meters > 0.5 ? DECIMAL_FORMAT.format(meters) + " m" : cm + " cm";
         }
      };
      TIME = (value) -> {
         double seconds = (double)value / 20.0;
         double minutes = seconds / 60.0;
         double hours = minutes / 60.0;
         double days = hours / 24.0;
         double years = days / 365.0;
         if (years > 0.5) {
            return DECIMAL_FORMAT.format(years) + " y";
         } else if (days > 0.5) {
            return DECIMAL_FORMAT.format(days) + " d";
         } else if (hours > 0.5) {
            return DECIMAL_FORMAT.format(hours) + " h";
         } else {
            return minutes > 0.5 ? DECIMAL_FORMAT.format(minutes) + " min" : seconds + " s";
         }
      };
   }
}
