package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public class ReloadableSearchTree<T> extends ReloadableIdSearchTree<T> {
   protected SuffixArray<T> tree = new SuffixArray();
   private final Function<T, Stream<String>> filler;

   public ReloadableSearchTree(Function<T, Stream<String>> var1, Function<T, Stream<ResourceLocation>> var2) {
      super(var2);
      this.filler = var1;
   }

   public void refresh() {
      this.tree = new SuffixArray();
      super.refresh();
      this.tree.generate();
   }

   protected void index(T var1) {
      super.index(var1);
      ((Stream)this.filler.apply(var1)).forEach((var2) -> {
         this.tree.add(var1, var2.toLowerCase(Locale.ROOT));
      });
   }

   public List<T> search(String var1) {
      int var2 = var1.indexOf(58);
      if (var2 < 0) {
         return this.tree.search(var1);
      } else {
         List var3 = this.namespaceTree.search(var1.substring(0, var2).trim());
         String var4 = var1.substring(var2 + 1).trim();
         List var5 = this.pathTree.search(var4);
         List var6 = this.tree.search(var4);
         return Lists.newArrayList(new ReloadableIdSearchTree.IntersectionIterator(var3.iterator(), new ReloadableSearchTree.MergingUniqueIterator(var5.iterator(), var6.iterator(), this::comparePosition), this::comparePosition));
      }
   }

   static class MergingUniqueIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> firstIterator;
      private final PeekingIterator<T> secondIterator;
      private final Comparator<T> orderT;

      public MergingUniqueIterator(Iterator<T> var1, Iterator<T> var2, Comparator<T> var3) {
         super();
         this.firstIterator = Iterators.peekingIterator(var1);
         this.secondIterator = Iterators.peekingIterator(var2);
         this.orderT = var3;
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
            int var3 = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if (var3 == 0) {
               this.secondIterator.next();
            }

            return var3 <= 0 ? this.firstIterator.next() : this.secondIterator.next();
         }
      }
   }
}
