package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Deque;
import org.jspecify.annotations.Nullable;

public final class SequencedPriorityIterator<T> extends AbstractIterator<T> {
   private static final int MIN_PRIO = -2147483648;
   private @Nullable Deque<T> highestPrioQueue = null;
   private int highestPrio = -2147483648;
   private final Int2ObjectMap<Deque<T>> queuesByPriority = new Int2ObjectOpenHashMap();

   public SequencedPriorityIterator() {
      super();
   }

   public void add(final T data, final int priority) {
      if (priority == this.highestPrio && this.highestPrioQueue != null) {
         this.highestPrioQueue.addLast(data);
      } else {
         Deque<T> queue = (Deque)this.queuesByPriority.computeIfAbsent(priority, (order) -> Queues.newArrayDeque());
         queue.addLast(data);
         if (priority >= this.highestPrio) {
            this.highestPrioQueue = queue;
            this.highestPrio = priority;
         }

      }
   }

   protected @Nullable T computeNext() {
      if (this.highestPrioQueue == null) {
         return (T)this.endOfData();
      } else {
         T result = (T)this.highestPrioQueue.removeFirst();
         if (result == null) {
            return (T)this.endOfData();
         } else {
            if (this.highestPrioQueue.isEmpty()) {
               this.switchCacheToNextHighestPrioQueue();
            }

            return result;
         }
      }
   }

   private void switchCacheToNextHighestPrioQueue() {
      int foundHighestPrio = -2147483648;
      Deque<T> foundHighestPrioQueue = null;
      ObjectIterator var3 = Int2ObjectMaps.fastIterable(this.queuesByPriority).iterator();

      while(var3.hasNext()) {
         Int2ObjectMap.Entry<Deque<T>> entry = (Int2ObjectMap.Entry)var3.next();
         Deque<T> queue = (Deque)entry.getValue();
         int prio = entry.getIntKey();
         if (prio > foundHighestPrio && !queue.isEmpty()) {
            foundHighestPrio = prio;
            foundHighestPrioQueue = queue;
            if (prio == this.highestPrio - 1) {
               break;
            }
         }
      }

      this.highestPrio = foundHighestPrio;
      this.highestPrioQueue = foundHighestPrioQueue;
   }
}
