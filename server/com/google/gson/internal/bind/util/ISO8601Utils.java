package com.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
   private static final String UTC_ID = "UTC";
   private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

   public ISO8601Utils() {
      super();
   }

   public static String format(Date var0) {
      return format(var0, false, TIMEZONE_UTC);
   }

   public static String format(Date var0, boolean var1) {
      return format(var0, var1, TIMEZONE_UTC);
   }

   public static String format(Date var0, boolean var1, TimeZone var2) {
      GregorianCalendar var3 = new GregorianCalendar(var2, Locale.US);
      var3.setTime(var0);
      int var4 = "yyyy-MM-ddThh:mm:ss".length();
      var4 += var1 ? ".sss".length() : 0;
      var4 += var2.getRawOffset() == 0 ? "Z".length() : "+hh:mm".length();
      StringBuilder var5 = new StringBuilder(var4);
      padInt(var5, var3.get(1), "yyyy".length());
      var5.append('-');
      padInt(var5, var3.get(2) + 1, "MM".length());
      var5.append('-');
      padInt(var5, var3.get(5), "dd".length());
      var5.append('T');
      padInt(var5, var3.get(11), "hh".length());
      var5.append(':');
      padInt(var5, var3.get(12), "mm".length());
      var5.append(':');
      padInt(var5, var3.get(13), "ss".length());
      if (var1) {
         var5.append('.');
         padInt(var5, var3.get(14), "sss".length());
      }

      int var6 = var2.getOffset(var3.getTimeInMillis());
      if (var6 != 0) {
         int var7 = Math.abs(var6 / '\uea60' / 60);
         int var8 = Math.abs(var6 / '\uea60' % 60);
         var5.append((char)(var6 < 0 ? '-' : '+'));
         padInt(var5, var7, "hh".length());
         var5.append(':');
         padInt(var5, var8, "mm".length());
      } else {
         var5.append('Z');
      }

      return var5.toString();
   }

   public static Date parse(String var0, ParsePosition var1) throws ParseException {
      Object var2 = null;

      try {
         int var21 = var1.getIndex();
         int var10001 = var21;
         var21 += 4;
         int var22 = parseInt(var0, var10001, var21);
         if (checkOffset(var0, var21, '-')) {
            ++var21;
         }

         var10001 = var21;
         var21 += 2;
         int var23 = parseInt(var0, var10001, var21);
         if (checkOffset(var0, var21, '-')) {
            ++var21;
         }

         var10001 = var21;
         var21 += 2;
         int var6 = parseInt(var0, var10001, var21);
         int var7 = 0;
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;
         boolean var11 = checkOffset(var0, var21, 'T');
         if (!var11 && var0.length() <= var21) {
            GregorianCalendar var26 = new GregorianCalendar(var22, var23 - 1, var6);
            var1.setIndex(var21);
            return var26.getTime();
         }

         if (var11) {
            ++var21;
            var10001 = var21;
            var21 += 2;
            var7 = parseInt(var0, var10001, var21);
            if (checkOffset(var0, var21, ':')) {
               ++var21;
            }

            var10001 = var21;
            var21 += 2;
            var8 = parseInt(var0, var10001, var21);
            if (checkOffset(var0, var21, ':')) {
               ++var21;
            }

            if (var0.length() > var21) {
               char var12 = var0.charAt(var21);
               if (var12 != 'Z' && var12 != '+' && var12 != '-') {
                  var10001 = var21;
                  var21 += 2;
                  var9 = parseInt(var0, var10001, var21);
                  if (var9 > 59 && var9 < 63) {
                     var9 = 59;
                  }

                  if (checkOffset(var0, var21, '.')) {
                     ++var21;
                     int var13 = indexOfNonDigit(var0, var21 + 1);
                     int var14 = Math.min(var13, var21 + 3);
                     int var15 = parseInt(var0, var21, var14);
                     switch(var14 - var21) {
                     case 1:
                        var10 = var15 * 100;
                        break;
                     case 2:
                        var10 = var15 * 10;
                        break;
                     default:
                        var10 = var15;
                     }

                     var21 = var13;
                  }
               }
            }
         }

         if (var0.length() <= var21) {
            throw new IllegalArgumentException("No time zone indicator");
         }

         TimeZone var24 = null;
         char var25 = var0.charAt(var21);
         if (var25 == 'Z') {
            var24 = TIMEZONE_UTC;
            ++var21;
         } else {
            if (var25 != '+' && var25 != '-') {
               throw new IndexOutOfBoundsException("Invalid time zone indicator '" + var25 + "'");
            }

            String var27 = var0.substring(var21);
            var27 = var27.length() >= 5 ? var27 : var27 + "00";
            var21 += var27.length();
            if (!"+0000".equals(var27) && !"+00:00".equals(var27)) {
               String var29 = "GMT" + var27;
               var24 = TimeZone.getTimeZone(var29);
               String var16 = var24.getID();
               if (!var16.equals(var29)) {
                  String var17 = var16.replace(":", "");
                  if (!var17.equals(var29)) {
                     throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + var29 + " given, resolves to " + var24.getID());
                  }
               }
            } else {
               var24 = TIMEZONE_UTC;
            }
         }

         GregorianCalendar var28 = new GregorianCalendar(var24);
         var28.setLenient(false);
         var28.set(1, var22);
         var28.set(2, var23 - 1);
         var28.set(5, var6);
         var28.set(11, var7);
         var28.set(12, var8);
         var28.set(13, var9);
         var28.set(14, var10);
         var1.setIndex(var21);
         return var28.getTime();
      } catch (IndexOutOfBoundsException var18) {
         var2 = var18;
      } catch (NumberFormatException var19) {
         var2 = var19;
      } catch (IllegalArgumentException var20) {
         var2 = var20;
      }

      String var3 = var0 == null ? null : '"' + var0 + "'";
      String var4 = ((Exception)var2).getMessage();
      if (var4 == null || var4.isEmpty()) {
         var4 = "(" + var2.getClass().getName() + ")";
      }

      ParseException var5 = new ParseException("Failed to parse date [" + var3 + "]: " + var4, var1.getIndex());
      var5.initCause((Throwable)var2);
      throw var5;
   }

   private static boolean checkOffset(String var0, int var1, char var2) {
      return var1 < var0.length() && var0.charAt(var1) == var2;
   }

   private static int parseInt(String var0, int var1, int var2) throws NumberFormatException {
      if (var1 >= 0 && var2 <= var0.length() && var1 <= var2) {
         int var3 = var1;
         int var4 = 0;
         int var5;
         if (var1 < var2) {
            var3 = var1 + 1;
            var5 = Character.digit(var0.charAt(var1), 10);
            if (var5 < 0) {
               throw new NumberFormatException("Invalid number: " + var0.substring(var1, var2));
            }

            var4 = -var5;
         }

         while(var3 < var2) {
            var5 = Character.digit(var0.charAt(var3++), 10);
            if (var5 < 0) {
               throw new NumberFormatException("Invalid number: " + var0.substring(var1, var2));
            }

            var4 *= 10;
            var4 -= var5;
         }

         return -var4;
      } else {
         throw new NumberFormatException(var0);
      }
   }

   private static void padInt(StringBuilder var0, int var1, int var2) {
      String var3 = Integer.toString(var1);

      for(int var4 = var2 - var3.length(); var4 > 0; --var4) {
         var0.append('0');
      }

      var0.append(var3);
   }

   private static int indexOfNonDigit(String var0, int var1) {
      for(int var2 = var1; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 < '0' || var3 > '9') {
            return var2;
         }
      }

      return var0.length();
   }
}
