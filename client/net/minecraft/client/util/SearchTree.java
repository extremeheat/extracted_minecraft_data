package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;

public class SearchTree<T> implements ISearchTree<T> {
   protected SuffixArray<T> field_194044_a = new SuffixArray();
   protected SuffixArray<T> field_195834_b = new SuffixArray();
   protected SuffixArray<T> field_195835_c = new SuffixArray();
   private final Function<T, Iterable<String>> field_194046_c;
   private final Function<T, Iterable<ResourceLocation>> field_194047_d;
   private final List<T> field_194048_e = Lists.newArrayList();
   private final Object2IntMap<T> field_194049_f = new Object2IntOpenHashMap();

   public SearchTree(Function<T, Iterable<String>> var1, Function<T, Iterable<ResourceLocation>> var2) {
      super();
      this.field_194046_c = var1;
      this.field_194047_d = var2;
   }

   public void func_194040_a() {
      this.field_194044_a = new SuffixArray();
      this.field_195834_b = new SuffixArray();
      this.field_195835_c = new SuffixArray();
      Iterator var1 = this.field_194048_e.iterator();

      while(var1.hasNext()) {
         Object var2 = var1.next();
         this.func_194042_b(var2);
      }

      this.field_194044_a.func_194058_a();
      this.field_195834_b.func_194058_a();
      this.field_195835_c.func_194058_a();
   }

   public void func_194043_a(T var1) {
      this.field_194049_f.put(var1, this.field_194048_e.size());
      this.field_194048_e.add(var1);
      this.func_194042_b(var1);
   }

   public void func_199550_b() {
      this.field_194048_e.clear();
      this.field_194049_f.clear();
   }

   private void func_194042_b(T var1) {
      ((Iterable)this.field_194047_d.apply(var1)).forEach((var2) -> {
         this.field_195834_b.func_194057_a(var1, var2.func_110624_b().toLowerCase(Locale.ROOT));
         this.field_195835_c.func_194057_a(var1, var2.func_110623_a().toLowerCase(Locale.ROOT));
      });
      ((Iterable)this.field_194046_c.apply(var1)).forEach((var2) -> {
         this.field_194044_a.func_194057_a(var1, var2.toLowerCase(Locale.ROOT));
      });
   }

   public List<T> func_194038_a(String var1) {
      int var2 = var1.indexOf(58);
      if (var2 < 0) {
         return this.field_194044_a.func_194055_a(var1);
      } else {
         List var3 = this.field_195834_b.func_194055_a(var1.substring(0, var2).trim());
         String var4 = var1.substring(var2 + 1, var1.length()).trim();
         List var5 = this.field_195835_c.func_194055_a(var4);
         List var6 = this.field_194044_a.func_194055_a(var4);
         return Lists.newArrayList(new SearchTree.IntersectingIterator(var3.iterator(), new SearchTree.MergingIterator(var5.iterator(), var6.iterator(), this.field_194049_f), this.field_194049_f));
      }
   }

   static class MergingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> field_194033_a;
      private final PeekingIterator<T> field_194034_b;
      private final Object2IntMap<T> field_194035_c;

      public MergingIterator(Iterator<T> var1, Iterator<T> var2, Object2IntMap<T> var3) {
         super();
         this.field_194033_a = Iterators.peekingIterator(var1);
         this.field_194034_b = Iterators.peekingIterator(var2);
         this.field_194035_c = var3;
      }

      protected T computeNext() {
         boolean var1 = !this.field_194033_a.hasNext();
         boolean var2 = !this.field_194034_b.hasNext();
         if (var1 && var2) {
            return this.endOfData();
         } else if (var1) {
            return this.field_194034_b.next();
         } else if (var2) {
            return this.field_194033_a.next();
         } else {
            int var3 = Integer.compare(this.field_194035_c.getInt(this.field_194033_a.peek()), this.field_194035_c.getInt(this.field_194034_b.peek()));
            if (var3 == 0) {
               this.field_194034_b.next();
            }

            return var3 <= 0 ? this.field_194033_a.next() : this.field_194034_b.next();
         }
      }
   }

   static class IntersectingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> field_195831_a;
      private final PeekingIterator<T> field_195832_b;
      private final Object2IntMap<T> field_195833_c;

      public IntersectingIterator(Iterator<T> var1, Iterator<T> var2, Object2IntMap<T> var3) {
         super();
         this.field_195831_a = Iterators.peekingIterator(var1);
         this.field_195832_b = Iterators.peekingIterator(var2);
         this.field_195833_c = var3;
      }

      protected T computeNext() {
         while(this.field_195831_a.hasNext() && this.field_195832_b.hasNext()) {
            int var1 = Integer.compare(this.field_195833_c.getInt(this.field_195831_a.peek()), this.field_195833_c.getInt(this.field_195832_b.peek()));
            if (var1 == 0) {
               this.field_195832_b.next();
               return this.field_195831_a.next();
            }

            if (var1 < 0) {
               this.field_195831_a.next();
            } else {
               this.field_195832_b.next();
            }
         }

         return this.endOfData();
      }
   }
}
