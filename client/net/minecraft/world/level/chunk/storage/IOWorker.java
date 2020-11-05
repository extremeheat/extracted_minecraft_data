package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final AtomicBoolean shutdownRequested = new AtomicBoolean();
   private final ProcessorMailbox<StrictQueue.IntRunnable> mailbox;
   private final RegionFileStorage storage;
   private final Map<ChunkPos, IOWorker.PendingStore> pendingWrites = Maps.newLinkedHashMap();

   protected IOWorker(File var1, boolean var2, String var3) {
      super();
      this.storage = new RegionFileStorage(var1, var2);
      this.mailbox = new ProcessorMailbox(new StrictQueue.FixedPriorityQueue(IOWorker.Priority.values().length), Util.ioPool(), "IOWorker-" + var3);
   }

   public CompletableFuture<Void> store(ChunkPos var1, @Nullable CompoundTag var2) {
      return this.submitTask(() -> {
         IOWorker.PendingStore var3 = (IOWorker.PendingStore)this.pendingWrites.computeIfAbsent(var1, (var1x) -> {
            return new IOWorker.PendingStore(var2);
         });
         var3.data = var2;
         return Either.left(var3.result);
      }).thenCompose(Function.identity());
   }

   @Nullable
   public CompoundTag load(ChunkPos var1) throws IOException {
      CompletableFuture var2 = this.loadAsync(var1);

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

   protected CompletableFuture<CompoundTag> loadAsync(ChunkPos var1) {
      return this.submitTask(() -> {
         IOWorker.PendingStore var2 = (IOWorker.PendingStore)this.pendingWrites.get(var1);
         if (var2 != null) {
            return Either.left(var2.data);
         } else {
            try {
               CompoundTag var3 = this.storage.read(var1);
               return Either.left(var3);
            } catch (Exception var4) {
               LOGGER.warn("Failed to read chunk {}", var1, var4);
               return Either.right(var4);
            }
         }
      });
   }

   public CompletableFuture<Void> synchronize() {
      CompletableFuture var1 = this.submitTask(() -> {
         return Either.left(CompletableFuture.allOf((CompletableFuture[])this.pendingWrites.values().stream().map((var0) -> {
            return var0.result;
         }).toArray((var0) -> {
            return new CompletableFuture[var0];
         })));
      }).thenCompose(Function.identity());
      return var1.thenCompose((var1x) -> {
         return this.submitTask(() -> {
            try {
               this.storage.flush();
               return Either.left((Object)null);
            } catch (Exception var2) {
               LOGGER.warn("Failed to synchronized chunks", var2);
               return Either.right(var2);
            }
         });
      });
   }

   private <T> CompletableFuture<T> submitTask(Supplier<Either<T, Exception>> var1) {
      return this.mailbox.askEither((var2) -> {
         return new StrictQueue.IntRunnable(IOWorker.Priority.FOREGROUND.ordinal(), () -> {
            if (!this.shutdownRequested.get()) {
               var2.tell(var1.get());
            }

            this.tellStorePending();
         });
      });
   }

   private void storePendingChunk() {
      Iterator var1 = this.pendingWrites.entrySet().iterator();
      if (var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         var1.remove();
         this.runStore((ChunkPos)var2.getKey(), (IOWorker.PendingStore)var2.getValue());
         this.tellStorePending();
      }
   }

   private void tellStorePending() {
      this.mailbox.tell(new StrictQueue.IntRunnable(IOWorker.Priority.BACKGROUND.ordinal(), this::storePendingChunk));
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

   public void close() throws IOException {
      if (this.shutdownRequested.compareAndSet(false, true)) {
         this.mailbox.ask((var0) -> {
            return new StrictQueue.IntRunnable(IOWorker.Priority.SHUTDOWN.ordinal(), () -> {
               var0.tell(Unit.INSTANCE);
            });
         }).join();
         this.mailbox.close();

         try {
            this.storage.close();
         } catch (Exception var2) {
            LOGGER.error("Failed to close storage", var2);
         }

      }
   }

   static class PendingStore {
      @Nullable
      private CompoundTag data;
      private final CompletableFuture<Void> result = new CompletableFuture();

      public PendingStore(@Nullable CompoundTag var1) {
         super();
         this.data = var1;
      }
   }

   static enum Priority {
      FOREGROUND,
      BACKGROUND,
      SHUTDOWN;

      private Priority() {
      }
   }
}
