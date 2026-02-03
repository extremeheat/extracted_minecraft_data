package net.minecraft.world.level.validation;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ContentValidationException extends Exception {
   private final Path directory;
   private final List<ForbiddenSymlinkInfo> entries;

   public ContentValidationException(final Path directory, final List<ForbiddenSymlinkInfo> entries) {
      super();
      this.directory = directory;
      this.entries = entries;
   }

   public String getMessage() {
      return getMessage(this.directory, this.entries);
   }

   public static String getMessage(final Path directory, final List<ForbiddenSymlinkInfo> entries) {
      String var10000 = String.valueOf(directory);
      return "Failed to validate '" + var10000 + "'. Found forbidden symlinks: " + (String)entries.stream().map((e) -> {
         String var10000 = String.valueOf(e.link());
         return var10000 + "->" + String.valueOf(e.target());
      }).collect(Collectors.joining(", "));
   }
}
