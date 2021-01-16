package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;

final class Log4jXmlModule extends JacksonXmlModule {
   private static final long serialVersionUID = 1L;
   private final boolean includeStacktrace;

   Log4jXmlModule(boolean var1) {
      super();
      this.includeStacktrace = var1;
      (new Initializers.SimpleModuleInitializer()).initialize(this);
   }

   public void setupModule(SetupContext var1) {
      super.setupModule(var1);
      (new Initializers.SetupContextInitializer()).setupModule(var1, this.includeStacktrace);
   }
}
