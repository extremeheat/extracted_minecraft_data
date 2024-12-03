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
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.PriorityConsecutiveExecutor;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class IOWorker implements ChunkScanAccess, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final AtomicBoolean shutdownRequested = new AtomicBoolean();
   private final PriorityConsecutiveExecutor consecutiveExecutor;
   private final RegionFileStorage storage;
   private final SequencedMap<ChunkPos, PendingStore> pendingWrites = new LinkedHashMap();
   private final Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> regionCacheForBlender = new Long2ObjectLinkedOpenHashMap();
   private static final int REGION_CACHE_SIZE = 1024;

   protected IOWorker(RegionStorageInfo var1, Path var2, boolean var3) {
      super();
      this.storage = new RegionFileStorage(var1, var2, var3);
      this.consecutiveExecutor = new PriorityConsecutiveExecutor(IOWorker.Priority.values().length, Util.ioPool(), "IOWorker-" + var1.type());
   }

   public boolean isOldChunkAround(ChunkPos var1, int var2) {
      ChunkPos var3 = new ChunkPos(var1.x - var2, var1.z - var2);
      ChunkPos var4 = new ChunkPos(var1.x + var2, var1.z + var2);

      for(int var5 = var3.getRegionX(); var5 <= var4.getRegionX(); ++var5) {
         for(int var6 = var3.getRegionZ(); var6 <= var4.getRegionZ(); ++var6) {
            BitSet var7 = (BitSet)this.getOrCreateOldDataForRegion(var5, var6).join();
            if (!var7.isEmpty()) {
               ChunkPos var8 = ChunkPos.minFromRegion(var5, var6);
               int var9 = Math.max(var3.x - var8.x, 0);
               int var10 = Math.max(var3.z - var8.z, 0);
               int var11 = Math.min(var4.x - var8.x, 31);
               int var12 = Math.min(var4.z - var8.z, 31);

               for(int var13 = var9; var13 <= var11; ++var13) {
                  for(int var14 = var10; var14 <= var12; ++var14) {
                     int var15 = var14 * 32 + var13;
                     if (var7.get(var15)) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private CompletableFuture<BitSet> getOrCreateOldDataForRegion(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);
      synchronized(this.regionCacheForBlender) {
         CompletableFuture var6 = (CompletableFuture)this.regionCacheForBlender.getAndMoveToFirst(var3);
         if (var6 == null) {
            var6 = this.createOldDataForRegion(var1, var2);
            this.regionCacheForBlender.putAndMoveToFirst(var3, var6);
            if (this.regionCacheForBlender.size() > 1024) {
               this.regionCacheForBlender.removeLast();
            }
         }

         return var6;
      }
   }

   private CompletableFuture<BitSet> createOldDataForRegion(int var1, int var2) {
      return CompletableFuture.supplyAsync(() -> {
         ChunkPos var3 = ChunkPos.minFromRegion(var1, var2);
         ChunkPos var4 = ChunkPos.maxFromRegion(var1, var2);
         BitSet var5 = new BitSet();
         ChunkPos.rangeClosed(var3, var4).forEach((var2x) -> {
            CollectFields var3 = new CollectFields(new FieldSelector[]{new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector(CompoundTag.TYPE, "blending_data")});

            try {
               this.scanChunk(var2x, var3).join();
            } catch (Exception var7) {
               LOGGER.warn("Failed to scan chunk {}", var2x, var7);
               return;
            }

            Tag var4 = var3.getResult();
            if (var4 instanceof CompoundTag var5x) {
               if (this.isOldChunk(var5x)) {
                  int var6 = var2x.getRegionLocalZ() * 32 + var2x.getRegionLocalX();
                  var5.set(var6);
               }
            }

         });
         return var5;
      }, Util.backgroundExecutor());
   }

   private boolean isOldChunk(CompoundTag var1) {
      return var1.contains("DataVersion", 99) && var1.getInt("DataVersion") >= 4185 ? var1.contains("blending_data", 10) : true;
   }

   public CompletableFuture<Void> store(ChunkPos var1, @Nullable CompoundTag var2) {
      return this.store(var1, (Supplier)(() -> var2));
   }

   public CompletableFuture<Void> store(ChunkPos var1, Supplier<CompoundTag> var2) {
      return this.submitTask(() -> {
         CompoundTag var3 = (CompoundTag)var2.get();
         PendingStore var4 = (PendingStore)this.pendingWrites.computeIfAbsent(var1, (var1x) -> new PendingStore(var3));
         var4.data = var3;
         return var4.result;
      }).thenCompose(Function.identity());
   }

   public CompletableFuture<Optional<CompoundTag>> loadAsync(ChunkPos var1) {
      return this.<Optional<CompoundTag>>submitThrowingTask(() -> {
         PendingStore var2 = (PendingStore)this.pendingWrites.get(var1);
         if (var2 != null) {
            return Optional.ofNullable(var2.copyData());
         } else {
            try {
               CompoundTag var3 = this.storage.read(var1);
               return Optional.ofNullable(var3);
            } catch (Exception var4) {
               LOGGER.warn("Failed to read chunk {}", var1, var4);
               throw var4;
            }
         }
      });
   }

   public CompletableFuture<Void> synchronize(boolean var1) {
      CompletableFuture var2 = this.submitTask(() -> CompletableFuture.allOf((CompletableFuture[])this.pendingWrites.values().stream().map((var0) -> var0.result).toArray((var0) -> new CompletableFuture[var0]))).thenCompose(Function.identity());
      return var1 ? var2.thenCompose((var1x) -> this.submitThrowingTask(() -> {
            try {
               this.storage.flush();
               return null;
            } catch (Exception var2) {
               LOGGER.warn("Failed to synchronize chunks", var2);
               throw var2;
            }
         })) : var2.thenCompose((var1x) -> this.submitTask(() -> null));
   }

   public CompletableFuture<Void> scanChunk(ChunkPos var1, StreamTagVisitor var2) {
      return this.<Void>submitThrowingTask(() -> {
         try {
            PendingStore var3 = (PendingStore)this.pendingWrites.get(var1);
            if (var3 != null) {
               if (var3.data != null) {
                  var3.data.acceptAsRoot(var2);
               }
            } else {
               this.storage.scanChunk(var1, var2);
            }

            return null;
         } catch (Exception var4) {
            LOGGER.warn("Failed to bulk scan chunk {}", var1, var4);
            throw var4;
         }
      });
   }

   private <T> CompletableFuture<T> submitThrowingTask(ThrowingSupplier<T> var1) {
      return this.consecutiveExecutor.<T>scheduleWithResult(IOWorker.Priority.FOREGROUND.ordinal(), (var2) -> {
         if (!this.shutdownRequested.get()) {
            try {
               var2.complete(var1.get());
            } catch (Exception var4) {
               var2.completeExceptionally(var4);
            }
         }

         this.tellStorePending();
      });
   }

   private <T> CompletableFuture<T> submitTask(Supplier<T> var1) {
      return this.consecutiveExecutor.<T>scheduleWithResult(IOWorker.Priority.FOREGROUND.ordinal(), (var2) -> {
         if (!this.shutdownRequested.get()) {
            var2.complete(var1.get());
         }

         this.tellStorePending();
      });
   }

   private void storePendingChunk() {
      Map.Entry var1 = this.pendingWrites.pollFirstEntry();
      if (var1 != null) {
         this.runStore((ChunkPos)var1.getKey(), (PendingStore)var1.getValue());
         this.tellStorePending();
      }
   }

   private void tellStorePending() {
      this.consecutiveExecutor.schedule(new StrictQueue.RunnableWithPriority(IOWorker.Priority.BACKGROUND.ordinal(), this::storePendingChunk));
   }

   private void runStore(ChunkPos var1, PendingStore var2) {
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
         this.waitForShutdown();
         this.consecutiveExecutor.close();

         try {
            this.storage.close();
         } catch (Exception var2) {
            LOGGER.error("Failed to close storage", var2);
         }

      }
   }

   private void waitForShutdown() {
      this.consecutiveExecutor.scheduleWithResult(IOWorker.Priority.SHUTDOWN.ordinal(), (var0) -> var0.complete(Unit.INSTANCE)).join();
   }

   public RegionStorageInfo storageInfo() {
      return this.storage.info();
   }

   static enum Priority {
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

   static class PendingStore {
      @Nullable
      CompoundTag data;
      final CompletableFuture<Void> result = new CompletableFuture();

      public PendingStore(@Nullable CompoundTag var1) {
         super();
         this.data = var1;
      }

      @Nullable
      CompoundTag copyData() {
         CompoundTag var1 = this.data;
         return var1 == null ? null : var1.copy();
      }
   }

   @FunctionalInterface
   interface ThrowingSupplier<T> {
      @Nullable
      T get() throws Exception;
   }
}
