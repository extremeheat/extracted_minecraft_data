package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;

class DefaultLoggerComponentBuilder extends DefaultComponentAndConfigurationBuilder<LoggerComponentBuilder> implements LoggerComponentBuilder {
   public DefaultLoggerComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3) {
      super(var1, var2, "Logger");
      this.addAttribute("level", var3);
   }

   public DefaultLoggerComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3, boolean var4) {
      super(var1, var2, "Logger");
      this.addAttribute("level", var3);
      this.addAttribute("includeLocation", var4);
   }

   public DefaultLoggerComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3, String var4) {
      super(var1, var2, var4);
      this.addAttribute("level", var3);
   }

   public DefaultLoggerComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3, String var4, boolean var5) {
      super(var1, var2, var4);
      this.addAttribute("level", var3);
      this.addAttribute("includeLocation", var5);
   }

   public LoggerComponentBuilder add(AppenderRefComponentBuilder var1) {
      return (LoggerComponentBuilder)this.addComponent(var1);
   }

   public LoggerComponentBuilder add(FilterComponentBuilder var1) {
      return (LoggerComponentBuilder)this.addComponent(var1);
   }
}
