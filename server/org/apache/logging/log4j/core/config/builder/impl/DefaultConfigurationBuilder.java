package org.apache.logging.log4j.core.config.builder.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.CustomLevelComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ScriptComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ScriptFileComponentBuilder;
import org.apache.logging.log4j.core.util.Throwables;

public class DefaultConfigurationBuilder<T extends BuiltConfiguration> implements ConfigurationBuilder<T> {
   private static final String INDENT = "  ";
   private static final String EOL = System.lineSeparator();
   private final Component root;
   private Component loggers;
   private Component appenders;
   private Component filters;
   private Component properties;
   private Component customLevels;
   private Component scripts;
   private final Class<T> clazz;
   private ConfigurationSource source;
   private int monitorInterval;
   private Level level;
   private String verbosity;
   private String destination;
   private String packages;
   private String shutdownFlag;
   private long shutdownTimeoutMillis;
   private String advertiser;
   private LoggerContext loggerContext;
   private String name;

   public DefaultConfigurationBuilder() {
      this(BuiltConfiguration.class);
      this.root.addAttribute("name", "Built");
   }

   public DefaultConfigurationBuilder(Class<T> var1) {
      super();
      this.root = new Component();
      if (var1 == null) {
         throw new IllegalArgumentException("A Configuration class must be provided");
      } else {
         this.clazz = var1;
         List var2 = this.root.getComponents();
         this.properties = new Component("Properties");
         var2.add(this.properties);
         this.scripts = new Component("Scripts");
         var2.add(this.scripts);
         this.customLevels = new Component("CustomLevels");
         var2.add(this.customLevels);
         this.filters = new Component("Filters");
         var2.add(this.filters);
         this.appenders = new Component("Appenders");
         var2.add(this.appenders);
         this.loggers = new Component("Loggers");
         var2.add(this.loggers);
      }
   }

   protected ConfigurationBuilder<T> add(Component var1, ComponentBuilder<?> var2) {
      var1.getComponents().add(var2.build());
      return this;
   }

   public ConfigurationBuilder<T> add(AppenderComponentBuilder var1) {
      return this.add(this.appenders, var1);
   }

   public ConfigurationBuilder<T> add(CustomLevelComponentBuilder var1) {
      return this.add(this.customLevels, var1);
   }

   public ConfigurationBuilder<T> add(FilterComponentBuilder var1) {
      return this.add(this.filters, var1);
   }

   public ConfigurationBuilder<T> add(ScriptComponentBuilder var1) {
      return this.add(this.scripts, var1);
   }

   public ConfigurationBuilder<T> add(ScriptFileComponentBuilder var1) {
      return this.add(this.scripts, var1);
   }

   public ConfigurationBuilder<T> add(LoggerComponentBuilder var1) {
      return this.add(this.loggers, var1);
   }

   public ConfigurationBuilder<T> add(RootLoggerComponentBuilder var1) {
      Iterator var2 = this.loggers.getComponents().iterator();

      Component var3;
      do {
         if (!var2.hasNext()) {
            return this.add(this.loggers, var1);
         }

         var3 = (Component)var2.next();
      } while(!var3.getPluginType().equals("root"));

      throw new ConfigurationException("Root Logger was previously defined");
   }

   public ConfigurationBuilder<T> addProperty(String var1, String var2) {
      this.properties.addComponent((Component)this.newComponent(var1, "Property", var2).build());
      return this;
   }

   public T build() {
      return this.build(true);
   }

   public T build(boolean var1) {
      BuiltConfiguration var2;
      try {
         if (this.source == null) {
            this.source = ConfigurationSource.NULL_SOURCE;
         }

         Constructor var3 = this.clazz.getConstructor(LoggerContext.class, ConfigurationSource.class, Component.class);
         var2 = (BuiltConfiguration)var3.newInstance(this.loggerContext, this.source, this.root);
         var2.setMonitorInterval(this.monitorInterval);
         var2.getRootNode().getAttributes().putAll(this.root.getAttributes());
         if (this.name != null) {
            var2.setName(this.name);
         }

         if (this.level != null) {
            var2.getStatusConfiguration().withStatus(this.level);
         }

         if (this.verbosity != null) {
            var2.getStatusConfiguration().withVerbosity(this.verbosity);
         }

         if (this.destination != null) {
            var2.getStatusConfiguration().withDestination(this.destination);
         }

         if (this.packages != null) {
            var2.setPluginPackages(this.packages);
         }

         if (this.shutdownFlag != null) {
            var2.setShutdownHook(this.shutdownFlag);
         }

         if (this.shutdownTimeoutMillis > 0L) {
            var2.setShutdownTimeoutMillis(this.shutdownTimeoutMillis);
         }

         if (this.advertiser != null) {
            var2.createAdvertiser(this.advertiser, this.source);
         }
      } catch (Exception var4) {
         throw new IllegalArgumentException("Invalid Configuration class specified", var4);
      }

      var2.getStatusConfiguration().initialize();
      if (var1) {
         var2.initialize();
      }

      return var2;
   }

   public void writeXmlConfiguration(OutputStream var1) throws IOException {
      try {
         XMLStreamWriter var2 = XMLOutputFactory.newInstance().createXMLStreamWriter(var1);
         this.writeXmlConfiguration(var2);
         var2.close();
      } catch (XMLStreamException var3) {
         if (var3.getNestedException() instanceof IOException) {
            throw (IOException)var3.getNestedException();
         }

         Throwables.rethrow(var3);
      }

   }

   public String toXmlConfiguration() {
      StringWriter var1 = new StringWriter();

      try {
         XMLStreamWriter var2 = XMLOutputFactory.newInstance().createXMLStreamWriter(var1);
         this.writeXmlConfiguration(var2);
         var2.close();
      } catch (XMLStreamException var3) {
         Throwables.rethrow(var3);
      }

      return var1.toString();
   }

   private void writeXmlConfiguration(XMLStreamWriter var1) throws XMLStreamException {
      var1.writeStartDocument();
      var1.writeCharacters(EOL);
      var1.writeStartElement("Configuration");
      if (this.name != null) {
         var1.writeAttribute("name", this.name);
      }

      if (this.level != null) {
         var1.writeAttribute("status", this.level.name());
      }

      if (this.verbosity != null) {
         var1.writeAttribute("verbose", this.verbosity);
      }

      if (this.destination != null) {
         var1.writeAttribute("dest", this.destination);
      }

      if (this.packages != null) {
         var1.writeAttribute("packages", this.packages);
      }

      if (this.shutdownFlag != null) {
         var1.writeAttribute("shutdownHook", this.shutdownFlag);
      }

      if (this.shutdownTimeoutMillis > 0L) {
         var1.writeAttribute("shutdownTimeout", String.valueOf(this.shutdownTimeoutMillis));
      }

      if (this.advertiser != null) {
         var1.writeAttribute("advertiser", this.advertiser);
      }

      if (this.monitorInterval > 0) {
         var1.writeAttribute("monitorInterval", String.valueOf(this.monitorInterval));
      }

      var1.writeCharacters(EOL);
      this.writeXmlSection(var1, this.properties);
      this.writeXmlSection(var1, this.scripts);
      this.writeXmlSection(var1, this.customLevels);
      if (this.filters.getComponents().size() == 1) {
         this.writeXmlComponent(var1, (Component)this.filters.getComponents().get(0), 1);
      } else if (this.filters.getComponents().size() > 1) {
         this.writeXmlSection(var1, this.filters);
      }

      this.writeXmlSection(var1, this.appenders);
      this.writeXmlSection(var1, this.loggers);
      var1.writeEndElement();
      var1.writeCharacters(EOL);
      var1.writeEndDocument();
   }

   private void writeXmlSection(XMLStreamWriter var1, Component var2) throws XMLStreamException {
      if (!var2.getAttributes().isEmpty() || !var2.getComponents().isEmpty() || var2.getValue() != null) {
         this.writeXmlComponent(var1, var2, 1);
      }

   }

   private void writeXmlComponent(XMLStreamWriter var1, Component var2, int var3) throws XMLStreamException {
      if (var2.getComponents().isEmpty() && var2.getValue() == null) {
         this.writeXmlIndent(var1, var3);
         var1.writeEmptyElement(var2.getPluginType());
         this.writeXmlAttributes(var1, var2);
      } else {
         this.writeXmlIndent(var1, var3);
         var1.writeStartElement(var2.getPluginType());
         this.writeXmlAttributes(var1, var2);
         if (!var2.getComponents().isEmpty()) {
            var1.writeCharacters(EOL);
         }

         Iterator var4 = var2.getComponents().iterator();

         while(var4.hasNext()) {
            Component var5 = (Component)var4.next();
            this.writeXmlComponent(var1, var5, var3 + 1);
         }

         if (var2.getValue() != null) {
            var1.writeCharacters(var2.getValue());
         }

         if (!var2.getComponents().isEmpty()) {
            this.writeXmlIndent(var1, var3);
         }

         var1.writeEndElement();
      }

      var1.writeCharacters(EOL);
   }

   private void writeXmlIndent(XMLStreamWriter var1, int var2) throws XMLStreamException {
      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeCharacters("  ");
      }

   }

   private void writeXmlAttributes(XMLStreamWriter var1, Component var2) throws XMLStreamException {
      Iterator var3 = var2.getAttributes().entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var1.writeAttribute((String)var4.getKey(), (String)var4.getValue());
      }

   }

   public ScriptComponentBuilder newScript(String var1, String var2, String var3) {
      return new DefaultScriptComponentBuilder(this, var1, var2, var3);
   }

   public ScriptFileComponentBuilder newScriptFile(String var1) {
      return new DefaultScriptFileComponentBuilder(this, var1, var1);
   }

   public ScriptFileComponentBuilder newScriptFile(String var1, String var2) {
      return new DefaultScriptFileComponentBuilder(this, var1, var2);
   }

   public AppenderComponentBuilder newAppender(String var1, String var2) {
      return new DefaultAppenderComponentBuilder(this, var1, var2);
   }

   public AppenderRefComponentBuilder newAppenderRef(String var1) {
      return new DefaultAppenderRefComponentBuilder(this, var1);
   }

   public LoggerComponentBuilder newAsyncLogger(String var1, Level var2) {
      return new DefaultLoggerComponentBuilder(this, var1, var2.toString(), "AsyncLogger");
   }

   public LoggerComponentBuilder newAsyncLogger(String var1, Level var2, boolean var3) {
      return new DefaultLoggerComponentBuilder(this, var1, var2.toString(), "AsyncLogger", var3);
   }

   public LoggerComponentBuilder newAsyncLogger(String var1, String var2) {
      return new DefaultLoggerComponentBuilder(this, var1, var2, "AsyncLogger");
   }

   public LoggerComponentBuilder newAsyncLogger(String var1, String var2, boolean var3) {
      return new DefaultLoggerComponentBuilder(this, var1, var2, "AsyncLogger");
   }

   public RootLoggerComponentBuilder newAsyncRootLogger(Level var1) {
      return new DefaultRootLoggerComponentBuilder(this, var1.toString(), "AsyncRoot");
   }

   public RootLoggerComponentBuilder newAsyncRootLogger(Level var1, boolean var2) {
      return new DefaultRootLoggerComponentBuilder(this, var1.toString(), "AsyncRoot", var2);
   }

   public RootLoggerComponentBuilder newAsyncRootLogger(String var1) {
      return new DefaultRootLoggerComponentBuilder(this, var1, "AsyncRoot");
   }

   public RootLoggerComponentBuilder newAsyncRootLogger(String var1, boolean var2) {
      return new DefaultRootLoggerComponentBuilder(this, var1, "AsyncRoot", var2);
   }

   public <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1) {
      return new DefaultComponentBuilder(this, var1);
   }

   public <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1, String var2) {
      return new DefaultComponentBuilder(this, var1, var2);
   }

   public <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1, String var2, String var3) {
      return new DefaultComponentBuilder(this, var1, var2, var3);
   }

   public CustomLevelComponentBuilder newCustomLevel(String var1, int var2) {
      return new DefaultCustomLevelComponentBuilder(this, var1, var2);
   }

   public FilterComponentBuilder newFilter(String var1, Filter.Result var2, Filter.Result var3) {
      return new DefaultFilterComponentBuilder(this, var1, var2.name(), var3.name());
   }

   public FilterComponentBuilder newFilter(String var1, String var2, String var3) {
      return new DefaultFilterComponentBuilder(this, var1, var2, var3);
   }

   public LayoutComponentBuilder newLayout(String var1) {
      return new DefaultLayoutComponentBuilder(this, var1);
   }

   public LoggerComponentBuilder newLogger(String var1, Level var2) {
      return new DefaultLoggerComponentBuilder(this, var1, var2.toString());
   }

   public LoggerComponentBuilder newLogger(String var1, Level var2, boolean var3) {
      return new DefaultLoggerComponentBuilder(this, var1, var2.toString(), var3);
   }

   public LoggerComponentBuilder newLogger(String var1, String var2) {
      return new DefaultLoggerComponentBuilder(this, var1, var2);
   }

   public LoggerComponentBuilder newLogger(String var1, String var2, boolean var3) {
      return new DefaultLoggerComponentBuilder(this, var1, var2, var3);
   }

   public RootLoggerComponentBuilder newRootLogger(Level var1) {
      return new DefaultRootLoggerComponentBuilder(this, var1.toString());
   }

   public RootLoggerComponentBuilder newRootLogger(Level var1, boolean var2) {
      return new DefaultRootLoggerComponentBuilder(this, var1.toString(), var2);
   }

   public RootLoggerComponentBuilder newRootLogger(String var1) {
      return new DefaultRootLoggerComponentBuilder(this, var1);
   }

   public RootLoggerComponentBuilder newRootLogger(String var1, boolean var2) {
      return new DefaultRootLoggerComponentBuilder(this, var1, var2);
   }

   public ConfigurationBuilder<T> setAdvertiser(String var1) {
      this.advertiser = var1;
      return this;
   }

   public ConfigurationBuilder<T> setConfigurationName(String var1) {
      this.name = var1;
      return this;
   }

   public ConfigurationBuilder<T> setConfigurationSource(ConfigurationSource var1) {
      this.source = var1;
      return this;
   }

   public ConfigurationBuilder<T> setMonitorInterval(String var1) {
      this.monitorInterval = Integer.parseInt(var1);
      return this;
   }

   public ConfigurationBuilder<T> setPackages(String var1) {
      this.packages = var1;
      return this;
   }

   public ConfigurationBuilder<T> setShutdownHook(String var1) {
      this.shutdownFlag = var1;
      return this;
   }

   public ConfigurationBuilder<T> setShutdownTimeout(long var1, TimeUnit var3) {
      this.shutdownTimeoutMillis = var3.toMillis(var1);
      return this;
   }

   public ConfigurationBuilder<T> setStatusLevel(Level var1) {
      this.level = var1;
      return this;
   }

   public ConfigurationBuilder<T> setVerbosity(String var1) {
      this.verbosity = var1;
      return this;
   }

   public ConfigurationBuilder<T> setDestination(String var1) {
      this.destination = var1;
      return this;
   }

   public void setLoggerContext(LoggerContext var1) {
      this.loggerContext = var1;
   }

   public ConfigurationBuilder<T> addRootProperty(String var1, String var2) {
      this.root.getAttributes().put(var1, var2);
      return this;
   }
}
