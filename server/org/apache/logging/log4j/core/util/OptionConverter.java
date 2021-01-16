package org.apache.logging.log4j.core.util;

import java.util.Locale;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public final class OptionConverter {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String DELIM_START = "${";
   private static final char DELIM_STOP = '}';
   private static final int DELIM_START_LEN = 2;
   private static final int DELIM_STOP_LEN = 1;
   private static final int ONE_K = 1024;

   private OptionConverter() {
      super();
   }

   public static String[] concatenateArrays(String[] var0, String[] var1) {
      int var2 = var0.length + var1.length;
      String[] var3 = new String[var2];
      System.arraycopy(var0, 0, var3, 0, var0.length);
      System.arraycopy(var1, 0, var3, var0.length, var1.length);
      return var3;
   }

   public static String convertSpecialChars(String var0) {
      int var2 = var0.length();
      StringBuilder var3 = new StringBuilder(var2);

      char var1;
      for(int var4 = 0; var4 < var2; var3.append(var1)) {
         var1 = var0.charAt(var4++);
         if (var1 == '\\') {
            var1 = var0.charAt(var4++);
            switch(var1) {
            case '"':
               var1 = '"';
               break;
            case '\'':
               var1 = '\'';
               break;
            case '\\':
               var1 = '\\';
               break;
            case 'b':
               var1 = '\b';
               break;
            case 'f':
               var1 = '\f';
               break;
            case 'n':
               var1 = '\n';
               break;
            case 'r':
               var1 = '\r';
               break;
            case 't':
               var1 = '\t';
            }
         }
      }

      return var3.toString();
   }

   public static Object instantiateByKey(Properties var0, String var1, Class<?> var2, Object var3) {
      String var4 = findAndSubst(var1, var0);
      if (var4 == null) {
         LOGGER.error((String)"Could not find value for key {}", (Object)var1);
         return var3;
      } else {
         return instantiateByClassName(var4.trim(), var2, var3);
      }
   }

   public static boolean toBoolean(String var0, boolean var1) {
      if (var0 == null) {
         return var1;
      } else {
         String var2 = var0.trim();
         if ("true".equalsIgnoreCase(var2)) {
            return true;
         } else {
            return "false".equalsIgnoreCase(var2) ? false : var1;
         }
      }
   }

   public static int toInt(String var0, int var1) {
      if (var0 != null) {
         String var2 = var0.trim();

         try {
            return Integer.parseInt(var2);
         } catch (NumberFormatException var4) {
            LOGGER.error((String)"[{}] is not in proper int form.", (Object)var2, (Object)var4);
         }
      }

      return var1;
   }

   public static long toFileSize(String var0, long var1) {
      if (var0 == null) {
         return var1;
      } else {
         String var3 = var0.trim().toUpperCase(Locale.ENGLISH);
         long var4 = 1L;
         int var6;
         if ((var6 = var3.indexOf("KB")) != -1) {
            var4 = 1024L;
            var3 = var3.substring(0, var6);
         } else if ((var6 = var3.indexOf("MB")) != -1) {
            var4 = 1048576L;
            var3 = var3.substring(0, var6);
         } else if ((var6 = var3.indexOf("GB")) != -1) {
            var4 = 1073741824L;
            var3 = var3.substring(0, var6);
         }

         try {
            return Long.parseLong(var3) * var4;
         } catch (NumberFormatException var8) {
            LOGGER.error((String)"[{}] is not in proper int form.", (Object)var3);
            LOGGER.error((String)"[{}] not in expected format.", (Object)var0, (Object)var8);
            return var1;
         }
      }
   }

   public static String findAndSubst(String var0, Properties var1) {
      String var2 = var1.getProperty(var0);
      if (var2 == null) {
         return null;
      } else {
         try {
            return substVars(var2, var1);
         } catch (IllegalArgumentException var4) {
            LOGGER.error((String)"Bad option value [{}].", (Object)var2, (Object)var4);
            return var2;
         }
      }
   }

   public static Object instantiateByClassName(String var0, Class<?> var1, Object var2) {
      if (var0 != null) {
         try {
            Class var3 = LoaderUtil.loadClass(var0);
            if (!var1.isAssignableFrom(var3)) {
               LOGGER.error((String)"A \"{}\" object is not assignable to a \"{}\" variable.", (Object)var0, (Object)var1.getName());
               LOGGER.error((String)"The class \"{}\" was loaded by [{}] whereas object of type [{}] was loaded by [{}].", (Object)var1.getName(), var1.getClassLoader(), var3.getName());
               return var2;
            }

            return var3.newInstance();
         } catch (Exception var4) {
            LOGGER.error((String)"Could not instantiate class [{}].", (Object)var0, (Object)var4);
         }
      }

      return var2;
   }

   public static String substVars(String var0, Properties var1) throws IllegalArgumentException {
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;

      while(true) {
         int var4 = var0.indexOf("${", var3);
         if (var4 == -1) {
            if (var3 == 0) {
               return var0;
            } else {
               var2.append(var0.substring(var3, var0.length()));
               return var2.toString();
            }
         }

         var2.append(var0.substring(var3, var4));
         int var5 = var0.indexOf(125, var4);
         if (var5 == -1) {
            throw new IllegalArgumentException(Strings.dquote(var0) + " has no closing brace. Opening brace at position " + var4 + '.');
         }

         var4 += 2;
         String var6 = var0.substring(var4, var5);
         String var7 = PropertiesUtil.getProperties().getStringProperty(var6, (String)null);
         if (var7 == null && var1 != null) {
            var7 = var1.getProperty(var6);
         }

         if (var7 != null) {
            String var8 = substVars(var7, var1);
            var2.append(var8);
         }

         var3 = var5 + 1;
      }
   }
}
