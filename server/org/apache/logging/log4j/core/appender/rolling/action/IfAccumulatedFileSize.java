package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.FileSize;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "IfAccumulatedFileSize",
   category = "Core",
   printObject = true
)
public final class IfAccumulatedFileSize implements PathCondition {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final long thresholdBytes;
   private long accumulatedSize;
   private final PathCondition[] nestedConditions;

   private IfAccumulatedFileSize(long var1, PathCondition[] var3) {
      super();
      if (var1 <= 0L) {
         throw new IllegalArgumentException("Count must be a positive integer but was " + var1);
      } else {
         this.thresholdBytes = var1;
         this.nestedConditions = var3 == null ? new PathCondition[0] : (PathCondition[])Arrays.copyOf(var3, var3.length);
      }
   }

   public long getThresholdBytes() {
      return this.thresholdBytes;
   }

   public List<PathCondition> getNestedConditions() {
      return Collections.unmodifiableList(Arrays.asList(this.nestedConditions));
   }

   public boolean accept(Path var1, Path var2, BasicFileAttributes var3) {
      this.accumulatedSize += var3.size();
      boolean var4 = this.accumulatedSize > this.thresholdBytes;
      String var5 = var4 ? ">" : "<=";
      String var6 = var4 ? "ACCEPTED" : "REJECTED";
      LOGGER.trace((String)"IfAccumulatedFileSize {}: {} accumulated size '{}' {} thresholdBytes '{}'", (Object)var6, var2, this.accumulatedSize, var5, this.thresholdBytes);
      return var4 ? IfAll.accept(this.nestedConditions, var1, var2, var3) : var4;
   }

   public void beforeFileTreeWalk() {
      this.accumulatedSize = 0L;
      IfAll.beforeFileTreeWalk(this.nestedConditions);
   }

   @PluginFactory
   public static IfAccumulatedFileSize createFileSizeCondition(@PluginAttribute("exceeds") String var0, @PluginElement("PathConditions") PathCondition... var1) {
      if (var0 == null) {
         LOGGER.error("IfAccumulatedFileSize missing mandatory size threshold.");
      }

      long var2 = var0 == null ? 9223372036854775807L : FileSize.parse(var0, 9223372036854775807L);
      return new IfAccumulatedFileSize(var2, var1);
   }

   public String toString() {
      String var1 = this.nestedConditions.length == 0 ? "" : " AND " + Arrays.toString(this.nestedConditions);
      return "IfAccumulatedFileSize(exceeds=" + this.thresholdBytes + var1 + ")";
   }
}
