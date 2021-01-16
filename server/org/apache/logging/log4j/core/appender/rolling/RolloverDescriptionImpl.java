package org.apache.logging.log4j.core.appender.rolling;

import java.util.Objects;
import org.apache.logging.log4j.core.appender.rolling.action.Action;

public final class RolloverDescriptionImpl implements RolloverDescription {
   private final String activeFileName;
   private final boolean append;
   private final Action synchronous;
   private final Action asynchronous;

   public RolloverDescriptionImpl(String var1, boolean var2, Action var3, Action var4) {
      super();
      Objects.requireNonNull(var1, "activeFileName");
      this.append = var2;
      this.activeFileName = var1;
      this.synchronous = var3;
      this.asynchronous = var4;
   }

   public String getActiveFileName() {
      return this.activeFileName;
   }

   public boolean getAppend() {
      return this.append;
   }

   public Action getSynchronous() {
      return this.synchronous;
   }

   public Action getAsynchronous() {
      return this.asynchronous;
   }
}
