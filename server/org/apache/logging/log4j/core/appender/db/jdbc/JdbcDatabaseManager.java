package org.apache.logging.log4j.core.appender.db.jdbc;

import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.appender.db.ColumnMapping;
import org.apache.logging.log4j.core.config.plugins.convert.DateTypeConverter;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.spi.ThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextStack;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.Strings;

public final class JdbcDatabaseManager extends AbstractDatabaseManager {
   private static final JdbcDatabaseManager.JdbcDatabaseManagerFactory INSTANCE = new JdbcDatabaseManager.JdbcDatabaseManagerFactory();
   private final List<ColumnMapping> columnMappings;
   private final List<ColumnConfig> columnConfigs;
   private final ConnectionSource connectionSource;
   private final String sqlStatement;
   private Connection connection;
   private PreparedStatement statement;
   private boolean isBatchSupported;

   private JdbcDatabaseManager(String var1, int var2, ConnectionSource var3, String var4, List<ColumnConfig> var5, List<ColumnMapping> var6) {
      super(var1, var2);
      this.connectionSource = var3;
      this.sqlStatement = var4;
      this.columnConfigs = var5;
      this.columnMappings = var6;
   }

   protected void startupInternal() throws Exception {
      this.connection = this.connectionSource.getConnection();
      DatabaseMetaData var1 = this.connection.getMetaData();
      this.isBatchSupported = var1.supportsBatchUpdates();
      Closer.closeSilently(this.connection);
   }

   protected boolean shutdownInternal() {
      return this.connection == null && this.statement == null ? true : this.commitAndClose();
   }

   protected void connectAndStart() {
      try {
         this.connection = this.connectionSource.getConnection();
         this.connection.setAutoCommit(false);
         this.statement = this.connection.prepareStatement(this.sqlStatement);
      } catch (SQLException var2) {
         throw new AppenderLoggingException("Cannot write logging event or flush buffer; JDBC manager cannot connect to the database.", var2);
      }
   }

   protected void writeInternal(LogEvent var1) {
      StringReader var2 = null;

      try {
         if (!this.isRunning() || this.connection == null || this.connection.isClosed() || this.statement == null || this.statement.isClosed()) {
            throw new AppenderLoggingException("Cannot write logging event; JDBC manager not connected to the database.");
         }

         int var3 = 1;
         Iterator var4 = this.columnMappings.iterator();

         while(var4.hasNext()) {
            ColumnMapping var5 = (ColumnMapping)var4.next();
            if (!ThreadContextMap.class.isAssignableFrom(var5.getType()) && !ReadOnlyStringMap.class.isAssignableFrom(var5.getType())) {
               if (ThreadContextStack.class.isAssignableFrom(var5.getType())) {
                  this.statement.setObject(var3++, var1.getContextStack().asList());
               } else if (Date.class.isAssignableFrom(var5.getType())) {
                  this.statement.setObject(var3++, DateTypeConverter.fromMillis(var1.getTimeMillis(), var5.getType().asSubclass(Date.class)));
               } else if (Clob.class.isAssignableFrom(var5.getType())) {
                  this.statement.setClob(var3++, new StringReader((String)var5.getLayout().toSerializable(var1)));
               } else if (NClob.class.isAssignableFrom(var5.getType())) {
                  this.statement.setNClob(var3++, new StringReader((String)var5.getLayout().toSerializable(var1)));
               } else {
                  Object var6 = TypeConverters.convert((String)var5.getLayout().toSerializable(var1), var5.getType(), (Object)null);
                  if (var6 == null) {
                     this.statement.setNull(var3++, 0);
                  } else {
                     this.statement.setObject(var3++, var6);
                  }
               }
            } else {
               this.statement.setObject(var3++, var1.getContextData().toMap());
            }
         }

         var4 = this.columnConfigs.iterator();

         while(var4.hasNext()) {
            ColumnConfig var12 = (ColumnConfig)var4.next();
            if (var12.isEventTimestamp()) {
               this.statement.setTimestamp(var3++, new Timestamp(var1.getTimeMillis()));
            } else if (var12.isClob()) {
               var2 = new StringReader(var12.getLayout().toSerializable(var1));
               if (var12.isUnicode()) {
                  this.statement.setNClob(var3++, var2);
               } else {
                  this.statement.setClob(var3++, var2);
               }
            } else if (var12.isUnicode()) {
               this.statement.setNString(var3++, var12.getLayout().toSerializable(var1));
            } else {
               this.statement.setString(var3++, var12.getLayout().toSerializable(var1));
            }
         }

         if (this.isBatchSupported) {
            this.statement.addBatch();
         } else if (this.statement.executeUpdate() == 0) {
            throw new AppenderLoggingException("No records inserted in database table for log event in JDBC manager.");
         }
      } catch (SQLException var10) {
         throw new AppenderLoggingException("Failed to insert record for log event in JDBC manager: " + var10.getMessage(), var10);
      } finally {
         Closer.closeSilently(var2);
      }

   }

   protected boolean commitAndClose() {
      boolean var1 = true;

      try {
         if (this.connection != null && !this.connection.isClosed()) {
            if (this.isBatchSupported) {
               this.statement.executeBatch();
            }

            this.connection.commit();
         }
      } catch (SQLException var63) {
         throw new AppenderLoggingException("Failed to commit transaction logging event or flushing buffer.", var63);
      } finally {
         try {
            Closer.close(this.statement);
         } catch (Exception var61) {
            this.logWarn("Failed to close SQL statement logging event or flushing buffer", var61);
            var1 = false;
         } finally {
            this.statement = null;
         }

         try {
            Closer.close(this.connection);
         } catch (Exception var59) {
            this.logWarn("Failed to close database connection logging event or flushing buffer", var59);
            var1 = false;
         } finally {
            this.connection = null;
         }

      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   public static JdbcDatabaseManager getJDBCDatabaseManager(String var0, int var1, ConnectionSource var2, String var3, ColumnConfig[] var4) {
      return (JdbcDatabaseManager)getManager(var0, new JdbcDatabaseManager.FactoryData(var1, var2, var3, var4, new ColumnMapping[0]), getFactory());
   }

   public static JdbcDatabaseManager getManager(String var0, int var1, ConnectionSource var2, String var3, ColumnConfig[] var4, ColumnMapping[] var5) {
      return (JdbcDatabaseManager)getManager(var0, new JdbcDatabaseManager.FactoryData(var1, var2, var3, var4, var5), getFactory());
   }

   private static JdbcDatabaseManager.JdbcDatabaseManagerFactory getFactory() {
      return INSTANCE;
   }

   // $FF: synthetic method
   JdbcDatabaseManager(String var1, int var2, ConnectionSource var3, String var4, List var5, List var6, Object var7) {
      this(var1, var2, var3, var4, var5, var6);
   }

   private static final class JdbcDatabaseManagerFactory implements ManagerFactory<JdbcDatabaseManager, JdbcDatabaseManager.FactoryData> {
      private JdbcDatabaseManagerFactory() {
         super();
      }

      public JdbcDatabaseManager createManager(String var1, JdbcDatabaseManager.FactoryData var2) {
         StringBuilder var3 = (new StringBuilder("INSERT INTO ")).append(var2.tableName).append(" (");
         ColumnMapping[] var4 = var2.columnMappings;
         int var5 = var4.length;

         int var6;
         for(var6 = 0; var6 < var5; ++var6) {
            ColumnMapping var7 = var4[var6];
            var3.append(var7.getName()).append(',');
         }

         ColumnConfig[] var10 = var2.columnConfigs;
         var5 = var10.length;

         for(var6 = 0; var6 < var5; ++var6) {
            ColumnConfig var14 = var10[var6];
            var3.append(var14.getColumnName()).append(',');
         }

         var3.setCharAt(var3.length() - 1, ')');
         var3.append(" VALUES (");
         ArrayList var11 = new ArrayList(var2.columnMappings.length);
         ColumnMapping[] var12 = var2.columnMappings;
         var6 = var12.length;

         int var15;
         for(var15 = 0; var15 < var6; ++var15) {
            ColumnMapping var8 = var12[var15];
            if (Strings.isNotEmpty(var8.getLiteralValue())) {
               var3.append(var8.getLiteralValue());
            } else {
               var3.append('?');
               var11.add(var8);
            }

            var3.append(',');
         }

         ArrayList var13 = new ArrayList(var2.columnConfigs.length);
         ColumnConfig[] var16 = var2.columnConfigs;
         var15 = var16.length;

         for(int var18 = 0; var18 < var15; ++var18) {
            ColumnConfig var9 = var16[var18];
            if (Strings.isNotEmpty(var9.getLiteralValue())) {
               var3.append(var9.getLiteralValue());
            } else {
               var3.append('?');
               var13.add(var9);
            }

            var3.append(',');
         }

         var3.setCharAt(var3.length() - 1, ')');
         String var17 = var3.toString();
         return new JdbcDatabaseManager(var1, var2.getBufferSize(), var2.connectionSource, var17, var13, var11);
      }

      // $FF: synthetic method
      JdbcDatabaseManagerFactory(Object var1) {
         this();
      }
   }

   private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData {
      private final ConnectionSource connectionSource;
      private final String tableName;
      private final ColumnConfig[] columnConfigs;
      private final ColumnMapping[] columnMappings;

      protected FactoryData(int var1, ConnectionSource var2, String var3, ColumnConfig[] var4, ColumnMapping[] var5) {
         super(var1);
         this.connectionSource = var2;
         this.tableName = var3;
         this.columnConfigs = var4;
         this.columnMappings = var5;
      }
   }
}
