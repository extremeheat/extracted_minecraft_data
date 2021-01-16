package org.apache.commons.lang3.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class WordUtils {
   public WordUtils() {
      super();
   }

   public static String wrap(String var0, int var1) {
      return wrap(var0, var1, (String)null, false);
   }

   public static String wrap(String var0, int var1, String var2, boolean var3) {
      return wrap(var0, var1, var2, var3, " ");
   }

   public static String wrap(String var0, int var1, String var2, boolean var3, String var4) {
      if (var0 == null) {
         return null;
      } else {
         if (var2 == null) {
            var2 = SystemUtils.LINE_SEPARATOR;
         }

         if (var1 < 1) {
            var1 = 1;
         }

         if (StringUtils.isBlank(var4)) {
            var4 = " ";
         }

         Pattern var5 = Pattern.compile(var4);
         int var6 = var0.length();
         int var7 = 0;
         StringBuilder var8 = new StringBuilder(var6 + 32);

         while(var7 < var6) {
            int var9 = -1;
            Matcher var10 = var5.matcher(var0.substring(var7, Math.min(var7 + var1 + 1, var6)));
            if (var10.find()) {
               if (var10.start() == 0) {
                  var7 += var10.end();
                  continue;
               }

               var9 = var10.start();
            }

            if (var6 - var7 <= var1) {
               break;
            }

            while(var10.find()) {
               var9 = var10.start() + var7;
            }

            if (var9 >= var7) {
               var8.append(var0.substring(var7, var9));
               var8.append(var2);
               var7 = var9 + 1;
            } else if (var3) {
               var8.append(var0.substring(var7, var1 + var7));
               var8.append(var2);
               var7 += var1;
            } else {
               var10 = var5.matcher(var0.substring(var7 + var1));
               if (var10.find()) {
                  var9 = var10.start() + var7 + var1;
               }

               if (var9 >= 0) {
                  var8.append(var0.substring(var7, var9));
                  var8.append(var2);
                  var7 = var9 + 1;
               } else {
                  var8.append(var0.substring(var7));
                  var7 = var6;
               }
            }
         }

         var8.append(var0.substring(var7));
         return var8.toString();
      }
   }

   public static String capitalize(String var0) {
      return capitalize(var0, (char[])null);
   }

   public static String capitalize(String var0, char... var1) {
      int var2 = var1 == null ? -1 : var1.length;
      if (!StringUtils.isEmpty(var0) && var2 != 0) {
         char[] var3 = var0.toCharArray();
         boolean var4 = true;

         for(int var5 = 0; var5 < var3.length; ++var5) {
            char var6 = var3[var5];
            if (isDelimiter(var6, var1)) {
               var4 = true;
            } else if (var4) {
               var3[var5] = Character.toTitleCase(var6);
               var4 = false;
            }
         }

         return new String(var3);
      } else {
         return var0;
      }
   }

   public static String capitalizeFully(String var0) {
      return capitalizeFully(var0, (char[])null);
   }

   public static String capitalizeFully(String var0, char... var1) {
      int var2 = var1 == null ? -1 : var1.length;
      if (!StringUtils.isEmpty(var0) && var2 != 0) {
         var0 = var0.toLowerCase();
         return capitalize(var0, var1);
      } else {
         return var0;
      }
   }

   public static String uncapitalize(String var0) {
      return uncapitalize(var0, (char[])null);
   }

   public static String uncapitalize(String var0, char... var1) {
      int var2 = var1 == null ? -1 : var1.length;
      if (!StringUtils.isEmpty(var0) && var2 != 0) {
         char[] var3 = var0.toCharArray();
         boolean var4 = true;

         for(int var5 = 0; var5 < var3.length; ++var5) {
            char var6 = var3[var5];
            if (isDelimiter(var6, var1)) {
               var4 = true;
            } else if (var4) {
               var3[var5] = Character.toLowerCase(var6);
               var4 = false;
            }
         }

         return new String(var3);
      } else {
         return var0;
      }
   }

   public static String swapCase(String var0) {
      if (StringUtils.isEmpty(var0)) {
         return var0;
      } else {
         char[] var1 = var0.toCharArray();
         boolean var2 = true;

         for(int var3 = 0; var3 < var1.length; ++var3) {
            char var4 = var1[var3];
            if (Character.isUpperCase(var4)) {
               var1[var3] = Character.toLowerCase(var4);
               var2 = false;
            } else if (Character.isTitleCase(var4)) {
               var1[var3] = Character.toLowerCase(var4);
               var2 = false;
            } else if (Character.isLowerCase(var4)) {
               if (var2) {
                  var1[var3] = Character.toTitleCase(var4);
                  var2 = false;
               } else {
                  var1[var3] = Character.toUpperCase(var4);
               }
            } else {
               var2 = Character.isWhitespace(var4);
            }
         }

         return new String(var1);
      }
   }

   public static String initials(String var0) {
      return initials(var0, (char[])null);
   }

   public static String initials(String var0, char... var1) {
      if (StringUtils.isEmpty(var0)) {
         return var0;
      } else if (var1 != null && var1.length == 0) {
         return "";
      } else {
         int var2 = var0.length();
         char[] var3 = new char[var2 / 2 + 1];
         int var4 = 0;
         boolean var5 = true;

         for(int var6 = 0; var6 < var2; ++var6) {
            char var7 = var0.charAt(var6);
            if (isDelimiter(var7, var1)) {
               var5 = true;
            } else if (var5) {
               var3[var4++] = var7;
               var5 = false;
            }
         }

         return new String(var3, 0, var4);
      }
   }

   public static boolean containsAllWords(CharSequence var0, CharSequence... var1) {
      if (!StringUtils.isEmpty(var0) && !ArrayUtils.isEmpty((Object[])var1)) {
         CharSequence[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence var5 = var2[var4];
            if (StringUtils.isBlank(var5)) {
               return false;
            }

            Pattern var6 = Pattern.compile(".*\\b" + var5 + "\\b.*");
            if (!var6.matcher(var0).matches()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean isDelimiter(char var0, char[] var1) {
      if (var1 == null) {
         return Character.isWhitespace(var0);
      } else {
         char[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var2[var4];
            if (var0 == var5) {
               return true;
            }
         }

         return false;
      }
   }
}
