package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

@GwtCompatible
class ConsumingQueueIterator<T> extends AbstractIterator<T> {
   private final Queue<T> queue;

   ConsumingQueueIterator(T... var1) {
      super();
      this.queue = new ArrayDeque(var1.length);
      Collections.addAll(this.queue, var1);
   }

   ConsumingQueueIterator(Queue<T> var1) {
      super();
      this.queue = (Queue)Preconditions.checkNotNull(var1);
   }

   public T computeNext() {
      return this.queue.isEmpty() ? this.endOfData() : this.queue.remove();
   }
}
