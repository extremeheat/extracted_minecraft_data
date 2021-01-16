package org.apache.logging.log4j.core.util.datetime;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.util.Throwables;

public class FastDatePrinter implements DatePrinter, Serializable {
   private static final long serialVersionUID = 1L;
   public static final int FULL = 0;
   public static final int LONG = 1;
   public static final int MEDIUM = 2;
   public static final int SHORT = 3;
   private final String mPattern;
   private final TimeZone mTimeZone;
   private final Locale mLocale;
   private transient FastDatePrinter.Rule[] mRules;
   private transient int mMaxLengthEstimate;
   private static final int MAX_DIGITS = 10;
   private static final ConcurrentMap<FastDatePrinter.TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap(7);

   protected FastDatePrinter(String var1, TimeZone var2, Locale var3) {
      super();
      this.mPattern = var1;
      this.mTimeZone = var2;
      this.mLocale = var3;
      this.init();
   }

   private void init() {
      List var1 = this.parsePattern();
      this.mRules = (FastDatePrinter.Rule[])var1.toArray(new FastDatePrinter.Rule[var1.size()]);
      int var2 = 0;
      int var3 = this.mRules.length;

      while(true) {
         --var3;
         if (var3 < 0) {
            this.mMaxLengthEstimate = var2;
            return;
         }

         var2 += this.mRules[var3].estimateLength();
      }
   }

   protected List<FastDatePrinter.Rule> parsePattern() {
      DateFormatSymbols var1 = new DateFormatSymbols(this.mLocale);
      ArrayList var2 = new ArrayList();
      String[] var3 = var1.getEras();
      String[] var4 = var1.getMonths();
      String[] var5 = var1.getShortMonths();
      String[] var6 = var1.getWeekdays();
      String[] var7 = var1.getShortWeekdays();
      String[] var8 = var1.getAmPmStrings();
      int var9 = this.mPattern.length();
      int[] var10 = new int[1];

      for(int var11 = 0; var11 < var9; ++var11) {
         var10[0] = var11;
         String var12 = this.parseToken(this.mPattern, var10);
         var11 = var10[0];
         int var13 = var12.length();
         if (var13 == 0) {
            break;
         }

         char var15 = var12.charAt(0);
         Object var14;
         switch(var15) {
         case '\'':
            String var16 = var12.substring(1);
            if (var16.length() == 1) {
               var14 = new FastDatePrinter.CharacterLiteral(var16.charAt(0));
            } else {
               var14 = new FastDatePrinter.StringLiteral(var16);
            }
            break;
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'A':
         case 'B':
         case 'C':
         case 'I':
         case 'J':
         case 'L':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'V':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '_':
         case '`':
         case 'b':
         case 'c':
         case 'e':
         case 'f':
         case 'g':
         case 'i':
         case 'j':
         case 'l':
         case 'n':
         case 'o':
         case 'p':
         case 'q':
         case 'r':
         case 't':
         case 'v':
         case 'x':
         default:
            throw new IllegalArgumentException("Illegal pattern component: " + var12);
         case 'D':
            var14 = this.selectNumberRule(6, var13);
            break;
         case 'E':
            var14 = new FastDatePrinter.TextField(7, var13 < 4 ? var7 : var6);
            break;
         case 'F':
            var14 = this.selectNumberRule(8, var13);
            break;
         case 'G':
            var14 = new FastDatePrinter.TextField(0, var3);
            break;
         case 'H':
            var14 = this.selectNumberRule(11, var13);
            break;
         case 'K':
            var14 = this.selectNumberRule(10, var13);
            break;
         case 'M':
            if (var13 >= 4) {
               var14 = new FastDatePrinter.TextField(2, var4);
            } else if (var13 == 3) {
               var14 = new FastDatePrinter.TextField(2, var5);
            } else if (var13 == 2) {
               var14 = FastDatePrinter.TwoDigitMonthField.INSTANCE;
            } else {
               var14 = FastDatePrinter.UnpaddedMonthField.INSTANCE;
            }
            break;
         case 'S':
            var14 = this.selectNumberRule(14, var13);
            break;
         case 'W':
            var14 = this.selectNumberRule(4, var13);
            break;
         case 'X':
            var14 = FastDatePrinter.Iso8601_Rule.getRule(var13);
            break;
         case 'Y':
         case 'y':
            if (var13 == 2) {
               var14 = FastDatePrinter.TwoDigitYearField.INSTANCE;
            } else {
               var14 = this.selectNumberRule(1, var13 < 4 ? 4 : var13);
            }

            if (var15 == 'Y') {
               var14 = new FastDatePrinter.WeekYear((FastDatePrinter.NumberRule)var14);
            }
            break;
         case 'Z':
            if (var13 == 1) {
               var14 = FastDatePrinter.TimeZoneNumberRule.INSTANCE_NO_COLON;
            } else if (var13 == 2) {
               var14 = FastDatePrinter.Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES;
            } else {
               var14 = FastDatePrinter.TimeZoneNumberRule.INSTANCE_COLON;
            }
            break;
         case 'a':
            var14 = new FastDatePrinter.TextField(9, var8);
            break;
         case 'd':
            var14 = this.selectNumberRule(5, var13);
            break;
         case 'h':
            var14 = new FastDatePrinter.TwelveHourField(this.selectNumberRule(10, var13));
            break;
         case 'k':
            var14 = new FastDatePrinter.TwentyFourHourField(this.selectNumberRule(11, var13));
            break;
         case 'm':
            var14 = this.selectNumberRule(12, var13);
            break;
         case 's':
            var14 = this.selectNumberRule(13, var13);
            break;
         case 'u':
            var14 = new FastDatePrinter.DayInWeekField(this.selectNumberRule(7, var13));
            break;
         case 'w':
            var14 = this.selectNumberRule(3, var13);
            break;
         case 'z':
            if (var13 >= 4) {
               var14 = new FastDatePrinter.TimeZoneNameRule(this.mTimeZone, this.mLocale, 1);
            } else {
               var14 = new FastDatePrinter.TimeZoneNameRule(this.mTimeZone, this.mLocale, 0);
            }
         }

         var2.add(var14);
      }

      return var2;
   }

   protected String parseToken(String var1, int[] var2) {
      StringBuilder var3 = new StringBuilder();
      int var4 = var2[0];
      int var5 = var1.length();
      char var6 = var1.charAt(var4);
      if (var6 >= 'A' && var6 <= 'Z' || var6 >= 'a' && var6 <= 'z') {
         var3.append(var6);

         while(var4 + 1 < var5) {
            char var8 = var1.charAt(var4 + 1);
            if (var8 != var6) {
               break;
            }

            var3.append(var6);
            ++var4;
         }
      } else {
         var3.append('\'');

         for(boolean var7 = false; var4 < var5; ++var4) {
            var6 = var1.charAt(var4);
            if (var6 == '\'') {
               if (var4 + 1 < var5 && var1.charAt(var4 + 1) == '\'') {
                  ++var4;
                  var3.append(var6);
               } else {
                  var7 = !var7;
               }
            } else {
               if (!var7 && (var6 >= 'A' && var6 <= 'Z' || var6 >= 'a' && var6 <= 'z')) {
                  --var4;
                  break;
               }

               var3.append(var6);
            }
         }
      }

      var2[0] = var4;
      return var3.toString();
   }

   protected FastDatePrinter.NumberRule selectNumberRule(int var1, int var2) {
      switch(var2) {
      case 1:
         return new FastDatePrinter.UnpaddedNumberField(var1);
      case 2:
         return new FastDatePrinter.TwoDigitNumberField(var1);
      default:
         return new FastDatePrinter.PaddedNumberField(var1, var2);
      }
   }

   /** @deprecated */
   @Deprecated
   public StringBuilder format(Object var1, StringBuilder var2, FieldPosition var3) {
      if (var1 instanceof Date) {
         return (StringBuilder)this.format((Date)((Date)var1), var2);
      } else if (var1 instanceof Calendar) {
         return (StringBuilder)this.format((Calendar)((Calendar)var1), var2);
      } else if (var1 instanceof Long) {
         return (StringBuilder)this.format((Long)var1, var2);
      } else {
         throw new IllegalArgumentException("Unknown class: " + (var1 == null ? "<null>" : var1.getClass().getName()));
      }
   }

   String format(Object var1) {
      if (var1 instanceof Date) {
         return this.format((Date)var1);
      } else if (var1 instanceof Calendar) {
         return this.format((Calendar)var1);
      } else if (var1 instanceof Long) {
         return this.format((Long)var1);
      } else {
         throw new IllegalArgumentException("Unknown class: " + (var1 == null ? "<null>" : var1.getClass().getName()));
      }
   }

   public String format(long var1) {
      Calendar var3 = this.newCalendar();
      var3.setTimeInMillis(var1);
      return this.applyRulesToString(var3);
   }

   private String applyRulesToString(Calendar var1) {
      return ((StringBuilder)this.applyRules(var1, (Appendable)(new StringBuilder(this.mMaxLengthEstimate)))).toString();
   }

   private Calendar newCalendar() {
      return Calendar.getInstance(this.mTimeZone, this.mLocale);
   }

   public String format(Date var1) {
      Calendar var2 = this.newCalendar();
      var2.setTime(var1);
      return this.applyRulesToString(var2);
   }

   public String format(Calendar var1) {
      return ((StringBuilder)this.format((Calendar)var1, new StringBuilder(this.mMaxLengthEstimate))).toString();
   }

   public <B extends Appendable> B format(long var1, B var3) {
      Calendar var4 = this.newCalendar();
      var4.setTimeInMillis(var1);
      return this.applyRules(var4, var3);
   }

   public <B extends Appendable> B format(Date var1, B var2) {
      Calendar var3 = this.newCalendar();
      var3.setTime(var1);
      return this.applyRules(var3, var2);
   }

   public <B extends Appendable> B format(Calendar var1, B var2) {
      if (!var1.getTimeZone().equals(this.mTimeZone)) {
         var1 = (Calendar)var1.clone();
         var1.setTimeZone(this.mTimeZone);
      }

      return this.applyRules(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   protected StringBuffer applyRules(Calendar var1, StringBuffer var2) {
      return (StringBuffer)this.applyRules(var1, (Appendable)var2);
   }

   private <B extends Appendable> B applyRules(Calendar var1, B var2) {
      try {
         FastDatePrinter.Rule[] var3 = this.mRules;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            FastDatePrinter.Rule var6 = var3[var5];
            var6.appendTo(var2, var1);
         }
      } catch (IOException var7) {
         Throwables.rethrow(var7);
      }

      return var2;
   }

   public String getPattern() {
      return this.mPattern;
   }

   public TimeZone getTimeZone() {
      return this.mTimeZone;
   }

   public Locale getLocale() {
      return this.mLocale;
   }

   public int getMaxLengthEstimate() {
      return this.mMaxLengthEstimate;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof FastDatePrinter)) {
         return false;
      } else {
         FastDatePrinter var2 = (FastDatePrinter)var1;
         return this.mPattern.equals(var2.mPattern) && this.mTimeZone.equals(var2.mTimeZone) && this.mLocale.equals(var2.mLocale);
      }
   }

   public int hashCode() {
      return this.mPattern.hashCode() + 13 * (this.mTimeZone.hashCode() + 13 * this.mLocale.hashCode());
   }

   public String toString() {
      return "FastDatePrinter[" + this.mPattern + "," + this.mLocale + "," + this.mTimeZone.getID() + "]";
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init();
   }

   private static void appendDigits(Appendable var0, int var1) throws IOException {
      var0.append((char)(var1 / 10 + 48));
      var0.append((char)(var1 % 10 + 48));
   }

   private static void appendFullDigits(Appendable var0, int var1, int var2) throws IOException {
      int var4;
      if (var1 < 10000) {
         int var3 = 4;
         if (var1 < 1000) {
            --var3;
            if (var1 < 100) {
               --var3;
               if (var1 < 10) {
                  --var3;
               }
            }
         }

         for(var4 = var2 - var3; var4 > 0; --var4) {
            var0.append('0');
         }

         switch(var3) {
         case 4:
            var0.append((char)(var1 / 1000 + 48));
            var1 %= 1000;
         case 3:
            if (var1 >= 100) {
               var0.append((char)(var1 / 100 + 48));
               var1 %= 100;
            } else {
               var0.append('0');
            }
         case 2:
            if (var1 >= 10) {
               var0.append((char)(var1 / 10 + 48));
               var1 %= 10;
            } else {
               var0.append('0');
            }
         case 1:
            var0.append((char)(var1 + 48));
         }
      } else {
         char[] var5 = new char[10];

         for(var4 = 0; var1 != 0; var1 /= 10) {
            var5[var4++] = (char)(var1 % 10 + 48);
         }

         while(var4 < var2) {
            var0.append('0');
            --var2;
         }

         while(true) {
            --var4;
            if (var4 < 0) {
               break;
            }

            var0.append(var5[var4]);
         }
      }

   }

   static String getTimeZoneDisplay(TimeZone var0, boolean var1, int var2, Locale var3) {
      FastDatePrinter.TimeZoneDisplayKey var4 = new FastDatePrinter.TimeZoneDisplayKey(var0, var1, var2, var3);
      String var5 = (String)cTimeZoneDisplayCache.get(var4);
      if (var5 == null) {
         var5 = var0.getDisplayName(var1, var2, var3);
         String var6 = (String)cTimeZoneDisplayCache.putIfAbsent(var4, var5);
         if (var6 != null) {
            var5 = var6;
         }
      }

      return var5;
   }

   private static class TimeZoneDisplayKey {
      private final TimeZone mTimeZone;
      private final int mStyle;
      private final Locale mLocale;

      TimeZoneDisplayKey(TimeZone var1, boolean var2, int var3, Locale var4) {
         super();
         this.mTimeZone = var1;
         if (var2) {
            this.mStyle = var3 | -2147483648;
         } else {
            this.mStyle = var3;
         }

         this.mLocale = var4;
      }

      public int hashCode() {
         return (this.mStyle * 31 + this.mLocale.hashCode()) * 31 + this.mTimeZone.hashCode();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof FastDatePrinter.TimeZoneDisplayKey)) {
            return false;
         } else {
            FastDatePrinter.TimeZoneDisplayKey var2 = (FastDatePrinter.TimeZoneDisplayKey)var1;
            return this.mTimeZone.equals(var2.mTimeZone) && this.mStyle == var2.mStyle && this.mLocale.equals(var2.mLocale);
         }
      }
   }

   private static class Iso8601_Rule implements FastDatePrinter.Rule {
      static final FastDatePrinter.Iso8601_Rule ISO8601_HOURS = new FastDatePrinter.Iso8601_Rule(3);
      static final FastDatePrinter.Iso8601_Rule ISO8601_HOURS_MINUTES = new FastDatePrinter.Iso8601_Rule(5);
      static final FastDatePrinter.Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new FastDatePrinter.Iso8601_Rule(6);
      final int length;

      static FastDatePrinter.Iso8601_Rule getRule(int var0) {
         switch(var0) {
         case 1:
            return ISO8601_HOURS;
         case 2:
            return ISO8601_HOURS_MINUTES;
         case 3:
            return ISO8601_HOURS_COLON_MINUTES;
         default:
            throw new IllegalArgumentException("invalid number of X");
         }
      }

      Iso8601_Rule(int var1) {
         super();
         this.length = var1;
      }

      public int estimateLength() {
         return this.length;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         int var3 = var2.get(15) + var2.get(16);
         if (var3 == 0) {
            var1.append("Z");
         } else {
            if (var3 < 0) {
               var1.append('-');
               var3 = -var3;
            } else {
               var1.append('+');
            }

            int var4 = var3 / 3600000;
            FastDatePrinter.appendDigits(var1, var4);
            if (this.length >= 5) {
               if (this.length == 6) {
                  var1.append(':');
               }

               int var5 = var3 / '\uea60' - 60 * var4;
               FastDatePrinter.appendDigits(var1, var5);
            }
         }
      }
   }

   private static class TimeZoneNumberRule implements FastDatePrinter.Rule {
      static final FastDatePrinter.TimeZoneNumberRule INSTANCE_COLON = new FastDatePrinter.TimeZoneNumberRule(true);
      static final FastDatePrinter.TimeZoneNumberRule INSTANCE_NO_COLON = new FastDatePrinter.TimeZoneNumberRule(false);
      final boolean mColon;

      TimeZoneNumberRule(boolean var1) {
         super();
         this.mColon = var1;
      }

      public int estimateLength() {
         return 5;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         int var3 = var2.get(15) + var2.get(16);
         if (var3 < 0) {
            var1.append('-');
            var3 = -var3;
         } else {
            var1.append('+');
         }

         int var4 = var3 / 3600000;
         FastDatePrinter.appendDigits(var1, var4);
         if (this.mColon) {
            var1.append(':');
         }

         int var5 = var3 / '\uea60' - 60 * var4;
         FastDatePrinter.appendDigits(var1, var5);
      }
   }

   private static class TimeZoneNameRule implements FastDatePrinter.Rule {
      private final Locale mLocale;
      private final int mStyle;
      private final String mStandard;
      private final String mDaylight;

      TimeZoneNameRule(TimeZone var1, Locale var2, int var3) {
         super();
         this.mLocale = var2;
         this.mStyle = var3;
         this.mStandard = FastDatePrinter.getTimeZoneDisplay(var1, false, var3, var2);
         this.mDaylight = FastDatePrinter.getTimeZoneDisplay(var1, true, var3, var2);
      }

      public int estimateLength() {
         return Math.max(this.mStandard.length(), this.mDaylight.length());
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         TimeZone var3 = var2.getTimeZone();
         if (var2.get(16) != 0) {
            var1.append(FastDatePrinter.getTimeZoneDisplay(var3, true, this.mStyle, this.mLocale));
         } else {
            var1.append(FastDatePrinter.getTimeZoneDisplay(var3, false, this.mStyle, this.mLocale));
         }

      }
   }

   private static class WeekYear implements FastDatePrinter.NumberRule {
      private final FastDatePrinter.NumberRule mRule;

      WeekYear(FastDatePrinter.NumberRule var1) {
         super();
         this.mRule = var1;
      }

      public int estimateLength() {
         return this.mRule.estimateLength();
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         this.mRule.appendTo(var1, var2.getWeekYear());
      }

      public void appendTo(Appendable var1, int var2) throws IOException {
         this.mRule.appendTo(var1, var2);
      }
   }

   private static class DayInWeekField implements FastDatePrinter.NumberRule {
      private final FastDatePrinter.NumberRule mRule;

      DayInWeekField(FastDatePrinter.NumberRule var1) {
         super();
         this.mRule = var1;
      }

      public int estimateLength() {
         return this.mRule.estimateLength();
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         int var3 = var2.get(7);
         this.mRule.appendTo(var1, var3 != 1 ? var3 - 1 : 7);
      }

      public void appendTo(Appendable var1, int var2) throws IOException {
         this.mRule.appendTo(var1, var2);
      }
   }

   private static class TwentyFourHourField implements FastDatePrinter.NumberRule {
      private final FastDatePrinter.NumberRule mRule;

      TwentyFourHourField(FastDatePrinter.NumberRule var1) {
         super();
         this.mRule = var1;
      }

      public int estimateLength() {
         return this.mRule.estimateLength();
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         int var3 = var2.get(11);
         if (var3 == 0) {
            var3 = var2.getMaximum(11) + 1;
         }

         this.mRule.appendTo(var1, var3);
      }

      public void appendTo(Appendable var1, int var2) throws IOException {
         this.mRule.appendTo(var1, var2);
      }
   }

   private static class TwelveHourField implements FastDatePrinter.NumberRule {
      private final FastDatePrinter.NumberRule mRule;

      TwelveHourField(FastDatePrinter.NumberRule var1) {
         super();
         this.mRule = var1;
      }

      public int estimateLength() {
         return this.mRule.estimateLength();
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         int var3 = var2.get(10);
         if (var3 == 0) {
            var3 = var2.getLeastMaximum(10) + 1;
         }

         this.mRule.appendTo(var1, var3);
      }

      public void appendTo(Appendable var1, int var2) throws IOException {
         this.mRule.appendTo(var1, var2);
      }
   }

   private static class TwoDigitMonthField implements FastDatePrinter.NumberRule {
      static final FastDatePrinter.TwoDigitMonthField INSTANCE = new FastDatePrinter.TwoDigitMonthField();

      TwoDigitMonthField() {
         super();
      }

      public int estimateLength() {
         return 2;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         this.appendTo(var1, var2.get(2) + 1);
      }

      public final void appendTo(Appendable var1, int var2) throws IOException {
         FastDatePrinter.appendDigits(var1, var2);
      }
   }

   private static class TwoDigitYearField implements FastDatePrinter.NumberRule {
      static final FastDatePrinter.TwoDigitYearField INSTANCE = new FastDatePrinter.TwoDigitYearField();

      TwoDigitYearField() {
         super();
      }

      public int estimateLength() {
         return 2;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         this.appendTo(var1, var2.get(1) % 100);
      }

      public final void appendTo(Appendable var1, int var2) throws IOException {
         FastDatePrinter.appendDigits(var1, var2);
      }
   }

   private static class TwoDigitNumberField implements FastDatePrinter.NumberRule {
      private final int mField;

      TwoDigitNumberField(int var1) {
         super();
         this.mField = var1;
      }

      public int estimateLength() {
         return 2;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         this.appendTo(var1, var2.get(this.mField));
      }

      public final void appendTo(Appendable var1, int var2) throws IOException {
         if (var2 < 100) {
            FastDatePrinter.appendDigits(var1, var2);
         } else {
            FastDatePrinter.appendFullDigits(var1, var2, 2);
         }

      }
   }

   private static class PaddedNumberField implements FastDatePrinter.NumberRule {
      private final int mField;
      private final int mSize;

      PaddedNumberField(int var1, int var2) {
         super();
         if (var2 < 3) {
            throw new IllegalArgumentException();
         } else {
            this.mField = var1;
            this.mSize = var2;
         }
      }

      public int estimateLength() {
         return this.mSize;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         this.appendTo(var1, var2.get(this.mField));
      }

      public final void appendTo(Appendable var1, int var2) throws IOException {
         FastDatePrinter.appendFullDigits(var1, var2, this.mSize);
      }
   }

   private static class UnpaddedMonthField implements FastDatePrinter.NumberRule {
      static final FastDatePrinter.UnpaddedMonthField INSTANCE = new FastDatePrinter.UnpaddedMonthField();

      UnpaddedMonthField() {
         super();
      }

      public int estimateLength() {
         return 2;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         this.appendTo(var1, var2.get(2) + 1);
      }

      public final void appendTo(Appendable var1, int var2) throws IOException {
         if (var2 < 10) {
            var1.append((char)(var2 + 48));
         } else {
            FastDatePrinter.appendDigits(var1, var2);
         }

      }
   }

   private static class UnpaddedNumberField implements FastDatePrinter.NumberRule {
      private final int mField;

      UnpaddedNumberField(int var1) {
         super();
         this.mField = var1;
      }

      public int estimateLength() {
         return 4;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         this.appendTo(var1, var2.get(this.mField));
      }

      public final void appendTo(Appendable var1, int var2) throws IOException {
         if (var2 < 10) {
            var1.append((char)(var2 + 48));
         } else if (var2 < 100) {
            FastDatePrinter.appendDigits(var1, var2);
         } else {
            FastDatePrinter.appendFullDigits(var1, var2, 1);
         }

      }
   }

   private static class TextField implements FastDatePrinter.Rule {
      private final int mField;
      private final String[] mValues;

      TextField(int var1, String[] var2) {
         super();
         this.mField = var1;
         this.mValues = var2;
      }

      public int estimateLength() {
         int var1 = 0;
         int var2 = this.mValues.length;

         while(true) {
            --var2;
            if (var2 < 0) {
               return var1;
            }

            int var3 = this.mValues[var2].length();
            if (var3 > var1) {
               var1 = var3;
            }
         }
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         var1.append(this.mValues[var2.get(this.mField)]);
      }
   }

   private static class StringLiteral implements FastDatePrinter.Rule {
      private final String mValue;

      StringLiteral(String var1) {
         super();
         this.mValue = var1;
      }

      public int estimateLength() {
         return this.mValue.length();
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         var1.append(this.mValue);
      }
   }

   private static class CharacterLiteral implements FastDatePrinter.Rule {
      private final char mValue;

      CharacterLiteral(char var1) {
         super();
         this.mValue = var1;
      }

      public int estimateLength() {
         return 1;
      }

      public void appendTo(Appendable var1, Calendar var2) throws IOException {
         var1.append(this.mValue);
      }
   }

   private interface NumberRule extends FastDatePrinter.Rule {
      void appendTo(Appendable var1, int var2) throws IOException;
   }

   private interface Rule {
      int estimateLength();

      void appendTo(Appendable var1, Calendar var2) throws IOException;
   }
}
