package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SortingVisitor extends SimpleFileVisitor<Path> {
   private final PathSorter sorter;
   private final List<PathWithAttributes> collected = new ArrayList();

   public SortingVisitor(PathSorter var1) {
      super();
      this.sorter = (PathSorter)Objects.requireNonNull(var1, "sorter");
   }

   public FileVisitResult visitFile(Path var1, BasicFileAttributes var2) throws IOException {
      this.collected.add(new PathWithAttributes(var1, var2));
      return FileVisitResult.CONTINUE;
   }

   public List<PathWithAttributes> getSortedPaths() {
      Collections.sort(this.collected, this.sorter);
      return this.collected;
   }
}
