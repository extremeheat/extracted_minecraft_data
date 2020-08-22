package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Thread thread;
   private final AtomicBoolean shutdownRequested = new AtomicBoolean();
   private final Queue inbox = Queues.newConcurrentLinkedQueue();
   private final RegionFileStorage storage;
   private final Map pendingWrites = Maps.newLinkedHashMap();
   private boolean running = true;
   private CompletableFuture shutdownListener = new CompletableFuture();

   IOWorker(RegionFileStorage var1, String var2) {
      this.storage = var1;
      this.thread = new Thread(this::loop);
      this.thread.setName(var2 + " IO worker");
      this.thread.start();
   }

   public CompletableFuture store(ChunkPos var1, CompoundTag var2) {
      return this.submitTask((var3) -> {
         return () -> {
            IOWorker.PendingStore var4 = (IOWorker.PendingStore)this.pendingWrites.computeIfAbsent(var1, (var0) -> {
               return new IOWorker.PendingStore();
            });
            var4.data = var2;
            var4.result.whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  var3.completeExceptionally(var2x);
               } else {
                  var3.complete((Object)null);
               }

            });
         };
      });
   }

   @Nullable
   public CompoundTag load(ChunkPos var1) throws IOException {
      CompletableFuture var2 = this.submitTask((var2x) -> {
         return () -> {
            IOWorker.PendingStore var3 = (IOWorker.PendingStore)this.pendingWrites.get(var1);
            if (var3 != null) {
               var2x.complete(var3.data);
            } else {
               try {
                  CompoundTag var4 = this.storage.read(var1);
                  var2x.complete(var4);
               } catch (Exception var5) {
                  LOGGER.warn("Failed to read chunk {}", var1, var5);
                  var2x.completeExceptionally(var5);
               }
            }

         };
      });

      try {
         return (CompoundTag)var2.join();
      } catch (CompletionException var4) {
         if (var4.getCause() instanceof IOException) {
            throw (IOException)var4.getCause();
         } else {
            throw var4;
         }
      }
   }

   private CompletableFuture shutdown() {
      return this.submitTask((var1) -> {
         return () -> {
            this.running = false;
            this.shutdownListener = var1;
         };
      });
   }

   public CompletableFuture synchronize() {
      return this.submitTask((var1) -> {
         return () -> {
            CompletableFuture var2 = CompletableFuture.allOf((CompletableFuture[])this.pendingWrites.values().stream().map((var0) -> {
               return var0.result;
            }).toArray((var0) -> {
               return new CompletableFuture[var0];
            }));
            var2.whenComplete((var1x, var2x) -> {
               var1.complete((Object)null);
            });
         };
      });
   }

   private CompletableFuture submitTask(Function var1) {
      CompletableFuture var2 = new CompletableFuture();
      this.inbox.add(var1.apply(var2));
      LockSupport.unpark(this.thread);
      return var2;
   }

   private void waitForQueueNonEmpty() {
      LockSupport.park("waiting for tasks");
   }

   private void loop() {
      try {
         while(this.running) {
            boolean var1 = this.processInbox();
            boolean var2 = this.storePendingChunk();
            if (!var1 && !var2) {
               this.waitForQueueNonEmpty();
            }
         }

         this.processInbox();
         this.storeRemainingPendingChunks();
      } finally {
         this.closeStorage();
      }

   }

   private boolean storePendingChunk() {
      Iterator var1 = this.pendingWrites.entrySet().iterator();
      if (!var1.hasNext()) {
         return false;
      } else {
         Entry var2 = (Entry)var1.next();
         var1.remove();
         this.runStore((ChunkPos)var2.getKey(), (IOWorker.PendingStore)var2.getValue());
         return true;
      }
   }

   private void storeRemainingPendingChunks() {
      this.pendingWrites.forEach(this::runStore);
      this.pendingWrites.clear();
   }

   private void runStore(ChunkPos var1, IOWorker.PendingStore var2) {
      try {
         this.storage.write(var1, var2.data);
         var2.result.complete((Object)null);
      } catch (Exception var4) {
         LOGGER.error("Failed to store chunk {}", var1, var4);
         var2.result.completeExceptionally(var4);
      }

   }

   private void closeStorage() {
      try {
         this.storage.close();
         this.shutdownListener.complete((Object)null);
      } catch (Exception var2) {
         LOGGER.error("Failed to close storage", var2);
         this.shutdownListener.completeExceptionally(var2);
      }

   }

   private boolean processInbox() {
      boolean var1 = false;

      Runnable var2;
      while((var2 = (Runnable)this.inbox.poll()) != null) {
         var1 = true;
         var2.run();
      }

      return var1;
   }

   public void close() throws IOException {
      if (this.shutdownRequested.compareAndSet(false, true)) {
         try {
            this.shutdown().join();
         } catch (CompletionException var2) {
            if (var2.getCause() instanceof IOException) {
               throw (IOException)var2.getCause();
            } else {
               throw var2;
            }
         }
      }
   }

   static class PendingStore {
      private CompoundTag data;
      private final CompletableFuture result;

      private PendingStore() {
         this.result = new CompletableFuture();
      }

      // $FF: synthetic method
      PendingStore(Object var1) {
         this();
      }
   }
}
