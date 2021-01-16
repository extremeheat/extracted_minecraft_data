package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "IfAny",
   category = "Core",
   printObject = true
)
public final class IfAny implements PathCondition {
   private final PathCondition[] components;

   private IfAny(PathCondition... var1) {
      super();
      this.components = (PathCondition[])Objects.requireNonNull(var1, "filters");
   }

   public PathCondition[] getDeleteFilters() {
      return this.components;
   }

   public boolean accept(Path var1, Path var2, BasicFileAttributes var3) {
      PathCondition[] var4 = this.components;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         PathCondition var7 = var4[var6];
         if (var7.accept(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   public void beforeFileTreeWalk() {
      PathCondition[] var1 = this.components;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         PathCondition var4 = var1[var3];
         var4.beforeFileTreeWalk();
      }

   }

   @PluginFactory
   public static IfAny createOrCondition(@PluginElement("PathConditions") PathCondition... var0) {
      return new IfAny(var0);
   }

   public String toString() {
      return "IfAny" + Arrays.toString(this.components);
   }
}
