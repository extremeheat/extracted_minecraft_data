package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.Serializable;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "SortByModificationTime",
   category = "Core",
   printObject = true
)
public class PathSortByModificationTime implements PathSorter, Serializable {
   private static final long serialVersionUID = 1L;
   private final boolean recentFirst;
   private final int multiplier;

   public PathSortByModificationTime(boolean var1) {
      super();
      this.recentFirst = var1;
      this.multiplier = var1 ? 1 : -1;
   }

   @PluginFactory
   public static PathSorter createSorter(@PluginAttribute(value = "recentFirst",defaultBoolean = true) boolean var0) {
      return new PathSortByModificationTime(var0);
   }

   public boolean isRecentFirst() {
      return this.recentFirst;
   }

   public int compare(PathWithAttributes var1, PathWithAttributes var2) {
      long var3 = var1.getAttributes().lastModifiedTime().toMillis();
      long var5 = var2.getAttributes().lastModifiedTime().toMillis();
      int var7 = Long.signum(var5 - var3);
      if (var7 == 0) {
         try {
            var7 = var2.getPath().compareTo(var1.getPath());
         } catch (ClassCastException var9) {
            var7 = var2.getPath().toString().compareTo(var1.getPath().toString());
         }
      }

      return this.multiplier * var7;
   }
}
