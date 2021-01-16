package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.j2objc.annotations.Weak;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;

final class SubscriberRegistry {
   private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers = Maps.newConcurrentMap();
   @Weak
   private final EventBus bus;
   private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, ImmutableList<Method>>() {
      public ImmutableList<Method> load(Class<?> var1) throws Exception {
         return SubscriberRegistry.getAnnotatedMethodsNotCached(var1);
      }
   });
   private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Class<?>, ImmutableSet<Class<?>>>() {
      public ImmutableSet<Class<?>> load(Class<?> var1) {
         return ImmutableSet.copyOf((Collection)TypeToken.of(var1).getTypes().rawTypes());
      }
   });

   SubscriberRegistry(EventBus var1) {
      super();
      this.bus = (EventBus)Preconditions.checkNotNull(var1);
   }

   void register(Object var1) {
      Multimap var2 = this.findAllSubscribers(var1);

      Collection var6;
      CopyOnWriteArraySet var7;
      for(Iterator var3 = var2.asMap().entrySet().iterator(); var3.hasNext(); var7.addAll(var6)) {
         Entry var4 = (Entry)var3.next();
         Class var5 = (Class)var4.getKey();
         var6 = (Collection)var4.getValue();
         var7 = (CopyOnWriteArraySet)this.subscribers.get(var5);
         if (var7 == null) {
            CopyOnWriteArraySet var8 = new CopyOnWriteArraySet();
            var7 = (CopyOnWriteArraySet)MoreObjects.firstNonNull(this.subscribers.putIfAbsent(var5, var8), var8);
         }
      }

   }

   void unregister(Object var1) {
      Multimap var2 = this.findAllSubscribers(var1);
      Iterator var3 = var2.asMap().entrySet().iterator();

      Collection var6;
      CopyOnWriteArraySet var7;
      do {
         if (!var3.hasNext()) {
            return;
         }

         Entry var4 = (Entry)var3.next();
         Class var5 = (Class)var4.getKey();
         var6 = (Collection)var4.getValue();
         var7 = (CopyOnWriteArraySet)this.subscribers.get(var5);
      } while(var7 != null && var7.removeAll(var6));

      throw new IllegalArgumentException("missing event subscriber for an annotated method. Is " + var1 + " registered?");
   }

   @VisibleForTesting
   Set<Subscriber> getSubscribersForTesting(Class<?> var1) {
      return (Set)MoreObjects.firstNonNull(this.subscribers.get(var1), ImmutableSet.of());
   }

   Iterator<Subscriber> getSubscribers(Object var1) {
      ImmutableSet var2 = flattenHierarchy(var1.getClass());
      ArrayList var3 = Lists.newArrayListWithCapacity(var2.size());
      UnmodifiableIterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Class var5 = (Class)var4.next();
         CopyOnWriteArraySet var6 = (CopyOnWriteArraySet)this.subscribers.get(var5);
         if (var6 != null) {
            var3.add(var6.iterator());
         }
      }

      return Iterators.concat(var3.iterator());
   }

   private Multimap<Class<?>, Subscriber> findAllSubscribers(Object var1) {
      HashMultimap var2 = HashMultimap.create();
      Class var3 = var1.getClass();
      UnmodifiableIterator var4 = getAnnotatedMethods(var3).iterator();

      while(var4.hasNext()) {
         Method var5 = (Method)var4.next();
         Class[] var6 = var5.getParameterTypes();
         Class var7 = var6[0];
         var2.put(var7, Subscriber.create(this.bus, var1, var5));
      }

      return var2;
   }

   private static ImmutableList<Method> getAnnotatedMethods(Class<?> var0) {
      return (ImmutableList)subscriberMethodsCache.getUnchecked(var0);
   }

   private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> var0) {
      Set var1 = TypeToken.of(var0).getTypes().rawTypes();
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Class var4 = (Class)var3.next();
         Method[] var5 = var4.getDeclaredMethods();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Method var8 = var5[var7];
            if (var8.isAnnotationPresent(Subscribe.class) && !var8.isSynthetic()) {
               Class[] var9 = var8.getParameterTypes();
               Preconditions.checkArgument(var9.length == 1, "Method %s has @Subscribe annotation but has %s parameters.Subscriber methods must have exactly 1 parameter.", var8, (int)var9.length);
               SubscriberRegistry.MethodIdentifier var10 = new SubscriberRegistry.MethodIdentifier(var8);
               if (!var2.containsKey(var10)) {
                  var2.put(var10, var8);
               }
            }
         }
      }

      return ImmutableList.copyOf(var2.values());
   }

   @VisibleForTesting
   static ImmutableSet<Class<?>> flattenHierarchy(Class<?> var0) {
      try {
         return (ImmutableSet)flattenHierarchyCache.getUnchecked(var0);
      } catch (UncheckedExecutionException var2) {
         throw Throwables.propagate(var2.getCause());
      }
   }

   private static final class MethodIdentifier {
      private final String name;
      private final List<Class<?>> parameterTypes;

      MethodIdentifier(Method var1) {
         super();
         this.name = var1.getName();
         this.parameterTypes = Arrays.asList(var1.getParameterTypes());
      }

      public int hashCode() {
         return Objects.hashCode(this.name, this.parameterTypes);
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof SubscriberRegistry.MethodIdentifier)) {
            return false;
         } else {
            SubscriberRegistry.MethodIdentifier var2 = (SubscriberRegistry.MethodIdentifier)var1;
            return this.name.equals(var2.name) && this.parameterTypes.equals(var2.parameterTypes);
         }
      }
   }
}
