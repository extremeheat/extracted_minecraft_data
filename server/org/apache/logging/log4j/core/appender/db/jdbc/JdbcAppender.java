package org.apache.logging.log4j.core.appender.db.jdbc;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.appender.db.ColumnMapping;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Assert;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(
   name = "JDBC",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class JdbcAppender extends AbstractDatabaseAppender<JdbcDatabaseManager> {
   private final String description;

   private JdbcAppender(String var1, Filter var2, boolean var3, JdbcDatabaseManager var4) {
      super(var1, var2, var3, var4);
      this.description = this.getName() + "{ manager=" + this.getManager() + " }";
   }

   public String toString() {
      return this.description;
   }

   /** @deprecated */
   @Deprecated
   public static <B extends JdbcAppender.Builder<B>> JdbcAppender createAppender(String var0, String var1, Filter var2, ConnectionSource var3, String var4, String var5, ColumnConfig[] var6) {
      Assert.requireNonEmpty(var0, "Name cannot be empty");
      Objects.requireNonNull(var3, "ConnectionSource cannot be null");
      Assert.requireNonEmpty(var5, "Table name cannot be empty");
      Assert.requireNonEmpty(var6, "ColumnConfigs cannot be empty");
      int var7 = AbstractAppender.parseInt(var4, 0);
      boolean var8 = Booleans.parseBoolean(var1, true);
      return ((JdbcAppender.Builder)((JdbcAppender.Builder)((JdbcAppender.Builder)newBuilder().setBufferSize(var7).setColumnConfigs(var6).setConnectionSource(var3).setTableName(var5).withName(var0)).withIgnoreExceptions(var8)).withFilter(var2)).build();
   }

   @PluginBuilderFactory
   public static <B extends JdbcAppender.Builder<B>> B newBuilder() {
      return (JdbcAppender.Builder)(new JdbcAppender.Builder()).asBuilder();
   }

   // $FF: synthetic method
   JdbcAppender(String var1, Filter var2, boolean var3, JdbcDatabaseManager var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   public static class Builder<B extends JdbcAppender.Builder<B>> extends AbstractAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<JdbcAppender> {
      @PluginElement("ConnectionSource")
      @Required(
         message = "No ConnectionSource provided"
      )
      private ConnectionSource connectionSource;
      @PluginBuilderAttribute
      private int bufferSize;
      @PluginBuilderAttribute
      @Required(
         message = "No table name provided"
      )
      private String tableName;
      @PluginElement("ColumnConfigs")
      private ColumnConfig[] columnConfigs;
      @PluginElement("ColumnMappings")
      private ColumnMapping[] columnMappings;

      public Builder() {
         super();
      }

      public B setConnectionSource(ConnectionSource var1) {
         this.connectionSource = var1;
         return (JdbcAppender.Builder)this.asBuilder();
      }

      public B setBufferSize(int var1) {
         this.bufferSize = var1;
         return (JdbcAppender.Builder)this.asBuilder();
      }

      public B setTableName(String var1) {
         this.tableName = var1;
         return (JdbcAppender.Builder)this.asBuilder();
      }

      public B setColumnConfigs(ColumnConfig... var1) {
         this.columnConfigs = var1;
         return (JdbcAppender.Builder)this.asBuilder();
      }

      public B setColumnMappings(ColumnMapping... var1) {
         this.columnMappings = var1;
         return (JdbcAppender.Builder)this.asBuilder();
      }

      public JdbcAppender build() {
         if (Assert.isEmpty(this.columnConfigs) && Assert.isEmpty(this.columnMappings)) {
            JdbcAppender.LOGGER.error("Cannot create JdbcAppender without any columns configured.");
            return null;
         } else {
            String var1 = "JdbcManager{name=" + this.getName() + ", bufferSize=" + this.bufferSize + ", tableName=" + this.tableName + ", columnConfigs=" + Arrays.toString(this.columnConfigs) + ", columnMappings=" + Arrays.toString(this.columnMappings) + '}';
            JdbcDatabaseManager var2 = JdbcDatabaseManager.getManager(var1, this.bufferSize, this.connectionSource, this.tableName, this.columnConfigs, this.columnMappings);
            return var2 == null ? null : new JdbcAppender(this.getName(), this.getFilter(), this.isIgnoreExceptions(), var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Layout<? extends Serializable> getLayout() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public B withLayout(Layout<? extends Serializable> var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Layout<? extends Serializable> getOrCreateLayout() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Layout<? extends Serializable> getOrCreateLayout(Charset var1) {
         throw new UnsupportedOperationException();
      }
   }
}
