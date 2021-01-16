package org.apache.logging.log4j.simple;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public class SimpleLogger extends AbstractLogger {
   private static final long serialVersionUID = 1L;
   private static final char SPACE = ' ';
   private final DateFormat dateFormatter;
   private Level level;
   private final boolean showDateTime;
   private final boolean showContextMap;
   private PrintStream stream;
   private final String logName;

   public SimpleLogger(String var1, Level var2, boolean var3, boolean var4, boolean var5, boolean var6, String var7, MessageFactory var8, PropertiesUtil var9, PrintStream var10) {
      super(var1, var8);
      String var11 = var9.getStringProperty("org.apache.logging.log4j.simplelog." + var1 + ".level");
      this.level = Level.toLevel(var11, var2);
      if (var4) {
         int var12 = var1.lastIndexOf(".");
         if (var12 > 0 && var12 < var1.length()) {
            this.logName = var1.substring(var12 + 1);
         } else {
            this.logName = var1;
         }
      } else if (var3) {
         this.logName = var1;
      } else {
         this.logName = null;
      }

      this.showDateTime = var5;
      this.showContextMap = var6;
      this.stream = var10;
      if (var5) {
         SimpleDateFormat var15;
         try {
            var15 = new SimpleDateFormat(var7);
         } catch (IllegalArgumentException var14) {
            var15 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");
         }

         this.dateFormatter = var15;
      } else {
         this.dateFormatter = null;
      }

   }

   public Level getLevel() {
      return this.level;
   }

   public boolean isEnabled(Level var1, Marker var2, Message var3, Throwable var4) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, CharSequence var3, Throwable var4) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, Object var3, Throwable var4) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object... var4) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Throwable var4) {
      return this.level.intLevel() >= var1.intLevel();
   }

   public void logMessage(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      StringBuilder var6 = new StringBuilder();
      if (this.showDateTime) {
         Date var7 = new Date();
         String var8;
         synchronized(this.dateFormatter) {
            var8 = this.dateFormatter.format(var7);
         }

         var6.append(var8);
         var6.append(' ');
      }

      var6.append(var2.toString());
      var6.append(' ');
      if (Strings.isNotEmpty(this.logName)) {
         var6.append(this.logName);
         var6.append(' ');
      }

      var6.append(var4.getFormattedMessage());
      if (this.showContextMap) {
         Map var12 = ThreadContext.getImmutableContext();
         if (var12.size() > 0) {
            var6.append(' ');
            var6.append(var12.toString());
            var6.append(' ');
         }
      }

      Object[] var13 = var4.getParameters();
      Throwable var14;
      if (var5 == null && var13 != null && var13.length > 0 && var13[var13.length - 1] instanceof Throwable) {
         var14 = (Throwable)var13[var13.length - 1];
      } else {
         var14 = var5;
      }

      this.stream.println(var6.toString());
      if (var14 != null) {
         this.stream.print(' ');
         var14.printStackTrace(this.stream);
      }

   }

   public void setLevel(Level var1) {
      if (var1 != null) {
         this.level = var1;
      }

   }

   public void setStream(PrintStream var1) {
      this.stream = var1;
   }
}
