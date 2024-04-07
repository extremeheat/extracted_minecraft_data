package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.Util;

public class ClassInstanceMultiMap<T> extends AbstractCollection<T> {
   private final Map<Class<?>, List<T>> byClass = Maps.newHashMap();
   private final Class<T> baseClass;
   private final List<T> allInstances = Lists.newArrayList();

   public ClassInstanceMultiMap(Class<T> var1) {
      super();
      this.baseClass = var1;
      this.byClass.put(var1, this.allInstances);
   }

   @Override
   public boolean add(T var1) {
      boolean var2 = false;

      for (Entry var4 : this.byClass.entrySet()) {
         if (((Class)var4.getKey()).isInstance(var1)) {
            var2 |= ((List)var4.getValue()).add(var1);
         }
      }

      return var2;
   }

   @Override
   public boolean remove(Object var1) {
      boolean var2 = false;

      for (Entry var4 : this.byClass.entrySet()) {
         if (((Class)var4.getKey()).isInstance(var1)) {
            List var5 = (List)var4.getValue();
            var2 |= var5.remove(var1);
         }
      }

      return var2;
   }

   @Override
   public boolean contains(Object var1) {
      return this.find(var1.getClass()).contains(var1);
   }

   public <S> Collection<S> find(Class<S> var1) {
      if (!this.baseClass.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Don't know how to search for " + var1);
      } else {
         List var2 = this.byClass.computeIfAbsent(var1, var1x -> this.allInstances.stream().filter(var1x::isInstance).collect(Util.toMutableList()));
         return Collections.unmodifiableCollection(var2);
      }
   }

   @Override
   public Iterator<T> iterator() {
      return (Iterator<T>)(this.allInstances.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allInstances.iterator()));
   }

   public List<T> getAllInstances() {
      return ImmutableList.copyOf(this.allInstances);
   }

   @Override
   public int size() {
      return this.allInstances.size();
   }
}
