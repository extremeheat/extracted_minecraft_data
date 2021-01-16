package org.apache.logging.log4j.core.appender;

public interface ManagerFactory<M, T> {
   M createManager(String var1, T var2);
}
