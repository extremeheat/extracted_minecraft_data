package org.apache.logging.log4j.core.appender.db;

import java.util.Date;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.spi.ThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextStack;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

@Plugin(
   name = "ColumnMapping",
   category = "Core",
   printObject = true
)
public class ColumnMapping {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String name;
   private final StringLayout layout;
   private final String literalValue;
   private final Class<?> type;

   private ColumnMapping(String var1, StringLayout var2, String var3, Class<?> var4) {
      super();
      this.name = var1;
      this.layout = var2;
      this.literalValue = var3;
      this.type = var4;
   }

   public String getName() {
      return this.name;
   }

   public StringLayout getLayout() {
      return this.layout;
   }

   public String getLiteralValue() {
      return this.literalValue;
   }

   public Class<?> getType() {
      return this.type;
   }

   @PluginBuilderFactory
   public static ColumnMapping.Builder newBuilder() {
      return new ColumnMapping.Builder();
   }

   // $FF: synthetic method
   ColumnMapping(String var1, StringLayout var2, String var3, Class var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<ColumnMapping> {
      @PluginBuilderAttribute
      @Required(
         message = "No column name provided"
      )
      private String name;
      @PluginElement("Layout")
      private StringLayout layout;
      @PluginBuilderAttribute
      private String pattern;
      @PluginBuilderAttribute
      private String literal;
      @PluginBuilderAttribute
      @Required(
         message = "No conversion type provided"
      )
      private Class<?> type = String.class;
      @PluginConfiguration
      private Configuration configuration;

      public Builder() {
         super();
      }

      public ColumnMapping.Builder setName(String var1) {
         this.name = var1;
         return this;
      }

      public ColumnMapping.Builder setLayout(StringLayout var1) {
         this.layout = var1;
         return this;
      }

      public ColumnMapping.Builder setPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public ColumnMapping.Builder setLiteral(String var1) {
         this.literal = var1;
         return this;
      }

      public ColumnMapping.Builder setType(Class<?> var1) {
         this.type = var1;
         return this;
      }

      public ColumnMapping.Builder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public ColumnMapping build() {
         if (this.pattern != null) {
            this.layout = PatternLayout.newBuilder().withPattern(this.pattern).withConfiguration(this.configuration).build();
         }

         if (this.layout == null && this.literal == null && !Date.class.isAssignableFrom(this.type) && !ReadOnlyStringMap.class.isAssignableFrom(this.type) && !ThreadContextMap.class.isAssignableFrom(this.type) && !ThreadContextStack.class.isAssignableFrom(this.type)) {
            ColumnMapping.LOGGER.error((String)"No layout or literal value specified and type ({}) is not compatible with ThreadContextMap, ThreadContextStack, or java.util.Date", (Object)this.type);
            return null;
         } else {
            return new ColumnMapping(this.name, this.layout, this.literal, this.type);
         }
      }
   }
}
