package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.slf4j.Logger;

public class FileSystemUtil {
   private static final Logger LOGGER = LogUtils.getLogger();

   public FileSystemUtil() {
      super();
   }

   public static Path safeGetPath(URI var0) throws IOException {
      try {
         return Paths.get(var0);
      } catch (FileSystemNotFoundException var3) {
      } catch (Throwable var4) {
         LOGGER.warn("Unable to get path for: {}", var0, var4);
      }

      try {
         FileSystems.newFileSystem(var0, Collections.emptyMap());
      } catch (FileSystemAlreadyExistsException var2) {
      }

      return Paths.get(var0);
   }
}
