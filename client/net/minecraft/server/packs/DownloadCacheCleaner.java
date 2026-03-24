package net.minecraft.server.packs;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;

public class DownloadCacheCleaner {
   private static final Logger LOGGER = LogUtils.getLogger();

   public DownloadCacheCleaner() {
      super();
   }

   public static void vacuumCacheDir(final Path cacheDir, final int maxFiles) {
      try {
         List<PathAndTime> filesAndDates = listFilesWithModificationTimes(cacheDir);
         int toRemove = filesAndDates.size() - maxFiles;
         if (toRemove <= 0) {
            return;
         }

         filesAndDates.sort(DownloadCacheCleaner.PathAndTime.NEWEST_FIRST);
         List<PathAndPriority> filesWithDirOrder = prioritizeFilesInDirs(filesAndDates);
         Collections.reverse(filesWithDirOrder);
         filesWithDirOrder.sort(DownloadCacheCleaner.PathAndPriority.HIGHEST_PRIORITY_FIRST);
         Set<Path> emptyDirectoryCandidates = new HashSet();

         for(int i = 0; i < toRemove; ++i) {
            PathAndPriority entry = (PathAndPriority)filesWithDirOrder.get(i);
            Path pathToRemove = entry.path;

            try {
               Files.delete(pathToRemove);
               if (entry.removalPriority == 0) {
                  emptyDirectoryCandidates.add(pathToRemove.getParent());
               }
            } catch (IOException e) {
               LOGGER.warn("Failed to delete cache file {}", pathToRemove, e);
            }
         }

         emptyDirectoryCandidates.remove(cacheDir);

         for(Path dir : emptyDirectoryCandidates) {
            try {
               Files.delete(dir);
            } catch (DirectoryNotEmptyException var10) {
            } catch (IOException e) {
               LOGGER.warn("Failed to delete empty(?) cache directory {}", dir, e);
            }
         }
      } catch (UncheckedIOException | IOException e) {
         LOGGER.error("Failed to vacuum cache dir {}", cacheDir, e);
      }

   }

   private static List<PathAndTime> listFilesWithModificationTimes(final Path cacheDir) throws IOException {
      try {
         final List<PathAndTime> unsortedFiles = new ArrayList();
         Files.walkFileTree(cacheDir, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
               if (attrs.isRegularFile() && !file.getParent().equals(cacheDir)) {
                  FileTime fileTime = attrs.lastModifiedTime();
                  unsortedFiles.add(new PathAndTime(file, fileTime));
               }

               return FileVisitResult.CONTINUE;
            }
         });
         return unsortedFiles;
      } catch (NoSuchFileException var2) {
         return List.of();
      }
   }

   private static List<PathAndPriority> prioritizeFilesInDirs(final List<PathAndTime> filesAndDates) {
      List<PathAndPriority> result = new ArrayList();
      Object2IntOpenHashMap<Path> parentCounts = new Object2IntOpenHashMap();

      for(PathAndTime entry : filesAndDates) {
         int removalPriority = parentCounts.addTo(entry.path.getParent(), 1);
         result.add(new PathAndPriority(entry.path, removalPriority));
      }

      return result;
   }

   private static record PathAndTime(Path path, FileTime modifiedTime) {
      public static final Comparator<PathAndTime> NEWEST_FIRST = Comparator.comparing(PathAndTime::modifiedTime).reversed();

      private PathAndTime {
         super();
      }
   }

   private static record PathAndPriority(Path path, int removalPriority) {
      public static final Comparator<PathAndPriority> HIGHEST_PRIORITY_FIRST = Comparator.comparing(PathAndPriority::removalPriority).reversed();

      private PathAndPriority {
         super();
      }
   }
}
