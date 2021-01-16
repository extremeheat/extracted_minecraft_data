package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;

class DefaultFilterComponentBuilder extends DefaultComponentAndConfigurationBuilder<FilterComponentBuilder> implements FilterComponentBuilder {
   public DefaultFilterComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3, String var4) {
      super(var1, var2);
      this.addAttribute("onMatch", var3);
      this.addAttribute("onMisMatch", var4);
   }
}
