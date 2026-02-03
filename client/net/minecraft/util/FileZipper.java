package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;

public class FileZipper implements Closeable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path outputFile;
   private final Path tempFile;
   private final FileSystem fs;

   public FileZipper(final Path outputFile) {
      super();
      this.outputFile = outputFile;
      this.tempFile = outputFile.resolveSibling(outputFile.getFileName().toString() + "_tmp");

      try {
         this.fs = Util.ZIP_FILE_SYSTEM_PROVIDER.newFileSystem(this.tempFile, ImmutableMap.of("create", "true"));
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public void add(final Path destinationRelativePath, final String content) {
      try {
         Path root = this.fs.getPath(File.separator);
         Path path = root.resolve(destinationRelativePath.toString());
         Files.createDirectories(path.getParent());
         Files.write(path, content.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public void add(final Path destinationRelativePath, final File file) {
      try {
         Path root = this.fs.getPath(File.separator);
         Path path = root.resolve(destinationRelativePath.toString());
         Files.createDirectories(path.getParent());
         Files.copy(file.toPath(), path);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public void add(final Path path) {
      try {
         Path root = this.fs.getPath(File.separator);
         if (Files.isRegularFile(path, new LinkOption[0])) {
            Path targetFile = root.resolve(path.getParent().relativize(path).toString());
            Files.copy(targetFile, path);
         } else {
            Stream<Path> sourceFiles = Files.find(path, 2147483647, (p, a) -> a.isRegularFile(), new FileVisitOption[0]);

            try {
               for(Path sourceFile : (List)sourceFiles.collect(Collectors.toList())) {
                  Path targetFile = root.resolve(path.relativize(sourceFile).toString());
                  Files.createDirectories(targetFile.getParent());
                  Files.copy(sourceFile, targetFile);
               }
            } catch (Throwable var8) {
               if (sourceFiles != null) {
                  try {
                     sourceFiles.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (sourceFiles != null) {
               sourceFiles.close();
            }

         }
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public void close() {
      try {
         this.fs.close();
         Files.move(this.tempFile, this.outputFile);
         LOGGER.info("Compressed to {}", this.outputFile);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }
}
