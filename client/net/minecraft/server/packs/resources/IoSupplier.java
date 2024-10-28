package net.minecraft.server.packs.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@FunctionalInterface
public interface IoSupplier<T> {
   static IoSupplier<InputStream> create(Path var0) {
      return () -> {
         return Files.newInputStream(var0);
      };
   }

   static IoSupplier<InputStream> create(ZipFile var0, ZipEntry var1) {
      return () -> {
         return var0.getInputStream(var1);
      };
   }

   T get() throws IOException;
}
