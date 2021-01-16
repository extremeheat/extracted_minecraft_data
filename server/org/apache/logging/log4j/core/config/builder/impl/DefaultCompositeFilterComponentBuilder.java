package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.CompositeFilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;

class DefaultCompositeFilterComponentBuilder extends DefaultComponentAndConfigurationBuilder<CompositeFilterComponentBuilder> implements CompositeFilterComponentBuilder {
   public DefaultCompositeFilterComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3) {
      super(var1, "Filters");
      this.addAttribute("onMatch", var2);
      this.addAttribute("onMisMatch", var3);
   }

   public CompositeFilterComponentBuilder add(FilterComponentBuilder var1) {
      return (CompositeFilterComponentBuilder)this.addComponent(var1);
   }
}
