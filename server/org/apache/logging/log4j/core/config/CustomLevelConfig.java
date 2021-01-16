package org.apache.logging.log4j.core.config;

import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "CustomLevel",
   category = "Core",
   printObject = true
)
public final class CustomLevelConfig {
   private final String levelName;
   private final int intLevel;

   private CustomLevelConfig(String var1, int var2) {
      super();
      this.levelName = (String)Objects.requireNonNull(var1, "levelName is null");
      this.intLevel = var2;
   }

   @PluginFactory
   public static CustomLevelConfig createLevel(@PluginAttribute("name") String var0, @PluginAttribute("intLevel") int var1) {
      StatusLogger.getLogger().debug("Creating CustomLevel(name='{}', intValue={})", var0, var1);
      Level.forName(var0, var1);
      return new CustomLevelConfig(var0, var1);
   }

   public String getLevelName() {
      return this.levelName;
   }

   public int getIntLevel() {
      return this.intLevel;
   }

   public int hashCode() {
      return this.intLevel ^ this.levelName.hashCode();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CustomLevelConfig)) {
         return false;
      } else {
         CustomLevelConfig var2 = (CustomLevelConfig)var1;
         return this.intLevel == var2.intLevel && this.levelName.equals(var2.levelName);
      }
   }

   public String toString() {
      return "CustomLevel[name=" + this.levelName + ", intLevel=" + this.intLevel + "]";
   }
}
