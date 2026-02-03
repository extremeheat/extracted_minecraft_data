package net.minecraft.client.resources.server;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface PackReloadConfig {
   void scheduleReload(Callbacks callbacks);

   public static record IdAndPath(UUID id, Path path) {
      public IdAndPath {
         super();
      }
   }

   public interface Callbacks {
      void onSuccess();

      void onFailure(boolean isRecovery);

      List<IdAndPath> packsToLoad();
   }
}
