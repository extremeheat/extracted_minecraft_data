package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingRandomAccessFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "RollingRandomAccessFile",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RollingRandomAccessFileAppender extends AbstractOutputStreamAppender<RollingRandomAccessFileManager> {
   private final String fileName;
   private final String filePattern;
   private final Object advertisement;
   private final Advertiser advertiser;

   private RollingRandomAccessFileAppender(String var1, Layout<? extends Serializable> var2, Filter var3, RollingRandomAccessFileManager var4, String var5, String var6, boolean var7, boolean var8, int var9, Advertiser var10) {
      super(var1, var2, var3, var7, var8, var4);
      if (var10 != null) {
         HashMap var11 = new HashMap(var2.getContentFormat());
         var11.put("contentType", var2.getContentType());
         var11.put("name", var1);
         this.advertisement = var10.advertise(var11);
      } else {
         this.advertisement = null;
      }

      this.fileName = var5;
      this.filePattern = var6;
      this.advertiser = var10;
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      if (this.advertiser != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

      this.setStopped();
      return true;
   }

   public void append(LogEvent var1) {
      RollingRandomAccessFileManager var2 = (RollingRandomAccessFileManager)this.getManager();
      var2.checkRollover(var1);
      var2.setEndOfBatch(var1.isEndOfBatch());
      super.append(var1);
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getFilePattern() {
      return this.filePattern;
   }

   public int getBufferSize() {
      return ((RollingRandomAccessFileManager)this.getManager()).getBufferSize();
   }

   /** @deprecated */
   @Deprecated
   public static <B extends RollingRandomAccessFileAppender.Builder<B>> RollingRandomAccessFileAppender createAppender(String var0, String var1, String var2, String var3, String var4, String var5, TriggeringPolicy var6, RolloverStrategy var7, Layout<? extends Serializable> var8, Filter var9, String var10, String var11, String var12, Configuration var13) {
      boolean var14 = Booleans.parseBoolean(var2, true);
      boolean var15 = Booleans.parseBoolean(var10, true);
      boolean var16 = Booleans.parseBoolean(var4, true);
      boolean var17 = Boolean.parseBoolean(var11);
      int var18 = Integers.parseInt(var5, 262144);
      return ((RollingRandomAccessFileAppender.Builder)((RollingRandomAccessFileAppender.Builder)((RollingRandomAccessFileAppender.Builder)((RollingRandomAccessFileAppender.Builder)((RollingRandomAccessFileAppender.Builder)((RollingRandomAccessFileAppender.Builder)((RollingRandomAccessFileAppender.Builder)newBuilder().withAdvertise(var17).withAdvertiseURI(var12).withAppend(var14).withBufferSize(var18)).setConfiguration(var13)).withFileName(var0).withFilePattern(var1).withFilter(var9)).withIgnoreExceptions(var15)).withImmediateFlush(var16)).withLayout(var8)).withName(var3)).withPolicy(var6).withStrategy(var7).build();
   }

   @PluginBuilderFactory
   public static <B extends RollingRandomAccessFileAppender.Builder<B>> B newBuilder() {
      return (RollingRandomAccessFileAppender.Builder)(new RollingRandomAccessFileAppender.Builder()).asBuilder();
   }

   // $FF: synthetic method
   RollingRandomAccessFileAppender(String var1, Layout var2, Filter var3, RollingRandomAccessFileManager var4, String var5, String var6, boolean var7, boolean var8, int var9, Advertiser var10, Object var11) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static class Builder<B extends RollingRandomAccessFileAppender.Builder<B>> extends AbstractOutputStreamAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<RollingRandomAccessFileAppender> {
      @PluginBuilderAttribute("fileName")
      private String fileName;
      @PluginBuilderAttribute("filePattern")
      private String filePattern;
      @PluginBuilderAttribute("append")
      private boolean append = true;
      @PluginElement("Policy")
      private TriggeringPolicy policy;
      @PluginElement("Strategy")
      private RolloverStrategy strategy;
      @PluginBuilderAttribute("advertise")
      private boolean advertise;
      @PluginBuilderAttribute("advertiseURI")
      private String advertiseURI;

      public Builder() {
         super();
         this.withBufferSize(262144);
         this.withIgnoreExceptions(true);
         this.withImmediateFlush(true);
      }

      public RollingRandomAccessFileAppender build() {
         String var1 = this.getName();
         if (var1 == null) {
            RollingRandomAccessFileAppender.LOGGER.error("No name provided for FileAppender");
            return null;
         } else if (this.fileName == null) {
            RollingRandomAccessFileAppender.LOGGER.error("No filename was provided for FileAppender with name " + var1);
            return null;
         } else if (this.filePattern == null) {
            RollingRandomAccessFileAppender.LOGGER.error("No filename pattern provided for FileAppender with name " + var1);
            return null;
         } else if (this.policy == null) {
            RollingRandomAccessFileAppender.LOGGER.error("A TriggeringPolicy must be provided");
            return null;
         } else {
            if (this.strategy == null) {
               this.strategy = DefaultRolloverStrategy.createStrategy((String)null, (String)null, (String)null, String.valueOf(-1), (Action[])null, true, this.getConfiguration());
            }

            Layout var2 = this.getOrCreateLayout();
            boolean var3 = this.isImmediateFlush();
            int var4 = this.getBufferSize();
            RollingRandomAccessFileManager var5 = RollingRandomAccessFileManager.getRollingRandomAccessFileManager(this.fileName, this.filePattern, this.append, var3, var4, this.policy, this.strategy, this.advertiseURI, var2, this.getConfiguration());
            if (var5 == null) {
               return null;
            } else {
               var5.initialize();
               return new RollingRandomAccessFileAppender(var1, var2, this.getFilter(), var5, this.fileName, this.filePattern, this.isIgnoreExceptions(), var3, var4, this.advertise ? this.getConfiguration().getAdvertiser() : null);
            }
         }
      }

      public B withFileName(String var1) {
         this.fileName = var1;
         return (RollingRandomAccessFileAppender.Builder)this.asBuilder();
      }

      public B withFilePattern(String var1) {
         this.filePattern = var1;
         return (RollingRandomAccessFileAppender.Builder)this.asBuilder();
      }

      public B withAppend(boolean var1) {
         this.append = var1;
         return (RollingRandomAccessFileAppender.Builder)this.asBuilder();
      }

      public B withPolicy(TriggeringPolicy var1) {
         this.policy = var1;
         return (RollingRandomAccessFileAppender.Builder)this.asBuilder();
      }

      public B withStrategy(RolloverStrategy var1) {
         this.strategy = var1;
         return (RollingRandomAccessFileAppender.Builder)this.asBuilder();
      }

      public B withAdvertise(boolean var1) {
         this.advertise = var1;
         return (RollingRandomAccessFileAppender.Builder)this.asBuilder();
      }

      public B withAdvertiseURI(String var1) {
         this.advertiseURI = var1;
         return (RollingRandomAccessFileAppender.Builder)this.asBuilder();
      }
   }
}
