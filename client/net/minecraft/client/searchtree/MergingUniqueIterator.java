package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;

public class MergingUniqueIterator<T> extends AbstractIterator<T> {
   private final PeekingIterator<T> firstIterator;
   private final PeekingIterator<T> secondIterator;
   private final Comparator<T> comparator;

   public MergingUniqueIterator(Iterator<T> var1, Iterator<T> var2, Comparator<T> var3) {
      super();
      this.firstIterator = Iterators.peekingIterator(var1);
      this.secondIterator = Iterators.peekingIterator(var2);
      this.comparator = var3;
   }

   protected T computeNext() {
      boolean var1 = !this.firstIterator.hasNext();
      boolean var2 = !this.secondIterator.hasNext();
      if (var1 && var2) {
         return this.endOfData();
      } else if (var1) {
         return this.secondIterator.next();
      } else if (var2) {
         return this.firstIterator.next();
      } else {
         int var3 = this.comparator.compare(this.firstIterator.peek(), this.secondIterator.peek());
         if (var3 == 0) {
            this.secondIterator.next();
         }

         return var3 <= 0 ? this.firstIterator.next() : this.secondIterator.next();
      }
   }
}
