package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T> {
   private static final Set<Class<?>> field_181158_a = Sets.newHashSet();
   private final Map<Class<?>, List<T>> field_180218_a = Maps.newHashMap();
   private final Set<Class<?>> field_180216_b = Sets.newIdentityHashSet();
   private final Class<T> field_180217_c;
   private final List<T> field_181745_e = Lists.newArrayList();

   public ClassInheritanceMultiMap(Class<T> var1) {
      super();
      this.field_180217_c = var1;
      this.field_180216_b.add(var1);
      this.field_180218_a.put(var1, this.field_181745_e);
      Iterator var2 = field_181158_a.iterator();

      while(var2.hasNext()) {
         Class var3 = (Class)var2.next();
         this.func_180213_a(var3);
      }

   }

   protected void func_180213_a(Class<?> var1) {
      field_181158_a.add(var1);
      Iterator var2 = this.field_181745_e.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         if (var1.isAssignableFrom(var3.getClass())) {
            this.func_181743_a(var3, var1);
         }
      }

      this.field_180216_b.add(var1);
   }

   protected Class<?> func_181157_b(Class<?> var1) {
      if (this.field_180217_c.isAssignableFrom(var1)) {
         if (!this.field_180216_b.contains(var1)) {
            this.func_180213_a(var1);
         }

         return var1;
      } else {
         throw new IllegalArgumentException("Don't know how to search for " + var1);
      }
   }

   public boolean add(T var1) {
      Iterator var2 = this.field_180216_b.iterator();

      while(var2.hasNext()) {
         Class var3 = (Class)var2.next();
         if (var3.isAssignableFrom(var1.getClass())) {
            this.func_181743_a(var1, var3);
         }
      }

      return true;
   }

   private void func_181743_a(T var1, Class<?> var2) {
      List var3 = (List)this.field_180218_a.get(var2);
      if (var3 == null) {
         this.field_180218_a.put(var2, Lists.newArrayList(new Object[]{var1}));
      } else {
         var3.add(var1);
      }

   }

   public boolean remove(Object var1) {
      Object var2 = var1;
      boolean var3 = false;
      Iterator var4 = this.field_180216_b.iterator();

      while(var4.hasNext()) {
         Class var5 = (Class)var4.next();
         if (var5.isAssignableFrom(var2.getClass())) {
            List var6 = (List)this.field_180218_a.get(var5);
            if (var6 != null && var6.remove(var2)) {
               var3 = true;
            }
         }
      }

      return var3;
   }

   public boolean contains(Object var1) {
      return Iterators.contains(this.func_180215_b(var1.getClass()).iterator(), var1);
   }

   public <S> Iterable<S> func_180215_b(final Class<S> var1) {
      return new Iterable<S>() {
         public Iterator<S> iterator() {
            List var1x = (List)ClassInheritanceMultiMap.this.field_180218_a.get(ClassInheritanceMultiMap.this.func_181157_b(var1));
            if (var1x == null) {
               return Iterators.emptyIterator();
            } else {
               Iterator var2 = var1x.iterator();
               return Iterators.filter(var2, var1);
            }
         }
      };
   }

   public Iterator<T> iterator() {
      return this.field_181745_e.isEmpty() ? Iterators.emptyIterator() : Iterators.unmodifiableIterator(this.field_181745_e.iterator());
   }

   public int size() {
      return this.field_181745_e.size();
   }
}
