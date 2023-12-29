package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.Deque;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public final class SequencedPriorityIterator<T> extends AbstractIterator<T> {
   private final Int2ObjectMap<Deque<T>> valuesByPriority = new Int2ObjectOpenHashMap();

   public SequencedPriorityIterator() {
      super();
   }

   public void add(T var1, int var2) {
      ((Deque)this.valuesByPriority.computeIfAbsent(var2, var0 -> Queues.newArrayDeque())).addLast(var1);
   }

   @Nullable
   protected T computeNext() {
      Optional var1 = this.valuesByPriority
         .int2ObjectEntrySet()
         .stream()
         .filter(var0 -> !((Deque)var0.getValue()).isEmpty())
         .max(Comparator.comparingInt(Entry::getKey))
         .map(Entry::getValue);
      return var1.map(Deque::removeFirst).orElseGet(() -> (T)this.endOfData());
   }
}
