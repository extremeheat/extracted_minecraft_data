package org.apache.logging.log4j.core.config.builder.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.util.Builder;

public interface ConfigurationBuilder<T extends Configuration> extends Builder<T> {
   ConfigurationBuilder<T> add(ScriptComponentBuilder var1);

   ConfigurationBuilder<T> add(ScriptFileComponentBuilder var1);

   ConfigurationBuilder<T> add(AppenderComponentBuilder var1);

   ConfigurationBuilder<T> add(CustomLevelComponentBuilder var1);

   ConfigurationBuilder<T> add(FilterComponentBuilder var1);

   ConfigurationBuilder<T> add(LoggerComponentBuilder var1);

   ConfigurationBuilder<T> add(RootLoggerComponentBuilder var1);

   ConfigurationBuilder<T> addProperty(String var1, String var2);

   ScriptComponentBuilder newScript(String var1, String var2, String var3);

   ScriptFileComponentBuilder newScriptFile(String var1);

   ScriptFileComponentBuilder newScriptFile(String var1, String var2);

   AppenderComponentBuilder newAppender(String var1, String var2);

   AppenderRefComponentBuilder newAppenderRef(String var1);

   LoggerComponentBuilder newAsyncLogger(String var1, Level var2);

   LoggerComponentBuilder newAsyncLogger(String var1, Level var2, boolean var3);

   LoggerComponentBuilder newAsyncLogger(String var1, String var2);

   LoggerComponentBuilder newAsyncLogger(String var1, String var2, boolean var3);

   RootLoggerComponentBuilder newAsyncRootLogger(Level var1);

   RootLoggerComponentBuilder newAsyncRootLogger(Level var1, boolean var2);

   RootLoggerComponentBuilder newAsyncRootLogger(String var1);

   RootLoggerComponentBuilder newAsyncRootLogger(String var1, boolean var2);

   <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1);

   <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1, String var2);

   <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1, String var2, String var3);

   CustomLevelComponentBuilder newCustomLevel(String var1, int var2);

   FilterComponentBuilder newFilter(String var1, Filter.Result var2, Filter.Result var3);

   FilterComponentBuilder newFilter(String var1, String var2, String var3);

   LayoutComponentBuilder newLayout(String var1);

   LoggerComponentBuilder newLogger(String var1, Level var2);

   LoggerComponentBuilder newLogger(String var1, Level var2, boolean var3);

   LoggerComponentBuilder newLogger(String var1, String var2);

   LoggerComponentBuilder newLogger(String var1, String var2, boolean var3);

   RootLoggerComponentBuilder newRootLogger(Level var1);

   RootLoggerComponentBuilder newRootLogger(Level var1, boolean var2);

   RootLoggerComponentBuilder newRootLogger(String var1);

   RootLoggerComponentBuilder newRootLogger(String var1, boolean var2);

   ConfigurationBuilder<T> setAdvertiser(String var1);

   ConfigurationBuilder<T> setConfigurationName(String var1);

   ConfigurationBuilder<T> setConfigurationSource(ConfigurationSource var1);

   ConfigurationBuilder<T> setMonitorInterval(String var1);

   ConfigurationBuilder<T> setPackages(String var1);

   ConfigurationBuilder<T> setShutdownHook(String var1);

   ConfigurationBuilder<T> setShutdownTimeout(long var1, TimeUnit var3);

   ConfigurationBuilder<T> setStatusLevel(Level var1);

   ConfigurationBuilder<T> setVerbosity(String var1);

   ConfigurationBuilder<T> setDestination(String var1);

   void setLoggerContext(LoggerContext var1);

   ConfigurationBuilder<T> addRootProperty(String var1, String var2);

   T build(boolean var1);

   void writeXmlConfiguration(OutputStream var1) throws IOException;

   String toXmlConfiguration();
}
