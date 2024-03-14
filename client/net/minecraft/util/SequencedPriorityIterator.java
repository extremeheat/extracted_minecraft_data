package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Deque;
import javax.annotation.Nullable;

public final class SequencedPriorityIterator<T> extends AbstractIterator<T> {
   private static final int MIN_PRIO = -2147483648;
   @Nullable
   private Deque<T> highestPrioQueue = null;
   private int highestPrio = -2147483648;
   private final Int2ObjectMap<Deque<T>> queuesByPriority = new Int2ObjectOpenHashMap();

   public SequencedPriorityIterator() {
      super();
   }

   public void add(T var1, int var2) {
      if (var2 == this.highestPrio && this.highestPrioQueue != null) {
         this.highestPrioQueue.addLast((T)var1);
      } else {
         Deque var3 = (Deque)this.queuesByPriority.computeIfAbsent(var2, var0 -> Queues.newArrayDeque());
         var3.addLast(var1);
         if (var2 >= this.highestPrio) {
            this.highestPrioQueue = var3;
            this.highestPrio = var2;
         }
      }
   }

   @Nullable
   protected T computeNext() {
      if (this.highestPrioQueue == null) {
         return (T)this.endOfData();
      } else {
         Object var1 = this.highestPrioQueue.removeFirst();
         if (var1 == null) {
            return (T)this.endOfData();
         } else {
            if (this.highestPrioQueue.isEmpty()) {
               this.switchCacheToNextHighestPrioQueue();
            }

            return (T)var1;
         }
      }
   }

   private void switchCacheToNextHighestPrioQueue() {
      int var1 = -2147483648;
      Deque var2 = null;
      ObjectIterator var3 = Int2ObjectMaps.fastIterable(this.queuesByPriority).iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Deque var5 = (Deque)var4.getValue();
         int var6 = var4.getIntKey();
         if (var6 > var1 && !var5.isEmpty()) {
            var1 = var6;
            var2 = var5;
            if (var6 == this.highestPrio - 1) {
               break;
            }
         }
      }

      this.highestPrio = var1;
      this.highestPrioQueue = var2;
   }
}
