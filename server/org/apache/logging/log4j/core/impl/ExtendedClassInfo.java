package org.apache.logging.log4j.core.impl;

import java.io.Serializable;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.TextRenderer;

public final class ExtendedClassInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   private final boolean exact;
   private final String location;
   private final String version;

   public ExtendedClassInfo(boolean var1, String var2, String var3) {
      super();
      this.exact = var1;
      this.location = var2;
      this.version = var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ExtendedClassInfo)) {
         return false;
      } else {
         ExtendedClassInfo var2 = (ExtendedClassInfo)var1;
         if (this.exact != var2.exact) {
            return false;
         } else {
            if (this.location == null) {
               if (var2.location != null) {
                  return false;
               }
            } else if (!this.location.equals(var2.location)) {
               return false;
            }

            if (this.version == null) {
               if (var2.version != null) {
                  return false;
               }
            } else if (!this.version.equals(var2.version)) {
               return false;
            }

            return true;
         }
      }
   }

   public boolean getExact() {
      return this.exact;
   }

   public String getLocation() {
      return this.location;
   }

   public String getVersion() {
      return this.version;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var3 = 31 * var2 + (this.exact ? 1231 : 1237);
      var3 = 31 * var3 + (this.location == null ? 0 : this.location.hashCode());
      var3 = 31 * var3 + (this.version == null ? 0 : this.version.hashCode());
      return var3;
   }

   public void renderOn(StringBuilder var1, TextRenderer var2) {
      if (!this.exact) {
         var2.render("~", var1, "ExtraClassInfo.Inexact");
      }

      var2.render("[", var1, "ExtraClassInfo.Container");
      var2.render(this.location, var1, "ExtraClassInfo.Location");
      var2.render(":", var1, "ExtraClassInfo.ContainerSeparator");
      var2.render(this.version, var1, "ExtraClassInfo.Version");
      var2.render("]", var1, "ExtraClassInfo.Container");
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.renderOn(var1, PlainTextRenderer.getInstance());
      return var1.toString();
   }
}
