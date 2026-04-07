package net.minecraft.world.level.chunk.storage;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.thread.PriorityConsecutiveExecutor;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class IOWorker implements AutoCloseable, ChunkScanAccess {
   public static final Supplier<CompoundTag> STORE_EMPTY = () -> null;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final AtomicBoolean shutdownRequested = new AtomicBoolean();
   private final PriorityConsecutiveExecutor consecutiveExecutor;
   private final RegionFileStorage storage;
   private final SequencedMap<ChunkPos, PendingStore> pendingWrites = new LinkedHashMap();
   private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> regionCacheForBlender = new Long2ObjectLinkedOpenHashMap();
   private static final int REGION_CACHE_SIZE = 1024;

   protected IOWorker(final RegionStorageInfo info, final Path dir, final boolean sync) {
      super();
      this.storage = new RegionFileStorage(info, dir, sync);
      this.consecutiveExecutor = new PriorityConsecutiveExecutor(IOWorker.Priority.values().length, Util.ioPool(), "IOWorker-" + info.type());
   }

   public boolean isOldChunkAround(final ChunkPos pos, final int range) {
      ChunkPos from = new ChunkPos(pos.x() - range, pos.z() - range);
      ChunkPos to = new ChunkPos(pos.x() + range, pos.z() + range);

      for(int regionX = from.getRegionX(); regionX <= to.getRegionX(); ++regionX) {
         for(int regionZ = from.getRegionZ(); regionZ <= to.getRegionZ(); ++regionZ) {
            BitSet data = (BitSet)this.getOrCreateOldDataForRegion(regionX, regionZ).join();
            if (!data.isEmpty()) {
               ChunkPos minChunkPos = ChunkPos.minFromRegion(regionX, regionZ);
               int startChunkX = Math.max(from.x() - minChunkPos.x(), 0);
               int startChunkZ = Math.max(from.z() - minChunkPos.z(), 0);
               int endChunkX = Math.min(to.x() - minChunkPos.x(), 31);
               int endChunkZ = Math.min(to.z() - minChunkPos.z(), 31);

               for(int x = startChunkX; x <= endChunkX; ++x) {
                  for(int z = startChunkZ; z <= endChunkZ; ++z) {
                     int chunkIndex = z * 32 + x;
                     if (data.get(chunkIndex)) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private CompletableFuture<BitSet> getOrCreateOldDataForRegion(final int regionX, final int regionZ) {
      long regionPos = ChunkPos.pack(regionX, regionZ);
      synchronized(this.regionCacheForBlender) {
         CompletableFuture<BitSet> result = (CompletableFuture)this.regionCacheForBlender.getAndMoveToFirst(regionPos);
         if (result == null) {
            result = this.createOldDataForRegion(regionX, regionZ);
            this.regionCacheForBlender.putAndMoveToFirst(regionPos, result);
            if (this.regionCacheForBlender.size() > 1024) {
               this.regionCacheForBlender.removeLast();
            }
         }

         return result;
      }
   }

   private CompletableFuture<BitSet> createOldDataForRegion(final int regionX, final int regionZ) {
      return CompletableFuture.supplyAsync(() -> {
         ChunkPos from = ChunkPos.minFromRegion(regionX, regionZ);
         ChunkPos to = ChunkPos.maxFromRegion(regionX, regionZ);
         BitSet resultSet = new BitSet();
         ChunkPos.rangeClosed(from, to).forEach((pos) -> {
            CollectFields collectFields = new CollectFields(new FieldSelector[]{new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector(CompoundTag.TYPE, "blending_data")});

            try {
               this.scanChunk(pos, collectFields).join();
            } catch (Exception e) {
               LOGGER.warn("Failed to scan chunk {}", pos, e);
               return;
            }

            Tag tag = collectFields.getResult();
            if (tag instanceof CompoundTag chunkTag) {
               if (this.isOldChunk(chunkTag)) {
                  int chunkIndex = pos.getRegionLocalZ() * 32 + pos.getRegionLocalX();
                  resultSet.set(chunkIndex);
               }
            }

         });
         return resultSet;
      }, Util.backgroundExecutor());
   }

   private boolean isOldChunk(final CompoundTag tag) {
      return tag.getIntOr("DataVersion", 0) < 4882 ? true : tag.getCompound("blending_data").isPresent();
   }

   public CompletableFuture<Void> store(final ChunkPos pos, final CompoundTag value) {
      return this.store(pos, (Supplier)(() -> value));
   }

   public CompletableFuture<Void> store(final ChunkPos pos, final Supplier<CompoundTag> supplier) {
      return this.submitTask(() -> {
         CompoundTag data = (CompoundTag)supplier.get();
         PendingStore pendingStore = (PendingStore)this.pendingWrites.computeIfAbsent(pos, (p) -> new PendingStore(data));
         pendingStore.data = data;
         return pendingStore.result;
      }).thenCompose(Function.identity());
   }

   public CompletableFuture<Optional<CompoundTag>> loadAsync(final ChunkPos pos) {
      return this.<Optional<CompoundTag>>submitThrowingTask(() -> {
         PendingStore pendingStore = (PendingStore)this.pendingWrites.get(pos);
         if (pendingStore != null) {
            return Optional.ofNullable(pendingStore.copyData());
         } else {
            try {
               CompoundTag data = this.storage.read(pos);
               return Optional.ofNullable(data);
            } catch (Exception e) {
               LOGGER.warn("Failed to read chunk {}", pos, e);
               throw e;
            }
         }
      });
   }

   public CompletableFuture<Void> synchronize(final boolean flush) {
      CompletableFuture<Void> currentWrites = this.submitTask(() -> CompletableFuture.allOf((CompletableFuture[])this.pendingWrites.values().stream().map((store) -> store.result).toArray((x$0) -> new CompletableFuture[x$0]))).thenCompose(Function.identity());
      return flush ? currentWrites.thenCompose((ignore) -> this.submitThrowingTask(() -> {
            try {
               this.storage.flush();
               return null;
            } catch (Exception e) {
               LOGGER.warn("Failed to synchronize chunks", e);
               throw e;
            }
         })) : currentWrites.thenCompose((ignore) -> this.submitTask(() -> null));
   }

   public CompletableFuture<Void> scanChunk(final ChunkPos pos, final StreamTagVisitor visitor) {
      return this.<Void>submitThrowingTask(() -> {
         try {
            PendingStore pendingStore = (PendingStore)this.pendingWrites.get(pos);
            if (pendingStore != null) {
               if (pendingStore.data != null) {
                  pendingStore.data.acceptAsRoot(visitor);
               }
            } else {
               this.storage.scanChunk(pos, visitor);
            }

            return null;
         } catch (Exception e) {
            LOGGER.warn("Failed to bulk scan chunk {}", pos, e);
            throw e;
         }
      });
   }

   private <T> CompletableFuture<T> submitThrowingTask(final ThrowingSupplier<T> task) {
      return this.consecutiveExecutor.<T>scheduleWithResult(IOWorker.Priority.FOREGROUND.ordinal(), (future) -> {
         if (!this.shutdownRequested.get()) {
            try {
               future.complete(task.get());
            } catch (Exception e) {
               future.completeExceptionally(e);
            }
         }

         this.tellStorePending();
      });
   }

   private <T> CompletableFuture<T> submitTask(final Supplier<T> task) {
      return this.consecutiveExecutor.<T>scheduleWithResult(IOWorker.Priority.FOREGROUND.ordinal(), (future) -> {
         if (!this.shutdownRequested.get()) {
            future.complete(task.get());
         }

         this.tellStorePending();
      });
   }

   private void storePendingChunk() {
      Map.Entry<ChunkPos, PendingStore> entry = this.pendingWrites.pollFirstEntry();
      if (entry != null) {
         this.runStore((ChunkPos)entry.getKey(), (PendingStore)entry.getValue());
         this.tellStorePending();
      }
   }

   private void tellStorePending() {
      this.consecutiveExecutor.schedule(new StrictQueue.RunnableWithPriority(IOWorker.Priority.BACKGROUND.ordinal(), this::storePendingChunk));
   }

   private void runStore(final ChunkPos pos, final PendingStore write) {
      try {
         this.storage.write(pos, write.data);
         write.result.complete((Object)null);
      } catch (Exception e) {
         LOGGER.error("Failed to store chunk {}", pos, e);
         write.result.completeExceptionally(e);
      }

   }

   public void close() throws IOException {
      if (this.shutdownRequested.compareAndSet(false, true)) {
         this.waitForShutdown();
         this.consecutiveExecutor.close();

         try {
            this.storage.close();
         } catch (Exception e) {
            LOGGER.error("Failed to close storage", e);
         }

      }
   }

   private void waitForShutdown() {
      this.consecutiveExecutor.scheduleWithResult(IOWorker.Priority.SHUTDOWN.ordinal(), (future) -> future.complete(Unit.INSTANCE)).join();
   }

   public RegionStorageInfo storageInfo() {
      return this.storage.info();
   }

   private static enum Priority {
      FOREGROUND,
      BACKGROUND,
      SHUTDOWN;

      private Priority() {
      }

      // $FF: synthetic method
      private static Priority[] $values() {
         return new Priority[]{FOREGROUND, BACKGROUND, SHUTDOWN};
      }
   }

   private static class PendingStore {
      private @Nullable CompoundTag data;
      private final CompletableFuture<Void> result = new CompletableFuture();

      public PendingStore(final @Nullable CompoundTag data) {
         super();
         this.data = data;
      }

      private @Nullable CompoundTag copyData() {
         CompoundTag data = this.data;
         return data == null ? null : data.copy();
      }
   }

   @FunctionalInterface
   private interface ThrowingSupplier<T> {
      @Nullable T get() throws Exception;
   }
}
