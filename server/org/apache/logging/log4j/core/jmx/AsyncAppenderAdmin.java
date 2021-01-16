package org.apache.logging.log4j.core.jmx;

import java.util.Objects;
import javax.management.ObjectName;
import org.apache.logging.log4j.core.appender.AsyncAppender;

public class AsyncAppenderAdmin implements AsyncAppenderAdminMBean {
   private final String contextName;
   private final AsyncAppender asyncAppender;
   private final ObjectName objectName;

   public AsyncAppenderAdmin(String var1, AsyncAppender var2) {
      super();
      this.contextName = (String)Objects.requireNonNull(var1, "contextName");
      this.asyncAppender = (AsyncAppender)Objects.requireNonNull(var2, "async appender");

      try {
         String var3 = Server.escape(this.contextName);
         String var4 = Server.escape(var2.getName());
         String var5 = String.format("org.apache.logging.log4j2:type=%s,component=AsyncAppenders,name=%s", var3, var4);
         this.objectName = new ObjectName(var5);
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }
   }

   public ObjectName getObjectName() {
      return this.objectName;
   }

   public String getName() {
      return this.asyncAppender.getName();
   }

   public String getLayout() {
      return String.valueOf(this.asyncAppender.getLayout());
   }

   public boolean isIgnoreExceptions() {
      return this.asyncAppender.ignoreExceptions();
   }

   public String getErrorHandler() {
      return String.valueOf(this.asyncAppender.getHandler());
   }

   public String getFilter() {
      return String.valueOf(this.asyncAppender.getFilter());
   }

   public String[] getAppenderRefs() {
      return this.asyncAppender.getAppenderRefStrings();
   }

   public boolean isIncludeLocation() {
      return this.asyncAppender.isIncludeLocation();
   }

   public boolean isBlocking() {
      return this.asyncAppender.isBlocking();
   }

   public String getErrorRef() {
      return this.asyncAppender.getErrorRef();
   }

   public int getQueueCapacity() {
      return this.asyncAppender.getQueueCapacity();
   }

   public int getQueueRemainingCapacity() {
      return this.asyncAppender.getQueueRemainingCapacity();
   }
}
