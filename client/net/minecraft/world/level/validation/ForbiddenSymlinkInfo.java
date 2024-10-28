package net.minecraft.world.level.validation;

import java.nio.file.Path;

public record ForbiddenSymlinkInfo(Path link, Path target) {
   public ForbiddenSymlinkInfo(Path link, Path target) {
      super();
      this.link = link;
      this.target = target;
   }

   public Path link() {
      return this.link;
   }

   public Path target() {
      return this.target;
   }
}
