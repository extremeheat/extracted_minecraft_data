package org.apache.logging.log4j.core.appender;

import java.io.OutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.CloseShieldOutputStream;

@Plugin(
   name = "OutputStream",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class OutputStreamAppender extends AbstractOutputStreamAppender<OutputStreamManager> {
   private static OutputStreamAppender.OutputStreamManagerFactory factory = new OutputStreamAppender.OutputStreamManagerFactory();

   @PluginFactory
   public static OutputStreamAppender createAppender(Layout<? extends Serializable> var0, Filter var1, OutputStream var2, String var3, boolean var4, boolean var5) {
      if (var3 == null) {
         LOGGER.error("No name provided for OutputStreamAppender");
         return null;
      } else {
         if (var0 == null) {
            var0 = PatternLayout.createDefaultLayout();
         }

         return new OutputStreamAppender(var3, (Layout)var0, var1, getManager(var2, var4, (Layout)var0), var5);
      }
   }

   private static OutputStreamManager getManager(OutputStream var0, boolean var1, Layout<? extends Serializable> var2) {
      CloseShieldOutputStream var3 = new CloseShieldOutputStream(var0);
      String var4 = var0.getClass().getName() + "@" + Integer.toHexString(var0.hashCode()) + '.' + var1;
      return OutputStreamManager.getManager(var4, new OutputStreamAppender.FactoryData(var3, var4, var2), factory);
   }

   @PluginBuilderFactory
   public static OutputStreamAppender.Builder newBuilder() {
      return new OutputStreamAppender.Builder();
   }

   private OutputStreamAppender(String var1, Layout<? extends Serializable> var2, Filter var3, OutputStreamManager var4, boolean var5) {
      super(var1, var2, var3, var5, true, var4);
   }

   // $FF: synthetic method
   OutputStreamAppender(String var1, Layout var2, Filter var3, OutputStreamManager var4, boolean var5, Object var6) {
      this(var1, var2, var3, var4, var5);
   }

   private static class OutputStreamManagerFactory implements ManagerFactory<OutputStreamManager, OutputStreamAppender.FactoryData> {
      private OutputStreamManagerFactory() {
         super();
      }

      public OutputStreamManager createManager(String var1, OutputStreamAppender.FactoryData var2) {
         return new OutputStreamManager(var2.os, var2.name, var2.layout, true);
      }

      // $FF: synthetic method
      OutputStreamManagerFactory(Object var1) {
         this();
      }
   }

   private static class FactoryData {
      private final Layout<? extends Serializable> layout;
      private final String name;
      private final OutputStream os;

      public FactoryData(OutputStream var1, String var2, Layout<? extends Serializable> var3) {
         super();
         this.os = var1;
         this.name = var2;
         this.layout = var3;
      }
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<OutputStreamAppender> {
      private Filter filter;
      private boolean follow = false;
      private boolean ignoreExceptions = true;
      private Layout<? extends Serializable> layout = PatternLayout.createDefaultLayout();
      private String name;
      private OutputStream target;

      public Builder() {
         super();
      }

      public OutputStreamAppender build() {
         return new OutputStreamAppender(this.name, this.layout, this.filter, OutputStreamAppender.getManager(this.target, this.follow, this.layout), this.ignoreExceptions);
      }

      public OutputStreamAppender.Builder setFilter(Filter var1) {
         this.filter = var1;
         return this;
      }

      public OutputStreamAppender.Builder setFollow(boolean var1) {
         this.follow = var1;
         return this;
      }

      public OutputStreamAppender.Builder setIgnoreExceptions(boolean var1) {
         this.ignoreExceptions = var1;
         return this;
      }

      public OutputStreamAppender.Builder setLayout(Layout<? extends Serializable> var1) {
         this.layout = var1;
         return this;
      }

      public OutputStreamAppender.Builder setName(String var1) {
         this.name = var1;
         return this;
      }

      public OutputStreamAppender.Builder setTarget(OutputStream var1) {
         this.target = var1;
         return this;
      }
   }
}
