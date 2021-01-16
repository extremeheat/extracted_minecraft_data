package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "IfAccumulatedFileCount",
   category = "Core",
   printObject = true
)
public final class IfAccumulatedFileCount implements PathCondition {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final int threshold;
   private int count;
   private final PathCondition[] nestedConditions;

   private IfAccumulatedFileCount(int var1, PathCondition[] var2) {
      super();
      if (var1 <= 0) {
         throw new IllegalArgumentException("Count must be a positive integer but was " + var1);
      } else {
         this.threshold = var1;
         this.nestedConditions = var2 == null ? new PathCondition[0] : (PathCondition[])Arrays.copyOf(var2, var2.length);
      }
   }

   public int getThresholdCount() {
      return this.threshold;
   }

   public List<PathCondition> getNestedConditions() {
      return Collections.unmodifiableList(Arrays.asList(this.nestedConditions));
   }

   public boolean accept(Path var1, Path var2, BasicFileAttributes var3) {
      boolean var4 = ++this.count > this.threshold;
      String var5 = var4 ? ">" : "<=";
      String var6 = var4 ? "ACCEPTED" : "REJECTED";
      LOGGER.trace((String)"IfAccumulatedFileCount {}: {} count '{}' {} threshold '{}'", (Object)var6, var2, this.count, var5, this.threshold);
      return var4 ? IfAll.accept(this.nestedConditions, var1, var2, var3) : var4;
   }

   public void beforeFileTreeWalk() {
      this.count = 0;
      IfAll.beforeFileTreeWalk(this.nestedConditions);
   }

   @PluginFactory
   public static IfAccumulatedFileCount createFileCountCondition(@PluginAttribute(value = "exceeds",defaultInt = 2147483647) int var0, @PluginElement("PathConditions") PathCondition... var1) {
      if (var0 == 2147483647) {
         LOGGER.error("IfAccumulatedFileCount invalid or missing threshold value.");
      }

      return new IfAccumulatedFileCount(var0, var1);
   }

   public String toString() {
      String var1 = this.nestedConditions.length == 0 ? "" : " AND " + Arrays.toString(this.nestedConditions);
      return "IfAccumulatedFileCount(exceeds=" + this.threshold + var1 + ")";
   }
}
