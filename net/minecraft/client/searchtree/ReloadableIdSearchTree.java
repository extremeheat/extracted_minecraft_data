package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

public class ReloadableIdSearchTree implements MutableSearchTree {
   protected SuffixArray namespaceTree = new SuffixArray();
   protected SuffixArray pathTree = new SuffixArray();
   private final Function idGetter;
   private final List contents = Lists.newArrayList();
   private final Object2IntMap orderT = new Object2IntOpenHashMap();

   public ReloadableIdSearchTree(Function var1) {
      this.idGetter = var1;
   }

   public void refresh() {
      this.namespaceTree = new SuffixArray();
      this.pathTree = new SuffixArray();
      Iterator var1 = this.contents.iterator();

      while(var1.hasNext()) {
         Object var2 = var1.next();
         this.index(var2);
      }

      this.namespaceTree.generate();
      this.pathTree.generate();
   }

   public void add(Object var1) {
      this.orderT.put(var1, this.contents.size());
      this.contents.add(var1);
      this.index(var1);
   }

   public void clear() {
      this.contents.clear();
      this.orderT.clear();
   }

   protected void index(Object var1) {
      ((Stream)this.idGetter.apply(var1)).forEach((var2) -> {
         this.namespaceTree.add(var1, var2.getNamespace().toLowerCase(Locale.ROOT));
         this.pathTree.add(var1, var2.getPath().toLowerCase(Locale.ROOT));
      });
   }

   protected int comparePosition(Object var1, Object var2) {
      return Integer.compare(this.orderT.getInt(var1), this.orderT.getInt(var2));
   }

   public List search(String var1) {
      int var2 = var1.indexOf(58);
      if (var2 == -1) {
         return this.pathTree.search(var1);
      } else {
         List var3 = this.namespaceTree.search(var1.substring(0, var2).trim());
         String var4 = var1.substring(var2 + 1).trim();
         List var5 = this.pathTree.search(var4);
         return Lists.newArrayList(new ReloadableIdSearchTree.IntersectionIterator(var3.iterator(), var5.iterator(), this::comparePosition));
      }
   }

   public static class IntersectionIterator extends AbstractIterator {
      private final PeekingIterator firstIterator;
      private final PeekingIterator secondIterator;
      private final Comparator orderT;

      public IntersectionIterator(Iterator var1, Iterator var2, Comparator var3) {
         this.firstIterator = Iterators.peekingIterator(var1);
         this.secondIterator = Iterators.peekingIterator(var2);
         this.orderT = var3;
      }

      protected Object computeNext() {
         while(this.firstIterator.hasNext() && this.secondIterator.hasNext()) {
            int var1 = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if (var1 == 0) {
               this.secondIterator.next();
               return this.firstIterator.next();
            }

            if (var1 < 0) {
               this.firstIterator.next();
            } else {
               this.secondIterator.next();
            }
         }

         return this.endOfData();
      }
   }
}
