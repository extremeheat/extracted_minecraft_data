package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastDateParser implements DateParser, Serializable {
   private static final long serialVersionUID = 3L;
   static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
   private final String pattern;
   private final TimeZone timeZone;
   private final Locale locale;
   private final int century;
   private final int startYear;
   private transient List<FastDateParser.StrategyAndWidth> patterns;
   private static final Comparator<String> LONGER_FIRST_LOWERCASE = new Comparator<String>() {
      public int compare(String var1, String var2) {
         return var2.compareTo(var1);
      }
   };
   private static final ConcurrentMap<Locale, FastDateParser.Strategy>[] caches = new ConcurrentMap[17];
   private static final FastDateParser.Strategy ABBREVIATED_YEAR_STRATEGY = new FastDateParser.NumberStrategy(1) {
      int modify(FastDateParser var1, int var2) {
         return var2 < 100 ? var1.adjustYear(var2) : var2;
      }
   };
   private static final FastDateParser.Strategy NUMBER_MONTH_STRATEGY = new FastDateParser.NumberStrategy(2) {
      int modify(FastDateParser var1, int var2) {
         return var2 - 1;
      }
   };
   private static final FastDateParser.Strategy LITERAL_YEAR_STRATEGY = new FastDateParser.NumberStrategy(1);
   private static final FastDateParser.Strategy WEEK_OF_YEAR_STRATEGY = new FastDateParser.NumberStrategy(3);
   private static final FastDateParser.Strategy WEEK_OF_MONTH_STRATEGY = new FastDateParser.NumberStrategy(4);
   private static final FastDateParser.Strategy DAY_OF_YEAR_STRATEGY = new FastDateParser.NumberStrategy(6);
   private static final FastDateParser.Strategy DAY_OF_MONTH_STRATEGY = new FastDateParser.NumberStrategy(5);
   private static final FastDateParser.Strategy DAY_OF_WEEK_STRATEGY = new FastDateParser.NumberStrategy(7) {
      int modify(FastDateParser var1, int var2) {
         return var2 != 7 ? var2 + 1 : 1;
      }
   };
   private static final FastDateParser.Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new FastDateParser.NumberStrategy(8);
   private static final FastDateParser.Strategy HOUR_OF_DAY_STRATEGY = new FastDateParser.NumberStrategy(11);
   private static final FastDateParser.Strategy HOUR24_OF_DAY_STRATEGY = new FastDateParser.NumberStrategy(11) {
      int modify(FastDateParser var1, int var2) {
         return var2 == 24 ? 0 : var2;
      }
   };
   private static final FastDateParser.Strategy HOUR12_STRATEGY = new FastDateParser.NumberStrategy(10) {
      int modify(FastDateParser var1, int var2) {
         return var2 == 12 ? 0 : var2;
      }
   };
   private static final FastDateParser.Strategy HOUR_STRATEGY = new FastDateParser.NumberStrategy(10);
   private static final FastDateParser.Strategy MINUTE_STRATEGY = new FastDateParser.NumberStrategy(12);
   private static final FastDateParser.Strategy SECOND_STRATEGY = new FastDateParser.NumberStrategy(13);
   private static final FastDateParser.Strategy MILLISECOND_STRATEGY = new FastDateParser.NumberStrategy(14);

   protected FastDateParser(String var1, TimeZone var2, Locale var3) {
      this(var1, var2, var3, (Date)null);
   }

   protected FastDateParser(String var1, TimeZone var2, Locale var3, Date var4) {
      super();
      this.pattern = var1;
      this.timeZone = var2;
      this.locale = var3;
      Calendar var5 = Calendar.getInstance(var2, var3);
      int var6;
      if (var4 != null) {
         var5.setTime(var4);
         var6 = var5.get(1);
      } else if (var3.equals(JAPANESE_IMPERIAL)) {
         var6 = 0;
      } else {
         var5.setTime(new Date());
         var6 = var5.get(1) - 80;
      }

      this.century = var6 / 100 * 100;
      this.startYear = var6 - this.century;
      this.init(var5);
   }

   private void init(Calendar var1) {
      this.patterns = new ArrayList();
      FastDateParser.StrategyParser var2 = new FastDateParser.StrategyParser(this.pattern, var1);

      while(true) {
         FastDateParser.StrategyAndWidth var3 = var2.getNextStrategy();
         if (var3 == null) {
            return;
         }

         this.patterns.add(var3);
      }
   }

   private static boolean isFormatLetter(char var0) {
      return var0 >= 'A' && var0 <= 'Z' || var0 >= 'a' && var0 <= 'z';
   }

   public String getPattern() {
      return this.pattern;
   }

   public TimeZone getTimeZone() {
      return this.timeZone;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof FastDateParser)) {
         return false;
      } else {
         FastDateParser var2 = (FastDateParser)var1;
         return this.pattern.equals(var2.pattern) && this.timeZone.equals(var2.timeZone) && this.locale.equals(var2.locale);
      }
   }

   public int hashCode() {
      return this.pattern.hashCode() + 13 * (this.timeZone.hashCode() + 13 * this.locale.hashCode());
   }

   public String toString() {
      return "FastDateParser[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Calendar var2 = Calendar.getInstance(this.timeZone, this.locale);
      this.init(var2);
   }

   public Object parseObject(String var1) throws ParseException {
      return this.parse(var1);
   }

   public Date parse(String var1) throws ParseException {
      ParsePosition var2 = new ParsePosition(0);
      Date var3 = this.parse(var1, var2);
      if (var3 == null) {
         if (this.locale.equals(JAPANESE_IMPERIAL)) {
            throw new ParseException("(The " + this.locale + " locale does not support dates before 1868 AD)\nUnparseable date: \"" + var1, var2.getErrorIndex());
         } else {
            throw new ParseException("Unparseable date: " + var1, var2.getErrorIndex());
         }
      } else {
         return var3;
      }
   }

   public Object parseObject(String var1, ParsePosition var2) {
      return this.parse(var1, var2);
   }

   public Date parse(String var1, ParsePosition var2) {
      Calendar var3 = Calendar.getInstance(this.timeZone, this.locale);
      var3.clear();
      return this.parse(var1, var2, var3) ? var3.getTime() : null;
   }

   public boolean parse(String var1, ParsePosition var2, Calendar var3) {
      ListIterator var4 = this.patterns.listIterator();

      FastDateParser.StrategyAndWidth var5;
      int var6;
      do {
         if (!var4.hasNext()) {
            return true;
         }

         var5 = (FastDateParser.StrategyAndWidth)var4.next();
         var6 = var5.getMaxWidth(var4);
      } while(var5.strategy.parse(this, var3, var1, var2, var6));

      return false;
   }

   private static StringBuilder simpleQuote(StringBuilder var0, String var1) {
      int var2 = 0;

      while(var2 < var1.length()) {
         char var3 = var1.charAt(var2);
         switch(var3) {
         case '$':
         case '(':
         case ')':
         case '*':
         case '+':
         case '.':
         case '?':
         case '[':
         case '\\':
         case '^':
         case '{':
         case '|':
            var0.append('\\');
         default:
            var0.append(var3);
            ++var2;
         }
      }

      return var0;
   }

   private static Map<String, Integer> appendDisplayNames(Calendar var0, Locale var1, int var2, StringBuilder var3) {
      HashMap var4 = new HashMap();
      Map var5 = var0.getDisplayNames(var2, 0, var1);
      TreeSet var6 = new TreeSet(LONGER_FIRST_LOWERCASE);
      Iterator var7 = var5.entrySet().iterator();

      while(var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         String var9 = ((String)var8.getKey()).toLowerCase(var1);
         if (var6.add(var9)) {
            var4.put(var9, var8.getValue());
         }
      }

      var7 = var6.iterator();

      while(var7.hasNext()) {
         String var10 = (String)var7.next();
         simpleQuote(var3, var10).append('|');
      }

      return var4;
   }

   private int adjustYear(int var1) {
      int var2 = this.century + var1;
      return var1 >= this.startYear ? var2 : var2 + 100;
   }

   private FastDateParser.Strategy getStrategy(char var1, int var2, Calendar var3) {
      switch(var1) {
      case 'D':
         return DAY_OF_YEAR_STRATEGY;
      case 'E':
         return this.getLocaleSpecificStrategy(7, var3);
      case 'F':
         return DAY_OF_WEEK_IN_MONTH_STRATEGY;
      case 'G':
         return this.getLocaleSpecificStrategy(0, var3);
      case 'H':
         return HOUR_OF_DAY_STRATEGY;
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
         throw new IllegalArgumentException("Format '" + var1 + "' not supported");
      case 'K':
         return HOUR_STRATEGY;
      case 'M':
         return var2 >= 3 ? this.getLocaleSpecificStrategy(2, var3) : NUMBER_MONTH_STRATEGY;
      case 'S':
         return MILLISECOND_STRATEGY;
      case 'W':
         return WEEK_OF_MONTH_STRATEGY;
      case 'X':
         return FastDateParser.ISO8601TimeZoneStrategy.getStrategy(var2);
      case 'Y':
      case 'y':
         return var2 > 2 ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY;
      case 'Z':
         if (var2 == 2) {
            return FastDateParser.ISO8601TimeZoneStrategy.ISO_8601_3_STRATEGY;
         }
      case 'z':
         return this.getLocaleSpecificStrategy(15, var3);
      case 'a':
         return this.getLocaleSpecificStrategy(9, var3);
      case 'd':
         return DAY_OF_MONTH_STRATEGY;
      case 'h':
         return HOUR12_STRATEGY;
      case 'k':
         return HOUR24_OF_DAY_STRATEGY;
      case 'm':
         return MINUTE_STRATEGY;
      case 's':
         return SECOND_STRATEGY;
      case 'u':
         return DAY_OF_WEEK_STRATEGY;
      case 'w':
         return WEEK_OF_YEAR_STRATEGY;
      }
   }

   private static ConcurrentMap<Locale, FastDateParser.Strategy> getCache(int var0) {
      synchronized(caches) {
         if (caches[var0] == null) {
            caches[var0] = new ConcurrentHashMap(3);
         }

         return caches[var0];
      }
   }

   private FastDateParser.Strategy getLocaleSpecificStrategy(int var1, Calendar var2) {
      ConcurrentMap var3 = getCache(var1);
      Object var4 = (FastDateParser.Strategy)var3.get(this.locale);
      if (var4 == null) {
         var4 = var1 == 15 ? new FastDateParser.TimeZoneStrategy(this.locale) : new FastDateParser.CaseInsensitiveTextStrategy(var1, var2, this.locale);
         FastDateParser.Strategy var5 = (FastDateParser.Strategy)var3.putIfAbsent(this.locale, var4);
         if (var5 != null) {
            return var5;
         }
      }

      return (FastDateParser.Strategy)var4;
   }

   private static class ISO8601TimeZoneStrategy extends FastDateParser.PatternStrategy {
      private static final FastDateParser.Strategy ISO_8601_1_STRATEGY = new FastDateParser.ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}))");
      private static final FastDateParser.Strategy ISO_8601_2_STRATEGY = new FastDateParser.ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}\\d{2}))");
      private static final FastDateParser.Strategy ISO_8601_3_STRATEGY = new FastDateParser.ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}(?::)\\d{2}))");

      ISO8601TimeZoneStrategy(String var1) {
         super(null);
         this.createPattern(var1);
      }

      void setCalendar(FastDateParser var1, Calendar var2, String var3) {
         if (var3.equals("Z")) {
            var2.setTimeZone(TimeZone.getTimeZone("UTC"));
         } else {
            var2.setTimeZone(TimeZone.getTimeZone("GMT" + var3));
         }

      }

      static FastDateParser.Strategy getStrategy(int var0) {
         switch(var0) {
         case 1:
            return ISO_8601_1_STRATEGY;
         case 2:
            return ISO_8601_2_STRATEGY;
         case 3:
            return ISO_8601_3_STRATEGY;
         default:
            throw new IllegalArgumentException("invalid number of X");
         }
      }
   }

   static class TimeZoneStrategy extends FastDateParser.PatternStrategy {
      private static final String RFC_822_TIME_ZONE = "[+-]\\d{4}";
      private static final String GMT_OPTION = "GMT[+-]\\d{1,2}:\\d{2}";
      private final Locale locale;
      private final Map<String, FastDateParser.TimeZoneStrategy.TzInfo> tzNames = new HashMap();
      private static final int ID = 0;

      TimeZoneStrategy(Locale var1) {
         super(null);
         this.locale = var1;
         StringBuilder var2 = new StringBuilder();
         var2.append("((?iu)[+-]\\d{4}|GMT[+-]\\d{1,2}:\\d{2}");
         TreeSet var3 = new TreeSet(FastDateParser.LONGER_FIRST_LOWERCASE);
         String[][] var4 = DateFormatSymbols.getInstance(var1).getZoneStrings();
         String[][] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String[] var8 = var5[var7];
            String var9 = var8[0];
            if (!var9.equalsIgnoreCase("GMT")) {
               TimeZone var10 = TimeZone.getTimeZone(var9);
               FastDateParser.TimeZoneStrategy.TzInfo var11 = new FastDateParser.TimeZoneStrategy.TzInfo(var10, false);
               FastDateParser.TimeZoneStrategy.TzInfo var12 = var11;

               for(int var13 = 1; var13 < var8.length; ++var13) {
                  switch(var13) {
                  case 3:
                     var12 = new FastDateParser.TimeZoneStrategy.TzInfo(var10, true);
                     break;
                  case 5:
                     var12 = var11;
                  }

                  String var14 = var8[var13].toLowerCase(var1);
                  if (var3.add(var14)) {
                     this.tzNames.put(var14, var12);
                  }
               }
            }
         }

         Iterator var15 = var3.iterator();

         while(var15.hasNext()) {
            String var16 = (String)var15.next();
            FastDateParser.simpleQuote(var2.append('|'), var16);
         }

         var2.append(")");
         this.createPattern(var2);
      }

      void setCalendar(FastDateParser var1, Calendar var2, String var3) {
         TimeZone var4;
         if (var3.charAt(0) != '+' && var3.charAt(0) != '-') {
            if (var3.regionMatches(true, 0, "GMT", 0, 3)) {
               var4 = TimeZone.getTimeZone(var3.toUpperCase());
               var2.setTimeZone(var4);
            } else {
               FastDateParser.TimeZoneStrategy.TzInfo var5 = (FastDateParser.TimeZoneStrategy.TzInfo)this.tzNames.get(var3.toLowerCase(this.locale));
               var2.set(16, var5.dstOffset);
               var2.set(15, var5.zone.getRawOffset());
            }
         } else {
            var4 = TimeZone.getTimeZone("GMT" + var3);
            var2.setTimeZone(var4);
         }

      }

      private static class TzInfo {
         TimeZone zone;
         int dstOffset;

         TzInfo(TimeZone var1, boolean var2) {
            super();
            this.zone = var1;
            this.dstOffset = var2 ? var1.getDSTSavings() : 0;
         }
      }
   }

   private static class NumberStrategy extends FastDateParser.Strategy {
      private final int field;

      NumberStrategy(int var1) {
         super(null);
         this.field = var1;
      }

      boolean isNumber() {
         return true;
      }

      boolean parse(FastDateParser var1, Calendar var2, String var3, ParsePosition var4, int var5) {
         int var6 = var4.getIndex();
         int var7 = var3.length();
         char var8;
         int var9;
         if (var5 != 0) {
            var9 = var6 + var5;
            if (var7 > var9) {
               var7 = var9;
            }
         } else {
            while(true) {
               if (var6 < var7) {
                  var8 = var3.charAt(var6);
                  if (Character.isWhitespace(var8)) {
                     ++var6;
                     continue;
                  }
               }

               var4.setIndex(var6);
               break;
            }
         }

         while(var6 < var7) {
            var8 = var3.charAt(var6);
            if (!Character.isDigit(var8)) {
               break;
            }

            ++var6;
         }

         if (var4.getIndex() == var6) {
            var4.setErrorIndex(var6);
            return false;
         } else {
            var9 = Integer.parseInt(var3.substring(var4.getIndex(), var6));
            var4.setIndex(var6);
            var2.set(this.field, this.modify(var1, var9));
            return true;
         }
      }

      int modify(FastDateParser var1, int var2) {
         return var2;
      }
   }

   private static class CaseInsensitiveTextStrategy extends FastDateParser.PatternStrategy {
      private final int field;
      final Locale locale;
      private final Map<String, Integer> lKeyValues;

      CaseInsensitiveTextStrategy(int var1, Calendar var2, Locale var3) {
         super(null);
         this.field = var1;
         this.locale = var3;
         StringBuilder var4 = new StringBuilder();
         var4.append("((?iu)");
         this.lKeyValues = FastDateParser.appendDisplayNames(var2, var3, var1, var4);
         var4.setLength(var4.length() - 1);
         var4.append(")");
         this.createPattern(var4);
      }

      void setCalendar(FastDateParser var1, Calendar var2, String var3) {
         Integer var4 = (Integer)this.lKeyValues.get(var3.toLowerCase(this.locale));
         var2.set(this.field, var4);
      }
   }

   private static class CopyQuotedStrategy extends FastDateParser.Strategy {
      private final String formatField;

      CopyQuotedStrategy(String var1) {
         super(null);
         this.formatField = var1;
      }

      boolean isNumber() {
         return false;
      }

      boolean parse(FastDateParser var1, Calendar var2, String var3, ParsePosition var4, int var5) {
         for(int var6 = 0; var6 < this.formatField.length(); ++var6) {
            int var7 = var6 + var4.getIndex();
            if (var7 == var3.length()) {
               var4.setErrorIndex(var7);
               return false;
            }

            if (this.formatField.charAt(var6) != var3.charAt(var7)) {
               var4.setErrorIndex(var7);
               return false;
            }
         }

         var4.setIndex(this.formatField.length() + var4.getIndex());
         return true;
      }
   }

   private abstract static class PatternStrategy extends FastDateParser.Strategy {
      private Pattern pattern;

      private PatternStrategy() {
         super(null);
      }

      void createPattern(StringBuilder var1) {
         this.createPattern(var1.toString());
      }

      void createPattern(String var1) {
         this.pattern = Pattern.compile(var1);
      }

      boolean isNumber() {
         return false;
      }

      boolean parse(FastDateParser var1, Calendar var2, String var3, ParsePosition var4, int var5) {
         Matcher var6 = this.pattern.matcher(var3.substring(var4.getIndex()));
         if (!var6.lookingAt()) {
            var4.setErrorIndex(var4.getIndex());
            return false;
         } else {
            var4.setIndex(var4.getIndex() + var6.end(1));
            this.setCalendar(var1, var2, var6.group(1));
            return true;
         }
      }

      abstract void setCalendar(FastDateParser var1, Calendar var2, String var3);

      // $FF: synthetic method
      PatternStrategy(Object var1) {
         this();
      }
   }

   private abstract static class Strategy {
      private Strategy() {
         super();
      }

      boolean isNumber() {
         return false;
      }

      abstract boolean parse(FastDateParser var1, Calendar var2, String var3, ParsePosition var4, int var5);

      // $FF: synthetic method
      Strategy(Object var1) {
         this();
      }
   }

   private class StrategyParser {
      private final String pattern;
      private final Calendar definingCalendar;
      private int currentIdx;

      StrategyParser(String var2, Calendar var3) {
         super();
         this.pattern = var2;
         this.definingCalendar = var3;
      }

      FastDateParser.StrategyAndWidth getNextStrategy() {
         if (this.currentIdx >= this.pattern.length()) {
            return null;
         } else {
            char var1 = this.pattern.charAt(this.currentIdx);
            return FastDateParser.isFormatLetter(var1) ? this.letterPattern(var1) : this.literal();
         }
      }

      private FastDateParser.StrategyAndWidth letterPattern(char var1) {
         int var2 = this.currentIdx;

         while(++this.currentIdx < this.pattern.length() && this.pattern.charAt(this.currentIdx) == var1) {
         }

         int var3 = this.currentIdx - var2;
         return new FastDateParser.StrategyAndWidth(FastDateParser.this.getStrategy(var1, var3, this.definingCalendar), var3);
      }

      private FastDateParser.StrategyAndWidth literal() {
         boolean var1 = false;
         StringBuilder var2 = new StringBuilder();

         while(this.currentIdx < this.pattern.length()) {
            char var3 = this.pattern.charAt(this.currentIdx);
            if (!var1 && FastDateParser.isFormatLetter(var3)) {
               break;
            }

            if (var3 != '\'' || ++this.currentIdx != this.pattern.length() && this.pattern.charAt(this.currentIdx) == '\'') {
               ++this.currentIdx;
               var2.append(var3);
            } else {
               var1 = !var1;
            }
         }

         if (var1) {
            throw new IllegalArgumentException("Unterminated quote");
         } else {
            String var4 = var2.toString();
            return new FastDateParser.StrategyAndWidth(new FastDateParser.CopyQuotedStrategy(var4), var4.length());
         }
      }
   }

   private static class StrategyAndWidth {
      final FastDateParser.Strategy strategy;
      final int width;

      StrategyAndWidth(FastDateParser.Strategy var1, int var2) {
         super();
         this.strategy = var1;
         this.width = var2;
      }

      int getMaxWidth(ListIterator<FastDateParser.StrategyAndWidth> var1) {
         if (this.strategy.isNumber() && var1.hasNext()) {
            FastDateParser.Strategy var2 = ((FastDateParser.StrategyAndWidth)var1.next()).strategy;
            var1.previous();
            return var2.isNumber() ? this.width : 0;
         } else {
            return 0;
         }
      }
   }
}
