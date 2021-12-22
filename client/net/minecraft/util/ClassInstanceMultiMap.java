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
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassInstanceMultiMap<T> extends AbstractCollection<T> {
   private final Map<Class<?>, List<T>> byClass = Maps.newHashMap();
   private final Class<T> baseClass;
   private final List<T> allInstances = Lists.newArrayList();

   public ClassInstanceMultiMap(Class<T> var1) {
      super();
      this.baseClass = var1;
      this.byClass.put(var1, this.allInstances);
   }

   public boolean add(T var1) {
      boolean var2 = false;
      Iterator var3 = this.byClass.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((Class)var4.getKey()).isInstance(var1)) {
            var2 |= ((List)var4.getValue()).add(var1);
         }
      }

      return var2;
   }

   public boolean remove(Object var1) {
      boolean var2 = false;
      Iterator var3 = this.byClass.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((Class)var4.getKey()).isInstance(var1)) {
            List var5 = (List)var4.getValue();
            var2 |= var5.remove(var1);
         }
      }

      return var2;
   }

   public boolean contains(Object var1) {
      return this.find(var1.getClass()).contains(var1);
   }

   public <S> Collection<S> find(Class<S> var1) {
      if (!this.baseClass.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Don't know how to search for " + var1);
      } else {
         List var2 = (List)this.byClass.computeIfAbsent(var1, (var1x) -> {
            Stream var10000 = this.allInstances.stream();
            Objects.requireNonNull(var1x);
            return (List)var10000.filter(var1x::isInstance).collect(Collectors.toList());
         });
         return Collections.unmodifiableCollection(var2);
      }
   }

   public Iterator<T> iterator() {
      return (Iterator)(this.allInstances.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allInstances.iterator()));
   }

   public List<T> getAllInstances() {
      return ImmutableList.copyOf(this.allInstances);
   }

   public int size() {
      return this.allInstances.size();
   }
}
