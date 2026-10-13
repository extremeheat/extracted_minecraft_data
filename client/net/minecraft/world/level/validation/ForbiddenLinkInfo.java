package net.minecraft.world.level.validation;

import java.nio.file.Path;

public record ForbiddenLinkInfo(Path link, Path target, boolean isJuction) {
   public ForbiddenLinkInfo(final Path link, final Path target) {
      this(link, target, false);
   }

   public ForbiddenLinkInfo {
      super();
   }
}
