package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.core.AbstractLifeCycle;

public abstract class AbstractTriggeringPolicy extends AbstractLifeCycle implements TriggeringPolicy {
   public AbstractTriggeringPolicy() {
      super();
   }
}
