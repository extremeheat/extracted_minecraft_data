package org.apache.logging.log4j.core.jmx;

public interface ContextSelectorAdminMBean {
   String PATTERN = "org.apache.logging.log4j2:type=%s,component=ContextSelector";

   String getImplementationClassName();
}
