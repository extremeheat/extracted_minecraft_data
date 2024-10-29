package com.mojang.blaze3d.resource;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

public class CrossFrameResourcePool implements GraphicsResourceAllocator, AutoCloseable {
   private final int framesToKeepResource;
   private final Deque<ResourceEntry<?>> pool = new ArrayDeque();

   public CrossFrameResourcePool(int var1) {
      super();
      this.framesToKeepResource = var1;
   }

   public void endFrame() {
      Iterator var1 = this.pool.iterator();

      while(var1.hasNext()) {
         ResourceEntry var2 = (ResourceEntry)var1.next();
         if (var2.framesToLive-- == 0) {
            var2.close();
            var1.remove();
         }
      }

   }

   public <T> T acquire(ResourceDescriptor<T> var1) {
      Iterator var2 = this.pool.iterator();

      ResourceEntry var3;
      do {
         if (!var2.hasNext()) {
            return var1.allocate();
         }

         var3 = (ResourceEntry)var2.next();
      } while(!var3.descriptor.equals(var1));

      var2.remove();
      return var3.value;
   }

   public <T> void release(ResourceDescriptor<T> var1, T var2) {
      this.pool.addFirst(new ResourceEntry(var1, var2, this.framesToKeepResource));
   }

   public void clear() {
      this.pool.forEach(ResourceEntry::close);
      this.pool.clear();
   }

   public void close() {
      this.clear();
   }

   @VisibleForTesting
   protected Collection<ResourceEntry<?>> entries() {
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
         this.value = var2;
         this.framesToLive = var3;
      }

      public void close() {
         this.descriptor.free(this.value);
      }
   }
}
