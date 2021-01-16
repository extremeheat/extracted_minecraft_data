package org.apache.logging.log4j.core.jmx;

public interface AppenderAdminMBean {
   String PATTERN = "org.apache.logging.log4j2:type=%s,component=Appenders,name=%s";

   String getName();

   String getLayout();

   boolean isIgnoreExceptions();

   String getErrorHandler();

   String getFilter();
}
