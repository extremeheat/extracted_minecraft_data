package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(
   name = "KeyValuePair",
   category = "Core",
   printObject = true
)
public final class KeyValuePair {
   private final String key;
   private final String value;

   public KeyValuePair(String var1, String var2) {
      super();
      this.key = var1;
      this.value = var2;
   }

   public String getKey() {
      return this.key;
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      return this.key + '=' + this.value;
   }

   @PluginBuilderFactory
   public static KeyValuePair.Builder newBuilder() {
      return new KeyValuePair.Builder();
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.key == null ? 0 : this.key.hashCode());
      var3 = 31 * var3 + (this.value == null ? 0 : this.value.hashCode());
      return var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         KeyValuePair var2 = (KeyValuePair)var1;
         if (this.key == null) {
            if (var2.key != null) {
               return false;
            }
         } else if (!this.key.equals(var2.key)) {
            return false;
         }

         if (this.value == null) {
            if (var2.value != null) {
               return false;
            }
         } else if (!this.value.equals(var2.value)) {
            return false;
         }

         return true;
      }
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<KeyValuePair> {
      @PluginBuilderAttribute
      private String key;
      @PluginBuilderAttribute
      private String value;

      public Builder() {
         super();
      }

      public KeyValuePair.Builder setKey(String var1) {
         this.key = var1;
         return this;
      }

      public KeyValuePair.Builder setValue(String var1) {
         this.value = var1;
         return this;
      }

      public KeyValuePair build() {
         return new KeyValuePair(this.key, this.value);
      }
   }
}
