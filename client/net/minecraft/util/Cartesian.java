package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Cartesian {
   public static <T> Iterable<T[]> func_179318_a(Class<T> var0, Iterable<? extends Iterable<? extends T>> var1) {
      return new Cartesian.Product(var0, (Iterable[])func_179322_b(Iterable.class, var1));
   }

   public static <T> Iterable<List<T>> func_179321_a(Iterable<? extends Iterable<? extends T>> var0) {
      return func_179323_b(func_179318_a(Object.class, var0));
   }

   private static <T> Iterable<List<T>> func_179323_b(Iterable<Object[]> var0) {
      return Iterables.transform(var0, new Cartesian.GetList());
   }

   private static <T> T[] func_179322_b(Class<? super T> var0, Iterable<? extends T> var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         var2.add(var4);
      }

      return (Object[])var2.toArray(func_179319_b(var0, var2.size()));
   }

   private static <T> T[] func_179319_b(Class<? super T> var0, int var1) {
      return (Object[])((Object[])Array.newInstance(var0, var1));
   }

   static class Product<T> implements Iterable<T[]> {
      private final Class<T> field_179429_a;
      private final Iterable<? extends T>[] field_179428_b;

      private Product(Class<T> var1, Iterable<? extends T>[] var2) {
         super();
         this.field_179429_a = var1;
         this.field_179428_b = var2;
      }

      public Iterator<T[]> iterator() {
         return (Iterator)(this.field_179428_b.length <= 0 ? Collections.singletonList((Object[])Cartesian.func_179319_b(this.field_179429_a, 0)).iterator() : new Cartesian.Product.ProductIterator(this.field_179429_a, this.field_179428_b));
      }

      // $FF: synthetic method
      Product(Class var1, Iterable[] var2, Object var3) {
         this(var1, var2);
      }

      static class ProductIterator<T> extends UnmodifiableIterator<T[]> {
         private int field_179426_a;
         private final Iterable<? extends T>[] field_179424_b;
         private final Iterator<? extends T>[] field_179425_c;
         private final T[] field_179423_d;

         private ProductIterator(Class<T> var1, Iterable<? extends T>[] var2) {
            super();
            this.field_179426_a = -2;
            this.field_179424_b = var2;
            this.field_179425_c = (Iterator[])Cartesian.func_179319_b(Iterator.class, this.field_179424_b.length);

            for(int var3 = 0; var3 < this.field_179424_b.length; ++var3) {
               this.field_179425_c[var3] = var2[var3].iterator();
            }

            this.field_179423_d = Cartesian.func_179319_b(var1, this.field_179425_c.length);
         }

         private void func_179422_b() {
            this.field_179426_a = -1;
            Arrays.fill(this.field_179425_c, (Object)null);
            Arrays.fill(this.field_179423_d, (Object)null);
         }

         public boolean hasNext() {
            if (this.field_179426_a == -2) {
               this.field_179426_a = 0;
               Iterator[] var5 = this.field_179425_c;
               int var2 = var5.length;

               for(int var3 = 0; var3 < var2; ++var3) {
                  Iterator var4 = var5[var3];
                  if (!var4.hasNext()) {
                     this.func_179422_b();
                     break;
                  }
               }

               return true;
            } else {
               if (this.field_179426_a >= this.field_179425_c.length) {
                  for(this.field_179426_a = this.field_179425_c.length - 1; this.field_179426_a >= 0; --this.field_179426_a) {
                     Iterator var1 = this.field_179425_c[this.field_179426_a];
                     if (var1.hasNext()) {
                        break;
                     }

                     if (this.field_179426_a == 0) {
                        this.func_179422_b();
                        break;
                     }

                     var1 = this.field_179424_b[this.field_179426_a].iterator();
                     this.field_179425_c[this.field_179426_a] = var1;
                     if (!var1.hasNext()) {
                        this.func_179422_b();
                        break;
                     }
                  }
               }

               return this.field_179426_a >= 0;
            }
         }

         public T[] next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               while(this.field_179426_a < this.field_179425_c.length) {
                  this.field_179423_d[this.field_179426_a] = this.field_179425_c[this.field_179426_a].next();
                  ++this.field_179426_a;
               }

               return (Object[])this.field_179423_d.clone();
            }
         }

         // $FF: synthetic method
         public Object next() {
            return this.next();
         }

         // $FF: synthetic method
         ProductIterator(Class var1, Iterable[] var2, Object var3) {
            this(var1, var2);
         }
      }
   }

   static class GetList<T> implements Function<Object[], List<T>> {
      private GetList() {
         super();
      }

      public List<T> apply(Object[] var1) {
         return Arrays.asList((Object[])var1);
      }

      // $FF: synthetic method
      public Object apply(Object var1) {
         return this.apply((Object[])var1);
      }

      // $FF: synthetic method
      GetList(Object var1) {
         this();
      }
   }
}
