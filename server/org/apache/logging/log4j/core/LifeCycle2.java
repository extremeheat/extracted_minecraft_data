package org.apache.logging.log4j.core;

import java.util.concurrent.TimeUnit;

public interface LifeCycle2 extends LifeCycle {
   boolean stop(long var1, TimeUnit var3);
}
