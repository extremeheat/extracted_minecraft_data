package org.apache.logging.log4j.core.config.builder.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;

class DefaultComponentBuilder<T extends ComponentBuilder<T>, CB extends ConfigurationBuilder<? extends Configuration>> implements ComponentBuilder<T> {
   private final CB builder;
   private final String type;
   private final Map<String, String> attributes;
   private final List<Component> components;
   private final String name;
   private final String value;

   public DefaultComponentBuilder(CB var1, String var2) {
      this(var1, (String)null, var2, (String)null);
   }

   public DefaultComponentBuilder(CB var1, String var2, String var3) {
      this(var1, var2, var3, (String)null);
   }

   public DefaultComponentBuilder(CB var1, String var2, String var3, String var4) {
      super();
      this.attributes = new LinkedHashMap();
      this.components = new ArrayList();
      this.type = var3;
      this.builder = var1;
      this.name = var2;
      this.value = var4;
   }

   public T addAttribute(String var1, boolean var2) {
      return this.put(var1, Boolean.toString(var2));
   }

   public T addAttribute(String var1, Enum<?> var2) {
      return this.put(var1, var2.name());
   }

   public T addAttribute(String var1, int var2) {
      return this.put(var1, Integer.toString(var2));
   }

   public T addAttribute(String var1, Level var2) {
      return this.put(var1, var2.toString());
   }

   public T addAttribute(String var1, Object var2) {
      return this.put(var1, var2.toString());
   }

   public T addAttribute(String var1, String var2) {
      return this.put(var1, var2);
   }

   public T addComponent(ComponentBuilder<?> var1) {
      this.components.add(var1.build());
      return this;
   }

   public Component build() {
      Component var1 = new Component(this.type, this.name, this.value);
      var1.getAttributes().putAll(this.attributes);
      var1.getComponents().addAll(this.components);
      return var1;
   }

   public CB getBuilder() {
      return this.builder;
   }

   public String getName() {
      return this.name;
   }

   protected T put(String var1, String var2) {
      this.attributes.put(var1, var2);
      return this;
   }
}
