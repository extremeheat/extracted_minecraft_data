package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import org.slf4j.Logger;

public class FileZipper implements Closeable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path outputFile;
   private final Path tempFile;
   private final FileSystem fs;

   public FileZipper(Path var1) {
      super();
      this.outputFile = var1;
      this.tempFile = var1.resolveSibling(var1.getFileName().toString() + "_tmp");

      try {
         this.fs = Util.ZIP_FILE_SYSTEM_PROVIDER.newFileSystem(this.tempFile, ImmutableMap.of("create", "true"));
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public void add(Path var1, String var2) {
      try {
         Path var3 = this.fs.getPath(File.separator);
         Path var4 = var3.resolve(var1.toString());
         Files.createDirectories(var4.getParent());
         Files.write(var4, var2.getBytes(StandardCharsets.UTF_8));
      } catch (IOException var5) {
         throw new UncheckedIOException(var5);
      }
   }

   public void add(Path var1, File var2) {
      try {
         Path var3 = this.fs.getPath(File.separator);
         Path var4 = var3.resolve(var1.toString());
         Files.createDirectories(var4.getParent());
         Files.copy(var2.toPath(), var4);
      } catch (IOException var5) {
         throw new UncheckedIOException(var5);
      }
   }

   public void add(Path var1) {
      try {
         Path var2 = this.fs.getPath(File.separator);
         if (Files.isRegularFile(var1)) {
            Path var10 = var2.resolve(var1.getParent().relativize(var1).toString());
            Files.copy(var10, var1);
         } else {
            try (Stream var3 = Files.find(var1, 2147483647, (var0, var1x) -> var1x.isRegularFile())) {
               for(Path var5 : (List)var3.collect(Collectors.toList())) {
                  Path var6 = var2.resolve(var1.relativize(var5).toString());
                  Files.createDirectories(var6.getParent());
                  Files.copy(var5, var6);
               }
            }
         }
      } catch (IOException var9) {
         throw new UncheckedIOException(var9);
      }
   }

   @Override
   public void close() {
      try {
         this.fs.close();
         Files.move(this.tempFile, this.outputFile);
         LOGGER.info("Compressed to {}", this.outputFile);
      } catch (IOException var2) {
         throw new UncheckedIOException(var2);
      }
   }
}
