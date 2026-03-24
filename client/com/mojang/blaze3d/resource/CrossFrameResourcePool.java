package com.mojang.blaze3d.resource;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

public class CrossFrameResourcePool implements GraphicsResourceAllocator, AutoCloseable {
   private final int framesToKeepResource;
   private final Deque<ResourceEntry<?>> pool = new ArrayDeque();

   public CrossFrameResourcePool(final int framesToKeepResource) {
      super();
      this.framesToKeepResource = framesToKeepResource;
   }

   public void endFrame() {
      Iterator<? extends ResourceEntry<?>> iterator = this.pool.iterator();

      while(iterator.hasNext()) {
         ResourceEntry<?> entry = (ResourceEntry)iterator.next();
         if (entry.framesToLive-- == 0) {
            entry.close();
            iterator.remove();
         }
      }

   }

   public <T> T acquire(final ResourceDescriptor<T> descriptor) {
      T resource = (T)this.acquireWithoutPreparing(descriptor);
      descriptor.prepare(resource);
      return resource;
   }

   private <T> T acquireWithoutPreparing(final ResourceDescriptor<T> descriptor) {
      Iterator<? extends ResourceEntry<?>> iterator = this.pool.iterator();

      while(iterator.hasNext()) {
         ResourceEntry<?> entry = (ResourceEntry)iterator.next();
         if (descriptor.canUsePhysicalResource(entry.descriptor)) {
            iterator.remove();
            return entry.value;
         }
      }

      return descriptor.allocate();
   }

   public <T> void release(final ResourceDescriptor<T> descriptor, final T resource) {
      this.pool.addFirst(new ResourceEntry(descriptor, resource, this.framesToKeepResource));
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
      private final ResourceDescriptor<T> descriptor;
      private final T value;
      private int framesToLive;

      private ResourceEntry(final ResourceDescriptor<T> descriptor, final T value, final int framesToLive) {
         super();
         this.descriptor = descriptor;
         this.value = value;
         this.framesToLive = framesToLive;
      }

      public void close() {
         this.descriptor.free(this.value);
      }
   }
}
