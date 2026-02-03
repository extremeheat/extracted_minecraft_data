package net.minecraft.util.filefix.access;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;

@FunctionalInterface
public interface FileRelation {
   Logger LOGGER = LogUtils.getLogger();
   FileRelation ORIGIN = List::of;
   FileRelation REGION = (basePath) -> List.of(basePath.resolve("region"));
   FileRelation DATA = (basePath) -> List.of(basePath.resolve("data"));
   FileRelation PLAYER_DATA = (basePath) -> List.of(basePath.resolve("players/data"));
   FileRelation DIMENSIONS = FileRelation::discoverDimensions;
   FileRelation DIMENSIONS_DATA = DIMENSIONS.resolve(DATA);
   FileRelation GENERATED_NAMESPACES = ORIGIN.resolve((basePath) -> directoriesInPath(basePath.resolve("generated")));
   FileRelation OLD_OVERWORLD = ORIGIN;
   FileRelation OLD_NETHER = ORIGIN.resolve((basePath) -> List.of(basePath.resolve("DIM-1")));
   FileRelation OLD_END = ORIGIN.resolve((basePath) -> List.of(basePath.resolve("DIM1")));

   List<Path> getPaths(final Path basePath);

   static FileRelation forDataFileInDimension(final String dimension, final String fileName) {
      return (basePath) -> List.of(basePath.resolve("dimensions/minecraft/" + dimension + "/data/" + fileName));
   }

   default FileRelation forFile(final String fileName) {
      return this.resolve((basePath) -> List.of(basePath.resolve(fileName)));
   }

   default FileRelation resolve(final FileRelation other) {
      return (basePath) -> this.getPaths(basePath).stream().flatMap((path) -> other.getPaths(path).stream()).toList();
   }

   default FileRelation join(final FileRelation... relations) {
      return (basePath) -> {
         Set<Path> paths = new HashSet();

         for(FileRelation relation : relations) {
            paths.addAll(relation.getPaths(basePath));
         }

         return List.copyOf(paths);
      };
   }

   private static List<Path> discoverDimensions(final Path basePath) {
      Path dimensionsRoot = basePath.resolve("dimensions");
      if (!Files.exists(dimensionsRoot, new LinkOption[0])) {
         return getDefaultDimensions(basePath);
      } else {
         try {
            Stream<Path> namespacePaths = Files.list(dimensionsRoot);

            List var4;
            try {
               List<Path> discoveredDimensions = namespacePaths.filter((x$0) -> Files.isDirectory(x$0, new LinkOption[0])).flatMap((path) -> directoriesInPath(path).stream()).toList();
               var4 = discoveredDimensions.isEmpty() ? getDefaultDimensions(basePath) : discoveredDimensions;
            } catch (Throwable var6) {
               if (namespacePaths != null) {
                  try {
                     namespacePaths.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (namespacePaths != null) {
               namespacePaths.close();
            }

            return var4;
         } catch (IOException e) {
            LOGGER.warn("Failed to discover dimensions, assuming default: {}", e.toString());
            return getDefaultDimensions(basePath);
         }
      }
   }

   static List<Path> directoriesInPath(final Path path) {
      try {
         Stream<Path> dimensionPaths = Files.list(path);

         List var2;
         try {
            var2 = dimensionPaths.filter((x$0) -> Files.isDirectory(x$0, new LinkOption[0])).toList();
         } catch (Throwable var5) {
            if (dimensionPaths != null) {
               try {
                  dimensionPaths.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (dimensionPaths != null) {
            dimensionPaths.close();
         }

         return var2;
      } catch (IOException var6) {
         return List.of();
      }
   }

   private static List<Path> getDefaultDimensions(final Path basePath) {
      return List.of(basePath.resolve("dimensions/minecraft/overworld"), basePath.resolve("dimensions/minecraft/the_nether"), basePath.resolve("dimensions/minecraft/the_end"));
   }
}
