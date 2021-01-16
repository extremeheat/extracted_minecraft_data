package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "IfNot",
   category = "Core",
   printObject = true
)
public final class IfNot implements PathCondition {
   private final PathCondition negate;

   private IfNot(PathCondition var1) {
      super();
      this.negate = (PathCondition)Objects.requireNonNull(var1, "filter");
   }

   public PathCondition getWrappedFilter() {
      return this.negate;
   }

   public boolean accept(Path var1, Path var2, BasicFileAttributes var3) {
      return !this.negate.accept(var1, var2, var3);
   }

   public void beforeFileTreeWalk() {
      this.negate.beforeFileTreeWalk();
   }

   @PluginFactory
   public static IfNot createNotCondition(@PluginElement("PathConditions") PathCondition var0) {
      return new IfNot(var0);
   }

   public String toString() {
      return "IfNot(" + this.negate + ")";
   }
}
