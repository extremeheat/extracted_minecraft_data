package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;

class DefaultRootLoggerComponentBuilder extends DefaultComponentAndConfigurationBuilder<RootLoggerComponentBuilder> implements RootLoggerComponentBuilder {
   public DefaultRootLoggerComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2) {
      super(var1, "", "Root");
      this.addAttribute("level", var2);
   }

   public DefaultRootLoggerComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, boolean var3) {
      super(var1, "", "Root");
      this.addAttribute("level", var2);
      this.addAttribute("includeLocation", var3);
   }

   public DefaultRootLoggerComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3) {
      super(var1, "", var3);
      this.addAttribute("level", var2);
   }

   public DefaultRootLoggerComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3, boolean var4) {
      super(var1, "", var3);
      this.addAttribute("level", var2);
      this.addAttribute("includeLocation", var4);
   }

   public RootLoggerComponentBuilder add(AppenderRefComponentBuilder var1) {
      return (RootLoggerComponentBuilder)this.addComponent(var1);
   }

   public RootLoggerComponentBuilder add(FilterComponentBuilder var1) {
      return (RootLoggerComponentBuilder)this.addComponent(var1);
   }
}
