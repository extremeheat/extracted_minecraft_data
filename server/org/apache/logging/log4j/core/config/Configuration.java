package org.apache.logging.log4j.core.config;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerConfigDelegate;
import org.apache.logging.log4j.core.filter.Filterable;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.core.util.WatchManager;

public interface Configuration extends Filterable {
   String CONTEXT_PROPERTIES = "ContextProperties";

   String getName();

   LoggerConfig getLoggerConfig(String var1);

   <T extends Appender> T getAppender(String var1);

   Map<String, Appender> getAppenders();

   void addAppender(Appender var1);

   Map<String, LoggerConfig> getLoggers();

   void addLoggerAppender(Logger var1, Appender var2);

   void addLoggerFilter(Logger var1, Filter var2);

   void setLoggerAdditive(Logger var1, boolean var2);

   void addLogger(String var1, LoggerConfig var2);

   void removeLogger(String var1);

   List<String> getPluginPackages();

   Map<String, String> getProperties();

   LoggerConfig getRootLogger();

   void addListener(ConfigurationListener var1);

   void removeListener(ConfigurationListener var1);

   StrSubstitutor getStrSubstitutor();

   void createConfiguration(Node var1, LogEvent var2);

   <T> T getComponent(String var1);

   void addComponent(String var1, Object var2);

   void setAdvertiser(Advertiser var1);

   Advertiser getAdvertiser();

   boolean isShutdownHookEnabled();

   long getShutdownTimeoutMillis();

   ConfigurationScheduler getScheduler();

   ConfigurationSource getConfigurationSource();

   List<CustomLevelConfig> getCustomLevels();

   ScriptManager getScriptManager();

   AsyncLoggerConfigDelegate getAsyncLoggerConfigDelegate();

   WatchManager getWatchManager();

   ReliabilityStrategy getReliabilityStrategy(LoggerConfig var1);

   NanoClock getNanoClock();

   void setNanoClock(NanoClock var1);

   LoggerContext getLoggerContext();
}
