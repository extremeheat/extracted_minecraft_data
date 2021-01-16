package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;

class Log4jJsonModule extends SimpleModule {
   private static final long serialVersionUID = 1L;
   private final boolean encodeThreadContextAsList;
   private final boolean includeStacktrace;

   Log4jJsonModule(boolean var1, boolean var2) {
      super(Log4jJsonModule.class.getName(), new Version(2, 0, 0, (String)null, (String)null, (String)null));
      this.encodeThreadContextAsList = var1;
      this.includeStacktrace = var2;
      (new Initializers.SimpleModuleInitializer()).initialize(this);
   }

   public void setupModule(SetupContext var1) {
      super.setupModule(var1);
      if (this.encodeThreadContextAsList) {
         (new Initializers.SetupContextInitializer()).setupModule(var1, this.includeStacktrace);
      } else {
         (new Initializers.SetupContextJsonInitializer()).setupModule(var1, this.includeStacktrace);
      }

   }
}
