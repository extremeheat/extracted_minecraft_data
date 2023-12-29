package net.minecraft.client.resources.server;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface PackReloadConfig {
   void scheduleReload(PackReloadConfig.Callbacks var1);

   public interface Callbacks {
      void onSuccess();

      void onFailure(boolean var1);

      List<PackReloadConfig.IdAndPath> packsToLoad();
   }

   public static record IdAndPath(UUID a, Path b) {
      private final UUID id;
      private final Path path;

      public IdAndPath(UUID var1, Path var2) {
         super();
         this.id = var1;
         this.path = var2;
      }
   }
}
