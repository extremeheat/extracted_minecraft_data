package net.minecraft.util;

import com.google.common.collect.Range;
import java.time.Month;
import java.time.MonthDay;
import java.time.ZonedDateTime;
import java.util.List;

public class SpecialDates {
   public static final Range<MonthDay> HALLOWEEN_RANGE;
   public static final MonthDay HALLOWEEN;
   public static final List<MonthDay> CHRISTMAS_RANGE;
   public static final MonthDay CHRISTMAS;
   public static final MonthDay NEW_YEAR;

   public SpecialDates() {
      super();
   }

   public static MonthDay dayNow() {
      return MonthDay.from(ZonedDateTime.now());
   }

   public static boolean isExtendedHalloween() {
      return HALLOWEEN_RANGE.contains(dayNow());
   }

   public static boolean isHalloween() {
      return HALLOWEEN.equals(dayNow());
   }

   public static boolean isExtendedChrismas() {
      return CHRISTMAS_RANGE.contains(dayNow());
   }

   static {
      HALLOWEEN_RANGE = Range.closed(MonthDay.of(Month.OCTOBER, 20), MonthDay.of(Month.NOVEMBER, 3));
      HALLOWEEN = MonthDay.of(Month.OCTOBER, 31);
      CHRISTMAS_RANGE = List.of(MonthDay.of(Month.DECEMBER, 24), MonthDay.of(Month.DECEMBER, 25), MonthDay.of(Month.DECEMBER, 26));
      CHRISTMAS = MonthDay.of(Month.DECEMBER, 24);
      NEW_YEAR = MonthDay.of(Month.JANUARY, 1);
   }
}
