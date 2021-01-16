package org.apache.logging.log4j.core.pattern;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PerformanceSensitive;

public abstract class AbstractStyleNameConverter extends LogEventPatternConverter {
   private final List<PatternFormatter> formatters;
   private final String style;

   protected AbstractStyleNameConverter(String var1, List<PatternFormatter> var2, String var3) {
      super(var1, "style");
      this.formatters = var2;
      this.style = var3;
   }

   protected static <T extends AbstractStyleNameConverter> T newInstance(Class<T> var0, String var1, Configuration var2, String[] var3) {
      List var4 = toPatternFormatterList(var2, var3);
      if (var4 == null) {
         return null;
      } else {
         try {
            Constructor var5 = var0.getConstructor(List.class, String.class);
            return (AbstractStyleNameConverter)var5.newInstance(var4, AnsiEscape.createSequence(var1));
         } catch (SecurityException var6) {
            LOGGER.error((String)var6.toString(), (Throwable)var6);
         } catch (NoSuchMethodException var7) {
            LOGGER.error((String)var7.toString(), (Throwable)var7);
         } catch (IllegalArgumentException var8) {
            LOGGER.error((String)var8.toString(), (Throwable)var8);
         } catch (InstantiationException var9) {
            LOGGER.error((String)var9.toString(), (Throwable)var9);
         } catch (IllegalAccessException var10) {
            LOGGER.error((String)var10.toString(), (Throwable)var10);
         } catch (InvocationTargetException var11) {
            LOGGER.error((String)var11.toString(), (Throwable)var11);
         }

         return null;
      }
   }

   private static List<PatternFormatter> toPatternFormatterList(Configuration var0, String[] var1) {
      if (var1.length != 0 && var1[0] != null) {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         if (var2 == null) {
            LOGGER.error("No PatternParser created for config=" + var0 + ", options=" + Arrays.toString(var1));
            return null;
         } else {
            return var2.parse(var1[0]);
         }
      } else {
         LOGGER.error("No pattern supplied on style for config=" + var0);
         return null;
      }
   }

   @PerformanceSensitive({"allocation"})
   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = var2.length();

      for(int var4 = 0; var4 < this.formatters.size(); ++var4) {
         PatternFormatter var5 = (PatternFormatter)this.formatters.get(var4);
         var5.format(var1, var2);
      }

      if (var2.length() > var3) {
         var2.insert(var3, this.style);
         var2.append(AnsiEscape.getDefaultStyle());
      }

   }

   @Plugin(
      name = "yellow",
      category = "Converter"
   )
   @ConverterKeys({"yellow"})
   public static final class Yellow extends AbstractStyleNameConverter {
      protected static final String NAME = "yellow";

      public Yellow(List<PatternFormatter> var1, String var2) {
         super("yellow", var1, var2);
      }

      public static AbstractStyleNameConverter.Yellow newInstance(Configuration var0, String[] var1) {
         return (AbstractStyleNameConverter.Yellow)newInstance(AbstractStyleNameConverter.Yellow.class, "yellow", var0, var1);
      }
   }

   @Plugin(
      name = "white",
      category = "Converter"
   )
   @ConverterKeys({"white"})
   public static final class White extends AbstractStyleNameConverter {
      protected static final String NAME = "white";

      public White(List<PatternFormatter> var1, String var2) {
         super("white", var1, var2);
      }

      public static AbstractStyleNameConverter.White newInstance(Configuration var0, String[] var1) {
         return (AbstractStyleNameConverter.White)newInstance(AbstractStyleNameConverter.White.class, "white", var0, var1);
      }
   }

   @Plugin(
      name = "red",
      category = "Converter"
   )
   @ConverterKeys({"red"})
   public static final class Red extends AbstractStyleNameConverter {
      protected static final String NAME = "red";

      public Red(List<PatternFormatter> var1, String var2) {
         super("red", var1, var2);
      }

      public static AbstractStyleNameConverter.Red newInstance(Configuration var0, String[] var1) {
         return (AbstractStyleNameConverter.Red)newInstance(AbstractStyleNameConverter.Red.class, "red", var0, var1);
      }
   }

   @Plugin(
      name = "magenta",
      category = "Converter"
   )
   @ConverterKeys({"magenta"})
   public static final class Magenta extends AbstractStyleNameConverter {
      protected static final String NAME = "magenta";

      public Magenta(List<PatternFormatter> var1, String var2) {
         super("magenta", var1, var2);
      }

      public static AbstractStyleNameConverter.Magenta newInstance(Configuration var0, String[] var1) {
         return (AbstractStyleNameConverter.Magenta)newInstance(AbstractStyleNameConverter.Magenta.class, "magenta", var0, var1);
      }
   }

   @Plugin(
      name = "green",
      category = "Converter"
   )
   @ConverterKeys({"green"})
   public static final class Green extends AbstractStyleNameConverter {
      protected static final String NAME = "green";

      public Green(List<PatternFormatter> var1, String var2) {
         super("green", var1, var2);
      }

      public static AbstractStyleNameConverter.Green newInstance(Configuration var0, String[] var1) {
         return (AbstractStyleNameConverter.Green)newInstance(AbstractStyleNameConverter.Green.class, "green", var0, var1);
      }
   }

   @Plugin(
      name = "cyan",
      category = "Converter"
   )
   @ConverterKeys({"cyan"})
   public static final class Cyan extends AbstractStyleNameConverter {
      protected static final String NAME = "cyan";

      public Cyan(List<PatternFormatter> var1, String var2) {
         super("cyan", var1, var2);
      }

      public static AbstractStyleNameConverter.Cyan newInstance(Configuration var0, String[] var1) {
         return (AbstractStyleNameConverter.Cyan)newInstance(AbstractStyleNameConverter.Cyan.class, "cyan", var0, var1);
      }
   }

   @Plugin(
      name = "blue",
      category = "Converter"
   )
   @ConverterKeys({"blue"})
   public static final class Blue extends AbstractStyleNameConverter {
      protected static final String NAME = "blue";

      public Blue(List<PatternFormatter> var1, String var2) {
         super("blue", var1, var2);
      }

      public static AbstractStyleNameConverter.Blue newInstance(Configuration var0, String[] var1) {
         return (AbstractStyleNameConverter.Blue)newInstance(AbstractStyleNameConverter.Blue.class, "blue", var0, var1);
      }
   }

   @Plugin(
      name = "black",
      category = "Converter"
   )
   @ConverterKeys({"black"})
   public static final class Black extends AbstractStyleNameConverter {
      protected static final String NAME = "black";

      public Black(List<PatternFormatter> var1, String var2) {
         super("black", var1, var2);
      }

      public static AbstractStyleNameConverter.Black newInstance(Configuration var0, String[] var1) {
         return (AbstractStyleNameConverter.Black)newInstance(AbstractStyleNameConverter.Black.class, "black", var0, var1);
      }
   }
}
