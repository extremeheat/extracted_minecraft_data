package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "IfLastModified",
   category = "Core",
   printObject = true
)
public final class IfLastModified implements PathCondition {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final Clock CLOCK = ClockFactory.getClock();
   private final Duration age;
   private final PathCondition[] nestedConditions;

   private IfLastModified(Duration var1, PathCondition[] var2) {
      super();
      this.age = (Duration)Objects.requireNonNull(var1, "age");
      this.nestedConditions = var2 == null ? new PathCondition[0] : (PathCondition[])Arrays.copyOf(var2, var2.length);
   }

   public Duration getAge() {
      return this.age;
   }

   public List<PathCondition> getNestedConditions() {
      return Collections.unmodifiableList(Arrays.asList(this.nestedConditions));
   }

   public boolean accept(Path var1, Path var2, BasicFileAttributes var3) {
      FileTime var4 = var3.lastModifiedTime();
      long var5 = var4.toMillis();
      long var7 = CLOCK.currentTimeMillis() - var5;
      boolean var9 = var7 >= this.age.toMillis();
      String var10 = var9 ? ">=" : "<";
      String var11 = var9 ? "ACCEPTED" : "REJECTED";
      LOGGER.trace((String)"IfLastModified {}: {} ageMillis '{}' {} '{}'", (Object)var11, var2, var7, var10, this.age);
      return var9 ? IfAll.accept(this.nestedConditions, var1, var2, var3) : var9;
   }

   public void beforeFileTreeWalk() {
      IfAll.beforeFileTreeWalk(this.nestedConditions);
   }

   @PluginFactory
   public static IfLastModified createAgeCondition(@PluginAttribute("age") Duration var0, @PluginElement("PathConditions") PathCondition... var1) {
      return new IfLastModified(var0, var1);
   }

   public String toString() {
      String var1 = this.nestedConditions.length == 0 ? "" : " AND " + Arrays.toString(this.nestedConditions);
      return "IfLastModified(age=" + this.age + var1 + ")";
   }
}
