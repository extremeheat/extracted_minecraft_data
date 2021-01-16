package org.apache.commons.lang3;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringUtils {
   public static final String SPACE = " ";
   public static final String EMPTY = "";
   public static final String LF = "\n";
   public static final String CR = "\r";
   public static final int INDEX_NOT_FOUND = -1;
   private static final int PAD_LIMIT = 8192;

   public StringUtils() {
      super();
   }

   public static boolean isEmpty(CharSequence var0) {
      return var0 == null || var0.length() == 0;
   }

   public static boolean isNotEmpty(CharSequence var0) {
      return !isEmpty(var0);
   }

   public static boolean isAnyEmpty(CharSequence... var0) {
      if (ArrayUtils.isEmpty((Object[])var0)) {
         return true;
      } else {
         CharSequence[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence var4 = var1[var3];
            if (isEmpty(var4)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isNoneEmpty(CharSequence... var0) {
      return !isAnyEmpty(var0);
   }

   public static boolean isBlank(CharSequence var0) {
      int var1;
      if (var0 != null && (var1 = var0.length()) != 0) {
         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isWhitespace(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isNotBlank(CharSequence var0) {
      return !isBlank(var0);
   }

   public static boolean isAnyBlank(CharSequence... var0) {
      if (ArrayUtils.isEmpty((Object[])var0)) {
         return true;
      } else {
         CharSequence[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence var4 = var1[var3];
            if (isBlank(var4)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isNoneBlank(CharSequence... var0) {
      return !isAnyBlank(var0);
   }

   public static String trim(String var0) {
      return var0 == null ? null : var0.trim();
   }

   public static String trimToNull(String var0) {
      String var1 = trim(var0);
      return isEmpty(var1) ? null : var1;
   }

   public static String trimToEmpty(String var0) {
      return var0 == null ? "" : var0.trim();
   }

   public static String truncate(String var0, int var1) {
      return truncate(var0, 0, var1);
   }

   public static String truncate(String var0, int var1, int var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("offset cannot be negative");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("maxWith cannot be negative");
      } else if (var0 == null) {
         return null;
      } else if (var1 > var0.length()) {
         return "";
      } else if (var0.length() > var2) {
         int var3 = var1 + var2 > var0.length() ? var0.length() : var1 + var2;
         return var0.substring(var1, var3);
      } else {
         return var0.substring(var1);
      }
   }

   public static String strip(String var0) {
      return strip(var0, (String)null);
   }

   public static String stripToNull(String var0) {
      if (var0 == null) {
         return null;
      } else {
         var0 = strip(var0, (String)null);
         return var0.isEmpty() ? null : var0;
      }
   }

   public static String stripToEmpty(String var0) {
      return var0 == null ? "" : strip(var0, (String)null);
   }

   public static String strip(String var0, String var1) {
      if (isEmpty(var0)) {
         return var0;
      } else {
         var0 = stripStart(var0, var1);
         return stripEnd(var0, var1);
      }
   }

   public static String stripStart(String var0, String var1) {
      int var2;
      if (var0 != null && (var2 = var0.length()) != 0) {
         int var3 = 0;
         if (var1 == null) {
            while(var3 != var2 && Character.isWhitespace(var0.charAt(var3))) {
               ++var3;
            }
         } else {
            if (var1.isEmpty()) {
               return var0;
            }

            while(var3 != var2 && var1.indexOf(var0.charAt(var3)) != -1) {
               ++var3;
            }
         }

         return var0.substring(var3);
      } else {
         return var0;
      }
   }

   public static String stripEnd(String var0, String var1) {
      int var2;
      if (var0 != null && (var2 = var0.length()) != 0) {
         if (var1 == null) {
            while(var2 != 0 && Character.isWhitespace(var0.charAt(var2 - 1))) {
               --var2;
            }
         } else {
            if (var1.isEmpty()) {
               return var0;
            }

            while(var2 != 0 && var1.indexOf(var0.charAt(var2 - 1)) != -1) {
               --var2;
            }
         }

         return var0.substring(0, var2);
      } else {
         return var0;
      }
   }

   public static String[] stripAll(String... var0) {
      return stripAll(var0, (String)null);
   }

   public static String[] stripAll(String[] var0, String var1) {
      int var2;
      if (var0 != null && (var2 = var0.length) != 0) {
         String[] var3 = new String[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = strip(var0[var4], var1);
         }

         return var3;
      } else {
         return var0;
      }
   }

   public static String stripAccents(String var0) {
      if (var0 == null) {
         return null;
      } else {
         Pattern var1 = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
         StringBuilder var2 = new StringBuilder(Normalizer.normalize(var0, Form.NFD));
         convertRemainingAccentCharacters(var2);
         return var1.matcher(var2).replaceAll("");
      }
   }

   private static void convertRemainingAccentCharacters(StringBuilder var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         if (var0.charAt(var1) == 321) {
            var0.deleteCharAt(var1);
            var0.insert(var1, 'L');
         } else if (var0.charAt(var1) == 322) {
            var0.deleteCharAt(var1);
            var0.insert(var1, 'l');
         }
      }

   }

   public static boolean equals(CharSequence var0, CharSequence var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.length() != var1.length()) {
            return false;
         } else {
            return var0 instanceof String && var1 instanceof String ? var0.equals(var1) : CharSequenceUtils.regionMatches(var0, false, 0, var1, 0, var0.length());
         }
      } else {
         return false;
      }
   }

   public static boolean equalsIgnoreCase(CharSequence var0, CharSequence var1) {
      if (var0 != null && var1 != null) {
         if (var0 == var1) {
            return true;
         } else {
            return var0.length() != var1.length() ? false : CharSequenceUtils.regionMatches(var0, true, 0, var1, 0, var0.length());
         }
      } else {
         return var0 == var1;
      }
   }

   public static int compare(String var0, String var1) {
      return compare(var0, var1, true);
   }

   public static int compare(String var0, String var1, boolean var2) {
      if (var0 == var1) {
         return 0;
      } else if (var0 == null) {
         return var2 ? -1 : 1;
      } else if (var1 == null) {
         return var2 ? 1 : -1;
      } else {
         return var0.compareTo(var1);
      }
   }

   public static int compareIgnoreCase(String var0, String var1) {
      return compareIgnoreCase(var0, var1, true);
   }

   public static int compareIgnoreCase(String var0, String var1, boolean var2) {
      if (var0 == var1) {
         return 0;
      } else if (var0 == null) {
         return var2 ? -1 : 1;
      } else if (var1 == null) {
         return var2 ? 1 : -1;
      } else {
         return var0.compareToIgnoreCase(var1);
      }
   }

   public static boolean equalsAny(CharSequence var0, CharSequence... var1) {
      if (ArrayUtils.isNotEmpty((Object[])var1)) {
         CharSequence[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence var5 = var2[var4];
            if (equals(var0, var5)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean equalsAnyIgnoreCase(CharSequence var0, CharSequence... var1) {
      if (ArrayUtils.isNotEmpty((Object[])var1)) {
         CharSequence[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence var5 = var2[var4];
            if (equalsIgnoreCase(var0, var5)) {
               return true;
            }
         }
      }

      return false;
   }

   public static int indexOf(CharSequence var0, int var1) {
      return isEmpty(var0) ? -1 : CharSequenceUtils.indexOf(var0, var1, 0);
   }

   public static int indexOf(CharSequence var0, int var1, int var2) {
      return isEmpty(var0) ? -1 : CharSequenceUtils.indexOf(var0, var1, var2);
   }

   public static int indexOf(CharSequence var0, CharSequence var1) {
      return var0 != null && var1 != null ? CharSequenceUtils.indexOf(var0, var1, 0) : -1;
   }

   public static int indexOf(CharSequence var0, CharSequence var1, int var2) {
      return var0 != null && var1 != null ? CharSequenceUtils.indexOf(var0, var1, var2) : -1;
   }

   public static int ordinalIndexOf(CharSequence var0, CharSequence var1, int var2) {
      return ordinalIndexOf(var0, var1, var2, false);
   }

   private static int ordinalIndexOf(CharSequence var0, CharSequence var1, int var2, boolean var3) {
      if (var0 != null && var1 != null && var2 > 0) {
         if (var1.length() == 0) {
            return var3 ? var0.length() : 0;
         } else {
            int var4 = 0;
            int var5 = var3 ? var0.length() : -1;

            do {
               if (var3) {
                  var5 = CharSequenceUtils.lastIndexOf(var0, var1, var5 - 1);
               } else {
                  var5 = CharSequenceUtils.indexOf(var0, var1, var5 + 1);
               }

               if (var5 < 0) {
                  return var5;
               }

               ++var4;
            } while(var4 < var2);

            return var5;
         }
      } else {
         return -1;
      }
   }

   public static int indexOfIgnoreCase(CharSequence var0, CharSequence var1) {
      return indexOfIgnoreCase(var0, var1, 0);
   }

   public static int indexOfIgnoreCase(CharSequence var0, CharSequence var1, int var2) {
      if (var0 != null && var1 != null) {
         if (var2 < 0) {
            var2 = 0;
         }

         int var3 = var0.length() - var1.length() + 1;
         if (var2 > var3) {
            return -1;
         } else if (var1.length() == 0) {
            return var2;
         } else {
            for(int var4 = var2; var4 < var3; ++var4) {
               if (CharSequenceUtils.regionMatches(var0, true, var4, var1, 0, var1.length())) {
                  return var4;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int lastIndexOf(CharSequence var0, int var1) {
      return isEmpty(var0) ? -1 : CharSequenceUtils.lastIndexOf(var0, var1, var0.length());
   }

   public static int lastIndexOf(CharSequence var0, int var1, int var2) {
      return isEmpty(var0) ? -1 : CharSequenceUtils.lastIndexOf(var0, var1, var2);
   }

   public static int lastIndexOf(CharSequence var0, CharSequence var1) {
      return var0 != null && var1 != null ? CharSequenceUtils.lastIndexOf(var0, var1, var0.length()) : -1;
   }

   public static int lastOrdinalIndexOf(CharSequence var0, CharSequence var1, int var2) {
      return ordinalIndexOf(var0, var1, var2, true);
   }

   public static int lastIndexOf(CharSequence var0, CharSequence var1, int var2) {
      return var0 != null && var1 != null ? CharSequenceUtils.lastIndexOf(var0, var1, var2) : -1;
   }

   public static int lastIndexOfIgnoreCase(CharSequence var0, CharSequence var1) {
      return var0 != null && var1 != null ? lastIndexOfIgnoreCase(var0, var1, var0.length()) : -1;
   }

   public static int lastIndexOfIgnoreCase(CharSequence var0, CharSequence var1, int var2) {
      if (var0 != null && var1 != null) {
         if (var2 > var0.length() - var1.length()) {
            var2 = var0.length() - var1.length();
         }

         if (var2 < 0) {
            return -1;
         } else if (var1.length() == 0) {
            return var2;
         } else {
            for(int var3 = var2; var3 >= 0; --var3) {
               if (CharSequenceUtils.regionMatches(var0, true, var3, var1, 0, var1.length())) {
                  return var3;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static boolean contains(CharSequence var0, int var1) {
      if (isEmpty(var0)) {
         return false;
      } else {
         return CharSequenceUtils.indexOf(var0, var1, 0) >= 0;
      }
   }

   public static boolean contains(CharSequence var0, CharSequence var1) {
      if (var0 != null && var1 != null) {
         return CharSequenceUtils.indexOf(var0, var1, 0) >= 0;
      } else {
         return false;
      }
   }

   public static boolean containsIgnoreCase(CharSequence var0, CharSequence var1) {
      if (var0 != null && var1 != null) {
         int var2 = var1.length();
         int var3 = var0.length() - var2;

         for(int var4 = 0; var4 <= var3; ++var4) {
            if (CharSequenceUtils.regionMatches(var0, true, var4, var1, 0, var2)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean containsWhitespace(CharSequence var0) {
      if (isEmpty(var0)) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (Character.isWhitespace(var0.charAt(var2))) {
               return true;
            }
         }

         return false;
      }
   }

   public static int indexOfAny(CharSequence var0, char... var1) {
      if (!isEmpty(var0) && !ArrayUtils.isEmpty(var1)) {
         int var2 = var0.length();
         int var3 = var2 - 1;
         int var4 = var1.length;
         int var5 = var4 - 1;

         for(int var6 = 0; var6 < var2; ++var6) {
            char var7 = var0.charAt(var6);

            for(int var8 = 0; var8 < var4; ++var8) {
               if (var1[var8] == var7) {
                  if (var6 >= var3 || var8 >= var5 || !Character.isHighSurrogate(var7)) {
                     return var6;
                  }

                  if (var1[var8 + 1] == var0.charAt(var6 + 1)) {
                     return var6;
                  }
               }
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int indexOfAny(CharSequence var0, String var1) {
      return !isEmpty(var0) && !isEmpty(var1) ? indexOfAny(var0, var1.toCharArray()) : -1;
   }

   public static boolean containsAny(CharSequence var0, char... var1) {
      if (!isEmpty(var0) && !ArrayUtils.isEmpty(var1)) {
         int var2 = var0.length();
         int var3 = var1.length;
         int var4 = var2 - 1;
         int var5 = var3 - 1;

         for(int var6 = 0; var6 < var2; ++var6) {
            char var7 = var0.charAt(var6);

            for(int var8 = 0; var8 < var3; ++var8) {
               if (var1[var8] == var7) {
                  if (!Character.isHighSurrogate(var7)) {
                     return true;
                  }

                  if (var8 == var5) {
                     return true;
                  }

                  if (var6 < var4 && var1[var8 + 1] == var0.charAt(var6 + 1)) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean containsAny(CharSequence var0, CharSequence var1) {
      return var1 == null ? false : containsAny(var0, CharSequenceUtils.toCharArray(var1));
   }

   public static boolean containsAny(CharSequence var0, CharSequence... var1) {
      if (!isEmpty(var0) && !ArrayUtils.isEmpty((Object[])var1)) {
         CharSequence[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence var5 = var2[var4];
            if (contains(var0, var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static int indexOfAnyBut(CharSequence var0, char... var1) {
      if (!isEmpty(var0) && !ArrayUtils.isEmpty(var1)) {
         int var2 = var0.length();
         int var3 = var2 - 1;
         int var4 = var1.length;
         int var5 = var4 - 1;

         label38:
         for(int var6 = 0; var6 < var2; ++var6) {
            char var7 = var0.charAt(var6);

            for(int var8 = 0; var8 < var4; ++var8) {
               if (var1[var8] == var7 && (var6 >= var3 || var8 >= var5 || !Character.isHighSurrogate(var7) || var1[var8 + 1] == var0.charAt(var6 + 1))) {
                  continue label38;
               }
            }

            return var6;
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int indexOfAnyBut(CharSequence var0, CharSequence var1) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         int var2 = var0.length();

         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var0.charAt(var3);
            boolean var5 = CharSequenceUtils.indexOf(var1, var4, 0) >= 0;
            if (var3 + 1 < var2 && Character.isHighSurrogate(var4)) {
               char var6 = var0.charAt(var3 + 1);
               if (var5 && CharSequenceUtils.indexOf(var1, var6, 0) < 0) {
                  return var3;
               }
            } else if (!var5) {
               return var3;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static boolean containsOnly(CharSequence var0, char... var1) {
      if (var1 != null && var0 != null) {
         if (var0.length() == 0) {
            return true;
         } else if (var1.length == 0) {
            return false;
         } else {
            return indexOfAnyBut(var0, var1) == -1;
         }
      } else {
         return false;
      }
   }

   public static boolean containsOnly(CharSequence var0, String var1) {
      return var0 != null && var1 != null ? containsOnly(var0, var1.toCharArray()) : false;
   }

   public static boolean containsNone(CharSequence var0, char... var1) {
      if (var0 != null && var1 != null) {
         int var2 = var0.length();
         int var3 = var2 - 1;
         int var4 = var1.length;
         int var5 = var4 - 1;

         for(int var6 = 0; var6 < var2; ++var6) {
            char var7 = var0.charAt(var6);

            for(int var8 = 0; var8 < var4; ++var8) {
               if (var1[var8] == var7) {
                  if (!Character.isHighSurrogate(var7)) {
                     return false;
                  }

                  if (var8 == var5) {
                     return false;
                  }

                  if (var6 < var3 && var1[var8 + 1] == var0.charAt(var6 + 1)) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean containsNone(CharSequence var0, String var1) {
      return var0 != null && var1 != null ? containsNone(var0, var1.toCharArray()) : true;
   }

   public static int indexOfAny(CharSequence var0, CharSequence... var1) {
      if (var0 != null && var1 != null) {
         int var2 = var1.length;
         int var3 = 2147483647;
         boolean var4 = false;

         for(int var5 = 0; var5 < var2; ++var5) {
            CharSequence var6 = var1[var5];
            if (var6 != null) {
               int var7 = CharSequenceUtils.indexOf(var0, var6, 0);
               if (var7 != -1 && var7 < var3) {
                  var3 = var7;
               }
            }
         }

         return var3 == 2147483647 ? -1 : var3;
      } else {
         return -1;
      }
   }

   public static int lastIndexOfAny(CharSequence var0, CharSequence... var1) {
      if (var0 != null && var1 != null) {
         int var2 = var1.length;
         int var3 = -1;
         boolean var4 = false;

         for(int var5 = 0; var5 < var2; ++var5) {
            CharSequence var6 = var1[var5];
            if (var6 != null) {
               int var7 = CharSequenceUtils.lastIndexOf(var0, var6, var0.length());
               if (var7 > var3) {
                  var3 = var7;
               }
            }
         }

         return var3;
      } else {
         return -1;
      }
   }

   public static String substring(String var0, int var1) {
      if (var0 == null) {
         return null;
      } else {
         if (var1 < 0) {
            var1 += var0.length();
         }

         if (var1 < 0) {
            var1 = 0;
         }

         return var1 > var0.length() ? "" : var0.substring(var1);
      }
   }

   public static String substring(String var0, int var1, int var2) {
      if (var0 == null) {
         return null;
      } else {
         if (var2 < 0) {
            var2 += var0.length();
         }

         if (var1 < 0) {
            var1 += var0.length();
         }

         if (var2 > var0.length()) {
            var2 = var0.length();
         }

         if (var1 > var2) {
            return "";
         } else {
            if (var1 < 0) {
               var1 = 0;
            }

            if (var2 < 0) {
               var2 = 0;
            }

            return var0.substring(var1, var2);
         }
      }
   }

   public static String left(String var0, int var1) {
      if (var0 == null) {
         return null;
      } else if (var1 < 0) {
         return "";
      } else {
         return var0.length() <= var1 ? var0 : var0.substring(0, var1);
      }
   }

   public static String right(String var0, int var1) {
      if (var0 == null) {
         return null;
      } else if (var1 < 0) {
         return "";
      } else {
         return var0.length() <= var1 ? var0 : var0.substring(var0.length() - var1);
      }
   }

   public static String mid(String var0, int var1, int var2) {
      if (var0 == null) {
         return null;
      } else if (var2 >= 0 && var1 <= var0.length()) {
         if (var1 < 0) {
            var1 = 0;
         }

         return var0.length() <= var1 + var2 ? var0.substring(var1) : var0.substring(var1, var1 + var2);
      } else {
         return "";
      }
   }

   public static String substringBefore(String var0, String var1) {
      if (!isEmpty(var0) && var1 != null) {
         if (var1.isEmpty()) {
            return "";
         } else {
            int var2 = var0.indexOf(var1);
            return var2 == -1 ? var0 : var0.substring(0, var2);
         }
      } else {
         return var0;
      }
   }

   public static String substringAfter(String var0, String var1) {
      if (isEmpty(var0)) {
         return var0;
      } else if (var1 == null) {
         return "";
      } else {
         int var2 = var0.indexOf(var1);
         return var2 == -1 ? "" : var0.substring(var2 + var1.length());
      }
   }

   public static String substringBeforeLast(String var0, String var1) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         int var2 = var0.lastIndexOf(var1);
         return var2 == -1 ? var0 : var0.substring(0, var2);
      } else {
         return var0;
      }
   }

   public static String substringAfterLast(String var0, String var1) {
      if (isEmpty(var0)) {
         return var0;
      } else if (isEmpty(var1)) {
         return "";
      } else {
         int var2 = var0.lastIndexOf(var1);
         return var2 != -1 && var2 != var0.length() - var1.length() ? var0.substring(var2 + var1.length()) : "";
      }
   }

   public static String substringBetween(String var0, String var1) {
      return substringBetween(var0, var1, var1);
   }

   public static String substringBetween(String var0, String var1, String var2) {
      if (var0 != null && var1 != null && var2 != null) {
         int var3 = var0.indexOf(var1);
         if (var3 != -1) {
            int var4 = var0.indexOf(var2, var3 + var1.length());
            if (var4 != -1) {
               return var0.substring(var3 + var1.length(), var4);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static String[] substringsBetween(String var0, String var1, String var2) {
      if (var0 != null && !isEmpty(var1) && !isEmpty(var2)) {
         int var3 = var0.length();
         if (var3 == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
         } else {
            int var4 = var2.length();
            int var5 = var1.length();
            ArrayList var6 = new ArrayList();

            int var9;
            for(int var7 = 0; var7 < var3 - var4; var7 = var9 + var4) {
               int var8 = var0.indexOf(var1, var7);
               if (var8 < 0) {
                  break;
               }

               var8 += var5;
               var9 = var0.indexOf(var2, var8);
               if (var9 < 0) {
                  break;
               }

               var6.add(var0.substring(var8, var9));
            }

            return var6.isEmpty() ? null : (String[])var6.toArray(new String[var6.size()]);
         }
      } else {
         return null;
      }
   }

   public static String[] split(String var0) {
      return split(var0, (String)null, -1);
   }

   public static String[] split(String var0, char var1) {
      return splitWorker(var0, var1, false);
   }

   public static String[] split(String var0, String var1) {
      return splitWorker(var0, var1, -1, false);
   }

   public static String[] split(String var0, String var1, int var2) {
      return splitWorker(var0, var1, var2, false);
   }

   public static String[] splitByWholeSeparator(String var0, String var1) {
      return splitByWholeSeparatorWorker(var0, var1, -1, false);
   }

   public static String[] splitByWholeSeparator(String var0, String var1, int var2) {
      return splitByWholeSeparatorWorker(var0, var1, var2, false);
   }

   public static String[] splitByWholeSeparatorPreserveAllTokens(String var0, String var1) {
      return splitByWholeSeparatorWorker(var0, var1, -1, true);
   }

   public static String[] splitByWholeSeparatorPreserveAllTokens(String var0, String var1, int var2) {
      return splitByWholeSeparatorWorker(var0, var1, var2, true);
   }

   private static String[] splitByWholeSeparatorWorker(String var0, String var1, int var2, boolean var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var0.length();
         if (var4 == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
         } else if (var1 != null && !"".equals(var1)) {
            int var5 = var1.length();
            ArrayList var6 = new ArrayList();
            int var7 = 0;
            int var8 = 0;
            int var9 = 0;

            while(var9 < var4) {
               var9 = var0.indexOf(var1, var8);
               if (var9 > -1) {
                  if (var9 > var8) {
                     ++var7;
                     if (var7 == var2) {
                        var9 = var4;
                        var6.add(var0.substring(var8));
                     } else {
                        var6.add(var0.substring(var8, var9));
                        var8 = var9 + var5;
                     }
                  } else {
                     if (var3) {
                        ++var7;
                        if (var7 == var2) {
                           var9 = var4;
                           var6.add(var0.substring(var8));
                        } else {
                           var6.add("");
                        }
                     }

                     var8 = var9 + var5;
                  }
               } else {
                  var6.add(var0.substring(var8));
                  var9 = var4;
               }
            }

            return (String[])var6.toArray(new String[var6.size()]);
         } else {
            return splitWorker(var0, (String)null, var2, var3);
         }
      }
   }

   public static String[] splitPreserveAllTokens(String var0) {
      return splitWorker(var0, (String)null, -1, true);
   }

   public static String[] splitPreserveAllTokens(String var0, char var1) {
      return splitWorker(var0, var1, true);
   }

   private static String[] splitWorker(String var0, char var1, boolean var2) {
      if (var0 == null) {
         return null;
      } else {
         int var3 = var0.length();
         if (var3 == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
         } else {
            ArrayList var4 = new ArrayList();
            int var5 = 0;
            int var6 = 0;
            boolean var7 = false;
            boolean var8 = false;

            while(true) {
               while(var5 < var3) {
                  if (var0.charAt(var5) == var1) {
                     if (var7 || var2) {
                        var4.add(var0.substring(var6, var5));
                        var7 = false;
                        var8 = true;
                     }

                     ++var5;
                     var6 = var5;
                  } else {
                     var8 = false;
                     var7 = true;
                     ++var5;
                  }
               }

               if (var7 || var2 && var8) {
                  var4.add(var0.substring(var6, var5));
               }

               return (String[])var4.toArray(new String[var4.size()]);
            }
         }
      }
   }

   public static String[] splitPreserveAllTokens(String var0, String var1) {
      return splitWorker(var0, var1, -1, true);
   }

   public static String[] splitPreserveAllTokens(String var0, String var1, int var2) {
      return splitWorker(var0, var1, var2, true);
   }

   private static String[] splitWorker(String var0, String var1, int var2, boolean var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var0.length();
         if (var4 == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
         } else {
            ArrayList var5 = new ArrayList();
            int var6 = 1;
            int var7 = 0;
            int var8 = 0;
            boolean var9 = false;
            boolean var10 = false;
            if (var1 != null) {
               if (var1.length() != 1) {
                  label87:
                  while(true) {
                     while(true) {
                        if (var7 >= var4) {
                           break label87;
                        }

                        if (var1.indexOf(var0.charAt(var7)) >= 0) {
                           if (var9 || var3) {
                              var10 = true;
                              if (var6++ == var2) {
                                 var7 = var4;
                                 var10 = false;
                              }

                              var5.add(var0.substring(var8, var7));
                              var9 = false;
                           }

                           ++var7;
                           var8 = var7;
                        } else {
                           var10 = false;
                           var9 = true;
                           ++var7;
                        }
                     }
                  }
               } else {
                  char var11 = var1.charAt(0);

                  label71:
                  while(true) {
                     while(true) {
                        if (var7 >= var4) {
                           break label71;
                        }

                        if (var0.charAt(var7) == var11) {
                           if (var9 || var3) {
                              var10 = true;
                              if (var6++ == var2) {
                                 var7 = var4;
                                 var10 = false;
                              }

                              var5.add(var0.substring(var8, var7));
                              var9 = false;
                           }

                           ++var7;
                           var8 = var7;
                        } else {
                           var10 = false;
                           var9 = true;
                           ++var7;
                        }
                     }
                  }
               }
            } else {
               label103:
               while(true) {
                  while(true) {
                     if (var7 >= var4) {
                        break label103;
                     }

                     if (Character.isWhitespace(var0.charAt(var7))) {
                        if (var9 || var3) {
                           var10 = true;
                           if (var6++ == var2) {
                              var7 = var4;
                              var10 = false;
                           }

                           var5.add(var0.substring(var8, var7));
                           var9 = false;
                        }

                        ++var7;
                        var8 = var7;
                     } else {
                        var10 = false;
                        var9 = true;
                        ++var7;
                     }
                  }
               }
            }

            if (var9 || var3 && var10) {
               var5.add(var0.substring(var8, var7));
            }

            return (String[])var5.toArray(new String[var5.size()]);
         }
      }
   }

   public static String[] splitByCharacterType(String var0) {
      return splitByCharacterType(var0, false);
   }

   public static String[] splitByCharacterTypeCamelCase(String var0) {
      return splitByCharacterType(var0, true);
   }

   private static String[] splitByCharacterType(String var0, boolean var1) {
      if (var0 == null) {
         return null;
      } else if (var0.isEmpty()) {
         return ArrayUtils.EMPTY_STRING_ARRAY;
      } else {
         char[] var2 = var0.toCharArray();
         ArrayList var3 = new ArrayList();
         int var4 = 0;
         int var5 = Character.getType(var2[var4]);

         for(int var6 = var4 + 1; var6 < var2.length; ++var6) {
            int var7 = Character.getType(var2[var6]);
            if (var7 != var5) {
               if (var1 && var7 == 2 && var5 == 1) {
                  int var8 = var6 - 1;
                  if (var8 != var4) {
                     var3.add(new String(var2, var4, var8 - var4));
                     var4 = var8;
                  }
               } else {
                  var3.add(new String(var2, var4, var6 - var4));
                  var4 = var6;
               }

               var5 = var7;
            }
         }

         var3.add(new String(var2, var4, var2.length - var4));
         return (String[])var3.toArray(new String[var3.size()]);
      }
   }

   public static <T> String join(T... var0) {
      return join((Object[])var0, (String)null);
   }

   public static String join(Object[] var0, char var1) {
      return var0 == null ? null : join((Object[])var0, var1, 0, var0.length);
   }

   public static String join(long[] var0, char var1) {
      return var0 == null ? null : join((long[])var0, var1, 0, var0.length);
   }

   public static String join(int[] var0, char var1) {
      return var0 == null ? null : join((int[])var0, var1, 0, var0.length);
   }

   public static String join(short[] var0, char var1) {
      return var0 == null ? null : join((short[])var0, var1, 0, var0.length);
   }

   public static String join(byte[] var0, char var1) {
      return var0 == null ? null : join((byte[])var0, var1, 0, var0.length);
   }

   public static String join(char[] var0, char var1) {
      return var0 == null ? null : join((char[])var0, var1, 0, var0.length);
   }

   public static String join(float[] var0, char var1) {
      return var0 == null ? null : join((float[])var0, var1, 0, var0.length);
   }

   public static String join(double[] var0, char var1) {
      return var0 == null ? null : join((double[])var0, var1, 0, var0.length);
   }

   public static String join(Object[] var0, char var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               if (var0[var6] != null) {
                  var5.append(var0[var6]);
               }
            }

            return var5.toString();
         }
      }
   }

   public static String join(long[] var0, char var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               var5.append(var0[var6]);
            }

            return var5.toString();
         }
      }
   }

   public static String join(int[] var0, char var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               var5.append(var0[var6]);
            }

            return var5.toString();
         }
      }
   }

   public static String join(byte[] var0, char var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               var5.append(var0[var6]);
            }

            return var5.toString();
         }
      }
   }

   public static String join(short[] var0, char var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               var5.append(var0[var6]);
            }

            return var5.toString();
         }
      }
   }

   public static String join(char[] var0, char var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               var5.append(var0[var6]);
            }

            return var5.toString();
         }
      }
   }

   public static String join(double[] var0, char var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               var5.append(var0[var6]);
            }

            return var5.toString();
         }
      }
   }

   public static String join(float[] var0, char var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               var5.append(var0[var6]);
            }

            return var5.toString();
         }
      }
   }

   public static String join(Object[] var0, String var1) {
      return var0 == null ? null : join(var0, var1, 0, var0.length);
   }

   public static String join(Object[] var0, String var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         if (var1 == null) {
            var1 = "";
         }

         int var4 = var3 - var2;
         if (var4 <= 0) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder(var4 * 16);

            for(int var6 = var2; var6 < var3; ++var6) {
               if (var6 > var2) {
                  var5.append(var1);
               }

               if (var0[var6] != null) {
                  var5.append(var0[var6]);
               }
            }

            return var5.toString();
         }
      }
   }

   public static String join(Iterator<?> var0, char var1) {
      if (var0 == null) {
         return null;
      } else if (!var0.hasNext()) {
         return "";
      } else {
         Object var2 = var0.next();
         if (!var0.hasNext()) {
            String var5 = ObjectUtils.toString(var2);
            return var5;
         } else {
            StringBuilder var3 = new StringBuilder(256);
            if (var2 != null) {
               var3.append(var2);
            }

            while(var0.hasNext()) {
               var3.append(var1);
               Object var4 = var0.next();
               if (var4 != null) {
                  var3.append(var4);
               }
            }

            return var3.toString();
         }
      }
   }

   public static String join(Iterator<?> var0, String var1) {
      if (var0 == null) {
         return null;
      } else if (!var0.hasNext()) {
         return "";
      } else {
         Object var2 = var0.next();
         if (!var0.hasNext()) {
            String var5 = ObjectUtils.toString(var2);
            return var5;
         } else {
            StringBuilder var3 = new StringBuilder(256);
            if (var2 != null) {
               var3.append(var2);
            }

            while(var0.hasNext()) {
               if (var1 != null) {
                  var3.append(var1);
               }

               Object var4 = var0.next();
               if (var4 != null) {
                  var3.append(var4);
               }
            }

            return var3.toString();
         }
      }
   }

   public static String join(Iterable<?> var0, char var1) {
      return var0 == null ? null : join(var0.iterator(), var1);
   }

   public static String join(Iterable<?> var0, String var1) {
      return var0 == null ? null : join(var0.iterator(), var1);
   }

   public static String joinWith(String var0, Object... var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Object varargs must not be null");
      } else {
         String var2 = defaultString(var0, "");
         StringBuilder var3 = new StringBuilder();
         Iterator var4 = Arrays.asList(var1).iterator();

         while(var4.hasNext()) {
            String var5 = ObjectUtils.toString(var4.next());
            var3.append(var5);
            if (var4.hasNext()) {
               var3.append(var2);
            }
         }

         return var3.toString();
      }
   }

   public static String deleteWhitespace(String var0) {
      if (isEmpty(var0)) {
         return var0;
      } else {
         int var1 = var0.length();
         char[] var2 = new char[var1];
         int var3 = 0;

         for(int var4 = 0; var4 < var1; ++var4) {
            if (!Character.isWhitespace(var0.charAt(var4))) {
               var2[var3++] = var0.charAt(var4);
            }
         }

         if (var3 == var1) {
            return var0;
         } else {
            return new String(var2, 0, var3);
         }
      }
   }

   public static String removeStart(String var0, String var1) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         return var0.startsWith(var1) ? var0.substring(var1.length()) : var0;
      } else {
         return var0;
      }
   }

   public static String removeStartIgnoreCase(String var0, String var1) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         return startsWithIgnoreCase(var0, var1) ? var0.substring(var1.length()) : var0;
      } else {
         return var0;
      }
   }

   public static String removeEnd(String var0, String var1) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         return var0.endsWith(var1) ? var0.substring(0, var0.length() - var1.length()) : var0;
      } else {
         return var0;
      }
   }

   public static String removeEndIgnoreCase(String var0, String var1) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         return endsWithIgnoreCase(var0, var1) ? var0.substring(0, var0.length() - var1.length()) : var0;
      } else {
         return var0;
      }
   }

   public static String remove(String var0, String var1) {
      return !isEmpty(var0) && !isEmpty(var1) ? replace(var0, var1, "", -1) : var0;
   }

   public static String removeIgnoreCase(String var0, String var1) {
      return !isEmpty(var0) && !isEmpty(var1) ? replaceIgnoreCase(var0, var1, "", -1) : var0;
   }

   public static String remove(String var0, char var1) {
      if (!isEmpty(var0) && var0.indexOf(var1) != -1) {
         char[] var2 = var0.toCharArray();
         int var3 = 0;

         for(int var4 = 0; var4 < var2.length; ++var4) {
            if (var2[var4] != var1) {
               var2[var3++] = var2[var4];
            }
         }

         return new String(var2, 0, var3);
      } else {
         return var0;
      }
   }

   public static String removeAll(String var0, String var1) {
      return replaceAll(var0, var1, "");
   }

   public static String removeFirst(String var0, String var1) {
      return replaceFirst(var0, var1, "");
   }

   public static String replaceOnce(String var0, String var1, String var2) {
      return replace(var0, var1, var2, 1);
   }

   public static String replaceOnceIgnoreCase(String var0, String var1, String var2) {
      return replaceIgnoreCase(var0, var1, var2, 1);
   }

   public static String replacePattern(String var0, String var1, String var2) {
      return var0 != null && var1 != null && var2 != null ? Pattern.compile(var1, 32).matcher(var0).replaceAll(var2) : var0;
   }

   public static String removePattern(String var0, String var1) {
      return replacePattern(var0, var1, "");
   }

   public static String replaceAll(String var0, String var1, String var2) {
      return var0 != null && var1 != null && var2 != null ? var0.replaceAll(var1, var2) : var0;
   }

   public static String replaceFirst(String var0, String var1, String var2) {
      return var0 != null && var1 != null && var2 != null ? var0.replaceFirst(var1, var2) : var0;
   }

   public static String replace(String var0, String var1, String var2) {
      return replace(var0, var1, var2, -1);
   }

   public static String replaceIgnoreCase(String var0, String var1, String var2) {
      return replaceIgnoreCase(var0, var1, var2, -1);
   }

   public static String replace(String var0, String var1, String var2, int var3) {
      return replace(var0, var1, var2, var3, false);
   }

   private static String replace(String var0, String var1, String var2, int var3, boolean var4) {
      if (!isEmpty(var0) && !isEmpty(var1) && var2 != null && var3 != 0) {
         String var5 = var0;
         if (var4) {
            var5 = var0.toLowerCase();
            var1 = var1.toLowerCase();
         }

         int var6 = 0;
         int var7 = var5.indexOf(var1, var6);
         if (var7 == -1) {
            return var0;
         } else {
            int var8 = var1.length();
            int var9 = var2.length() - var8;
            var9 = var9 < 0 ? 0 : var9;
            var9 *= var3 < 0 ? 16 : (var3 > 64 ? 64 : var3);

            StringBuilder var10;
            for(var10 = new StringBuilder(var0.length() + var9); var7 != -1; var7 = var5.indexOf(var1, var6)) {
               var10.append(var0.substring(var6, var7)).append(var2);
               var6 = var7 + var8;
               --var3;
               if (var3 == 0) {
                  break;
               }
            }

            var10.append(var0.substring(var6));
            return var10.toString();
         }
      } else {
         return var0;
      }
   }

   public static String replaceIgnoreCase(String var0, String var1, String var2, int var3) {
      return replace(var0, var1, var2, var3, true);
   }

   public static String replaceEach(String var0, String[] var1, String[] var2) {
      return replaceEach(var0, var1, var2, false, 0);
   }

   public static String replaceEachRepeatedly(String var0, String[] var1, String[] var2) {
      int var3 = var1 == null ? 0 : var1.length;
      return replaceEach(var0, var1, var2, true, var3);
   }

   private static String replaceEach(String var0, String[] var1, String[] var2, boolean var3, int var4) {
      if (var0 != null && !var0.isEmpty() && var1 != null && var1.length != 0 && var2 != null && var2.length != 0) {
         if (var4 < 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - output of one loop is the input of another");
         } else {
            int var5 = var1.length;
            int var6 = var2.length;
            if (var5 != var6) {
               throw new IllegalArgumentException("Search and Replace array lengths don't match: " + var5 + " vs " + var6);
            } else {
               boolean[] var7 = new boolean[var5];
               int var8 = -1;
               int var9 = -1;
               boolean var10 = true;

               int var11;
               int var16;
               for(var11 = 0; var11 < var5; ++var11) {
                  if (!var7[var11] && var1[var11] != null && !var1[var11].isEmpty() && var2[var11] != null) {
                     var16 = var0.indexOf(var1[var11]);
                     if (var16 == -1) {
                        var7[var11] = true;
                     } else if (var8 == -1 || var16 < var8) {
                        var8 = var16;
                        var9 = var11;
                     }
                  }
               }

               if (var8 == -1) {
                  return var0;
               } else {
                  var11 = 0;
                  int var12 = 0;

                  int var14;
                  for(int var13 = 0; var13 < var1.length; ++var13) {
                     if (var1[var13] != null && var2[var13] != null) {
                        var14 = var2[var13].length() - var1[var13].length();
                        if (var14 > 0) {
                           var12 += 3 * var14;
                        }
                     }
                  }

                  var12 = Math.min(var12, var0.length() / 5);
                  StringBuilder var17 = new StringBuilder(var0.length() + var12);

                  while(var8 != -1) {
                     for(var14 = var11; var14 < var8; ++var14) {
                        var17.append(var0.charAt(var14));
                     }

                     var17.append(var2[var9]);
                     var11 = var8 + var1[var9].length();
                     var8 = -1;
                     var9 = -1;
                     var10 = true;

                     for(var14 = 0; var14 < var5; ++var14) {
                        if (!var7[var14] && var1[var14] != null && !var1[var14].isEmpty() && var2[var14] != null) {
                           var16 = var0.indexOf(var1[var14], var11);
                           if (var16 == -1) {
                              var7[var14] = true;
                           } else if (var8 == -1 || var16 < var8) {
                              var8 = var16;
                              var9 = var14;
                           }
                        }
                     }
                  }

                  var14 = var0.length();

                  for(int var15 = var11; var15 < var14; ++var15) {
                     var17.append(var0.charAt(var15));
                  }

                  String var18 = var17.toString();
                  if (!var3) {
                     return var18;
                  } else {
                     return replaceEach(var18, var1, var2, var3, var4 - 1);
                  }
               }
            }
         }
      } else {
         return var0;
      }
   }

   public static String replaceChars(String var0, char var1, char var2) {
      return var0 == null ? null : var0.replace(var1, var2);
   }

   public static String replaceChars(String var0, String var1, String var2) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         if (var2 == null) {
            var2 = "";
         }

         boolean var3 = false;
         int var4 = var2.length();
         int var5 = var0.length();
         StringBuilder var6 = new StringBuilder(var5);

         for(int var7 = 0; var7 < var5; ++var7) {
            char var8 = var0.charAt(var7);
            int var9 = var1.indexOf(var8);
            if (var9 >= 0) {
               var3 = true;
               if (var9 < var4) {
                  var6.append(var2.charAt(var9));
               }
            } else {
               var6.append(var8);
            }
         }

         if (var3) {
            return var6.toString();
         } else {
            return var0;
         }
      } else {
         return var0;
      }
   }

   public static String overlay(String var0, String var1, int var2, int var3) {
      if (var0 == null) {
         return null;
      } else {
         if (var1 == null) {
            var1 = "";
         }

         int var4 = var0.length();
         if (var2 < 0) {
            var2 = 0;
         }

         if (var2 > var4) {
            var2 = var4;
         }

         if (var3 < 0) {
            var3 = 0;
         }

         if (var3 > var4) {
            var3 = var4;
         }

         if (var2 > var3) {
            int var5 = var2;
            var2 = var3;
            var3 = var5;
         }

         return (new StringBuilder(var4 + var2 - var3 + var1.length() + 1)).append(var0.substring(0, var2)).append(var1).append(var0.substring(var3)).toString();
      }
   }

   public static String chomp(String var0) {
      if (isEmpty(var0)) {
         return var0;
      } else if (var0.length() == 1) {
         char var3 = var0.charAt(0);
         return var3 != '\r' && var3 != '\n' ? var0 : "";
      } else {
         int var1 = var0.length() - 1;
         char var2 = var0.charAt(var1);
         if (var2 == '\n') {
            if (var0.charAt(var1 - 1) == '\r') {
               --var1;
            }
         } else if (var2 != '\r') {
            ++var1;
         }

         return var0.substring(0, var1);
      }
   }

   /** @deprecated */
   @Deprecated
   public static String chomp(String var0, String var1) {
      return removeEnd(var0, var1);
   }

   public static String chop(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.length();
         if (var1 < 2) {
            return "";
         } else {
            int var2 = var1 - 1;
            String var3 = var0.substring(0, var2);
            char var4 = var0.charAt(var2);
            return var4 == '\n' && var3.charAt(var2 - 1) == '\r' ? var3.substring(0, var2 - 1) : var3;
         }
      }
   }

   public static String repeat(String var0, int var1) {
      if (var0 == null) {
         return null;
      } else if (var1 <= 0) {
         return "";
      } else {
         int var2 = var0.length();
         if (var1 != 1 && var2 != 0) {
            if (var2 == 1 && var1 <= 8192) {
               return repeat(var0.charAt(0), var1);
            } else {
               int var3 = var2 * var1;
               switch(var2) {
               case 1:
                  return repeat(var0.charAt(0), var1);
               case 2:
                  char var4 = var0.charAt(0);
                  char var5 = var0.charAt(1);
                  char[] var6 = new char[var3];

                  for(int var7 = var1 * 2 - 2; var7 >= 0; --var7) {
                     var6[var7] = var4;
                     var6[var7 + 1] = var5;
                     --var7;
                  }

                  return new String(var6);
               default:
                  StringBuilder var9 = new StringBuilder(var3);

                  for(int var8 = 0; var8 < var1; ++var8) {
                     var9.append(var0);
                  }

                  return var9.toString();
               }
            }
         } else {
            return var0;
         }
      }
   }

   public static String repeat(String var0, String var1, int var2) {
      if (var0 != null && var1 != null) {
         String var3 = repeat(var0 + var1, var2);
         return removeEnd(var3, var1);
      } else {
         return repeat(var0, var2);
      }
   }

   public static String repeat(char var0, int var1) {
      if (var1 <= 0) {
         return "";
      } else {
         char[] var2 = new char[var1];

         for(int var3 = var1 - 1; var3 >= 0; --var3) {
            var2[var3] = var0;
         }

         return new String(var2);
      }
   }

   public static String rightPad(String var0, int var1) {
      return rightPad(var0, var1, ' ');
   }

   public static String rightPad(String var0, int var1, char var2) {
      if (var0 == null) {
         return null;
      } else {
         int var3 = var1 - var0.length();
         if (var3 <= 0) {
            return var0;
         } else {
            return var3 > 8192 ? rightPad(var0, var1, String.valueOf(var2)) : var0.concat(repeat(var2, var3));
         }
      }
   }

   public static String rightPad(String var0, int var1, String var2) {
      if (var0 == null) {
         return null;
      } else {
         if (isEmpty(var2)) {
            var2 = " ";
         }

         int var3 = var2.length();
         int var4 = var0.length();
         int var5 = var1 - var4;
         if (var5 <= 0) {
            return var0;
         } else if (var3 == 1 && var5 <= 8192) {
            return rightPad(var0, var1, var2.charAt(0));
         } else if (var5 == var3) {
            return var0.concat(var2);
         } else if (var5 < var3) {
            return var0.concat(var2.substring(0, var5));
         } else {
            char[] var6 = new char[var5];
            char[] var7 = var2.toCharArray();

            for(int var8 = 0; var8 < var5; ++var8) {
               var6[var8] = var7[var8 % var3];
            }

            return var0.concat(new String(var6));
         }
      }
   }

   public static String leftPad(String var0, int var1) {
      return leftPad(var0, var1, ' ');
   }

   public static String leftPad(String var0, int var1, char var2) {
      if (var0 == null) {
         return null;
      } else {
         int var3 = var1 - var0.length();
         if (var3 <= 0) {
            return var0;
         } else {
            return var3 > 8192 ? leftPad(var0, var1, String.valueOf(var2)) : repeat(var2, var3).concat(var0);
         }
      }
   }

   public static String leftPad(String var0, int var1, String var2) {
      if (var0 == null) {
         return null;
      } else {
         if (isEmpty(var2)) {
            var2 = " ";
         }

         int var3 = var2.length();
         int var4 = var0.length();
         int var5 = var1 - var4;
         if (var5 <= 0) {
            return var0;
         } else if (var3 == 1 && var5 <= 8192) {
            return leftPad(var0, var1, var2.charAt(0));
         } else if (var5 == var3) {
            return var2.concat(var0);
         } else if (var5 < var3) {
            return var2.substring(0, var5).concat(var0);
         } else {
            char[] var6 = new char[var5];
            char[] var7 = var2.toCharArray();

            for(int var8 = 0; var8 < var5; ++var8) {
               var6[var8] = var7[var8 % var3];
            }

            return (new String(var6)).concat(var0);
         }
      }
   }

   public static int length(CharSequence var0) {
      return var0 == null ? 0 : var0.length();
   }

   public static String center(String var0, int var1) {
      return center(var0, var1, ' ');
   }

   public static String center(String var0, int var1, char var2) {
      if (var0 != null && var1 > 0) {
         int var3 = var0.length();
         int var4 = var1 - var3;
         if (var4 <= 0) {
            return var0;
         } else {
            var0 = leftPad(var0, var3 + var4 / 2, var2);
            var0 = rightPad(var0, var1, var2);
            return var0;
         }
      } else {
         return var0;
      }
   }

   public static String center(String var0, int var1, String var2) {
      if (var0 != null && var1 > 0) {
         if (isEmpty(var2)) {
            var2 = " ";
         }

         int var3 = var0.length();
         int var4 = var1 - var3;
         if (var4 <= 0) {
            return var0;
         } else {
            var0 = leftPad(var0, var3 + var4 / 2, var2);
            var0 = rightPad(var0, var1, var2);
            return var0;
         }
      } else {
         return var0;
      }
   }

   public static String upperCase(String var0) {
      return var0 == null ? null : var0.toUpperCase();
   }

   public static String upperCase(String var0, Locale var1) {
      return var0 == null ? null : var0.toUpperCase(var1);
   }

   public static String lowerCase(String var0) {
      return var0 == null ? null : var0.toLowerCase();
   }

   public static String lowerCase(String var0, Locale var1) {
      return var0 == null ? null : var0.toLowerCase(var1);
   }

   public static String capitalize(String var0) {
      int var1;
      if (var0 != null && (var1 = var0.length()) != 0) {
         char var2 = var0.charAt(0);
         char var3 = Character.toTitleCase(var2);
         if (var2 == var3) {
            return var0;
         } else {
            char[] var4 = new char[var1];
            var4[0] = var3;
            var0.getChars(1, var1, var4, 1);
            return String.valueOf(var4);
         }
      } else {
         return var0;
      }
   }

   public static String uncapitalize(String var0) {
      int var1;
      if (var0 != null && (var1 = var0.length()) != 0) {
         char var2 = var0.charAt(0);
         char var3 = Character.toLowerCase(var2);
         if (var2 == var3) {
            return var0;
         } else {
            char[] var4 = new char[var1];
            var4[0] = var3;
            var0.getChars(1, var1, var4, 1);
            return String.valueOf(var4);
         }
      } else {
         return var0;
      }
   }

   public static String swapCase(String var0) {
      if (isEmpty(var0)) {
         return var0;
      } else {
         char[] var1 = var0.toCharArray();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            char var3 = var1[var2];
            if (Character.isUpperCase(var3)) {
               var1[var2] = Character.toLowerCase(var3);
            } else if (Character.isTitleCase(var3)) {
               var1[var2] = Character.toLowerCase(var3);
            } else if (Character.isLowerCase(var3)) {
               var1[var2] = Character.toUpperCase(var3);
            }
         }

         return new String(var1);
      }
   }

   public static int countMatches(CharSequence var0, CharSequence var1) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         int var2 = 0;

         for(int var3 = 0; (var3 = CharSequenceUtils.indexOf(var0, var1, var3)) != -1; var3 += var1.length()) {
            ++var2;
         }

         return var2;
      } else {
         return 0;
      }
   }

   public static int countMatches(CharSequence var0, char var1) {
      if (isEmpty(var0)) {
         return 0;
      } else {
         int var2 = 0;

         for(int var3 = 0; var3 < var0.length(); ++var3) {
            if (var1 == var0.charAt(var3)) {
               ++var2;
            }
         }

         return var2;
      }
   }

   public static boolean isAlpha(CharSequence var0) {
      if (isEmpty(var0)) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isLetter(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphaSpace(CharSequence var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isLetter(var0.charAt(var2)) && var0.charAt(var2) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphanumeric(CharSequence var0) {
      if (isEmpty(var0)) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isLetterOrDigit(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphanumericSpace(CharSequence var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isLetterOrDigit(var0.charAt(var2)) && var0.charAt(var2) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAsciiPrintable(CharSequence var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!CharUtils.isAsciiPrintable(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNumeric(CharSequence var0) {
      if (isEmpty(var0)) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isDigit(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNumericSpace(CharSequence var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isDigit(var0.charAt(var2)) && var0.charAt(var2) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isWhitespace(CharSequence var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isWhitespace(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAllLowerCase(CharSequence var0) {
      if (var0 != null && !isEmpty(var0)) {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isLowerCase(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean isAllUpperCase(CharSequence var0) {
      if (var0 != null && !isEmpty(var0)) {
         int var1 = var0.length();

         for(int var2 = 0; var2 < var1; ++var2) {
            if (!Character.isUpperCase(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static String defaultString(String var0) {
      return var0 == null ? "" : var0;
   }

   public static String defaultString(String var0, String var1) {
      return var0 == null ? var1 : var0;
   }

   public static <T extends CharSequence> T defaultIfBlank(T var0, T var1) {
      return isBlank(var0) ? var1 : var0;
   }

   public static <T extends CharSequence> T defaultIfEmpty(T var0, T var1) {
      return isEmpty(var0) ? var1 : var0;
   }

   public static String rotate(String var0, int var1) {
      if (var0 == null) {
         return null;
      } else {
         int var2 = var0.length();
         if (var1 != 0 && var2 != 0 && var1 % var2 != 0) {
            StringBuilder var3 = new StringBuilder(var2);
            int var4 = -(var1 % var2);
            var3.append(substring(var0, var4));
            var3.append(substring(var0, 0, var4));
            return var3.toString();
         } else {
            return var0;
         }
      }
   }

   public static String reverse(String var0) {
      return var0 == null ? null : (new StringBuilder(var0)).reverse().toString();
   }

   public static String reverseDelimited(String var0, char var1) {
      if (var0 == null) {
         return null;
      } else {
         String[] var2 = split(var0, var1);
         ArrayUtils.reverse((Object[])var2);
         return join((Object[])var2, var1);
      }
   }

   public static String abbreviate(String var0, int var1) {
      return abbreviate(var0, 0, var1);
   }

   public static String abbreviate(String var0, int var1, int var2) {
      if (var0 == null) {
         return null;
      } else if (var2 < 4) {
         throw new IllegalArgumentException("Minimum abbreviation width is 4");
      } else if (var0.length() <= var2) {
         return var0;
      } else {
         if (var1 > var0.length()) {
            var1 = var0.length();
         }

         if (var0.length() - var1 < var2 - 3) {
            var1 = var0.length() - (var2 - 3);
         }

         String var3 = "...";
         if (var1 <= 4) {
            return var0.substring(0, var2 - 3) + "...";
         } else if (var2 < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
         } else {
            return var1 + var2 - 3 < var0.length() ? "..." + abbreviate(var0.substring(var1), var2 - 3) : "..." + var0.substring(var0.length() - (var2 - 3));
         }
      }
   }

   public static String abbreviateMiddle(String var0, String var1, int var2) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         if (var2 < var0.length() && var2 >= var1.length() + 2) {
            int var3 = var2 - var1.length();
            int var4 = var3 / 2 + var3 % 2;
            int var5 = var0.length() - var3 / 2;
            StringBuilder var6 = new StringBuilder(var2);
            var6.append(var0.substring(0, var4));
            var6.append(var1);
            var6.append(var0.substring(var5));
            return var6.toString();
         } else {
            return var0;
         }
      } else {
         return var0;
      }
   }

   public static String difference(String var0, String var1) {
      if (var0 == null) {
         return var1;
      } else if (var1 == null) {
         return var0;
      } else {
         int var2 = indexOfDifference(var0, var1);
         return var2 == -1 ? "" : var1.substring(var2);
      }
   }

   public static int indexOfDifference(CharSequence var0, CharSequence var1) {
      if (var0 == var1) {
         return -1;
      } else if (var0 != null && var1 != null) {
         int var2;
         for(var2 = 0; var2 < var0.length() && var2 < var1.length() && var0.charAt(var2) == var1.charAt(var2); ++var2) {
         }

         return var2 >= var1.length() && var2 >= var0.length() ? -1 : var2;
      } else {
         return 0;
      }
   }

   public static int indexOfDifference(CharSequence... var0) {
      if (var0 != null && var0.length > 1) {
         boolean var1 = false;
         boolean var2 = true;
         int var3 = var0.length;
         int var4 = 2147483647;
         int var5 = 0;

         int var6;
         for(var6 = 0; var6 < var3; ++var6) {
            if (var0[var6] == null) {
               var1 = true;
               var4 = 0;
            } else {
               var2 = false;
               var4 = Math.min(var0[var6].length(), var4);
               var5 = Math.max(var0[var6].length(), var5);
            }
         }

         if (var2 || var5 == 0 && !var1) {
            return -1;
         } else if (var4 == 0) {
            return 0;
         } else {
            var6 = -1;

            for(int var7 = 0; var7 < var4; ++var7) {
               char var8 = var0[0].charAt(var7);

               for(int var9 = 1; var9 < var3; ++var9) {
                  if (var0[var9].charAt(var7) != var8) {
                     var6 = var7;
                     break;
                  }
               }

               if (var6 != -1) {
                  break;
               }
            }

            return var6 == -1 && var4 != var5 ? var4 : var6;
         }
      } else {
         return -1;
      }
   }

   public static String getCommonPrefix(String... var0) {
      if (var0 != null && var0.length != 0) {
         int var1 = indexOfDifference(var0);
         if (var1 == -1) {
            return var0[0] == null ? "" : var0[0];
         } else {
            return var1 == 0 ? "" : var0[0].substring(0, var1);
         }
      } else {
         return "";
      }
   }

   public static int getLevenshteinDistance(CharSequence var0, CharSequence var1) {
      if (var0 != null && var1 != null) {
         int var2 = var0.length();
         int var3 = var1.length();
         if (var2 == 0) {
            return var3;
         } else if (var3 == 0) {
            return var2;
         } else {
            if (var2 > var3) {
               CharSequence var4 = var0;
               var0 = var1;
               var1 = var4;
               var2 = var3;
               var3 = var4.length();
            }

            int[] var11 = new int[var2 + 1];
            int[] var5 = new int[var2 + 1];

            int var7;
            for(var7 = 0; var7 <= var2; var11[var7] = var7++) {
            }

            for(int var8 = 1; var8 <= var3; ++var8) {
               char var9 = var1.charAt(var8 - 1);
               var5[0] = var8;

               for(var7 = 1; var7 <= var2; ++var7) {
                  int var10 = var0.charAt(var7 - 1) == var9 ? 0 : 1;
                  var5[var7] = Math.min(Math.min(var5[var7 - 1] + 1, var11[var7] + 1), var11[var7 - 1] + var10);
               }

               int[] var6 = var11;
               var11 = var5;
               var5 = var6;
            }

            return var11[var2];
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   public static int getLevenshteinDistance(CharSequence var0, CharSequence var1, int var2) {
      if (var0 != null && var1 != null) {
         if (var2 < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
         } else {
            int var3 = var0.length();
            int var4 = var1.length();
            if (var3 == 0) {
               return var4 <= var2 ? var4 : -1;
            } else if (var4 == 0) {
               return var3 <= var2 ? var3 : -1;
            } else if (Math.abs(var3 - var4) > var2) {
               return -1;
            } else {
               if (var3 > var4) {
                  CharSequence var5 = var0;
                  var0 = var1;
                  var1 = var5;
                  var3 = var4;
                  var4 = var5.length();
               }

               int[] var14 = new int[var3 + 1];
               int[] var6 = new int[var3 + 1];
               int var8 = Math.min(var3, var2) + 1;

               int var9;
               for(var9 = 0; var9 < var8; var14[var9] = var9++) {
               }

               Arrays.fill(var14, var8, var14.length, 2147483647);
               Arrays.fill(var6, 2147483647);

               for(var9 = 1; var9 <= var4; ++var9) {
                  char var10 = var1.charAt(var9 - 1);
                  var6[0] = var9;
                  int var11 = Math.max(1, var9 - var2);
                  int var12 = var9 > 2147483647 - var2 ? var3 : Math.min(var3, var9 + var2);
                  if (var11 > var12) {
                     return -1;
                  }

                  if (var11 > 1) {
                     var6[var11 - 1] = 2147483647;
                  }

                  for(int var13 = var11; var13 <= var12; ++var13) {
                     if (var0.charAt(var13 - 1) == var10) {
                        var6[var13] = var14[var13 - 1];
                     } else {
                        var6[var13] = 1 + Math.min(Math.min(var6[var13 - 1], var14[var13]), var14[var13 - 1]);
                     }
                  }

                  int[] var7 = var14;
                  var14 = var6;
                  var6 = var7;
               }

               if (var14[var3] <= var2) {
                  return var14[var3];
               } else {
                  return -1;
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   public static double getJaroWinklerDistance(CharSequence var0, CharSequence var1) {
      double var2 = 0.1D;
      if (var0 != null && var1 != null) {
         int[] var4 = matches(var0, var1);
         double var5 = (double)var4[0];
         if (var5 == 0.0D) {
            return 0.0D;
         } else {
            double var7 = (var5 / (double)var0.length() + var5 / (double)var1.length() + (var5 - (double)var4[1]) / var5) / 3.0D;
            double var9 = var7 < 0.7D ? var7 : var7 + Math.min(0.1D, 1.0D / (double)var4[3]) * (double)var4[2] * (1.0D - var7);
            return (double)Math.round(var9 * 100.0D) / 100.0D;
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   private static int[] matches(CharSequence var0, CharSequence var1) {
      CharSequence var2;
      CharSequence var3;
      if (var0.length() > var1.length()) {
         var2 = var0;
         var3 = var1;
      } else {
         var2 = var1;
         var3 = var0;
      }

      int var4 = Math.max(var2.length() / 2 - 1, 0);
      int[] var5 = new int[var3.length()];
      Arrays.fill(var5, -1);
      boolean[] var6 = new boolean[var2.length()];
      int var7 = 0;

      int var10;
      int var11;
      for(int var8 = 0; var8 < var3.length(); ++var8) {
         char var9 = var3.charAt(var8);
         var10 = Math.max(var8 - var4, 0);

         for(var11 = Math.min(var8 + var4 + 1, var2.length()); var10 < var11; ++var10) {
            if (!var6[var10] && var9 == var2.charAt(var10)) {
               var5[var8] = var10;
               var6[var10] = true;
               ++var7;
               break;
            }
         }
      }

      char[] var13 = new char[var7];
      char[] var14 = new char[var7];
      var10 = 0;

      for(var11 = 0; var10 < var3.length(); ++var10) {
         if (var5[var10] != -1) {
            var13[var11] = var3.charAt(var10);
            ++var11;
         }
      }

      var10 = 0;

      for(var11 = 0; var10 < var2.length(); ++var10) {
         if (var6[var10]) {
            var14[var11] = var2.charAt(var10);
            ++var11;
         }
      }

      var10 = 0;

      for(var11 = 0; var11 < var13.length; ++var11) {
         if (var13[var11] != var14[var11]) {
            ++var10;
         }
      }

      var11 = 0;

      for(int var12 = 0; var12 < var3.length() && var0.charAt(var12) == var1.charAt(var12); ++var12) {
         ++var11;
      }

      return new int[]{var7, var10 / 2, var11, var2.length()};
   }

   public static int getFuzzyDistance(CharSequence var0, CharSequence var1, Locale var2) {
      if (var0 != null && var1 != null) {
         if (var2 == null) {
            throw new IllegalArgumentException("Locale must not be null");
         } else {
            String var3 = var0.toString().toLowerCase(var2);
            String var4 = var1.toString().toLowerCase(var2);
            int var5 = 0;
            int var6 = 0;
            int var7 = -2147483648;

            for(int var8 = 0; var8 < var4.length(); ++var8) {
               char var9 = var4.charAt(var8);

               for(boolean var10 = false; var6 < var3.length() && !var10; ++var6) {
                  char var11 = var3.charAt(var6);
                  if (var9 == var11) {
                     ++var5;
                     if (var7 + 1 == var6) {
                        var5 += 2;
                     }

                     var7 = var6;
                     var10 = true;
                  }
               }
            }

            return var5;
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   public static boolean startsWith(CharSequence var0, CharSequence var1) {
      return startsWith(var0, var1, false);
   }

   public static boolean startsWithIgnoreCase(CharSequence var0, CharSequence var1) {
      return startsWith(var0, var1, true);
   }

   private static boolean startsWith(CharSequence var0, CharSequence var1, boolean var2) {
      if (var0 != null && var1 != null) {
         return var1.length() > var0.length() ? false : CharSequenceUtils.regionMatches(var0, var2, 0, var1, 0, var1.length());
      } else {
         return var0 == null && var1 == null;
      }
   }

   public static boolean startsWithAny(CharSequence var0, CharSequence... var1) {
      if (!isEmpty(var0) && !ArrayUtils.isEmpty((Object[])var1)) {
         CharSequence[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence var5 = var2[var4];
            if (startsWith(var0, var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean endsWith(CharSequence var0, CharSequence var1) {
      return endsWith(var0, var1, false);
   }

   public static boolean endsWithIgnoreCase(CharSequence var0, CharSequence var1) {
      return endsWith(var0, var1, true);
   }

   private static boolean endsWith(CharSequence var0, CharSequence var1, boolean var2) {
      if (var0 != null && var1 != null) {
         if (var1.length() > var0.length()) {
            return false;
         } else {
            int var3 = var0.length() - var1.length();
            return CharSequenceUtils.regionMatches(var0, var2, var3, var1, 0, var1.length());
         }
      } else {
         return var0 == null && var1 == null;
      }
   }

   public static String normalizeSpace(String var0) {
      if (isEmpty(var0)) {
         return var0;
      } else {
         int var1 = var0.length();
         char[] var2 = new char[var1];
         int var3 = 0;
         int var4 = 0;
         boolean var5 = true;

         for(int var6 = 0; var6 < var1; ++var6) {
            char var7 = var0.charAt(var6);
            boolean var8 = Character.isWhitespace(var7);
            if (!var8) {
               var5 = false;
               var2[var3++] = var7 == 160 ? 32 : var7;
               var4 = 0;
            } else {
               if (var4 == 0 && !var5) {
                  var2[var3++] = " ".charAt(0);
               }

               ++var4;
            }
         }

         if (var5) {
            return "";
         } else {
            return (new String(var2, 0, var3 - (var4 > 0 ? 1 : 0))).trim();
         }
      }
   }

   public static boolean endsWithAny(CharSequence var0, CharSequence... var1) {
      if (!isEmpty(var0) && !ArrayUtils.isEmpty((Object[])var1)) {
         CharSequence[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence var5 = var2[var4];
            if (endsWith(var0, var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static String appendIfMissing(String var0, CharSequence var1, boolean var2, CharSequence... var3) {
      if (var0 != null && !isEmpty(var1) && !endsWith(var0, var1, var2)) {
         if (var3 != null && var3.length > 0) {
            CharSequence[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               CharSequence var7 = var4[var6];
               if (endsWith(var0, var7, var2)) {
                  return var0;
               }
            }
         }

         return var0 + var1.toString();
      } else {
         return var0;
      }
   }

   public static String appendIfMissing(String var0, CharSequence var1, CharSequence... var2) {
      return appendIfMissing(var0, var1, false, var2);
   }

   public static String appendIfMissingIgnoreCase(String var0, CharSequence var1, CharSequence... var2) {
      return appendIfMissing(var0, var1, true, var2);
   }

   private static String prependIfMissing(String var0, CharSequence var1, boolean var2, CharSequence... var3) {
      if (var0 != null && !isEmpty(var1) && !startsWith(var0, var1, var2)) {
         if (var3 != null && var3.length > 0) {
            CharSequence[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               CharSequence var7 = var4[var6];
               if (startsWith(var0, var7, var2)) {
                  return var0;
               }
            }
         }

         return var1.toString() + var0;
      } else {
         return var0;
      }
   }

   public static String prependIfMissing(String var0, CharSequence var1, CharSequence... var2) {
      return prependIfMissing(var0, var1, false, var2);
   }

   public static String prependIfMissingIgnoreCase(String var0, CharSequence var1, CharSequence... var2) {
      return prependIfMissing(var0, var1, true, var2);
   }

   /** @deprecated */
   @Deprecated
   public static String toString(byte[] var0, String var1) throws UnsupportedEncodingException {
      return var1 != null ? new String(var0, var1) : new String(var0, Charset.defaultCharset());
   }

   public static String toEncodedString(byte[] var0, Charset var1) {
      return new String(var0, var1 != null ? var1 : Charset.defaultCharset());
   }

   public static String wrap(String var0, char var1) {
      return !isEmpty(var0) && var1 != 0 ? var1 + var0 + var1 : var0;
   }

   public static String wrap(String var0, String var1) {
      return !isEmpty(var0) && !isEmpty(var1) ? var1.concat(var0).concat(var1) : var0;
   }

   public static String wrapIfMissing(String var0, char var1) {
      if (!isEmpty(var0) && var1 != 0) {
         StringBuilder var2 = new StringBuilder(var0.length() + 2);
         if (var0.charAt(0) != var1) {
            var2.append(var1);
         }

         var2.append(var0);
         if (var0.charAt(var0.length() - 1) != var1) {
            var2.append(var1);
         }

         return var2.toString();
      } else {
         return var0;
      }
   }

   public static String wrapIfMissing(String var0, String var1) {
      if (!isEmpty(var0) && !isEmpty(var1)) {
         StringBuilder var2 = new StringBuilder(var0.length() + var1.length() + var1.length());
         if (!var0.startsWith(var1)) {
            var2.append(var1);
         }

         var2.append(var0);
         if (!var0.endsWith(var1)) {
            var2.append(var1);
         }

         return var2.toString();
      } else {
         return var0;
      }
   }
}
