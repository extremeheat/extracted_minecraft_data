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
import java.util.Objects;
import java.util.stream.Stream;

public class ClassInstanceMultiMap<T> extends AbstractCollection<T> {
   private final Map<Class<?>, List<T>> byClass = Maps.newHashMap();
   private final Class<T> baseClass;
   private final List<T> allInstances = Lists.newArrayList();

   public ClassInstanceMultiMap(final Class<T> baseClass) {
      super();
      this.baseClass = baseClass;
      this.byClass.put(baseClass, this.allInstances);
   }

   public boolean add(final T instance) {
      boolean success = false;

      for(Map.Entry<Class<?>, List<T>> entry : this.byClass.entrySet()) {
         if (((Class)entry.getKey()).isInstance(instance)) {
            success |= ((List)entry.getValue()).add(instance);
         }
      }

      return success;
   }

   public boolean remove(final Object object) {
      boolean success = false;

      for(Map.Entry<Class<?>, List<T>> entry : this.byClass.entrySet()) {
         if (((Class)entry.getKey()).isInstance(object)) {
            List<T> list = (List)entry.getValue();
            success |= list.remove(object);
         }
      }

      return success;
   }

   public boolean contains(final Object o) {
      return this.find(o.getClass()).contains(o);
   }

   public <S> Collection<S> find(final Class<S> index) {
      if (!this.baseClass.isAssignableFrom(index)) {
         throw new IllegalArgumentException("Don't know how to search for " + String.valueOf(index));
      } else {
         List<? extends T> instances = (List)this.byClass.computeIfAbsent(index, (k) -> {
            Stream var10000 = this.allInstances.stream();
            Objects.requireNonNull(k);
            return (List)var10000.filter(k::isInstance).collect(Util.toMutableList());
         });
         return Collections.unmodifiableCollection(instances);
      }
   }

   public Iterator<T> iterator() {
      return (Iterator<T>)(this.allInstances.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allInstances.iterator()));
   }

   public List<T> getAllInstances() {
      return ImmutableList.copyOf(this.allInstances);
   }

   public int size() {
      return this.allInstances.size();
   }
}
