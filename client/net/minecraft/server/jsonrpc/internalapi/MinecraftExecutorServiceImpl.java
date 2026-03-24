package net.minecraft.server.jsonrpc.internalapi;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.server.dedicated.DedicatedServer;

public class MinecraftExecutorServiceImpl implements MinecraftExecutorService {
   private final DedicatedServer server;

   public MinecraftExecutorServiceImpl(final DedicatedServer server) {
      super();
      this.server = server;
   }

   public <V> CompletableFuture<V> submit(final Supplier<V> supplier) {
      return this.server.submit(supplier);
   }

   public CompletableFuture<Void> submit(final Runnable runnable) {
      return this.server.submit(runnable);
   }
}
