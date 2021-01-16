package org.apache.logging.log4j.core.layout;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(
   name = "PatternMatch",
   category = "Core",
   printObject = true
)
public final class PatternMatch {
   private final String key;
   private final String pattern;

   public PatternMatch(String var1, String var2) {
      super();
      this.key = var1;
      this.pattern = var2;
   }

   public String getKey() {
      return this.key;
   }

   public String getPattern() {
      return this.pattern;
   }

   public String toString() {
      return this.key + '=' + this.pattern;
   }

   @PluginBuilderFactory
   public static PatternMatch.Builder newBuilder() {
      return new PatternMatch.Builder();
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.key == null ? 0 : this.key.hashCode());
      var3 = 31 * var3 + (this.pattern == null ? 0 : this.pattern.hashCode());
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
         PatternMatch var2 = (PatternMatch)var1;
         if (this.key == null) {
            if (var2.key != null) {
               return false;
            }
         } else if (!this.key.equals(var2.key)) {
            return false;
         }

         if (this.pattern == null) {
            if (var2.pattern != null) {
               return false;
            }
         } else if (!this.pattern.equals(var2.pattern)) {
            return false;
         }

         return true;
      }
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<PatternMatch>, Serializable {
      private static final long serialVersionUID = 1L;
      @PluginBuilderAttribute
      private String key;
      @PluginBuilderAttribute
      private String pattern;

      public Builder() {
         super();
      }

      public PatternMatch.Builder setKey(String var1) {
         this.key = var1;
         return this;
      }

      public PatternMatch.Builder setPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public PatternMatch build() {
         return new PatternMatch(this.key, this.pattern);
      }

      protected Object readResolve() throws ObjectStreamException {
         return new PatternMatch(this.key, this.pattern);
      }
   }
}
