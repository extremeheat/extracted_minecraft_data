package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;

public class IntersectionIterator<T> extends AbstractIterator<T> {
   private final PeekingIterator<T> firstIterator;
   private final PeekingIterator<T> secondIterator;
   private final Comparator<T> comparator;

   public IntersectionIterator(Iterator<T> var1, Iterator<T> var2, Comparator<T> var3) {
      super();
      this.firstIterator = Iterators.peekingIterator(var1);
      this.secondIterator = Iterators.peekingIterator(var2);
      this.comparator = var3;
   }

   protected T computeNext() {
      while (this.firstIterator.hasNext() && this.secondIterator.hasNext()) {
         int var1 = this.comparator.compare((T)this.firstIterator.peek(), (T)this.secondIterator.peek());
         if (var1 == 0) {
            this.secondIterator.next();
            return (T)this.firstIterator.next();
         }

         if (var1 < 0) {
            this.firstIterator.next();
         } else {
            this.secondIterator.next();
         }
      }

      return (T)this.endOfData();
   }
}
