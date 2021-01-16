package org.apache.logging.log4j.core.pattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "highlight",
   category = "Converter"
)
@ConverterKeys({"highlight"})
@PerformanceSensitive({"allocation"})
public final class HighlightConverter extends LogEventPatternConverter implements AnsiConverter {
   private static final Map<Level, String> DEFAULT_STYLES = new HashMap();
   private static final Map<Level, String> LOGBACK_STYLES = new HashMap();
   private static final String STYLE_KEY = "STYLE";
   private static final String STYLE_KEY_DEFAULT = "DEFAULT";
   private static final String STYLE_KEY_LOGBACK = "LOGBACK";
   private static final Map<String, Map<Level, String>> STYLES = new HashMap();
   private final Map<Level, String> levelStyles;
   private final List<PatternFormatter> patternFormatters;
   private final boolean noAnsi;
   private final String defaultStyle;

   private static Map<Level, String> createLevelStyleMap(String[] var0) {
      if (var0.length < 2) {
         return DEFAULT_STYLES;
      } else {
         String var1 = var0[1].replaceAll("disableAnsi=(true|false)", "").replaceAll("noConsoleNoAnsi=(true|false)", "");
         Map var2 = AnsiEscape.createMap(var1, new String[]{"STYLE"});
         HashMap var3 = new HashMap(DEFAULT_STYLES);
         Iterator var4 = var2.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            String var6 = ((String)var5.getKey()).toUpperCase(Locale.ENGLISH);
            String var7 = (String)var5.getValue();
            if ("STYLE".equalsIgnoreCase(var6)) {
               Map var8 = (Map)STYLES.get(var7.toUpperCase(Locale.ENGLISH));
               if (var8 == null) {
                  LOGGER.error("Unknown level style: " + var7 + ". Use one of " + Arrays.toString(STYLES.keySet().toArray()));
               } else {
                  var3.putAll(var8);
               }
            } else {
               Level var9 = Level.toLevel(var6);
               if (var9 == null) {
                  LOGGER.error("Unknown level name: " + var6 + ". Use one of " + Arrays.toString(DEFAULT_STYLES.keySet().toArray()));
               } else {
                  var3.put(var9, var7);
               }
            }
         }

         return var3;
      }
   }

   public static HighlightConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length < 1) {
         LOGGER.error("Incorrect number of options on style. Expected at least 1, received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on style");
         return null;
      } else {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         List var3 = var2.parse(var1[0]);
         boolean var4 = Arrays.toString(var1).contains("disableAnsi=true");
         boolean var5 = Arrays.toString(var1).contains("noConsoleNoAnsi=true");
         boolean var6 = var4 || var5 && System.console() == null;
         return new HighlightConverter(var3, createLevelStyleMap(var1), var6);
      }
   }

   private HighlightConverter(List<PatternFormatter> var1, Map<Level, String> var2, boolean var3) {
      super("style", "style");
      this.patternFormatters = var1;
      this.levelStyles = var2;
      this.defaultStyle = AnsiEscape.getDefaultStyle();
      this.noAnsi = var3;
   }

   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = 0;
      int var4 = 0;
      if (!this.noAnsi) {
         var3 = var2.length();
         var2.append((String)this.levelStyles.get(var1.getLevel()));
         var4 = var2.length();
      }

      int var5 = 0;

      for(int var6 = this.patternFormatters.size(); var5 < var6; ++var5) {
         ((PatternFormatter)this.patternFormatters.get(var5)).format(var1, var2);
      }

      boolean var7 = var2.length() == var4;
      if (!this.noAnsi) {
         if (var7) {
            var2.setLength(var3);
         } else {
            var2.append(this.defaultStyle);
         }
      }

   }

   public boolean handlesThrowable() {
      Iterator var1 = this.patternFormatters.iterator();

      PatternFormatter var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (PatternFormatter)var1.next();
      } while(!var2.handlesThrowable());

      return true;
   }

   static {
      DEFAULT_STYLES.put(Level.FATAL, AnsiEscape.createSequence("BRIGHT", "RED"));
      DEFAULT_STYLES.put(Level.ERROR, AnsiEscape.createSequence("BRIGHT", "RED"));
      DEFAULT_STYLES.put(Level.WARN, AnsiEscape.createSequence("YELLOW"));
      DEFAULT_STYLES.put(Level.INFO, AnsiEscape.createSequence("GREEN"));
      DEFAULT_STYLES.put(Level.DEBUG, AnsiEscape.createSequence("CYAN"));
      DEFAULT_STYLES.put(Level.TRACE, AnsiEscape.createSequence("BLACK"));
      LOGBACK_STYLES.put(Level.FATAL, AnsiEscape.createSequence("BLINK", "BRIGHT", "RED"));
      LOGBACK_STYLES.put(Level.ERROR, AnsiEscape.createSequence("BRIGHT", "RED"));
      LOGBACK_STYLES.put(Level.WARN, AnsiEscape.createSequence("RED"));
      LOGBACK_STYLES.put(Level.INFO, AnsiEscape.createSequence("BLUE"));
      LOGBACK_STYLES.put(Level.DEBUG, AnsiEscape.createSequence((String[])null));
      LOGBACK_STYLES.put(Level.TRACE, AnsiEscape.createSequence((String[])null));
      STYLES.put("DEFAULT", DEFAULT_STYLES);
      STYLES.put("LOGBACK", LOGBACK_STYLES);
   }
}
