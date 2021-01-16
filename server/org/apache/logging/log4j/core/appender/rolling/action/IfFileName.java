package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
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
   name = "IfFileName",
   category = "Core",
   printObject = true
)
public final class IfFileName implements PathCondition {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final PathMatcher pathMatcher;
   private final String syntaxAndPattern;
   private final PathCondition[] nestedConditions;

   private IfFileName(String var1, String var2, PathCondition[] var3) {
      super();
      if (var2 == null && var1 == null) {
         throw new IllegalArgumentException("Specify either a path glob or a regular expression. Both cannot be null.");
      } else {
         this.syntaxAndPattern = createSyntaxAndPatternString(var1, var2);
         this.pathMatcher = FileSystems.getDefault().getPathMatcher(this.syntaxAndPattern);
         this.nestedConditions = var3 == null ? new PathCondition[0] : (PathCondition[])Arrays.copyOf(var3, var3.length);
      }
   }

   static String createSyntaxAndPatternString(String var0, String var1) {
      if (var0 != null) {
         return var0.startsWith("glob:") ? var0 : "glob:" + var0;
      } else {
         return var1.startsWith("regex:") ? var1 : "regex:" + var1;
      }
   }

   public String getSyntaxAndPattern() {
      return this.syntaxAndPattern;
   }

   public List<PathCondition> getNestedConditions() {
      return Collections.unmodifiableList(Arrays.asList(this.nestedConditions));
   }

   public boolean accept(Path var1, Path var2, BasicFileAttributes var3) {
      boolean var4 = this.pathMatcher.matches(var2);
      String var5 = var4 ? "matches" : "does not match";
      String var6 = var4 ? "ACCEPTED" : "REJECTED";
      LOGGER.trace((String)"IfFileName {}: '{}' {} relative path '{}'", (Object)var6, this.syntaxAndPattern, var5, var2);
      return var4 ? IfAll.accept(this.nestedConditions, var1, var2, var3) : var4;
   }

   public void beforeFileTreeWalk() {
      IfAll.beforeFileTreeWalk(this.nestedConditions);
   }

   @PluginFactory
   public static IfFileName createNameCondition(@PluginAttribute("glob") String var0, @PluginAttribute("regex") String var1, @PluginElement("PathConditions") PathCondition... var2) {
      return new IfFileName(var0, var1, var2);
   }

   public String toString() {
      String var1 = this.nestedConditions.length == 0 ? "" : " AND " + Arrays.toString(this.nestedConditions);
      return "IfFileName(" + this.syntaxAndPattern + var1 + ")";
   }
}
