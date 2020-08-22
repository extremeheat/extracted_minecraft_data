package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;

public interface ReloadInstance {
   CompletableFuture done();

   float getActualProgress();

   boolean isApplying();

   boolean isDone();

   void checkExceptions();
}
