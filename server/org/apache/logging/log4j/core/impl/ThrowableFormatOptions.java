package org.apache.logging.log4j.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.core.pattern.JAnsiTextRenderer;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public final class ThrowableFormatOptions {
   private static final int DEFAULT_LINES = 2147483647;
   protected static final ThrowableFormatOptions DEFAULT = new ThrowableFormatOptions();
   private static final String FULL = "full";
   private static final String NONE = "none";
   private static final String SHORT = "short";
   private final TextRenderer textRenderer;
   private final int lines;
   private final String separator;
   private final List<String> ignorePackages;
   public static final String CLASS_NAME = "short.className";
   public static final String METHOD_NAME = "short.methodName";
   public static final String LINE_NUMBER = "short.lineNumber";
   public static final String FILE_NAME = "short.fileName";
   public static final String MESSAGE = "short.message";
   public static final String LOCALIZED_MESSAGE = "short.localizedMessage";

   protected ThrowableFormatOptions(int var1, String var2, List<String> var3, TextRenderer var4) {
      super();
      this.lines = var1;
      this.separator = var2 == null ? Strings.LINE_SEPARATOR : var2;
      this.ignorePackages = var3;
      this.textRenderer = (TextRenderer)(var4 == null ? PlainTextRenderer.getInstance() : var4);
   }

   protected ThrowableFormatOptions(List<String> var1) {
      this(2147483647, (String)null, var1, (TextRenderer)null);
   }

   protected ThrowableFormatOptions() {
      this(2147483647, (String)null, (List)null, (TextRenderer)null);
   }

   public int getLines() {
      return this.lines;
   }

   public String getSeparator() {
      return this.separator;
   }

   public TextRenderer getTextRenderer() {
      return this.textRenderer;
   }

   public List<String> getIgnorePackages() {
      return this.ignorePackages;
   }

   public boolean allLines() {
      return this.lines == 2147483647;
   }

   public boolean anyLines() {
      return this.lines > 0;
   }

   public int minLines(int var1) {
      return this.lines > var1 ? var1 : this.lines;
   }

   public boolean hasPackages() {
      return this.ignorePackages != null && !this.ignorePackages.isEmpty();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('{').append(this.allLines() ? "full" : (this.lines == 2 ? "short" : (this.anyLines() ? String.valueOf(this.lines) : "none"))).append('}');
      var1.append("{separator(").append(this.separator).append(")}");
      if (this.hasPackages()) {
         var1.append("{filters(");
         Iterator var2 = this.ignorePackages.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.append(var3).append(',');
         }

         var1.deleteCharAt(var1.length() - 1);
         var1.append(")}");
      }

      return var1.toString();
   }

   public static ThrowableFormatOptions newInstance(String[] var0) {
      if (var0 != null && var0.length != 0) {
         String var2;
         if (var0.length == 1 && Strings.isNotEmpty(var0[0])) {
            String[] var1 = var0[0].split(Patterns.COMMA_SEPARATOR, 2);
            var2 = var1[0].trim();
            Scanner var3 = new Scanner(var2);
            Throwable var4 = null;

            try {
               if (var1.length > 1 && (var2.equalsIgnoreCase("full") || var2.equalsIgnoreCase("short") || var2.equalsIgnoreCase("none") || var3.hasNextInt())) {
                  var0 = new String[]{var2, var1[1].trim()};
               }
            } catch (Throwable var21) {
               var4 = var21;
               throw var21;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var20) {
                        var4.addSuppressed(var20);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         }

         int var23 = DEFAULT.lines;
         var2 = DEFAULT.separator;
         Object var24 = DEFAULT.ignorePackages;
         Object var25 = DEFAULT.textRenderer;
         String[] var5 = var0;
         int var6 = var0.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            if (var8 != null) {
               String var9 = var8.trim();
               if (!var9.isEmpty()) {
                  if (var9.startsWith("separator(") && var9.endsWith(")")) {
                     var2 = var9.substring("separator(".length(), var9.length() - 1);
                  } else {
                     String var10;
                     if (var9.startsWith("filters(") && var9.endsWith(")")) {
                        var10 = var9.substring("filters(".length(), var9.length() - 1);
                        if (var10.length() > 0) {
                           String[] var11 = var10.split(Patterns.COMMA_SEPARATOR);
                           if (var11.length > 0) {
                              var24 = new ArrayList(var11.length);
                              String[] var12 = var11;
                              int var13 = var11.length;

                              for(int var14 = 0; var14 < var13; ++var14) {
                                 String var15 = var12[var14];
                                 var15 = var15.trim();
                                 if (var15.length() > 0) {
                                    ((List)var24).add(var15);
                                 }
                              }
                           }
                        }
                     } else if (var9.equalsIgnoreCase("none")) {
                        var23 = 0;
                     } else if (!var9.equalsIgnoreCase("short") && !var9.equalsIgnoreCase("short.className") && !var9.equalsIgnoreCase("short.methodName") && !var9.equalsIgnoreCase("short.lineNumber") && !var9.equalsIgnoreCase("short.fileName") && !var9.equalsIgnoreCase("short.message") && !var9.equalsIgnoreCase("short.localizedMessage")) {
                        if ((!var9.startsWith("ansi(") || !var9.endsWith(")")) && !var9.equals("ansi")) {
                           if (!var9.equalsIgnoreCase("full")) {
                              var23 = Integer.parseInt(var9);
                           }
                        } else if (Loader.isJansiAvailable()) {
                           var10 = var9.equals("ansi") ? "" : var9.substring("ansi(".length(), var9.length() - 1);
                           var25 = new JAnsiTextRenderer(new String[]{null, var10}, JAnsiTextRenderer.DefaultExceptionStyleMap);
                        } else {
                           StatusLogger.getLogger().warn("You requested ANSI exception rendering but JANSI is not on the classpath. Please see https://logging.apache.org/log4j/2.x/runtime-dependencies.html");
                        }
                     } else {
                        var23 = 2;
                     }
                  }
               }
            }
         }

         return new ThrowableFormatOptions(var23, var2, (List)var24, (TextRenderer)var25);
      } else {
         return DEFAULT;
      }
   }
}
