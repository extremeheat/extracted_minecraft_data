package com.mojang.blaze3d.vulkan;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.util.Objects;

public class DestructionQueue<T> implements AutoCloseable {
   private final Destroyer<T> destroyCallback;
   private final ReferenceList<ReferenceArrayList<T>> destructionQueues;
   private int currentDestructionQueueIndex = 0;

   public DestructionQueue(final int internalQueueCount, final Destroyer<T> destroyCallback) {
      super();
      this.destroyCallback = destroyCallback;
      this.destructionQueues = new ReferenceArrayList(internalQueueCount);

      for(int i = 0; i < internalQueueCount; ++i) {
         this.destructionQueues.add(new ReferenceArrayList());
      }

   }

   public void close() {
      for(int i = 0; i < this.destructionQueues.size(); ++i) {
         if (this.rotate()) {
            i = 0;
         }
      }

   }

   public boolean rotate() {
      ++this.currentDestructionQueueIndex;
      this.currentDestructionQueueIndex %= this.destructionQueues.size();
      ReferenceArrayList<T> currentQueue = (ReferenceArrayList)this.destructionQueues.set(this.currentDestructionQueueIndex, new ReferenceArrayList());
      if (currentQueue.isEmpty()) {
         return false;
      } else {
         this.destroyCallback.begin(currentQueue.size());
         Destroyer var10001 = this.destroyCallback;
         Objects.requireNonNull(var10001);
         currentQueue.forEach(var10001::destroy);
         this.destroyCallback.end();
         return true;
      }
   }

   public void add(final T t) {
      ReferenceArrayList<T> currentQueue = (ReferenceArrayList)this.destructionQueues.get(this.currentDestructionQueueIndex);
      currentQueue.add(t);
   }

   public interface Destroyer<T> {
      default void begin(final int count) {
      }

      void destroy(T t);

      default void end() {
      }
   }
}
