package org.apache.logging.log4j.message;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.StringBuilderFormattable;

final class ParameterFormatter {
   static final String RECURSION_PREFIX = "[...";
   static final String RECURSION_SUFFIX = "...]";
   static final String ERROR_PREFIX = "[!!!";
   static final String ERROR_SEPARATOR = "=>";
   static final String ERROR_MSG_SEPARATOR = ":";
   static final String ERROR_SUFFIX = "!!!]";
   private static final char DELIM_START = '{';
   private static final char DELIM_STOP = '}';
   private static final char ESCAPE_CHAR = '\\';
   private static ThreadLocal<SimpleDateFormat> threadLocalSimpleDateFormat = new ThreadLocal();

   private ParameterFormatter() {
      super();
   }

   static int countArgumentPlaceholders(String var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = var0.length();
         int var2 = 0;
         boolean var3 = false;

         for(int var4 = 0; var4 < var1 - 1; ++var4) {
            char var5 = var0.charAt(var4);
            if (var5 == '\\') {
               var3 = !var3;
            } else if (var5 == '{') {
               if (!var3 && var0.charAt(var4 + 1) == '}') {
                  ++var2;
                  ++var4;
               }

               var3 = false;
            } else {
               var3 = false;
            }
         }

         return var2;
      }
   }

   static int countArgumentPlaceholders2(String var0, int[] var1) {
      if (var0 == null) {
         return 0;
      } else {
         int var2 = var0.length();
         int var3 = 0;
         boolean var4 = false;

         for(int var5 = 0; var5 < var2 - 1; ++var5) {
            char var6 = var0.charAt(var5);
            if (var6 == '\\') {
               var4 = !var4;
               var1[0] = -1;
               ++var3;
            } else if (var6 == '{') {
               if (!var4 && var0.charAt(var5 + 1) == '}') {
                  var1[var3] = var5;
                  ++var3;
                  ++var5;
               }

               var4 = false;
            } else {
               var4 = false;
            }
         }

         return var3;
      }
   }

   static int countArgumentPlaceholders3(char[] var0, int var1, int[] var2) {
      int var3 = 0;
      boolean var4 = false;

      for(int var5 = 0; var5 < var1 - 1; ++var5) {
         char var6 = var0[var5];
         if (var6 == '\\') {
            var4 = !var4;
         } else if (var6 == '{') {
            if (!var4 && var0[var5 + 1] == '}') {
               var2[var3] = var5;
               ++var3;
               ++var5;
            }

            var4 = false;
         } else {
            var4 = false;
         }
      }

      return var3;
   }

   static String format(String var0, Object[] var1) {
      StringBuilder var2 = new StringBuilder();
      int var3 = var1 == null ? 0 : var1.length;
      formatMessage(var2, var0, var1, var3);
      return var2.toString();
   }

   static void formatMessage2(StringBuilder var0, String var1, Object[] var2, int var3, int[] var4) {
      if (var1 != null && var2 != null && var3 != 0) {
         int var5 = 0;

         for(int var6 = 0; var6 < var3; ++var6) {
            var0.append(var1, var5, var4[var6]);
            var5 = var4[var6] + 2;
            recursiveDeepToString(var2[var6], var0, (Set)null);
         }

         var0.append(var1, var5, var1.length());
      } else {
         var0.append(var1);
      }
   }

   static void formatMessage3(StringBuilder var0, char[] var1, int var2, Object[] var3, int var4, int[] var5) {
      if (var1 != null) {
         if (var3 != null && var4 != 0) {
            int var6 = 0;

            for(int var7 = 0; var7 < var4; ++var7) {
               var0.append(var1, var6, var5[var7]);
               var6 = var5[var7] + 2;
               recursiveDeepToString(var3[var7], var0, (Set)null);
            }

            var0.append(var1, var6, var2);
         } else {
            var0.append(var1);
         }
      }
   }

   static void formatMessage(StringBuilder var0, String var1, Object[] var2, int var3) {
      if (var1 != null && var2 != null && var3 != 0) {
         int var4 = 0;
         int var5 = 0;
         int var6 = 0;

         int var7;
         for(var7 = var1.length(); var6 < var7 - 1; ++var6) {
            char var8 = var1.charAt(var6);
            if (var8 == '\\') {
               ++var4;
            } else {
               if (isDelimPair(var8, var1, var6)) {
                  ++var6;
                  writeEscapedEscapeChars(var4, var0);
                  if (isOdd(var4)) {
                     writeDelimPair(var0);
                  } else {
                     writeArgOrDelimPair(var2, var3, var5, var0);
                     ++var5;
                  }
               } else {
                  handleLiteralChar(var0, var4, var8);
               }

               var4 = 0;
            }
         }

         handleRemainingCharIfAny(var1, var7, var0, var4, var6);
      } else {
         var0.append(var1);
      }
   }

   private static boolean isDelimPair(char var0, String var1, int var2) {
      return var0 == '{' && var1.charAt(var2 + 1) == '}';
   }

   private static void handleRemainingCharIfAny(String var0, int var1, StringBuilder var2, int var3, int var4) {
      if (var4 == var1 - 1) {
         char var5 = var0.charAt(var4);
         handleLastChar(var2, var3, var5);
      }

   }

   private static void handleLastChar(StringBuilder var0, int var1, char var2) {
      if (var2 == '\\') {
         writeUnescapedEscapeChars(var1 + 1, var0);
      } else {
         handleLiteralChar(var0, var1, var2);
      }

   }

   private static void handleLiteralChar(StringBuilder var0, int var1, char var2) {
      writeUnescapedEscapeChars(var1, var0);
      var0.append(var2);
   }

   private static void writeDelimPair(StringBuilder var0) {
      var0.append('{');
      var0.append('}');
   }

   private static boolean isOdd(int var0) {
      return (var0 & 1) == 1;
   }

   private static void writeEscapedEscapeChars(int var0, StringBuilder var1) {
      int var2 = var0 >> 1;
      writeUnescapedEscapeChars(var2, var1);
   }

   private static void writeUnescapedEscapeChars(int var0, StringBuilder var1) {
      while(var0 > 0) {
         var1.append('\\');
         --var0;
      }

   }

   private static void writeArgOrDelimPair(Object[] var0, int var1, int var2, StringBuilder var3) {
      if (var2 < var1) {
         recursiveDeepToString(var0[var2], var3, (Set)null);
      } else {
         writeDelimPair(var3);
      }

   }

   static String deepToString(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof String) {
         return (String)var0;
      } else {
         StringBuilder var1 = new StringBuilder();
         HashSet var2 = new HashSet();
         recursiveDeepToString(var0, var1, var2);
         return var1.toString();
      }
   }

   private static void recursiveDeepToString(Object var0, StringBuilder var1, Set<String> var2) {
      if (!appendSpecialTypes(var0, var1)) {
         if (isMaybeRecursive(var0)) {
            appendPotentiallyRecursiveValue(var0, var1, var2);
         } else {
            tryObjectToString(var0, var1);
         }

      }
   }

   private static boolean appendSpecialTypes(Object var0, StringBuilder var1) {
      if (var0 != null && !(var0 instanceof String)) {
         if (var0 instanceof CharSequence) {
            var1.append((CharSequence)var0);
            return true;
         } else if (var0 instanceof StringBuilderFormattable) {
            ((StringBuilderFormattable)var0).formatTo(var1);
            return true;
         } else if (var0 instanceof Integer) {
            var1.append((Integer)var0);
            return true;
         } else if (var0 instanceof Long) {
            var1.append((Long)var0);
            return true;
         } else if (var0 instanceof Double) {
            var1.append((Double)var0);
            return true;
         } else if (var0 instanceof Boolean) {
            var1.append((Boolean)var0);
            return true;
         } else if (var0 instanceof Character) {
            var1.append((Character)var0);
            return true;
         } else if (var0 instanceof Short) {
            var1.append((Short)var0);
            return true;
         } else if (var0 instanceof Float) {
            var1.append((Float)var0);
            return true;
         } else {
            return appendDate(var0, var1);
         }
      } else {
         var1.append((String)var0);
         return true;
      }
   }

   private static boolean appendDate(Object var0, StringBuilder var1) {
      if (!(var0 instanceof Date)) {
         return false;
      } else {
         Date var2 = (Date)var0;
         SimpleDateFormat var3 = getSimpleDateFormat();
         var1.append(var3.format(var2));
         return true;
      }
   }

   private static SimpleDateFormat getSimpleDateFormat() {
      SimpleDateFormat var0 = (SimpleDateFormat)threadLocalSimpleDateFormat.get();
      if (var0 == null) {
         var0 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
         threadLocalSimpleDateFormat.set(var0);
      }

      return var0;
   }

   private static boolean isMaybeRecursive(Object var0) {
      return var0.getClass().isArray() || var0 instanceof Map || var0 instanceof Collection;
   }

   private static void appendPotentiallyRecursiveValue(Object var0, StringBuilder var1, Set<String> var2) {
      Class var3 = var0.getClass();
      if (var3.isArray()) {
         appendArray(var0, var1, var2, var3);
      } else if (var0 instanceof Map) {
         appendMap(var0, var1, var2);
      } else if (var0 instanceof Collection) {
         appendCollection(var0, var1, var2);
      }

   }

   private static void appendArray(Object var0, StringBuilder var1, Set<String> var2, Class<?> var3) {
      if (var3 == byte[].class) {
         var1.append(Arrays.toString((byte[])((byte[])var0)));
      } else if (var3 == short[].class) {
         var1.append(Arrays.toString((short[])((short[])var0)));
      } else if (var3 == int[].class) {
         var1.append(Arrays.toString((int[])((int[])var0)));
      } else if (var3 == long[].class) {
         var1.append(Arrays.toString((long[])((long[])var0)));
      } else if (var3 == float[].class) {
         var1.append(Arrays.toString((float[])((float[])var0)));
      } else if (var3 == double[].class) {
         var1.append(Arrays.toString((double[])((double[])var0)));
      } else if (var3 == boolean[].class) {
         var1.append(Arrays.toString((boolean[])((boolean[])var0)));
      } else if (var3 == char[].class) {
         var1.append(Arrays.toString((char[])((char[])var0)));
      } else {
         if (var2 == null) {
            var2 = new HashSet();
         }

         String var4 = identityToString(var0);
         if (((Set)var2).contains(var4)) {
            var1.append("[...").append(var4).append("...]");
         } else {
            ((Set)var2).add(var4);
            Object[] var5 = (Object[])((Object[])var0);
            var1.append('[');
            boolean var6 = true;
            Object[] var7 = var5;
            int var8 = var5.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Object var10 = var7[var9];
               if (var6) {
                  var6 = false;
               } else {
                  var1.append(", ");
               }

               recursiveDeepToString(var10, var1, new HashSet((Collection)var2));
            }

            var1.append(']');
         }
      }

   }

   private static void appendMap(Object var0, StringBuilder var1, Set<String> var2) {
      if (var2 == null) {
         var2 = new HashSet();
      }

      String var3 = identityToString(var0);
      if (((Set)var2).contains(var3)) {
         var1.append("[...").append(var3).append("...]");
      } else {
         ((Set)var2).add(var3);
         Map var4 = (Map)var0;
         var1.append('{');
         boolean var5 = true;
         Iterator var6 = var4.entrySet().iterator();

         while(var6.hasNext()) {
            Object var7 = var6.next();
            Entry var8 = (Entry)var7;
            if (var5) {
               var5 = false;
            } else {
               var1.append(", ");
            }

            Object var9 = var8.getKey();
            Object var10 = var8.getValue();
            recursiveDeepToString(var9, var1, new HashSet((Collection)var2));
            var1.append('=');
            recursiveDeepToString(var10, var1, new HashSet((Collection)var2));
         }

         var1.append('}');
      }

   }

   private static void appendCollection(Object var0, StringBuilder var1, Set<String> var2) {
      if (var2 == null) {
         var2 = new HashSet();
      }

      String var3 = identityToString(var0);
      if (((Set)var2).contains(var3)) {
         var1.append("[...").append(var3).append("...]");
      } else {
         ((Set)var2).add(var3);
         Collection var4 = (Collection)var0;
         var1.append('[');
         boolean var5 = true;

         Object var7;
         for(Iterator var6 = var4.iterator(); var6.hasNext(); recursiveDeepToString(var7, var1, new HashSet((Collection)var2))) {
            var7 = var6.next();
            if (var5) {
               var5 = false;
            } else {
               var1.append(", ");
            }
         }

         var1.append(']');
      }

   }

   private static void tryObjectToString(Object var0, StringBuilder var1) {
      try {
         var1.append(var0.toString());
      } catch (Throwable var3) {
         handleErrorInObjectToString(var0, var1, var3);
      }

   }

   private static void handleErrorInObjectToString(Object var0, StringBuilder var1, Throwable var2) {
      var1.append("[!!!");
      var1.append(identityToString(var0));
      var1.append("=>");
      String var3 = var2.getMessage();
      String var4 = var2.getClass().getName();
      var1.append(var4);
      if (!var4.equals(var3)) {
         var1.append(":");
         var1.append(var3);
      }

      var1.append("!!!]");
   }

   static String identityToString(Object var0) {
      return var0 == null ? null : var0.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(var0));
   }
}
