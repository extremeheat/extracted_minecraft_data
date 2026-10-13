package net.minecraft.world.level.validation;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ContentValidationException extends Exception {
   private final Path path;
   private final List<ForbiddenLinkInfo> entries;

   public ContentValidationException(final Path path, final List<ForbiddenLinkInfo> entries) {
      super();
      this.path = path;
      this.entries = entries;
   }

   public String getMessage() {
      String var10000 = String.valueOf(this.path);
      return "Failed to validate '" + var10000 + "'. Found forbidden symlinks: " + (String)this.entries.stream().map((e) -> {
         String var10000 = String.valueOf(e.link());
         return var10000 + "->" + String.valueOf(e.target()) + (e.isJuction() ? " (Allowing Junctions is not supported)" : "");
      }).collect(Collectors.joining(", "));
   }

   public List<ForbiddenLinkInfo> getIssues() {
      return this.entries;
   }
}
