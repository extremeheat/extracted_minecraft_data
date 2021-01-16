package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DirectFileRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DirectWriteRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "RollingFile",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RollingFileAppender extends AbstractOutputStreamAppender<RollingFileManager> {
   public static final String PLUGIN_NAME = "RollingFile";
   private static final int DEFAULT_BUFFER_SIZE = 8192;
   private final String fileName;
   private final String filePattern;
   private Object advertisement;
   private final Advertiser advertiser;

   private RollingFileAppender(String var1, Layout<? extends Serializable> var2, Filter var3, RollingFileManager var4, String var5, String var6, boolean var7, boolean var8, Advertiser var9) {
      super(var1, var2, var3, var7, var8, var4);
      if (var9 != null) {
         HashMap var10 = new HashMap(var2.getContentFormat());
         var10.put("contentType", var2.getContentType());
         var10.put("name", var1);
         this.advertisement = var9.advertise(var10);
      }

      this.fileName = var5;
      this.filePattern = var6;
      this.advertiser = var9;
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      if (this.advertiser != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

      this.setStopped();
      return var4;
   }

   public void append(LogEvent var1) {
      ((RollingFileManager)this.getManager()).checkRollover(var1);
      super.append(var1);
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getFilePattern() {
      return this.filePattern;
   }

   public <T extends TriggeringPolicy> T getTriggeringPolicy() {
      return ((RollingFileManager)this.getManager()).getTriggeringPolicy();
   }

   /** @deprecated */
   @Deprecated
   public static <B extends RollingFileAppender.Builder<B>> RollingFileAppender createAppender(String var0, String var1, String var2, String var3, String var4, String var5, String var6, TriggeringPolicy var7, RolloverStrategy var8, Layout<? extends Serializable> var9, Filter var10, String var11, String var12, String var13, Configuration var14) {
      int var15 = Integers.parseInt(var5, 8192);
      return ((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)newBuilder().withAdvertise(Boolean.parseBoolean(var12)).withAdvertiseUri(var13).withAppend(Booleans.parseBoolean(var2, true)).withBufferedIo(Booleans.parseBoolean(var4, true))).withBufferSize(var15)).setConfiguration(var14)).withFileName(var0).withFilePattern(var1).withFilter(var10)).withIgnoreExceptions(Booleans.parseBoolean(var11, true))).withImmediateFlush(Booleans.parseBoolean(var6, true))).withLayout(var9)).withCreateOnDemand(false).withLocking(false).withName(var3)).withPolicy(var7).withStrategy(var8).build();
   }

   @PluginBuilderFactory
   public static <B extends RollingFileAppender.Builder<B>> B newBuilder() {
      return (RollingFileAppender.Builder)(new RollingFileAppender.Builder()).asBuilder();
   }

   // $FF: synthetic method
   RollingFileAppender(String var1, Layout var2, Filter var3, RollingFileManager var4, String var5, String var6, boolean var7, boolean var8, Advertiser var9, Object var10) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static class Builder<B extends RollingFileAppender.Builder<B>> extends AbstractOutputStreamAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<RollingFileAppender> {
      @PluginBuilderAttribute
      private String fileName;
      @PluginBuilderAttribute
      @Required
      private String filePattern;
      @PluginBuilderAttribute
      private boolean append = true;
      @PluginBuilderAttribute
      private boolean locking;
      @PluginElement("Policy")
      @Required
      private TriggeringPolicy policy;
      @PluginElement("Strategy")
      private RolloverStrategy strategy;
      @PluginBuilderAttribute
      private boolean advertise;
      @PluginBuilderAttribute
      private String advertiseUri;
      @PluginBuilderAttribute
      private boolean createOnDemand;

      public Builder() {
         super();
      }

      public RollingFileAppender build() {
         boolean var1 = this.isBufferedIo();
         int var2 = this.getBufferSize();
         if (this.getName() == null) {
            RollingFileAppender.LOGGER.error((String)"RollingFileAppender '{}': No name provided.", (Object)this.getName());
            return null;
         } else {
            if (!var1 && var2 > 0) {
               RollingFileAppender.LOGGER.warn((String)"RollingFileAppender '{}': The bufferSize is set to {} but bufferedIO is not true", (Object)this.getName(), (Object)var2);
            }

            if (this.filePattern == null) {
               RollingFileAppender.LOGGER.error((String)"RollingFileAppender '{}': No file name pattern provided.", (Object)this.getName());
               return null;
            } else if (this.policy == null) {
               RollingFileAppender.LOGGER.error((String)"RollingFileAppender '{}': No TriggeringPolicy provided.", (Object)this.getName());
               return null;
            } else {
               if (this.strategy == null) {
                  if (this.fileName != null) {
                     this.strategy = DefaultRolloverStrategy.createStrategy((String)null, (String)null, (String)null, String.valueOf(-1), (Action[])null, true, this.getConfiguration());
                  } else {
                     this.strategy = DirectWriteRolloverStrategy.createStrategy((String)null, String.valueOf(-1), (Action[])null, true, this.getConfiguration());
                  }
               } else if (this.fileName == null && !(this.strategy instanceof DirectFileRolloverStrategy)) {
                  RollingFileAppender.LOGGER.error("RollingFileAppender '{}': When no file name is provided a DirectFilenameRolloverStrategy must be configured");
                  return null;
               }

               Layout var3 = this.getOrCreateLayout();
               RollingFileManager var4 = RollingFileManager.getFileManager(this.fileName, this.filePattern, this.append, var1, this.policy, this.strategy, this.advertiseUri, var3, var2, this.isImmediateFlush(), this.createOnDemand, this.getConfiguration());
               if (var4 == null) {
                  return null;
               } else {
                  var4.initialize();
                  return new RollingFileAppender(this.getName(), var3, this.getFilter(), var4, this.fileName, this.filePattern, this.isIgnoreExceptions(), this.isImmediateFlush(), this.advertise ? this.getConfiguration().getAdvertiser() : null);
               }
            }
         }
      }

      public String getAdvertiseUri() {
         return this.advertiseUri;
      }

      public String getFileName() {
         return this.fileName;
      }

      public boolean isAdvertise() {
         return this.advertise;
      }

      public boolean isAppend() {
         return this.append;
      }

      public boolean isCreateOnDemand() {
         return this.createOnDemand;
      }

      public boolean isLocking() {
         return this.locking;
      }

      public B withAdvertise(boolean var1) {
         this.advertise = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }

      public B withAdvertiseUri(String var1) {
         this.advertiseUri = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }

      public B withAppend(boolean var1) {
         this.append = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }

      public B withFileName(String var1) {
         this.fileName = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }

      public B withCreateOnDemand(boolean var1) {
         this.createOnDemand = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }

      public B withLocking(boolean var1) {
         this.locking = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }

      public String getFilePattern() {
         return this.filePattern;
      }

      public TriggeringPolicy getPolicy() {
         return this.policy;
      }

      public RolloverStrategy getStrategy() {
         return this.strategy;
      }

      public B withFilePattern(String var1) {
         this.filePattern = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }

      public B withPolicy(TriggeringPolicy var1) {
         this.policy = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }

      public B withStrategy(RolloverStrategy var1) {
         this.strategy = var1;
         return (RollingFileAppender.Builder)this.asBuilder();
      }
   }
}
