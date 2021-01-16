package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Objects;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Integers;

public abstract class AbstractAppender extends AbstractFilterable implements Appender {
   private final String name;
   private final boolean ignoreExceptions;
   private final Layout<? extends Serializable> layout;
   private ErrorHandler handler;

   protected AbstractAppender(String var1, Filter var2, Layout<? extends Serializable> var3) {
      this(var1, var2, var3, true);
   }

   protected AbstractAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4) {
      super(var2);
      this.handler = new DefaultErrorHandler(this);
      this.name = (String)Objects.requireNonNull(var1, "name");
      this.layout = var3;
      this.ignoreExceptions = var4;
   }

   public static int parseInt(String var0, int var1) {
      try {
         return Integers.parseInt(var0, var1);
      } catch (NumberFormatException var3) {
         LOGGER.error((String)"Could not parse \"{}\" as an integer,  using default value {}: {}", (Object)var0, var1, var3);
         return var1;
      }
   }

   public void error(String var1) {
      this.handler.error(var1);
   }

   public void error(String var1, LogEvent var2, Throwable var3) {
      this.handler.error(var1, var2, var3);
   }

   public void error(String var1, Throwable var2) {
      this.handler.error(var1, var2);
   }

   public ErrorHandler getHandler() {
      return this.handler;
   }

   public Layout<? extends Serializable> getLayout() {
      return this.layout;
   }

   public String getName() {
      return this.name;
   }

   public boolean ignoreExceptions() {
      return this.ignoreExceptions;
   }

   public void setHandler(ErrorHandler var1) {
      if (var1 == null) {
         LOGGER.error("The handler cannot be set to null");
      }

      if (this.isStarted()) {
         LOGGER.error("The handler cannot be changed once the appender is started");
      } else {
         this.handler = var1;
      }
   }

   public String toString() {
      return this.name;
   }

   public abstract static class Builder<B extends AbstractAppender.Builder<B>> extends AbstractFilterable.Builder<B> {
      @PluginBuilderAttribute
      private boolean ignoreExceptions = true;
      @PluginElement("Layout")
      private Layout<? extends Serializable> layout;
      @PluginBuilderAttribute
      @Required(
         message = "No appender name provided"
      )
      private String name;
      @PluginConfiguration
      private Configuration configuration;

      public Builder() {
         super();
      }

      public String getName() {
         return this.name;
      }

      public boolean isIgnoreExceptions() {
         return this.ignoreExceptions;
      }

      public Layout<? extends Serializable> getLayout() {
         return this.layout;
      }

      public B withName(String var1) {
         this.name = var1;
         return (AbstractAppender.Builder)this.asBuilder();
      }

      public B withIgnoreExceptions(boolean var1) {
         this.ignoreExceptions = var1;
         return (AbstractAppender.Builder)this.asBuilder();
      }

      public B withLayout(Layout<? extends Serializable> var1) {
         this.layout = var1;
         return (AbstractAppender.Builder)this.asBuilder();
      }

      public Layout<? extends Serializable> getOrCreateLayout() {
         return (Layout)(this.layout == null ? PatternLayout.createDefaultLayout() : this.layout);
      }

      public Layout<? extends Serializable> getOrCreateLayout(Charset var1) {
         return (Layout)(this.layout == null ? PatternLayout.newBuilder().withCharset(var1).build() : this.layout);
      }

      /** @deprecated */
      @Deprecated
      public B withConfiguration(Configuration var1) {
         this.configuration = var1;
         return (AbstractAppender.Builder)this.asBuilder();
      }

      public B setConfiguration(Configuration var1) {
         this.configuration = var1;
         return (AbstractAppender.Builder)this.asBuilder();
      }

      public Configuration getConfiguration() {
         return this.configuration;
      }
   }
}
