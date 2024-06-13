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
import org.slf4j.Logger;

public class DownloadCacheCleaner {
   private static final Logger LOGGER = LogUtils.getLogger();

   public DownloadCacheCleaner() {
      super();
   }

   public static void vacuumCacheDir(Path var0, int var1) {
      try {
         List var2 = listFilesWithModificationTimes(var0);
         int var3 = var2.size() - var1;
         if (var3 <= 0) {
            return;
         }

         var2.sort(DownloadCacheCleaner.PathAndTime.NEWEST_FIRST);
         List var4 = prioritizeFilesInDirs(var2);
         Collections.reverse(var4);
         var4.sort(DownloadCacheCleaner.PathAndPriority.HIGHEST_PRIORITY_FIRST);
         HashSet var5 = new HashSet();

         for (int var6 = 0; var6 < var3; var6++) {
            DownloadCacheCleaner.PathAndPriority var7 = (DownloadCacheCleaner.PathAndPriority)var4.get(var6);
            Path var8 = var7.path;

            try {
               Files.delete(var8);
               if (var7.removalPriority == 0) {
                  var5.add(var8.getParent());
               }
            } catch (IOException var12) {
               LOGGER.warn("Failed to delete cache file {}", var8, var12);
            }
         }

         var5.remove(var0);

         for (Path var15 : var5) {
            try {
               Files.delete(var15);
            } catch (DirectoryNotEmptyException var10) {
            } catch (IOException var11) {
               LOGGER.warn("Failed to delete empty(?) cache directory {}", var15, var11);
            }
         }
      } catch (UncheckedIOException | IOException var13) {
         LOGGER.error("Failed to vacuum cache dir {}", var0, var13);
      }
   }

   private static List<DownloadCacheCleaner.PathAndTime> listFilesWithModificationTimes(final Path var0) throws IOException {
      try {
         final ArrayList var1 = new ArrayList();
         Files.walkFileTree(var0, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path var1x, BasicFileAttributes var2) {
               if (var2.isRegularFile() && !var1x.getParent().equals(var0)) {
                  FileTime var3 = var2.lastModifiedTime();
                  var1.add(new DownloadCacheCleaner.PathAndTime(var1x, var3));
               }

               return FileVisitResult.CONTINUE;
            }
         });
         return var1;
      } catch (NoSuchFileException var2) {
         return List.of();
      }
   }

   private static List<DownloadCacheCleaner.PathAndPriority> prioritizeFilesInDirs(List<DownloadCacheCleaner.PathAndTime> var0) {
      ArrayList var1 = new ArrayList();
      Object2IntOpenHashMap var2 = new Object2IntOpenHashMap();

      for (DownloadCacheCleaner.PathAndTime var4 : var0) {
         int var5 = var2.addTo(var4.path.getParent(), 1);
         var1.add(new DownloadCacheCleaner.PathAndPriority(var4.path, var5));
      }

      return var1;
   }

   static record PathAndPriority(Path path, int removalPriority) {
      public static final Comparator<DownloadCacheCleaner.PathAndPriority> HIGHEST_PRIORITY_FIRST = Comparator.comparing(
            DownloadCacheCleaner.PathAndPriority::removalPriority
         )
         .reversed();

      PathAndPriority(Path path, int removalPriority) {
         super();
         this.path = path;
         this.removalPriority = removalPriority;
      }
   }

   static record PathAndTime(Path path, FileTime modifiedTime) {
      public static final Comparator<DownloadCacheCleaner.PathAndTime> NEWEST_FIRST = Comparator.comparing(DownloadCacheCleaner.PathAndTime::modifiedTime)
         .reversed();

      PathAndTime(Path path, FileTime modifiedTime) {
         super();
         this.path = path;
         this.modifiedTime = modifiedTime;
      }
   }
}
