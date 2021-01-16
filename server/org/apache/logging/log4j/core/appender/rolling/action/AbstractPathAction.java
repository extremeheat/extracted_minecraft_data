package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

public abstract class AbstractPathAction extends AbstractAction {
   private final String basePathString;
   private final Set<FileVisitOption> options;
   private final int maxDepth;
   private final List<PathCondition> pathConditions;
   private final StrSubstitutor subst;

   protected AbstractPathAction(String var1, boolean var2, int var3, PathCondition[] var4, StrSubstitutor var5) {
      super();
      this.basePathString = var1;
      this.options = (Set)(var2 ? EnumSet.of(FileVisitOption.FOLLOW_LINKS) : Collections.emptySet());
      this.maxDepth = var3;
      this.pathConditions = Arrays.asList(Arrays.copyOf(var4, var4.length));
      this.subst = var5;
   }

   public boolean execute() throws IOException {
      return this.execute(this.createFileVisitor(this.getBasePath(), this.pathConditions));
   }

   public boolean execute(FileVisitor<Path> var1) throws IOException {
      long var2 = System.nanoTime();
      LOGGER.debug((String)"Starting {}", (Object)this);
      Files.walkFileTree(this.getBasePath(), this.options, this.maxDepth, var1);
      double var4 = (double)(System.nanoTime() - var2);
      LOGGER.debug((String)"{} complete in {} seconds", (Object)this.getClass().getSimpleName(), (Object)(var4 / (double)TimeUnit.SECONDS.toNanos(1L)));
      return true;
   }

   protected abstract FileVisitor<Path> createFileVisitor(Path var1, List<PathCondition> var2);

   public Path getBasePath() {
      return Paths.get(this.subst.replace(this.getBasePathString()));
   }

   public String getBasePathString() {
      return this.basePathString;
   }

   public StrSubstitutor getStrSubstitutor() {
      return this.subst;
   }

   public Set<FileVisitOption> getOptions() {
      return Collections.unmodifiableSet(this.options);
   }

   public boolean isFollowSymbolicLinks() {
      return this.options.contains(FileVisitOption.FOLLOW_LINKS);
   }

   public int getMaxDepth() {
      return this.maxDepth;
   }

   public List<PathCondition> getPathConditions() {
      return Collections.unmodifiableList(this.pathConditions);
   }

   public String toString() {
      return this.getClass().getSimpleName() + "[basePath=" + this.getBasePath() + ", options=" + this.options + ", maxDepth=" + this.maxDepth + ", conditions=" + this.pathConditions + "]";
   }
}
