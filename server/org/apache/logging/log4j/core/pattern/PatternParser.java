package org.apache.logging.log4j.core.pattern;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.util.SystemNanoClock;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public final class PatternParser {
   static final String DISABLE_ANSI = "disableAnsi";
   static final String NO_CONSOLE_NO_ANSI = "noConsoleNoAnsi";
   private static final char ESCAPE_CHAR = '%';
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int BUF_SIZE = 32;
   private static final int DECIMAL = 10;
   private final Configuration config;
   private final Map<String, Class<PatternConverter>> converterRules;

   public PatternParser(String var1) {
      this((Configuration)null, var1, (Class)null, (Class)null);
   }

   public PatternParser(Configuration var1, String var2, Class<?> var3) {
      this(var1, var2, var3, (Class)null);
   }

   public PatternParser(Configuration var1, String var2, Class<?> var3, Class<?> var4) {
      super();
      this.config = var1;
      PluginManager var5 = new PluginManager(var2);
      var5.collectPlugins(var1 == null ? null : var1.getPluginPackages());
      Map var6 = var5.getPlugins();
      LinkedHashMap var7 = new LinkedHashMap();
      Iterator var8 = var6.values().iterator();

      while(var8.hasNext()) {
         PluginType var9 = (PluginType)var8.next();

         try {
            Class var10 = var9.getPluginClass();
            if (var4 == null || var4.isAssignableFrom(var10)) {
               ConverterKeys var11 = (ConverterKeys)var10.getAnnotation(ConverterKeys.class);
               if (var11 != null) {
                  String[] var12 = var11.value();
                  int var13 = var12.length;

                  for(int var14 = 0; var14 < var13; ++var14) {
                     String var15 = var12[var14];
                     if (var7.containsKey(var15)) {
                        LOGGER.warn((String)"Converter key '{}' is already mapped to '{}'. Sorry, Dave, I can't let you do that! Ignoring plugin [{}].", (Object)var15, var7.get(var15), var10);
                     } else {
                        var7.put(var15, var10);
                     }
                  }
               }
            }
         } catch (Exception var16) {
            LOGGER.error((String)("Error processing plugin " + var9.getElementName()), (Throwable)var16);
         }
      }

      this.converterRules = var7;
   }

   public List<PatternFormatter> parse(String var1) {
      return this.parse(var1, false, false, false);
   }

   public List<PatternFormatter> parse(String var1, boolean var2, boolean var3) {
      return this.parse(var1, var2, false, var3);
   }

   public List<PatternFormatter> parse(String var1, boolean var2, boolean var3, boolean var4) {
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();
      this.parse(var1, var6, var7, var3, var4, true);
      Iterator var8 = var7.iterator();
      boolean var9 = false;

      Object var12;
      FormattingInfo var13;
      for(Iterator var10 = var6.iterator(); var10.hasNext(); var5.add(new PatternFormatter((LogEventPatternConverter)var12, var13))) {
         PatternConverter var11 = (PatternConverter)var10.next();
         if (var11 instanceof NanoTimePatternConverter && this.config != null) {
            this.config.setNanoClock(new SystemNanoClock());
         }

         if (var11 instanceof LogEventPatternConverter) {
            var12 = (LogEventPatternConverter)var11;
            var9 |= ((LogEventPatternConverter)var12).handlesThrowable();
         } else {
            var12 = new LiteralPatternConverter(this.config, "", true);
         }

         if (var8.hasNext()) {
            var13 = (FormattingInfo)var8.next();
         } else {
            var13 = FormattingInfo.getDefault();
         }
      }

      if (var2 && !var9) {
         ExtendedThrowablePatternConverter var14 = ExtendedThrowablePatternConverter.newInstance((String[])null);
         var5.add(new PatternFormatter(var14, FormattingInfo.getDefault()));
      }

      return var5;
   }

   private static int extractConverter(char var0, String var1, int var2, StringBuilder var3, StringBuilder var4) {
      int var5 = var2;
      var3.setLength(0);
      if (!Character.isUnicodeIdentifierStart(var0)) {
         return var2;
      } else {
         var3.append(var0);

         while(var5 < var1.length() && Character.isUnicodeIdentifierPart(var1.charAt(var5))) {
            var3.append(var1.charAt(var5));
            var4.append(var1.charAt(var5));
            ++var5;
         }

         return var5;
      }
   }

   private static int extractOptions(String var0, int var1, List<String> var2) {
      int var3;
      int var5;
      for(var3 = var1; var3 < var0.length() && var0.charAt(var3) == '{'; var3 = var5 + 1) {
         int var4 = var3++;
         int var6 = 0;

         do {
            var5 = var0.indexOf(125, var3);
            if (var5 == -1) {
               break;
            }

            int var7 = var0.indexOf("{", var3);
            if (var7 != -1 && var7 < var5) {
               var3 = var5 + 1;
               ++var6;
            } else if (var6 > 0) {
               --var6;
            }
         } while(var6 > 0);

         if (var5 == -1) {
            break;
         }

         String var8 = var0.substring(var4 + 1, var5);
         var2.add(var8);
      }

      return var3;
   }

   public void parse(String var1, List<PatternConverter> var2, List<FormattingInfo> var3, boolean var4, boolean var5) {
      this.parse(var1, var2, var3, false, var4, var5);
   }

   public void parse(String var1, List<PatternConverter> var2, List<FormattingInfo> var3, boolean var4, boolean var5, boolean var6) {
      Objects.requireNonNull(var1, "pattern");
      StringBuilder var7 = new StringBuilder(32);
      int var8 = var1.length();
      PatternParser.ParserState var9 = PatternParser.ParserState.LITERAL_STATE;
      int var11 = 0;
      FormattingInfo var12 = FormattingInfo.getDefault();

      while(true) {
         while(true) {
            while(var11 < var8) {
               char var10 = var1.charAt(var11++);
               switch(var9) {
               case LITERAL_STATE:
                  if (var11 == var8) {
                     var7.append(var10);
                  } else if (var10 == '%') {
                     switch(var1.charAt(var11)) {
                     case '%':
                        var7.append(var10);
                        ++var11;
                        break;
                     default:
                        if (var7.length() != 0) {
                           var2.add(new LiteralPatternConverter(this.config, var7.toString(), var6));
                           var3.add(FormattingInfo.getDefault());
                        }

                        var7.setLength(0);
                        var7.append(var10);
                        var9 = PatternParser.ParserState.CONVERTER_STATE;
                        var12 = FormattingInfo.getDefault();
                     }
                  } else {
                     var7.append(var10);
                  }
                  break;
               case CONVERTER_STATE:
                  var7.append(var10);
                  switch(var10) {
                  case '-':
                     var12 = new FormattingInfo(true, var12.getMinLength(), var12.getMaxLength(), var12.isLeftTruncate());
                     continue;
                  case '.':
                     var9 = PatternParser.ParserState.DOT_STATE;
                     continue;
                  default:
                     if (var10 >= '0' && var10 <= '9') {
                        var12 = new FormattingInfo(var12.isLeftAligned(), var10 - 48, var12.getMaxLength(), var12.isLeftTruncate());
                        var9 = PatternParser.ParserState.MIN_STATE;
                        continue;
                     }

                     var11 = this.finalizeConverter(var10, var1, var11, var7, var12, this.converterRules, var2, var3, var4, var5, var6);
                     var9 = PatternParser.ParserState.LITERAL_STATE;
                     var12 = FormattingInfo.getDefault();
                     var7.setLength(0);
                     continue;
                  }
               case MIN_STATE:
                  var7.append(var10);
                  if (var10 >= '0' && var10 <= '9') {
                     var12 = new FormattingInfo(var12.isLeftAligned(), var12.getMinLength() * 10 + var10 - 48, var12.getMaxLength(), var12.isLeftTruncate());
                  } else if (var10 == '.') {
                     var9 = PatternParser.ParserState.DOT_STATE;
                  } else {
                     var11 = this.finalizeConverter(var10, var1, var11, var7, var12, this.converterRules, var2, var3, var4, var5, var6);
                     var9 = PatternParser.ParserState.LITERAL_STATE;
                     var12 = FormattingInfo.getDefault();
                     var7.setLength(0);
                  }
                  break;
               case DOT_STATE:
                  var7.append(var10);
                  switch(var10) {
                  case '-':
                     var12 = new FormattingInfo(var12.isLeftAligned(), var12.getMinLength(), var12.getMaxLength(), false);
                     continue;
                  default:
                     if (var10 >= '0' && var10 <= '9') {
                        var12 = new FormattingInfo(var12.isLeftAligned(), var12.getMinLength(), var10 - 48, var12.isLeftTruncate());
                        var9 = PatternParser.ParserState.MAX_STATE;
                        continue;
                     }

                     LOGGER.error("Error occurred in position " + var11 + ".\n Was expecting digit, instead got char \"" + var10 + "\".");
                     var9 = PatternParser.ParserState.LITERAL_STATE;
                     continue;
                  }
               case MAX_STATE:
                  var7.append(var10);
                  if (var10 >= '0' && var10 <= '9') {
                     var12 = new FormattingInfo(var12.isLeftAligned(), var12.getMinLength(), var12.getMaxLength() * 10 + var10 - 48, var12.isLeftTruncate());
                  } else {
                     var11 = this.finalizeConverter(var10, var1, var11, var7, var12, this.converterRules, var2, var3, var4, var5, var6);
                     var9 = PatternParser.ParserState.LITERAL_STATE;
                     var12 = FormattingInfo.getDefault();
                     var7.setLength(0);
                  }
               }
            }

            if (var7.length() != 0) {
               var2.add(new LiteralPatternConverter(this.config, var7.toString(), var6));
               var3.add(FormattingInfo.getDefault());
            }

            return;
         }
      }
   }

   private PatternConverter createConverter(String var1, StringBuilder var2, Map<String, Class<PatternConverter>> var3, List<String> var4, boolean var5, boolean var6) {
      String var7 = var1;
      Class var8 = null;
      if (var3 == null) {
         LOGGER.error("Null rules for [" + var1 + ']');
         return null;
      } else {
         for(int var9 = var1.length(); var9 > 0 && var8 == null; --var9) {
            var7 = var7.substring(0, var9);
            var8 = (Class)var3.get(var7);
         }

         if (var8 == null) {
            LOGGER.error("Unrecognized format specifier [" + var1 + ']');
            return null;
         } else {
            if (AnsiConverter.class.isAssignableFrom(var8)) {
               var4.add("disableAnsi=" + var5);
               var4.add("noConsoleNoAnsi=" + var6);
            }

            Method[] var21 = var8.getDeclaredMethods();
            Method var10 = null;
            Method[] var11 = var21;
            int var12 = var21.length;

            int var13;
            for(var13 = 0; var13 < var12; ++var13) {
               Method var14 = var11[var13];
               if (Modifier.isStatic(var14.getModifiers()) && var14.getDeclaringClass().equals(var8) && var14.getName().equals("newInstance")) {
                  if (var10 == null) {
                     var10 = var14;
                  } else if (var14.getReturnType().equals(var10.getReturnType())) {
                     LOGGER.error("Class " + var8 + " cannot contain multiple static newInstance methods");
                     return null;
                  }
               }
            }

            if (var10 == null) {
               LOGGER.error("Class " + var8 + " does not contain a static newInstance method");
               return null;
            } else {
               Class[] var22 = var10.getParameterTypes();
               Object[] var23 = var22.length > 0 ? new Object[var22.length] : null;
               if (var23 != null) {
                  var13 = 0;
                  boolean var24 = false;
                  Class[] var15 = var22;
                  int var16 = var22.length;

                  for(int var17 = 0; var17 < var16; ++var17) {
                     Class var18 = var15[var17];
                     if (var18.isArray() && var18.getName().equals("[Ljava.lang.String;")) {
                        String[] var19 = (String[])var4.toArray(new String[var4.size()]);
                        var23[var13] = var19;
                     } else if (var18.isAssignableFrom(Configuration.class)) {
                        var23[var13] = this.config;
                     } else {
                        LOGGER.error("Unknown parameter type " + var18.getName() + " for static newInstance method of " + var8.getName());
                        var24 = true;
                     }

                     ++var13;
                  }

                  if (var24) {
                     return null;
                  }
               }

               try {
                  Object var25 = var10.invoke((Object)null, var23);
                  if (var25 instanceof PatternConverter) {
                     var2.delete(0, var2.length() - (var1.length() - var7.length()));
                     return (PatternConverter)var25;
                  }

                  LOGGER.warn((String)"Class {} does not extend PatternConverter.", (Object)var8.getName());
               } catch (Exception var20) {
                  LOGGER.error((String)("Error creating converter for " + var1), (Throwable)var20);
               }

               return null;
            }
         }
      }
   }

   private int finalizeConverter(char var1, String var2, int var3, StringBuilder var4, FormattingInfo var5, Map<String, Class<PatternConverter>> var6, List<PatternConverter> var7, List<FormattingInfo> var8, boolean var9, boolean var10, boolean var11) {
      StringBuilder var13 = new StringBuilder();
      int var12 = extractConverter(var1, var2, var3, var13, var4);
      String var14 = var13.toString();
      ArrayList var15 = new ArrayList();
      var12 = extractOptions(var2, var12, var15);
      PatternConverter var16 = this.createConverter(var14, var4, var6, var15, var9, var10);
      if (var16 == null) {
         StringBuilder var17;
         if (Strings.isEmpty(var14)) {
            var17 = new StringBuilder("Empty conversion specifier starting at position ");
         } else {
            var17 = new StringBuilder("Unrecognized conversion specifier [");
            var17.append(var14);
            var17.append("] starting at position ");
         }

         var17.append(Integer.toString(var12));
         var17.append(" in conversion pattern.");
         LOGGER.error(var17.toString());
         var7.add(new LiteralPatternConverter(this.config, var4.toString(), var11));
         var8.add(FormattingInfo.getDefault());
      } else {
         var7.add(var16);
         var8.add(var5);
         if (var4.length() > 0) {
            var7.add(new LiteralPatternConverter(this.config, var4.toString(), var11));
            var8.add(FormattingInfo.getDefault());
         }
      }

      var4.setLength(0);
      return var12;
   }

   private static enum ParserState {
      LITERAL_STATE,
      CONVERTER_STATE,
      DOT_STATE,
      MIN_STATE,
      MAX_STATE;

      private ParserState() {
      }
   }
}
