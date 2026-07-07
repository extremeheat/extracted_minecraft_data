package com.mojang.blaze3d.vertex;

import com.mojang.datafixers.util.Pair;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.device.GpuDevice;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import org.jspecify.annotations.Nullable;

public class UberGpuBuffer<T> implements AutoCloseable {
   private final @GpuBuffer.Usage int bufferUsage;
   private final int heapSize;
   private final int alignSize;
   private final String name;
   private final List<Pair<TlsfAllocator, UberGpuBufferHeap>> nodes = new ArrayList();
   private final StagingBuffer stagingBuffer;
   private final Object2ObjectOpenHashMap<T, StagedAllocationEntry<? extends T>> stagedAllocations = new Object2ObjectOpenHashMap(32);
   private final ObjectOpenHashSet<T> skippedStagedAllocations = new ObjectOpenHashSet(32);
   private final Map<T, TlsfAllocator.Allocation> allocationMap = new HashMap(256);

   public UberGpuBuffer(final String name, final @GpuBuffer.Usage int bufferUsage, final int heapSize, final int alignSize, final StagingBuffer stagingBuffer) {
      super();
      this.name = "UberBuffer " + name;
      this.bufferUsage = bufferUsage;
      this.heapSize = heapSize;
      this.alignSize = alignSize;
      this.stagingBuffer = stagingBuffer;
   }

   public <U extends T> boolean addAllocation(final U allocationKey, final UploadCallback<U> callback, final ByteBuffer buffer) {
      StagingBuffer.BufferHandle handle = this.stagingBuffer.tryAppend(buffer);
      if (handle == null) {
         return false;
      } else {
         StagedAllocationEntry<U> entry = new StagedAllocationEntry<U>(handle, callback);
         StagedAllocationEntry<? extends T> oldEntry = (StagedAllocationEntry)this.stagedAllocations.put(allocationKey, entry);
         if (oldEntry != null) {
            oldEntry.close();
         }

         return true;
      }
   }

   public boolean uploadStagedAllocations(final GpuDevice gpuDevice, final StagingBuffer.Uploader uploader) {
      uploader.checkValidFor(this.stagingBuffer);
      ObjectIterator var3 = this.stagedAllocations.keySet().iterator();

      while(var3.hasNext()) {
         T key = (T)var3.next();
         this.freeAllocation(key);
      }

      boolean newHeapCreatedOrDestroyed = false;

      try (Zone iterator = Profiler.get().zone("uploadStagedAllocations")) {
         ObjectIterator var5 = this.stagedAllocations.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<T, StagedAllocationEntry<? extends T>> entry = (Map.Entry)var5.next();

            try (StagedAllocationEntry<? extends T> staged = (StagedAllocationEntry)entry.getValue()) {
               long allocationSize = staged.buffer.size();
               if (!this.skippedStagedAllocations.contains(entry.getKey())) {
                  TlsfAllocator.Allocation allocation = null;

                  for(Pair<TlsfAllocator, UberGpuBufferHeap> node : this.nodes) {
                     allocation = ((TlsfAllocator)node.getFirst()).allocate(allocationSize, this.alignSize);
                     if (allocation != null) {
                        break;
                     }
                  }

                  if (allocation == null) {
                     try (Zone var25 = Profiler.get().zone("createNewHeap")) {
                        assert allocationSize <= (long)this.heapSize;

                        String heapName = String.format(Locale.ROOT, "%s %d", this.name, this.nodes.size());
                        UberGpuBufferHeap newHeap = new UberGpuBufferHeap((long)this.heapSize, gpuDevice, this.bufferUsage, heapName);
                        TlsfAllocator newTlsfAllocator = new TlsfAllocator(newHeap);
                        this.nodes.add(new Pair(newTlsfAllocator, newHeap));
                        allocation = newTlsfAllocator.allocate(allocationSize, this.alignSize);
                        newHeapCreatedOrDestroyed = true;
                     }
                  }

                  if (allocation != null) {
                     TlsfAllocator.Heap allocationHeap = allocation.getHeap();
                     GpuBuffer allocationDestBuffer = ((UberGpuBufferHeap)allocationHeap).gpuBuffer;
                     uploader.copyTo(staged.buffer, allocationDestBuffer, allocation.getOffsetFromHeap());
                     this.allocationMap.put(entry.getKey(), allocation);
                     runCallbackUnchecked(entry.getKey(), (StagedAllocationEntry)entry.getValue());
                  }
               }
            }
         }

         this.stagedAllocations.clear();
         this.skippedStagedAllocations.clear();
      }

      Iterator<Pair<TlsfAllocator, UberGpuBufferHeap>> iterator = this.nodes.iterator();

      while(iterator.hasNext()) {
         Pair<TlsfAllocator, UberGpuBufferHeap> node = (Pair)iterator.next();
         if (((TlsfAllocator)node.getFirst()).isCompletelyFree()) {
            ((UberGpuBufferHeap)node.getSecond()).gpuBuffer.close();
            iterator.remove();
            newHeapCreatedOrDestroyed = true;
            break;
         }
      }

      return newHeapCreatedOrDestroyed;
   }

   private static <T, U extends T> void runCallbackUnchecked(final T key, final StagedAllocationEntry<U> value) {
      if (value.callback != null) {
         value.callback.bufferHasBeenUploaded(key);
      }

   }

   public TlsfAllocator.@Nullable Allocation getAllocation(final T allocationKey) {
      return (TlsfAllocator.Allocation)this.allocationMap.get(allocationKey);
   }

   public void removeAllocation(final T allocationKey) {
      this.skippedStagedAllocations.add(allocationKey);
      this.freeAllocation(allocationKey);
   }

   private void freeAllocation(final T allocationKey) {
      TlsfAllocator.Allocation allocation = (TlsfAllocator.Allocation)this.allocationMap.remove(allocationKey);
      if (allocation != null) {
         for(Pair<TlsfAllocator, UberGpuBufferHeap> node : this.nodes) {
            if (node.getSecond() == allocation.getHeap()) {
               ((TlsfAllocator)node.getFirst()).free(allocation);
               break;
            }
         }
      }

   }

   public GpuBuffer getGpuBuffer(final TlsfAllocator.Allocation allocation) {
      return ((UberGpuBufferHeap)allocation.getHeap()).gpuBuffer;
   }

   @VisibleForDebug
   public void printStatistics() {
      for(int i = 0; i < this.nodes.size(); ++i) {
         Pair<TlsfAllocator, UberGpuBufferHeap> node = (Pair)this.nodes.get(i);
         String heapName = String.format(Locale.ROOT, "%s %d", this.name, i);
         ((TlsfAllocator)node.getFirst()).printAllocatorStatistics(heapName);
      }

   }

   public void close() {
      this.stagedAllocations.values().forEach(StagedAllocationEntry::close);
      this.stagedAllocations.clear();
      this.allocationMap.clear();

      for(Pair<TlsfAllocator, UberGpuBufferHeap> node : this.nodes) {
         ((UberGpuBufferHeap)node.getSecond()).gpuBuffer.close();
      }

      this.nodes.clear();
   }

   private static record StagedAllocationEntry<T>(StagingBuffer.BufferHandle buffer, @Nullable UploadCallback<T> callback) implements AutoCloseable {
      private StagedAllocationEntry {
         super();
      }

      public void close() {
         this.buffer.close();
      }
   }

   public static class UberGpuBufferHeap extends TlsfAllocator.Heap {
      private final GpuBuffer gpuBuffer;

      public UberGpuBufferHeap(final long size, final GpuDevice gpuDevice, final @GpuBuffer.Usage int usage, final String name) {
         super(size);
         this.gpuBuffer = gpuDevice.createBuffer(() -> name, usage | 8 | 16, size);
      }
   }

   public interface UploadCallback<T> {
      void bufferHasBeenUploaded(T key);
   }
}
