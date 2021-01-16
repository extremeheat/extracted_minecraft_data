package org.apache.logging.log4j.core.script;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractScript {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected static final String DEFAULT_LANGUAGE = "JavaScript";
   private final String language;
   private final String scriptText;
   private final String name;

   public AbstractScript(String var1, String var2, String var3) {
      super();
      this.language = var2;
      this.scriptText = var3;
      this.name = var1 == null ? this.toString() : var1;
   }

   public String getLanguage() {
      return this.language;
   }

   public String getScriptText() {
      return this.scriptText;
   }

   public String getName() {
      return this.name;
   }
}
