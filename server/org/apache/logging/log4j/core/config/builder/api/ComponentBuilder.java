package org.apache.logging.log4j.core.config.builder.api;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.Builder;

public interface ComponentBuilder<T extends ComponentBuilder<T>> extends Builder<Component> {
   T addAttribute(String var1, String var2);

   T addAttribute(String var1, Level var2);

   T addAttribute(String var1, Enum<?> var2);

   T addAttribute(String var1, int var2);

   T addAttribute(String var1, boolean var2);

   T addAttribute(String var1, Object var2);

   T addComponent(ComponentBuilder<?> var1);

   String getName();

   ConfigurationBuilder<? extends Configuration> getBuilder();
}
