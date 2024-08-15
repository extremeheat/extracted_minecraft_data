package com.mojang.blaze3d.resource;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

public class CrossFrameResourcePool implements GraphicsResourceAllocator, AutoCloseable {
   private final int framesToKeepResource;
   private final Deque<CrossFrameResourcePool.ResourceEntry<?>> pool = new ArrayDeque<>();

   public CrossFrameResourcePool(int var1) {
      super();
      this.framesToKeepResource = var1;
   }

   public void endFrame() {
      Iterator var1 = this.pool.iterator();

      while (var1.hasNext()) {
         CrossFrameResourcePool.ResourceEntry var2 = (CrossFrameResourcePool.ResourceEntry)var1.next();
         if (var2.framesToLive-- == 0) {
            var2.close();
            var1.remove();
         }
      }
   }

   @Override
   public <T> T acquire(ResourceDescriptor<T> var1) {
      Iterator var2 = this.pool.iterator();

      while (var2.hasNext()) {
         CrossFrameResourcePool.ResourceEntry var3 = (CrossFrameResourcePool.ResourceEntry)var2.next();
         if (var3.descriptor.equals(var1)) {
            var2.remove();
            return var3.value;
         }
      }

      return (T)var1.allocate();
   }

   @Override
   public <T> void release(ResourceDescriptor<T> var1, T var2) {
      this.pool.addFirst(new CrossFrameResourcePool.ResourceEntry<>(var1, var2, this.framesToKeepResource));
   }

   public void clear() {
      this.pool.forEach(CrossFrameResourcePool.ResourceEntry::close);
      this.pool.clear();
   }

   @Override
   public void close() {
      this.clear();
   }

   @VisibleForTesting
   protected Collection<CrossFrameResourcePool.ResourceEntry<?>> entries() {
      return this.pool;
   }

   @VisibleForTesting
   protected static final class ResourceEntry<T> implements AutoCloseable {
      final ResourceDescriptor<T> descriptor;
      final T value;
      int framesToLive;

      ResourceEntry(ResourceDescriptor<T> var1, T var2, int var3) {
         super();
         this.descriptor = var1;
         this.value = (T)var2;
         this.framesToLive = var3;
      }

      @Override
      public void close() {
         this.descriptor.free(this.value);
      }
   }
}
