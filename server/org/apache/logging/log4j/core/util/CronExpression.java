package org.apache.logging.log4j.core.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;

public final class CronExpression {
   protected static final int SECOND = 0;
   protected static final int MINUTE = 1;
   protected static final int HOUR = 2;
   protected static final int DAY_OF_MONTH = 3;
   protected static final int MONTH = 4;
   protected static final int DAY_OF_WEEK = 5;
   protected static final int YEAR = 6;
   protected static final int ALL_SPEC_INT = 99;
   protected static final int NO_SPEC_INT = 98;
   protected static final Integer ALL_SPEC = 99;
   protected static final Integer NO_SPEC = 98;
   protected static final Map<String, Integer> monthMap = new HashMap(20);
   protected static final Map<String, Integer> dayMap = new HashMap(60);
   private final String cronExpression;
   private TimeZone timeZone = null;
   protected transient TreeSet<Integer> seconds;
   protected transient TreeSet<Integer> minutes;
   protected transient TreeSet<Integer> hours;
   protected transient TreeSet<Integer> daysOfMonth;
   protected transient TreeSet<Integer> months;
   protected transient TreeSet<Integer> daysOfWeek;
   protected transient TreeSet<Integer> years;
   protected transient boolean lastdayOfWeek = false;
   protected transient int nthdayOfWeek = 0;
   protected transient boolean lastdayOfMonth = false;
   protected transient boolean nearestWeekday = false;
   protected transient int lastdayOffset = 0;
   protected transient boolean expressionParsed = false;
   public static final int MAX_YEAR;
   public static final Calendar MIN_CAL;
   public static final Date MIN_DATE;

   public CronExpression(String var1) throws ParseException {
      super();
      if (var1 == null) {
         throw new IllegalArgumentException("cronExpression cannot be null");
      } else {
         this.cronExpression = var1.toUpperCase(Locale.US);
         this.buildExpression(this.cronExpression);
      }
   }

   public boolean isSatisfiedBy(Date var1) {
      Calendar var2 = Calendar.getInstance(this.getTimeZone());
      var2.setTime(var1);
      var2.set(14, 0);
      Date var3 = var2.getTime();
      var2.add(13, -1);
      Date var4 = this.getTimeAfter(var2.getTime());
      return var4 != null && var4.equals(var3);
   }

   public Date getNextValidTimeAfter(Date var1) {
      return this.getTimeAfter(var1);
   }

   public Date getNextInvalidTimeAfter(Date var1) {
      long var2 = 1000L;
      Calendar var4 = Calendar.getInstance(this.getTimeZone());
      var4.setTime(var1);
      var4.set(14, 0);
      Date var5 = var4.getTime();

      while(var2 == 1000L) {
         Date var6 = this.getTimeAfter(var5);
         if (var6 == null) {
            break;
         }

         var2 = var6.getTime() - var5.getTime();
         if (var2 == 1000L) {
            var5 = var6;
         }
      }

      return new Date(var5.getTime() + 1000L);
   }

   public TimeZone getTimeZone() {
      if (this.timeZone == null) {
         this.timeZone = TimeZone.getDefault();
      }

      return this.timeZone;
   }

   public void setTimeZone(TimeZone var1) {
      this.timeZone = var1;
   }

   public String toString() {
      return this.cronExpression;
   }

   public static boolean isValidExpression(String var0) {
      try {
         new CronExpression(var0);
         return true;
      } catch (ParseException var2) {
         return false;
      }
   }

   public static void validateExpression(String var0) throws ParseException {
      new CronExpression(var0);
   }

   protected void buildExpression(String var1) throws ParseException {
      this.expressionParsed = true;

      try {
         if (this.seconds == null) {
            this.seconds = new TreeSet();
         }

         if (this.minutes == null) {
            this.minutes = new TreeSet();
         }

         if (this.hours == null) {
            this.hours = new TreeSet();
         }

         if (this.daysOfMonth == null) {
            this.daysOfMonth = new TreeSet();
         }

         if (this.months == null) {
            this.months = new TreeSet();
         }

         if (this.daysOfWeek == null) {
            this.daysOfWeek = new TreeSet();
         }

         if (this.years == null) {
            this.years = new TreeSet();
         }

         int var2 = 0;

         for(StringTokenizer var3 = new StringTokenizer(var1, " \t", false); var3.hasMoreTokens() && var2 <= 6; ++var2) {
            String var4 = var3.nextToken().trim();
            if (var2 == 3 && var4.indexOf(76) != -1 && var4.length() > 1 && var4.contains(",")) {
               throw new ParseException("Support for specifying 'L' and 'LW' with other days of the month is not implemented", -1);
            }

            if (var2 == 5 && var4.indexOf(76) != -1 && var4.length() > 1 && var4.contains(",")) {
               throw new ParseException("Support for specifying 'L' with other days of the week is not implemented", -1);
            }

            if (var2 == 5 && var4.indexOf(35) != -1 && var4.indexOf(35, var4.indexOf(35) + 1) != -1) {
               throw new ParseException("Support for specifying multiple \"nth\" days is not implemented.", -1);
            }

            StringTokenizer var5 = new StringTokenizer(var4, ",");

            while(var5.hasMoreTokens()) {
               String var6 = var5.nextToken();
               this.storeExpressionVals(0, var6, var2);
            }
         }

         if (var2 <= 5) {
            throw new ParseException("Unexpected end of expression.", var1.length());
         } else {
            if (var2 <= 6) {
               this.storeExpressionVals(0, "*", 6);
            }

            TreeSet var10 = this.getSet(5);
            TreeSet var11 = this.getSet(3);
            boolean var12 = !var11.contains(NO_SPEC);
            boolean var7 = !var10.contains(NO_SPEC);
            if ((!var12 || var7) && (!var7 || var12)) {
               throw new ParseException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.", 0);
            }
         }
      } catch (ParseException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new ParseException("Illegal cron expression format (" + var9.toString() + ")", 0);
      }
   }

   protected int storeExpressionVals(int var1, String var2, int var3) throws ParseException {
      byte var4 = 0;
      int var5 = this.skipWhiteSpace(var1, var2);
      if (var5 >= var2.length()) {
         return var5;
      } else {
         char var6 = var2.charAt(var5);
         if (var6 >= 'A' && var6 <= 'Z' && !var2.equals("L") && !var2.equals("LW") && !var2.matches("^L-[0-9]*[W]?")) {
            String var15 = var2.substring(var5, var5 + 3);
            boolean var14 = true;
            int var9 = -1;
            int var16;
            if (var3 == 4) {
               var16 = this.getMonthNumber(var15) + 1;
               if (var16 <= 0) {
                  throw new ParseException("Invalid Month value: '" + var15 + "'", var5);
               }

               if (var2.length() > var5 + 3) {
                  var6 = var2.charAt(var5 + 3);
                  if (var6 == '-') {
                     var5 += 4;
                     var15 = var2.substring(var5, var5 + 3);
                     var9 = this.getMonthNumber(var15) + 1;
                     if (var9 <= 0) {
                        throw new ParseException("Invalid Month value: '" + var15 + "'", var5);
                     }
                  }
               }
            } else {
               if (var3 != 5) {
                  throw new ParseException("Illegal characters for this position: '" + var15 + "'", var5);
               }

               var16 = this.getDayOfWeekNumber(var15);
               if (var16 < 0) {
                  throw new ParseException("Invalid Day-of-Week value: '" + var15 + "'", var5);
               }

               if (var2.length() > var5 + 3) {
                  var6 = var2.charAt(var5 + 3);
                  if (var6 == '-') {
                     var5 += 4;
                     var15 = var2.substring(var5, var5 + 3);
                     var9 = this.getDayOfWeekNumber(var15);
                     if (var9 < 0) {
                        throw new ParseException("Invalid Day-of-Week value: '" + var15 + "'", var5);
                     }
                  } else if (var6 == '#') {
                     try {
                        var5 += 4;
                        this.nthdayOfWeek = Integer.parseInt(var2.substring(var5));
                        if (this.nthdayOfWeek < 1 || this.nthdayOfWeek > 5) {
                           throw new Exception();
                        }
                     } catch (Exception var11) {
                        throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", var5);
                     }
                  } else if (var6 == 'L') {
                     this.lastdayOfWeek = true;
                     ++var5;
                  }
               }
            }

            if (var9 != -1) {
               var4 = 1;
            }

            this.addToSet(var16, var9, var4, var3);
            return var5 + 3;
         } else {
            int var7;
            if (var6 == '?') {
               ++var5;
               if (var5 + 1 < var2.length() && var2.charAt(var5) != ' ' && var2.charAt(var5 + 1) != '\t') {
                  throw new ParseException("Illegal character after '?': " + var2.charAt(var5), var5);
               } else if (var3 != 5 && var3 != 3) {
                  throw new ParseException("'?' can only be specfied for Day-of-Month or Day-of-Week.", var5);
               } else {
                  if (var3 == 5 && !this.lastdayOfMonth) {
                     var7 = (Integer)this.daysOfMonth.last();
                     if (var7 == 98) {
                        throw new ParseException("'?' can only be specfied for Day-of-Month -OR- Day-of-Week.", var5);
                     }
                  }

                  this.addToSet(98, -1, 0, var3);
                  return var5;
               }
            } else if (var6 != '*' && var6 != '/') {
               if (var6 == 'L') {
                  ++var5;
                  if (var3 == 3) {
                     this.lastdayOfMonth = true;
                  }

                  if (var3 == 5) {
                     this.addToSet(7, 7, 0, var3);
                  }

                  if (var3 == 3 && var2.length() > var5) {
                     var6 = var2.charAt(var5);
                     if (var6 == '-') {
                        CronExpression.ValueSet var13 = this.getValue(0, var2, var5 + 1);
                        this.lastdayOffset = var13.value;
                        if (this.lastdayOffset > 30) {
                           throw new ParseException("Offset from last day must be <= 30", var5 + 1);
                        }

                        var5 = var13.pos;
                     }

                     if (var2.length() > var5) {
                        var6 = var2.charAt(var5);
                        if (var6 == 'W') {
                           this.nearestWeekday = true;
                           ++var5;
                        }
                     }
                  }

                  return var5;
               } else if (var6 >= '0' && var6 <= '9') {
                  var7 = Integer.parseInt(String.valueOf(var6));
                  ++var5;
                  if (var5 >= var2.length()) {
                     this.addToSet(var7, -1, -1, var3);
                     return var5;
                  } else {
                     var6 = var2.charAt(var5);
                     if (var6 >= '0' && var6 <= '9') {
                        CronExpression.ValueSet var8 = this.getValue(var7, var2, var5);
                        var7 = var8.value;
                        var5 = var8.pos;
                     }

                     var5 = this.checkNext(var5, var2, var7, var3);
                     return var5;
                  }
               } else {
                  throw new ParseException("Unexpected character: " + var6, var5);
               }
            } else if (var6 == '*' && var5 + 1 >= var2.length()) {
               this.addToSet(99, -1, var4, var3);
               return var5 + 1;
            } else if (var6 == '/' && (var5 + 1 >= var2.length() || var2.charAt(var5 + 1) == ' ' || var2.charAt(var5 + 1) == '\t')) {
               throw new ParseException("'/' must be followed by an integer.", var5);
            } else {
               if (var6 == '*') {
                  ++var5;
               }

               var6 = var2.charAt(var5);
               int var12;
               if (var6 != '/') {
                  var12 = 1;
               } else {
                  ++var5;
                  if (var5 >= var2.length()) {
                     throw new ParseException("Unexpected end of string.", var5);
                  }

                  var12 = this.getNumericValue(var2, var5);
                  ++var5;
                  if (var12 > 10) {
                     ++var5;
                  }

                  if (var12 > 59 && (var3 == 0 || var3 == 1)) {
                     throw new ParseException("Increment > 60 : " + var12, var5);
                  }

                  if (var12 > 23 && var3 == 2) {
                     throw new ParseException("Increment > 24 : " + var12, var5);
                  }

                  if (var12 > 31 && var3 == 3) {
                     throw new ParseException("Increment > 31 : " + var12, var5);
                  }

                  if (var12 > 7 && var3 == 5) {
                     throw new ParseException("Increment > 7 : " + var12, var5);
                  }

                  if (var12 > 12 && var3 == 4) {
                     throw new ParseException("Increment > 12 : " + var12, var5);
                  }
               }

               this.addToSet(99, -1, var12, var3);
               return var5;
            }
         }
      }
   }

   protected int checkNext(int var1, String var2, int var3, int var4) throws ParseException {
      byte var5 = -1;
      if (var1 >= var2.length()) {
         this.addToSet(var3, var5, -1, var4);
         return var1;
      } else {
         char var7 = var2.charAt(var1);
         int var6;
         TreeSet var8;
         if (var7 == 'L') {
            if (var4 == 5) {
               if (var3 >= 1 && var3 <= 7) {
                  this.lastdayOfWeek = true;
                  var8 = this.getSet(var4);
                  var8.add(var3);
                  var6 = var1 + 1;
                  return var6;
               } else {
                  throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
               }
            } else {
               throw new ParseException("'L' option is not valid here. (pos=" + var1 + ")", var1);
            }
         } else if (var7 == 'W') {
            if (var4 == 3) {
               this.nearestWeekday = true;
               if (var3 > 31) {
                  throw new ParseException("The 'W' option does not make sense with values larger than 31 (max number of days in a month)", var1);
               } else {
                  var8 = this.getSet(var4);
                  var8.add(var3);
                  var6 = var1 + 1;
                  return var6;
               }
            } else {
               throw new ParseException("'W' option is not valid here. (pos=" + var1 + ")", var1);
            }
         } else if (var7 != '#') {
            CronExpression.ValueSet var9;
            int var14;
            if (var7 == '-') {
               var6 = var1 + 1;
               var7 = var2.charAt(var6);
               var14 = Integer.parseInt(String.valueOf(var7));
               int var13 = var14;
               ++var6;
               if (var6 >= var2.length()) {
                  this.addToSet(var3, var14, 1, var4);
                  return var6;
               } else {
                  var7 = var2.charAt(var6);
                  if (var7 >= '0' && var7 <= '9') {
                     var9 = this.getValue(var14, var2, var6);
                     var13 = var9.value;
                     var6 = var9.pos;
                  }

                  if (var6 < var2.length() && var2.charAt(var6) == '/') {
                     ++var6;
                     var7 = var2.charAt(var6);
                     int var15 = Integer.parseInt(String.valueOf(var7));
                     ++var6;
                     if (var6 >= var2.length()) {
                        this.addToSet(var3, var13, var15, var4);
                        return var6;
                     } else {
                        var7 = var2.charAt(var6);
                        if (var7 >= '0' && var7 <= '9') {
                           CronExpression.ValueSet var16 = this.getValue(var15, var2, var6);
                           int var11 = var16.value;
                           this.addToSet(var3, var13, var11, var4);
                           var6 = var16.pos;
                           return var6;
                        } else {
                           this.addToSet(var3, var13, var15, var4);
                           return var6;
                        }
                     }
                  } else {
                     this.addToSet(var3, var13, 1, var4);
                     return var6;
                  }
               }
            } else if (var7 == '/') {
               var6 = var1 + 1;
               var7 = var2.charAt(var6);
               var14 = Integer.parseInt(String.valueOf(var7));
               ++var6;
               if (var6 >= var2.length()) {
                  this.addToSet(var3, var5, var14, var4);
                  return var6;
               } else {
                  var7 = var2.charAt(var6);
                  if (var7 >= '0' && var7 <= '9') {
                     var9 = this.getValue(var14, var2, var6);
                     int var10 = var9.value;
                     this.addToSet(var3, var5, var10, var4);
                     var6 = var9.pos;
                     return var6;
                  } else {
                     throw new ParseException("Unexpected character '" + var7 + "' after '/'", var6);
                  }
               }
            } else {
               this.addToSet(var3, var5, 0, var4);
               var6 = var1 + 1;
               return var6;
            }
         } else if (var4 != 5) {
            throw new ParseException("'#' option is not valid here. (pos=" + var1 + ")", var1);
         } else {
            var6 = var1 + 1;

            try {
               this.nthdayOfWeek = Integer.parseInt(var2.substring(var6));
               if (this.nthdayOfWeek < 1 || this.nthdayOfWeek > 5) {
                  throw new Exception();
               }
            } catch (Exception var12) {
               throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", var6);
            }

            var8 = this.getSet(var4);
            var8.add(var3);
            ++var6;
            return var6;
         }
      }
   }

   public String getCronExpression() {
      return this.cronExpression;
   }

   public String getExpressionSummary() {
      StringBuilder var1 = new StringBuilder();
      var1.append("seconds: ");
      var1.append(this.getExpressionSetSummary((Set)this.seconds));
      var1.append("\n");
      var1.append("minutes: ");
      var1.append(this.getExpressionSetSummary((Set)this.minutes));
      var1.append("\n");
      var1.append("hours: ");
      var1.append(this.getExpressionSetSummary((Set)this.hours));
      var1.append("\n");
      var1.append("daysOfMonth: ");
      var1.append(this.getExpressionSetSummary((Set)this.daysOfMonth));
      var1.append("\n");
      var1.append("months: ");
      var1.append(this.getExpressionSetSummary((Set)this.months));
      var1.append("\n");
      var1.append("daysOfWeek: ");
      var1.append(this.getExpressionSetSummary((Set)this.daysOfWeek));
      var1.append("\n");
      var1.append("lastdayOfWeek: ");
      var1.append(this.lastdayOfWeek);
      var1.append("\n");
      var1.append("nearestWeekday: ");
      var1.append(this.nearestWeekday);
      var1.append("\n");
      var1.append("NthDayOfWeek: ");
      var1.append(this.nthdayOfWeek);
      var1.append("\n");
      var1.append("lastdayOfMonth: ");
      var1.append(this.lastdayOfMonth);
      var1.append("\n");
      var1.append("years: ");
      var1.append(this.getExpressionSetSummary((Set)this.years));
      var1.append("\n");
      return var1.toString();
   }

   protected String getExpressionSetSummary(Set<Integer> var1) {
      if (var1.contains(NO_SPEC)) {
         return "?";
      } else if (var1.contains(ALL_SPEC)) {
         return "*";
      } else {
         StringBuilder var2 = new StringBuilder();
         Iterator var3 = var1.iterator();

         for(boolean var4 = true; var3.hasNext(); var4 = false) {
            Integer var5 = (Integer)var3.next();
            String var6 = var5.toString();
            if (!var4) {
               var2.append(",");
            }

            var2.append(var6);
         }

         return var2.toString();
      }
   }

   protected String getExpressionSetSummary(ArrayList<Integer> var1) {
      if (var1.contains(NO_SPEC)) {
         return "?";
      } else if (var1.contains(ALL_SPEC)) {
         return "*";
      } else {
         StringBuilder var2 = new StringBuilder();
         Iterator var3 = var1.iterator();

         for(boolean var4 = true; var3.hasNext(); var4 = false) {
            Integer var5 = (Integer)var3.next();
            String var6 = var5.toString();
            if (!var4) {
               var2.append(",");
            }

            var2.append(var6);
         }

         return var2.toString();
      }
   }

   protected int skipWhiteSpace(int var1, String var2) {
      while(var1 < var2.length() && (var2.charAt(var1) == ' ' || var2.charAt(var1) == '\t')) {
         ++var1;
      }

      return var1;
   }

   protected int findNextWhiteSpace(int var1, String var2) {
      while(var1 < var2.length() && (var2.charAt(var1) != ' ' || var2.charAt(var1) != '\t')) {
         ++var1;
      }

      return var1;
   }

   protected void addToSet(int var1, int var2, int var3, int var4) throws ParseException {
      TreeSet var5 = this.getSet(var4);
      if (var4 != 0 && var4 != 1) {
         if (var4 == 2) {
            if ((var1 < 0 || var1 > 23 || var2 > 23) && var1 != 99) {
               throw new ParseException("Hour values must be between 0 and 23", -1);
            }
         } else if (var4 == 3) {
            if ((var1 < 1 || var1 > 31 || var2 > 31) && var1 != 99 && var1 != 98) {
               throw new ParseException("Day of month values must be between 1 and 31", -1);
            }
         } else if (var4 == 4) {
            if ((var1 < 1 || var1 > 12 || var2 > 12) && var1 != 99) {
               throw new ParseException("Month values must be between 1 and 12", -1);
            }
         } else if (var4 == 5 && (var1 == 0 || var1 > 7 || var2 > 7) && var1 != 99 && var1 != 98) {
            throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
         }
      } else if ((var1 < 0 || var1 > 59 || var2 > 59) && var1 != 99) {
         throw new ParseException("Minute and Second values must be between 0 and 59", -1);
      }

      if ((var3 == 0 || var3 == -1) && var1 != 99) {
         if (var1 != -1) {
            var5.add(var1);
         } else {
            var5.add(NO_SPEC);
         }

      } else {
         int var6 = var1;
         int var7 = var2;
         if (var1 == 99 && var3 <= 0) {
            var3 = 1;
            var5.add(ALL_SPEC);
         }

         if (var4 != 0 && var4 != 1) {
            if (var4 == 2) {
               if (var2 == -1) {
                  var7 = 23;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 0;
               }
            } else if (var4 == 3) {
               if (var2 == -1) {
                  var7 = 31;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 1;
               }
            } else if (var4 == 4) {
               if (var2 == -1) {
                  var7 = 12;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 1;
               }
            } else if (var4 == 5) {
               if (var2 == -1) {
                  var7 = 7;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 1;
               }
            } else if (var4 == 6) {
               if (var2 == -1) {
                  var7 = MAX_YEAR;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 1970;
               }
            }
         } else {
            if (var2 == -1) {
               var7 = 59;
            }

            if (var1 == -1 || var1 == 99) {
               var6 = 0;
            }
         }

         byte var8 = -1;
         if (var7 < var6) {
            switch(var4) {
            case 0:
               var8 = 60;
               break;
            case 1:
               var8 = 60;
               break;
            case 2:
               var8 = 24;
               break;
            case 3:
               var8 = 31;
               break;
            case 4:
               var8 = 12;
               break;
            case 5:
               var8 = 7;
               break;
            case 6:
               throw new IllegalArgumentException("Start year must be less than stop year");
            default:
               throw new IllegalArgumentException("Unexpected type encountered");
            }

            var7 += var8;
         }

         for(int var9 = var6; var9 <= var7; var9 += var3) {
            if (var8 == -1) {
               var5.add(var9);
            } else {
               int var10 = var9 % var8;
               if (var10 == 0 && (var4 == 4 || var4 == 5 || var4 == 3)) {
                  var10 = var8;
               }

               var5.add(var10);
            }
         }

      }
   }

   TreeSet<Integer> getSet(int var1) {
      switch(var1) {
      case 0:
         return this.seconds;
      case 1:
         return this.minutes;
      case 2:
         return this.hours;
      case 3:
         return this.daysOfMonth;
      case 4:
         return this.months;
      case 5:
         return this.daysOfWeek;
      case 6:
         return this.years;
      default:
         return null;
      }
   }

   protected CronExpression.ValueSet getValue(int var1, String var2, int var3) {
      char var4 = var2.charAt(var3);

      StringBuilder var5;
      for(var5 = new StringBuilder(String.valueOf(var1)); var4 >= '0' && var4 <= '9'; var4 = var2.charAt(var3)) {
         var5.append(var4);
         ++var3;
         if (var3 >= var2.length()) {
            break;
         }
      }

      CronExpression.ValueSet var6 = new CronExpression.ValueSet();
      var6.pos = var3 < var2.length() ? var3 : var3 + 1;
      var6.value = Integer.parseInt(var5.toString());
      return var6;
   }

   protected int getNumericValue(String var1, int var2) {
      int var3 = this.findNextWhiteSpace(var2, var1);
      String var4 = var1.substring(var2, var3);
      return Integer.parseInt(var4);
   }

   protected int getMonthNumber(String var1) {
      Integer var2 = (Integer)monthMap.get(var1);
      return var2 == null ? -1 : var2;
   }

   protected int getDayOfWeekNumber(String var1) {
      Integer var2 = (Integer)dayMap.get(var1);
      return var2 == null ? -1 : var2;
   }

   public Date getTimeAfter(Date var1) {
      GregorianCalendar var2 = new GregorianCalendar(this.getTimeZone());
      var1 = new Date(var1.getTime() + 1000L);
      var2.setTime(var1);
      var2.set(14, 0);
      boolean var3 = false;

      while(true) {
         while(true) {
            while(!var3) {
               if (var2.get(1) > 2999) {
                  return null;
               }

               SortedSet var4 = null;
               boolean var5 = false;
               int var6 = var2.get(13);
               int var7 = var2.get(12);
               var4 = this.seconds.tailSet(var6);
               if (var4 != null && var4.size() != 0) {
                  var6 = (Integer)var4.first();
               } else {
                  var6 = (Integer)this.seconds.first();
                  ++var7;
                  var2.set(12, var7);
               }

               var2.set(13, var6);
               var7 = var2.get(12);
               int var8 = var2.get(11);
               int var19 = -1;
               var4 = this.minutes.tailSet(var7);
               if (var4 != null && var4.size() != 0) {
                  var19 = var7;
                  var7 = (Integer)var4.first();
               } else {
                  var7 = (Integer)this.minutes.first();
                  ++var8;
               }

               if (var7 == var19) {
                  var2.set(12, var7);
                  var8 = var2.get(11);
                  int var9 = var2.get(5);
                  var19 = -1;
                  var4 = this.hours.tailSet(var8);
                  if (var4 != null && var4.size() != 0) {
                     var19 = var8;
                     var8 = (Integer)var4.first();
                  } else {
                     var8 = (Integer)this.hours.first();
                     ++var9;
                  }

                  if (var8 == var19) {
                     var2.set(11, var8);
                     var9 = var2.get(5);
                     int var10 = var2.get(2) + 1;
                     var19 = -1;
                     int var11 = var10;
                     boolean var12 = !this.daysOfMonth.contains(NO_SPEC);
                     boolean var13 = !this.daysOfWeek.contains(NO_SPEC);
                     int var14;
                     int var15;
                     int var16;
                     if (var12 && !var13) {
                        var4 = this.daysOfMonth.tailSet(var9);
                        Calendar var20;
                        Date var22;
                        if (this.lastdayOfMonth) {
                           if (!this.nearestWeekday) {
                              var19 = var9;
                              var9 = this.getLastDayOfMonth(var10, var2.get(1));
                              var9 -= this.lastdayOffset;
                              if (var19 > var9) {
                                 ++var10;
                                 if (var10 > 12) {
                                    var10 = 1;
                                    var11 = 3333;
                                    var2.add(1, 1);
                                 }

                                 var9 = 1;
                              }
                           } else {
                              var19 = var9;
                              var9 = this.getLastDayOfMonth(var10, var2.get(1));
                              var9 -= this.lastdayOffset;
                              var20 = Calendar.getInstance(this.getTimeZone());
                              var20.set(13, 0);
                              var20.set(12, 0);
                              var20.set(11, 0);
                              var20.set(5, var9);
                              var20.set(2, var10 - 1);
                              var20.set(1, var2.get(1));
                              var15 = this.getLastDayOfMonth(var10, var2.get(1));
                              var16 = var20.get(7);
                              if (var16 == 7 && var9 == 1) {
                                 var9 += 2;
                              } else if (var16 == 7) {
                                 --var9;
                              } else if (var16 == 1 && var9 == var15) {
                                 var9 -= 2;
                              } else if (var16 == 1) {
                                 ++var9;
                              }

                              var20.set(13, var6);
                              var20.set(12, var7);
                              var20.set(11, var8);
                              var20.set(5, var9);
                              var20.set(2, var10 - 1);
                              var22 = var20.getTime();
                              if (var22.before(var1)) {
                                 var9 = 1;
                                 ++var10;
                              }
                           }
                        } else if (this.nearestWeekday) {
                           var19 = var9;
                           var9 = (Integer)this.daysOfMonth.first();
                           var20 = Calendar.getInstance(this.getTimeZone());
                           var20.set(13, 0);
                           var20.set(12, 0);
                           var20.set(11, 0);
                           var20.set(5, var9);
                           var20.set(2, var10 - 1);
                           var20.set(1, var2.get(1));
                           var15 = this.getLastDayOfMonth(var10, var2.get(1));
                           var16 = var20.get(7);
                           if (var16 == 7 && var9 == 1) {
                              var9 += 2;
                           } else if (var16 == 7) {
                              --var9;
                           } else if (var16 == 1 && var9 == var15) {
                              var9 -= 2;
                           } else if (var16 == 1) {
                              ++var9;
                           }

                           var20.set(13, var6);
                           var20.set(12, var7);
                           var20.set(11, var8);
                           var20.set(5, var9);
                           var20.set(2, var10 - 1);
                           var22 = var20.getTime();
                           if (var22.before(var1)) {
                              var9 = (Integer)this.daysOfMonth.first();
                              ++var10;
                           }
                        } else if (var4 != null && var4.size() != 0) {
                           var19 = var9;
                           var9 = (Integer)var4.first();
                           var14 = this.getLastDayOfMonth(var10, var2.get(1));
                           if (var9 > var14) {
                              var9 = (Integer)this.daysOfMonth.first();
                              ++var10;
                           }
                        } else {
                           var9 = (Integer)this.daysOfMonth.first();
                           ++var10;
                        }

                        if (var9 != var19 || var10 != var11) {
                           var2.set(13, 0);
                           var2.set(12, 0);
                           var2.set(11, 0);
                           var2.set(5, var9);
                           var2.set(2, var10 - 1);
                           continue;
                        }
                     } else {
                        if (!var13 || var12) {
                           throw new UnsupportedOperationException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
                        }

                        int var17;
                        if (this.lastdayOfWeek) {
                           var14 = (Integer)this.daysOfWeek.first();
                           var15 = var2.get(7);
                           var16 = 0;
                           if (var15 < var14) {
                              var16 = var14 - var15;
                           }

                           if (var15 > var14) {
                              var16 = var14 + (7 - var15);
                           }

                           var17 = this.getLastDayOfMonth(var10, var2.get(1));
                           if (var9 + var16 > var17) {
                              var2.set(13, 0);
                              var2.set(12, 0);
                              var2.set(11, 0);
                              var2.set(5, 1);
                              var2.set(2, var10);
                              continue;
                           }

                           while(var9 + var16 + 7 <= var17) {
                              var16 += 7;
                           }

                           var9 += var16;
                           if (var16 > 0) {
                              var2.set(13, 0);
                              var2.set(12, 0);
                              var2.set(11, 0);
                              var2.set(5, var9);
                              var2.set(2, var10 - 1);
                              continue;
                           }
                        } else if (this.nthdayOfWeek != 0) {
                           var14 = (Integer)this.daysOfWeek.first();
                           var15 = var2.get(7);
                           var16 = 0;
                           if (var15 < var14) {
                              var16 = var14 - var15;
                           } else if (var15 > var14) {
                              var16 = var14 + (7 - var15);
                           }

                           boolean var21 = false;
                           if (var16 > 0) {
                              var21 = true;
                           }

                           var9 += var16;
                           int var18 = var9 / 7;
                           if (var9 % 7 > 0) {
                              ++var18;
                           }

                           var16 = (this.nthdayOfWeek - var18) * 7;
                           var9 += var16;
                           if (var16 < 0 || var9 > this.getLastDayOfMonth(var10, var2.get(1))) {
                              var2.set(13, 0);
                              var2.set(12, 0);
                              var2.set(11, 0);
                              var2.set(5, 1);
                              var2.set(2, var10);
                              continue;
                           }

                           if (var16 > 0 || var21) {
                              var2.set(13, 0);
                              var2.set(12, 0);
                              var2.set(11, 0);
                              var2.set(5, var9);
                              var2.set(2, var10 - 1);
                              continue;
                           }
                        } else {
                           var14 = var2.get(7);
                           var15 = (Integer)this.daysOfWeek.first();
                           var4 = this.daysOfWeek.tailSet(var14);
                           if (var4 != null && var4.size() > 0) {
                              var15 = (Integer)var4.first();
                           }

                           var16 = 0;
                           if (var14 < var15) {
                              var16 = var15 - var14;
                           }

                           if (var14 > var15) {
                              var16 = var15 + (7 - var14);
                           }

                           var17 = this.getLastDayOfMonth(var10, var2.get(1));
                           if (var9 + var16 > var17) {
                              var2.set(13, 0);
                              var2.set(12, 0);
                              var2.set(11, 0);
                              var2.set(5, 1);
                              var2.set(2, var10);
                              continue;
                           }

                           if (var16 > 0) {
                              var2.set(13, 0);
                              var2.set(12, 0);
                              var2.set(11, 0);
                              var2.set(5, var9 + var16);
                              var2.set(2, var10 - 1);
                              continue;
                           }
                        }
                     }

                     var2.set(5, var9);
                     var10 = var2.get(2) + 1;
                     var14 = var2.get(1);
                     var19 = -1;
                     if (var14 > MAX_YEAR) {
                        return null;
                     }

                     var4 = this.months.tailSet(var10);
                     if (var4 != null && var4.size() != 0) {
                        var19 = var10;
                        var10 = (Integer)var4.first();
                     } else {
                        var10 = (Integer)this.months.first();
                        ++var14;
                     }

                     if (var10 != var19) {
                        var2.set(13, 0);
                        var2.set(12, 0);
                        var2.set(11, 0);
                        var2.set(5, 1);
                        var2.set(2, var10 - 1);
                        var2.set(1, var14);
                     } else {
                        var2.set(2, var10 - 1);
                        var14 = var2.get(1);
                        var5 = true;
                        var4 = this.years.tailSet(var14);
                        if (var4 == null || var4.size() == 0) {
                           return null;
                        }

                        var19 = var14;
                        var14 = (Integer)var4.first();
                        if (var14 != var19) {
                           var2.set(13, 0);
                           var2.set(12, 0);
                           var2.set(11, 0);
                           var2.set(5, 1);
                           var2.set(2, 0);
                           var2.set(1, var14);
                        } else {
                           var2.set(1, var14);
                           var3 = true;
                        }
                     }
                  } else {
                     var2.set(13, 0);
                     var2.set(12, 0);
                     var2.set(5, var9);
                     this.setCalendarHour(var2, var8);
                  }
               } else {
                  var2.set(13, 0);
                  var2.set(12, var7);
                  this.setCalendarHour(var2, var8);
               }
            }

            return var2.getTime();
         }
      }
   }

   protected void setCalendarHour(Calendar var1, int var2) {
      var1.set(11, var2);
      if (var1.get(11) != var2 && var2 != 24) {
         var1.set(11, var2 + 1);
      }

   }

   protected Date getTimeBefore(Date var1) {
      Calendar var2 = Calendar.getInstance(this.getTimeZone());
      Date var3 = var1;
      long var4 = this.findMinIncrement();

      Date var6;
      do {
         Date var7 = new Date(var3.getTime() - var4);
         var6 = this.getTimeAfter(var7);
         if (var6 == null || var6.before(MIN_DATE)) {
            return null;
         }

         var3 = var7;
      } while(var6.compareTo(var1) >= 0);

      return var6;
   }

   public Date getPrevFireTime(Date var1) {
      return this.getTimeBefore(var1);
   }

   private long findMinIncrement() {
      if (this.seconds.size() != 1) {
         return (long)(this.minInSet(this.seconds) * 1000);
      } else if ((Integer)this.seconds.first() == 99) {
         return 1000L;
      } else if (this.minutes.size() != 1) {
         return (long)(this.minInSet(this.minutes) * '\uea60');
      } else if ((Integer)this.minutes.first() == 99) {
         return 60000L;
      } else if (this.hours.size() != 1) {
         return (long)(this.minInSet(this.hours) * 3600000);
      } else {
         return (Integer)this.hours.first() == 99 ? 3600000L : 86400000L;
      }
   }

   private int minInSet(TreeSet<Integer> var1) {
      int var2 = 0;
      int var3 = 2147483647;
      boolean var4 = true;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         int var6 = (Integer)var5.next();
         if (var4) {
            var2 = var6;
            var4 = false;
         } else {
            int var7 = var6 - var2;
            if (var7 < var3) {
               var3 = var7;
            }
         }
      }

      return var3;
   }

   public Date getFinalFireTime() {
      return null;
   }

   protected boolean isLeapYear(int var1) {
      return var1 % 4 == 0 && var1 % 100 != 0 || var1 % 400 == 0;
   }

   protected int getLastDayOfMonth(int var1, int var2) {
      switch(var1) {
      case 1:
         return 31;
      case 2:
         return this.isLeapYear(var2) ? 29 : 28;
      case 3:
         return 31;
      case 4:
         return 30;
      case 5:
         return 31;
      case 6:
         return 30;
      case 7:
         return 31;
      case 8:
         return 31;
      case 9:
         return 30;
      case 10:
         return 31;
      case 11:
         return 30;
      case 12:
         return 31;
      default:
         throw new IllegalArgumentException("Illegal month number: " + var1);
      }
   }

   static {
      monthMap.put("JAN", 0);
      monthMap.put("FEB", 1);
      monthMap.put("MAR", 2);
      monthMap.put("APR", 3);
      monthMap.put("MAY", 4);
      monthMap.put("JUN", 5);
      monthMap.put("JUL", 6);
      monthMap.put("AUG", 7);
      monthMap.put("SEP", 8);
      monthMap.put("OCT", 9);
      monthMap.put("NOV", 10);
      monthMap.put("DEC", 11);
      dayMap.put("SUN", 1);
      dayMap.put("MON", 2);
      dayMap.put("TUE", 3);
      dayMap.put("WED", 4);
      dayMap.put("THU", 5);
      dayMap.put("FRI", 6);
      dayMap.put("SAT", 7);
      MAX_YEAR = Calendar.getInstance().get(1) + 100;
      MIN_CAL = Calendar.getInstance();
      MIN_CAL.set(1970, 0, 1);
      MIN_DATE = MIN_CAL.getTime();
   }

   private class ValueSet {
      public int value;
      public int pos;

      private ValueSet() {
         super();
      }

      // $FF: synthetic method
      ValueSet(Object var2) {
         this();
      }
   }
}
