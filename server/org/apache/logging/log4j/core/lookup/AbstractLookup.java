package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;

public abstract class AbstractLookup implements StrLookup {
   public AbstractLookup() {
      super();
   }

   public String lookup(String var1) {
      return this.lookup((LogEvent)null, var1);
   }
}
