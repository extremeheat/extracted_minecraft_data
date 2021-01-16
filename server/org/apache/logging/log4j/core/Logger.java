package org.apache.logging.log4j.core;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.ReliabilityStrategy;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.Supplier;

public class Logger extends AbstractLogger implements Supplier<LoggerConfig> {
   private static final long serialVersionUID = 1L;
   protected volatile Logger.PrivateConfig privateConfig;
   private final LoggerContext context;

   protected Logger(LoggerContext var1, String var2, MessageFactory var3) {
      super(var2, var3);
      this.context = var1;
      this.privateConfig = new Logger.PrivateConfig(var1.getConfiguration(), this);
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new Logger.LoggerProxy(this.getName(), this.getMessageFactory());
   }

   public Logger getParent() {
      LoggerConfig var1 = this.privateConfig.loggerConfig.getName().equals(this.getName()) ? this.privateConfig.loggerConfig.getParent() : this.privateConfig.loggerConfig;
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.getName();
         MessageFactory var3 = this.getMessageFactory();
         return this.context.hasLogger(var2, var3) ? this.context.getLogger(var2, var3) : new Logger(this.context, var2, var3);
      }
   }

   public LoggerContext getContext() {
      return this.context;
   }

   public synchronized void setLevel(Level var1) {
      if (var1 != this.getLevel()) {
         Level var2;
         if (var1 != null) {
            var2 = var1;
         } else {
            Logger var3 = this.getParent();
            var2 = var3 != null ? var3.getLevel() : this.privateConfig.loggerConfigLevel;
         }

         this.privateConfig = new Logger.PrivateConfig(this.privateConfig, var2);
      }
   }

   public LoggerConfig get() {
      return this.privateConfig.loggerConfig;
   }

   public void logMessage(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      Object var6 = var4 == null ? new SimpleMessage("") : var4;
      ReliabilityStrategy var7 = this.privateConfig.loggerConfig.getReliabilityStrategy();
      var7.log(this, this.getName(), var1, var3, var2, (Message)var6, var5);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Throwable var4) {
      return this.privateConfig.filter(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3) {
      return this.privateConfig.filter(var1, var2, var3);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object... var4) {
      return this.privateConfig.filter(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4) {
      return this.privateConfig.filter(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5, var6);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5, var6, var7);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public boolean isEnabled(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return this.privateConfig.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   public boolean isEnabled(Level var1, Marker var2, CharSequence var3, Throwable var4) {
      return this.privateConfig.filter(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, Object var3, Throwable var4) {
      return this.privateConfig.filter(var1, var2, var3, var4);
   }

   public boolean isEnabled(Level var1, Marker var2, Message var3, Throwable var4) {
      return this.privateConfig.filter(var1, var2, var3, var4);
   }

   public void addAppender(Appender var1) {
      this.privateConfig.config.addLoggerAppender(this, var1);
   }

   public void removeAppender(Appender var1) {
      this.privateConfig.loggerConfig.removeAppender(var1.getName());
   }

   public Map<String, Appender> getAppenders() {
      return this.privateConfig.loggerConfig.getAppenders();
   }

   public Iterator<Filter> getFilters() {
      Filter var1 = this.privateConfig.loggerConfig.getFilter();
      if (var1 == null) {
         return (new ArrayList()).iterator();
      } else if (var1 instanceof CompositeFilter) {
         return ((CompositeFilter)var1).iterator();
      } else {
         ArrayList var2 = new ArrayList();
         var2.add(var1);
         return var2.iterator();
      }
   }

   public Level getLevel() {
      return this.privateConfig.loggerConfigLevel;
   }

   public int filterCount() {
      Filter var1 = this.privateConfig.loggerConfig.getFilter();
      if (var1 == null) {
         return 0;
      } else {
         return var1 instanceof CompositeFilter ? ((CompositeFilter)var1).size() : 1;
      }
   }

   public void addFilter(Filter var1) {
      this.privateConfig.config.addLoggerFilter(this, var1);
   }

   public boolean isAdditive() {
      return this.privateConfig.loggerConfig.isAdditive();
   }

   public void setAdditive(boolean var1) {
      this.privateConfig.config.setLoggerAdditive(this, var1);
   }

   protected void updateConfiguration(Configuration var1) {
      this.privateConfig = new Logger.PrivateConfig(var1, this);
   }

   public String toString() {
      String var1 = "" + this.getName() + ':' + this.getLevel();
      if (this.context == null) {
         return var1;
      } else {
         String var2 = this.context.getName();
         return var2 == null ? var1 : var1 + " in " + var2;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Logger var2 = (Logger)var1;
         return this.getName().equals(var2.getName());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   protected static class LoggerProxy implements Serializable {
      private static final long serialVersionUID = 1L;
      private final String name;
      private final MessageFactory messageFactory;

      public LoggerProxy(String var1, MessageFactory var2) {
         super();
         this.name = var1;
         this.messageFactory = var2;
      }

      protected Object readResolve() throws ObjectStreamException {
         return new Logger(LoggerContext.getContext(), this.name, this.messageFactory);
      }
   }

   protected class PrivateConfig {
      public final LoggerConfig loggerConfig;
      public final Configuration config;
      private final Level loggerConfigLevel;
      private final int intLevel;
      private final Logger logger;

      public PrivateConfig(Configuration var2, Logger var3) {
         super();
         this.config = var2;
         this.loggerConfig = var2.getLoggerConfig(Logger.this.getName());
         this.loggerConfigLevel = this.loggerConfig.getLevel();
         this.intLevel = this.loggerConfigLevel.intLevel();
         this.logger = var3;
      }

      public PrivateConfig(Logger.PrivateConfig var2, Level var3) {
         super();
         this.config = var2.config;
         this.loggerConfig = var2.loggerConfig;
         this.loggerConfigLevel = var3;
         this.intLevel = this.loggerConfigLevel.intLevel();
         this.logger = var2.logger;
      }

      public PrivateConfig(Logger.PrivateConfig var2, LoggerConfig var3) {
         super();
         this.config = var2.config;
         this.loggerConfig = var3;
         this.loggerConfigLevel = var3.getLevel();
         this.intLevel = this.loggerConfigLevel.intLevel();
         this.logger = var2.logger;
      }

      public void logEvent(LogEvent var1) {
         this.loggerConfig.log(var1);
      }

      boolean filter(Level var1, Marker var2, String var3) {
         Filter var4 = this.config.getFilter();
         if (var4 != null) {
            Filter.Result var5 = var4.filter(this.logger, var1, var2, var3);
            if (var5 != Filter.Result.NEUTRAL) {
               return var5 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Throwable var4) {
         Filter var5 = this.config.getFilter();
         if (var5 != null) {
            Filter.Result var6 = var5.filter(this.logger, var1, var2, (Object)var3, (Throwable)var4);
            if (var6 != Filter.Result.NEUTRAL) {
               return var6 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object... var4) {
         Filter var5 = this.config.getFilter();
         if (var5 != null) {
            Filter.Result var6 = var5.filter(this.logger, var1, var2, var3, var4);
            if (var6 != Filter.Result.NEUTRAL) {
               return var6 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4) {
         Filter var5 = this.config.getFilter();
         if (var5 != null) {
            Filter.Result var6 = var5.filter(this.logger, var1, var2, var3, var4);
            if (var6 != Filter.Result.NEUTRAL) {
               return var6 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5) {
         Filter var6 = this.config.getFilter();
         if (var6 != null) {
            Filter.Result var7 = var6.filter(this.logger, var1, var2, var3, var4, var5);
            if (var7 != Filter.Result.NEUTRAL) {
               return var7 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6) {
         Filter var7 = this.config.getFilter();
         if (var7 != null) {
            Filter.Result var8 = var7.filter(this.logger, var1, var2, var3, var4, var5, var6);
            if (var8 != Filter.Result.NEUTRAL) {
               return var8 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7) {
         Filter var8 = this.config.getFilter();
         if (var8 != null) {
            Filter.Result var9 = var8.filter(this.logger, var1, var2, var3, var4, var5, var6, var7);
            if (var9 != Filter.Result.NEUTRAL) {
               return var9 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
         Filter var9 = this.config.getFilter();
         if (var9 != null) {
            Filter.Result var10 = var9.filter(this.logger, var1, var2, var3, var4, var5, var6, var7, var8);
            if (var10 != Filter.Result.NEUTRAL) {
               return var10 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
         Filter var10 = this.config.getFilter();
         if (var10 != null) {
            Filter.Result var11 = var10.filter(this.logger, var1, var2, var3, var4, var5, var6, var7, var8, var9);
            if (var11 != Filter.Result.NEUTRAL) {
               return var11 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
         Filter var11 = this.config.getFilter();
         if (var11 != null) {
            Filter.Result var12 = var11.filter(this.logger, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
            if (var12 != Filter.Result.NEUTRAL) {
               return var12 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
         Filter var12 = this.config.getFilter();
         if (var12 != null) {
            Filter.Result var13 = var12.filter(this.logger, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
            if (var13 != Filter.Result.NEUTRAL) {
               return var13 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
         Filter var13 = this.config.getFilter();
         if (var13 != null) {
            Filter.Result var14 = var13.filter(this.logger, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
            if (var14 != Filter.Result.NEUTRAL) {
               return var14 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
         Filter var14 = this.config.getFilter();
         if (var14 != null) {
            Filter.Result var15 = var14.filter(this.logger, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
            if (var15 != Filter.Result.NEUTRAL) {
               return var15 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, CharSequence var3, Throwable var4) {
         Filter var5 = this.config.getFilter();
         if (var5 != null) {
            Filter.Result var6 = var5.filter(this.logger, var1, var2, (Object)var3, (Throwable)var4);
            if (var6 != Filter.Result.NEUTRAL) {
               return var6 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, Object var3, Throwable var4) {
         Filter var5 = this.config.getFilter();
         if (var5 != null) {
            Filter.Result var6 = var5.filter(this.logger, var1, var2, var3, var4);
            if (var6 != Filter.Result.NEUTRAL) {
               return var6 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      boolean filter(Level var1, Marker var2, Message var3, Throwable var4) {
         Filter var5 = this.config.getFilter();
         if (var5 != null) {
            Filter.Result var6 = var5.filter(this.logger, var1, var2, var3, var4);
            if (var6 != Filter.Result.NEUTRAL) {
               return var6 == Filter.Result.ACCEPT;
            }
         }

         return var1 != null && this.intLevel >= var1.intLevel();
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("PrivateConfig [loggerConfig=");
         var1.append(this.loggerConfig);
         var1.append(", config=");
         var1.append(this.config);
         var1.append(", loggerConfigLevel=");
         var1.append(this.loggerConfigLevel);
         var1.append(", intLevel=");
         var1.append(this.intLevel);
         var1.append(", logger=");
         var1.append(this.logger);
         var1.append("]");
         return var1.toString();
      }
   }
}
