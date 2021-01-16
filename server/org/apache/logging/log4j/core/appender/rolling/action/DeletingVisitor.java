package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public class DeletingVisitor extends SimpleFileVisitor<Path> {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Path basePath;
   private final boolean testMode;
   private final List<? extends PathCondition> pathConditions;

   public DeletingVisitor(Path var1, List<? extends PathCondition> var2, boolean var3) {
      super();
      this.testMode = var3;
      this.basePath = (Path)Objects.requireNonNull(var1, "basePath");
      this.pathConditions = (List)Objects.requireNonNull(var2, "pathConditions");
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         PathCondition var5 = (PathCondition)var4.next();
         var5.beforeFileTreeWalk();
      }

   }

   public FileVisitResult visitFile(Path var1, BasicFileAttributes var2) throws IOException {
      Iterator var3 = this.pathConditions.iterator();

      PathCondition var4;
      Path var5;
      do {
         if (!var3.hasNext()) {
            if (this.isTestMode()) {
               LOGGER.info((String)"Deleting {} (TEST MODE: file not actually deleted)", (Object)var1);
            } else {
               this.delete(var1);
            }

            return FileVisitResult.CONTINUE;
         }

         var4 = (PathCondition)var3.next();
         var5 = this.basePath.relativize(var1);
      } while(var4.accept(this.basePath, var5, var2));

      LOGGER.trace((String)"Not deleting base={}, relative={}", (Object)this.basePath, (Object)var5);
      return FileVisitResult.CONTINUE;
   }

   protected void delete(Path var1) throws IOException {
      LOGGER.trace((String)"Deleting {}", (Object)var1);
      Files.deleteIfExists(var1);
   }

   public boolean isTestMode() {
      return this.testMode;
   }
}
