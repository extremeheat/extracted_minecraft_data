package org.apache.logging.log4j.core.selector;

import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;

public class CoreContextSelectors {
   public static final Class<?>[] CLASSES = new Class[]{ClassLoaderContextSelector.class, BasicContextSelector.class, AsyncLoggerContextSelector.class};

   public CoreContextSelectors() {
      super();
   }
}
