package org.apache.logging.log4j.core.config.plugins.processor;

import java.io.Serializable;

public class PluginEntry implements Serializable {
   private static final long serialVersionUID = 1L;
   private String key;
   private String className;
   private String name;
   private boolean printable;
   private boolean defer;
   private transient String category;

   public PluginEntry() {
      super();
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String var1) {
      this.key = var1;
   }

   public String getClassName() {
      return this.className;
   }

   public void setClassName(String var1) {
      this.className = var1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public boolean isPrintable() {
      return this.printable;
   }

   public void setPrintable(boolean var1) {
      this.printable = var1;
   }

   public boolean isDefer() {
      return this.defer;
   }

   public void setDefer(boolean var1) {
      this.defer = var1;
   }

   public String getCategory() {
      return this.category;
   }

   public void setCategory(String var1) {
      this.category = var1;
   }

   public String toString() {
      return "PluginEntry [key=" + this.key + ", className=" + this.className + ", name=" + this.name + ", printable=" + this.printable + ", defer=" + this.defer + ", category=" + this.category + "]";
   }
}
