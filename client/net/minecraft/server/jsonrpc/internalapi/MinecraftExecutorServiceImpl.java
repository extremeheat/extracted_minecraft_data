package net.minecraft.server.jsonrpc.internalapi;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.server.dedicated.DedicatedServer;

public class MinecraftExecutorServiceImpl implements MinecraftExecutorService {
   private final DedicatedServer server;

   public MinecraftExecutorServiceImpl(DedicatedServer var1) {
      super();
      this.server = var1;
   }

   public <V> CompletableFuture<V> submit(Supplier<V> var1) {
      return this.server.submit(var1);
   }

   public CompletableFuture<Void> submit(Runnable var1) {
      return this.server.submit(var1);
   }
}
