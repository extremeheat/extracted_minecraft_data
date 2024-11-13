package net.minecraft.client.resources.server;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface PackReloadConfig {
   void scheduleReload(Callbacks var1);

   public static record IdAndPath(UUID id, Path path) {
      public IdAndPath(UUID var1, Path var2) {
         super();
         this.id = var1;
         this.path = var2;
      }
   }

   public interface Callbacks {
      void onSuccess();

      void onFailure(boolean var1);

      List<IdAndPath> packsToLoad();
   }
}
