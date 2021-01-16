package org.apache.logging.log4j.core.jmx;

import java.util.Objects;
import javax.management.ObjectName;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.filter.AbstractFilterable;

public class AppenderAdmin implements AppenderAdminMBean {
   private final String contextName;
   private final Appender appender;
   private final ObjectName objectName;

   public AppenderAdmin(String var1, Appender var2) {
      super();
      this.contextName = (String)Objects.requireNonNull(var1, "contextName");
      this.appender = (Appender)Objects.requireNonNull(var2, "appender");

      try {
         String var3 = Server.escape(this.contextName);
         String var4 = Server.escape(var2.getName());
         String var5 = String.format("org.apache.logging.log4j2:type=%s,component=Appenders,name=%s", var3, var4);
         this.objectName = new ObjectName(var5);
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }
   }

   public ObjectName getObjectName() {
      return this.objectName;
   }

   public String getName() {
      return this.appender.getName();
   }

   public String getLayout() {
      return String.valueOf(this.appender.getLayout());
   }

   public boolean isIgnoreExceptions() {
      return this.appender.ignoreExceptions();
   }

   public String getErrorHandler() {
      return String.valueOf(this.appender.getHandler());
   }

   public String getFilter() {
      return this.appender instanceof AbstractFilterable ? String.valueOf(((AbstractFilterable)this.appender).getFilter()) : null;
   }
}
