package org.apache.logging.log4j.core.appender.routing;

import org.apache.logging.log4j.core.LogEvent;

public interface PurgePolicy {
   void purge();

   void update(String var1, LogEvent var2);

   void initialize(RoutingAppender var1);
}
