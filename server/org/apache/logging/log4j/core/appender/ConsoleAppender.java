package org.apache.logging.log4j.core.appender;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.CloseShieldOutputStream;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

@Plugin(
   name = "Console",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class ConsoleAppender extends AbstractOutputStreamAppender<OutputStreamManager> {
   public static final String PLUGIN_NAME = "Console";
   private static final String JANSI_CLASS = "org.fusesource.jansi.WindowsAnsiOutputStream";
   private static ConsoleAppender.ConsoleManagerFactory factory = new ConsoleAppender.ConsoleManagerFactory();
   private static final ConsoleAppender.Target DEFAULT_TARGET;
   private static final AtomicInteger COUNT;
   private final ConsoleAppender.Target target;

   private ConsoleAppender(String var1, Layout<? extends Serializable> var2, Filter var3, OutputStreamManager var4, boolean var5, ConsoleAppender.Target var6) {
      super(var1, var2, var3, var5, true, var4);
      this.target = var6;
   }

   /** @deprecated */
   @Deprecated
   public static ConsoleAppender createAppender(Layout<? extends Serializable> var0, Filter var1, String var2, String var3, String var4, String var5) {
      if (var3 == null) {
         LOGGER.error("No name provided for ConsoleAppender");
         return null;
      } else {
         if (var0 == null) {
            var0 = PatternLayout.createDefaultLayout();
         }

         boolean var6 = Boolean.parseBoolean(var4);
         boolean var7 = Booleans.parseBoolean(var5, true);
         ConsoleAppender.Target var8 = var2 == null ? DEFAULT_TARGET : ConsoleAppender.Target.valueOf(var2);
         return new ConsoleAppender(var3, (Layout)var0, var1, getManager(var8, var6, false, (Layout)var0), var7, var8);
      }
   }

   /** @deprecated */
   @Deprecated
   public static ConsoleAppender createAppender(Layout<? extends Serializable> var0, Filter var1, ConsoleAppender.Target var2, String var3, boolean var4, boolean var5, boolean var6) {
      if (var3 == null) {
         LOGGER.error("No name provided for ConsoleAppender");
         return null;
      } else {
         if (var0 == null) {
            var0 = PatternLayout.createDefaultLayout();
         }

         var2 = var2 == null ? ConsoleAppender.Target.SYSTEM_OUT : var2;
         if (var4 && var5) {
            LOGGER.error("Cannot use both follow and direct on ConsoleAppender");
            return null;
         } else {
            return new ConsoleAppender(var3, (Layout)var0, var1, getManager(var2, var4, var5, (Layout)var0), var6, var2);
         }
      }
   }

   public static ConsoleAppender createDefaultAppenderForLayout(Layout<? extends Serializable> var0) {
      return new ConsoleAppender("DefaultConsole-" + COUNT.incrementAndGet(), var0, (Filter)null, getDefaultManager(DEFAULT_TARGET, false, false, var0), true, DEFAULT_TARGET);
   }

   @PluginBuilderFactory
   public static <B extends ConsoleAppender.Builder<B>> B newBuilder() {
      return (ConsoleAppender.Builder)(new ConsoleAppender.Builder()).asBuilder();
   }

   private static OutputStreamManager getDefaultManager(ConsoleAppender.Target var0, boolean var1, boolean var2, Layout<? extends Serializable> var3) {
      OutputStream var4 = getOutputStream(var1, var2, var0);
      String var5 = var0.name() + '.' + var1 + '.' + var2 + "-" + COUNT.get();
      return OutputStreamManager.getManager(var5, new ConsoleAppender.FactoryData(var4, var5, var3), factory);
   }

   private static OutputStreamManager getManager(ConsoleAppender.Target var0, boolean var1, boolean var2, Layout<? extends Serializable> var3) {
      OutputStream var4 = getOutputStream(var1, var2, var0);
      String var5 = var0.name() + '.' + var1 + '.' + var2;
      return OutputStreamManager.getManager(var5, new ConsoleAppender.FactoryData(var4, var5, var3), factory);
   }

   private static OutputStream getOutputStream(boolean var0, boolean var1, ConsoleAppender.Target var2) {
      String var3 = Charset.defaultCharset().name();

      CloseShieldOutputStream var12;
      try {
         Object var4 = var2 == ConsoleAppender.Target.SYSTEM_OUT ? (var1 ? new FileOutputStream(FileDescriptor.out) : (var0 ? new PrintStream(new ConsoleAppender.SystemOutStream(), true, var3) : System.out)) : (var1 ? new FileOutputStream(FileDescriptor.err) : (var0 ? new PrintStream(new ConsoleAppender.SystemErrStream(), true, var3) : System.err));
         var12 = new CloseShieldOutputStream((OutputStream)var4);
      } catch (UnsupportedEncodingException var11) {
         throw new IllegalStateException("Unsupported default encoding " + var3, var11);
      }

      PropertiesUtil var5 = PropertiesUtil.getProperties();
      if (var5.isOsWindows() && !var5.getBooleanProperty("log4j.skipJansi") && !var1) {
         try {
            Class var6 = LoaderUtil.loadClass("org.fusesource.jansi.WindowsAnsiOutputStream");
            Constructor var7 = var6.getConstructor(OutputStream.class);
            return new CloseShieldOutputStream((OutputStream)var7.newInstance(var12));
         } catch (ClassNotFoundException var8) {
            LOGGER.debug((String)"Jansi is not installed, cannot find {}", (Object)"org.fusesource.jansi.WindowsAnsiOutputStream");
         } catch (NoSuchMethodException var9) {
            LOGGER.warn((String)"{} is missing the proper constructor", (Object)"org.fusesource.jansi.WindowsAnsiOutputStream");
         } catch (Exception var10) {
            LOGGER.warn((String)"Unable to instantiate {}", (Object)"org.fusesource.jansi.WindowsAnsiOutputStream");
         }

         return var12;
      } else {
         return var12;
      }
   }

   public ConsoleAppender.Target getTarget() {
      return this.target;
   }

   // $FF: synthetic method
   ConsoleAppender(String var1, Layout var2, Filter var3, OutputStreamManager var4, boolean var5, ConsoleAppender.Target var6, Object var7) {
      this(var1, var2, var3, var4, var5, var6);
   }

   static {
      DEFAULT_TARGET = ConsoleAppender.Target.SYSTEM_OUT;
      COUNT = new AtomicInteger();
   }

   private static class ConsoleManagerFactory implements ManagerFactory<OutputStreamManager, ConsoleAppender.FactoryData> {
      private ConsoleManagerFactory() {
         super();
      }

      public OutputStreamManager createManager(String var1, ConsoleAppender.FactoryData var2) {
         return new OutputStreamManager(var2.os, var2.name, var2.layout, true);
      }

      // $FF: synthetic method
      ConsoleManagerFactory(Object var1) {
         this();
      }
   }

   private static class FactoryData {
      private final OutputStream os;
      private final String name;
      private final Layout<? extends Serializable> layout;

      public FactoryData(OutputStream var1, String var2, Layout<? extends Serializable> var3) {
         super();
         this.os = var1;
         this.name = var2;
         this.layout = var3;
      }
   }

   private static class SystemOutStream extends OutputStream {
      public SystemOutStream() {
         super();
      }

      public void close() {
      }

      public void flush() {
         System.out.flush();
      }

      public void write(byte[] var1) throws IOException {
         System.out.write(var1);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         System.out.write(var1, var2, var3);
      }

      public void write(int var1) throws IOException {
         System.out.write(var1);
      }
   }

   private static class SystemErrStream extends OutputStream {
      public SystemErrStream() {
         super();
      }

      public void close() {
      }

      public void flush() {
         System.err.flush();
      }

      public void write(byte[] var1) throws IOException {
         System.err.write(var1);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         System.err.write(var1, var2, var3);
      }

      public void write(int var1) {
         System.err.write(var1);
      }
   }

   public static class Builder<B extends ConsoleAppender.Builder<B>> extends AbstractOutputStreamAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<ConsoleAppender> {
      @PluginBuilderAttribute
      @Required
      private ConsoleAppender.Target target;
      @PluginBuilderAttribute
      private boolean follow;
      @PluginBuilderAttribute
      private boolean direct;

      public Builder() {
         super();
         this.target = ConsoleAppender.DEFAULT_TARGET;
      }

      public B setTarget(ConsoleAppender.Target var1) {
         this.target = var1;
         return (ConsoleAppender.Builder)this.asBuilder();
      }

      public B setFollow(boolean var1) {
         this.follow = var1;
         return (ConsoleAppender.Builder)this.asBuilder();
      }

      public B setDirect(boolean var1) {
         this.direct = var1;
         return (ConsoleAppender.Builder)this.asBuilder();
      }

      public ConsoleAppender build() {
         if (this.follow && this.direct) {
            throw new IllegalArgumentException("Cannot use both follow and direct on ConsoleAppender '" + this.getName() + "'");
         } else {
            Layout var1 = this.getOrCreateLayout(this.target.getDefaultCharset());
            return new ConsoleAppender(this.getName(), var1, this.getFilter(), ConsoleAppender.getManager(this.target, this.follow, this.direct, var1), this.isIgnoreExceptions(), this.target);
         }
      }
   }

   public static enum Target {
      SYSTEM_OUT {
         public Charset getDefaultCharset() {
            return this.getCharset("sun.stdout.encoding");
         }
      },
      SYSTEM_ERR {
         public Charset getDefaultCharset() {
            return this.getCharset("sun.stderr.encoding");
         }
      };

      private Target() {
      }

      public abstract Charset getDefaultCharset();

      protected Charset getCharset(String var1) {
         return (new PropertiesUtil(PropertiesUtil.getSystemProperties())).getCharsetProperty(var1);
      }

      // $FF: synthetic method
      Target(Object var3) {
         this();
      }
   }
}
