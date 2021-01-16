package org.apache.commons.lang3.time;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.commons.lang3.exception.ExceptionUtils;

class CalendarReflection {
   private static final Method IS_WEEK_DATE_SUPPORTED = getCalendarMethod("isWeekDateSupported");
   private static final Method GET_WEEK_YEAR = getCalendarMethod("getWeekYear");

   CalendarReflection() {
      super();
   }

   private static Method getCalendarMethod(String var0, Class<?>... var1) {
      try {
         Method var2 = Calendar.class.getMethod(var0, var1);
         return var2;
      } catch (Exception var3) {
         return null;
      }
   }

   static boolean isWeekDateSupported(Calendar var0) {
      try {
         return IS_WEEK_DATE_SUPPORTED != null && (Boolean)IS_WEEK_DATE_SUPPORTED.invoke(var0);
      } catch (Exception var2) {
         return (Boolean)ExceptionUtils.rethrow(var2);
      }
   }

   public static int getWeekYear(Calendar var0) {
      try {
         if (isWeekDateSupported(var0)) {
            return (Integer)GET_WEEK_YEAR.invoke(var0);
         }
      } catch (Exception var2) {
         return (Integer)ExceptionUtils.rethrow(var2);
      }

      int var1 = var0.get(1);
      if (IS_WEEK_DATE_SUPPORTED == null && var0 instanceof GregorianCalendar) {
         switch(var0.get(2)) {
         case 0:
            if (var0.get(3) >= 52) {
               --var1;
            }
            break;
         case 11:
            if (var0.get(3) == 1) {
               ++var1;
            }
         }
      }

      return var1;
   }
}
