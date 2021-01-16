package org.apache.logging.log4j.core.appender.db.jdbc;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "ConnectionFactory",
   category = "Core",
   elementType = "connectionSource",
   printObject = true
)
public final class FactoryMethodConnectionSource implements ConnectionSource {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final DataSource dataSource;
   private final String description;

   private FactoryMethodConnectionSource(DataSource var1, String var2, String var3, String var4) {
      super();
      this.dataSource = var1;
      this.description = "factory{ public static " + var4 + ' ' + var2 + '.' + var3 + "() }";
   }

   public Connection getConnection() throws SQLException {
      return this.dataSource.getConnection();
   }

   public String toString() {
      return this.description;
   }

   @PluginFactory
   public static FactoryMethodConnectionSource createConnectionSource(@PluginAttribute("class") String var0, @PluginAttribute("method") String var1) {
      if (!Strings.isEmpty(var0) && !Strings.isEmpty(var1)) {
         final Method var2;
         Class var3;
         try {
            var3 = LoaderUtil.loadClass(var0);
            var2 = var3.getMethod(var1);
         } catch (Exception var8) {
            LOGGER.error((String)var8.toString(), (Throwable)var8);
            return null;
         }

         var3 = var2.getReturnType();
         String var4 = var3.getName();
         DataSource var5;
         if (var3 == DataSource.class) {
            try {
               var5 = (DataSource)var2.invoke((Object)null);
               var4 = var4 + "[" + var5 + ']';
            } catch (Exception var7) {
               LOGGER.error((String)var7.toString(), (Throwable)var7);
               return null;
            }
         } else {
            if (var3 != Connection.class) {
               LOGGER.error((String)"Method [{}.{}()] returns unsupported type [{}].", (Object)var0, var1, var3.getName());
               return null;
            }

            var5 = new DataSource() {
               public Connection getConnection() throws SQLException {
                  try {
                     return (Connection)var2.invoke((Object)null);
                  } catch (Exception var2x) {
                     throw new SQLException("Failed to obtain connection from factory method.", var2x);
                  }
               }

               public Connection getConnection(String var1, String var2x) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public int getLoginTimeout() throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public PrintWriter getLogWriter() throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public java.util.logging.Logger getParentLogger() {
                  throw new UnsupportedOperationException();
               }

               public boolean isWrapperFor(Class<?> var1) throws SQLException {
                  return false;
               }

               public void setLoginTimeout(int var1) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public void setLogWriter(PrintWriter var1) throws SQLException {
                  throw new UnsupportedOperationException();
               }

               public <T> T unwrap(Class<T> var1) throws SQLException {
                  return null;
               }
            };
         }

         return new FactoryMethodConnectionSource(var5, var0, var1, var4);
      } else {
         LOGGER.error("No class name or method name specified for the connection factory method.");
         return null;
      }
   }
}
