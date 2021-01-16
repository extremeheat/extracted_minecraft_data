package org.apache.logging.log4j.core.appender.db.jdbc;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "Column",
   category = "Core",
   printObject = true
)
public final class ColumnConfig {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String columnName;
   private final PatternLayout layout;
   private final String literalValue;
   private final boolean eventTimestamp;
   private final boolean unicode;
   private final boolean clob;

   private ColumnConfig(String var1, PatternLayout var2, String var3, boolean var4, boolean var5, boolean var6) {
      super();
      this.columnName = var1;
      this.layout = var2;
      this.literalValue = var3;
      this.eventTimestamp = var4;
      this.unicode = var5;
      this.clob = var6;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public PatternLayout getLayout() {
      return this.layout;
   }

   public String getLiteralValue() {
      return this.literalValue;
   }

   public boolean isEventTimestamp() {
      return this.eventTimestamp;
   }

   public boolean isUnicode() {
      return this.unicode;
   }

   public boolean isClob() {
      return this.clob;
   }

   public String toString() {
      return "{ name=" + this.columnName + ", layout=" + this.layout + ", literal=" + this.literalValue + ", timestamp=" + this.eventTimestamp + " }";
   }

   /** @deprecated */
   @Deprecated
   public static ColumnConfig createColumnConfig(Configuration var0, String var1, String var2, String var3, String var4, String var5, String var6) {
      if (Strings.isEmpty(var1)) {
         LOGGER.error("The column config is not valid because it does not contain a column name.");
         return null;
      } else {
         boolean var7 = Boolean.parseBoolean(var4);
         boolean var8 = Booleans.parseBoolean(var5, true);
         boolean var9 = Boolean.parseBoolean(var6);
         return newBuilder().setConfiguration(var0).setName(var1).setPattern(var2).setLiteral(var3).setEventTimestamp(var7).setUnicode(var8).setClob(var9).build();
      }
   }

   @PluginBuilderFactory
   public static ColumnConfig.Builder newBuilder() {
      return new ColumnConfig.Builder();
   }

   // $FF: synthetic method
   ColumnConfig(String var1, PatternLayout var2, String var3, boolean var4, boolean var5, boolean var6, Object var7) {
      this(var1, var2, var3, var4, var5, var6);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<ColumnConfig> {
      @PluginConfiguration
      private Configuration configuration;
      @PluginBuilderAttribute
      @Required(
         message = "No name provided"
      )
      private String name;
      @PluginBuilderAttribute
      private String pattern;
      @PluginBuilderAttribute
      private String literal;
      @PluginBuilderAttribute
      private boolean isEventTimestamp;
      @PluginBuilderAttribute
      private boolean isUnicode = true;
      @PluginBuilderAttribute
      private boolean isClob;

      public Builder() {
         super();
      }

      public ColumnConfig.Builder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public ColumnConfig.Builder setName(String var1) {
         this.name = var1;
         return this;
      }

      public ColumnConfig.Builder setPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public ColumnConfig.Builder setLiteral(String var1) {
         this.literal = var1;
         return this;
      }

      public ColumnConfig.Builder setEventTimestamp(boolean var1) {
         this.isEventTimestamp = var1;
         return this;
      }

      public ColumnConfig.Builder setUnicode(boolean var1) {
         this.isUnicode = var1;
         return this;
      }

      public ColumnConfig.Builder setClob(boolean var1) {
         this.isClob = var1;
         return this;
      }

      public ColumnConfig build() {
         if (Strings.isEmpty(this.name)) {
            ColumnConfig.LOGGER.error("The column config is not valid because it does not contain a column name.");
            return null;
         } else {
            boolean var1 = Strings.isNotEmpty(this.pattern);
            boolean var2 = Strings.isNotEmpty(this.literal);
            if (var1 && var2 || var1 && this.isEventTimestamp || var2 && this.isEventTimestamp) {
               ColumnConfig.LOGGER.error("The pattern, literal, and isEventTimestamp attributes are mutually exclusive.");
               return null;
            } else if (this.isEventTimestamp) {
               return new ColumnConfig(this.name, (PatternLayout)null, (String)null, true, false, false);
            } else if (var2) {
               return new ColumnConfig(this.name, (PatternLayout)null, this.literal, false, false, false);
            } else if (var1) {
               PatternLayout var3 = PatternLayout.newBuilder().withPattern(this.pattern).withConfiguration(this.configuration).withAlwaysWriteExceptions(false).build();
               return new ColumnConfig(this.name, var3, (String)null, false, this.isUnicode, this.isClob);
            } else {
               ColumnConfig.LOGGER.error("To configure a column you must specify a pattern or literal or set isEventDate to true.");
               return null;
            }
         }
      }
   }
}
