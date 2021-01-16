package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

@GwtIncompatible
public final class ConcurrentHashMultiset<E> extends AbstractMultiset<E> implements Serializable {
   private final transient ConcurrentMap<E, AtomicInteger> countMap;
   private static final long serialVersionUID = 1L;

   public static <E> ConcurrentHashMultiset<E> create() {
      return new ConcurrentHashMultiset(new ConcurrentHashMap());
   }

   public static <E> ConcurrentHashMultiset<E> create(Iterable<? extends E> var0) {
      ConcurrentHashMultiset var1 = create();
      Iterables.addAll(var1, var0);
      return var1;
   }

   @Beta
   public static <E> ConcurrentHashMultiset<E> create(ConcurrentMap<E, AtomicInteger> var0) {
      return new ConcurrentHashMultiset(var0);
   }

   @VisibleForTesting
   ConcurrentHashMultiset(ConcurrentMap<E, AtomicInteger> var1) {
      super();
      Preconditions.checkArgument(var1.isEmpty(), "the backing map (%s) must be empty", (Object)var1);
      this.countMap = var1;
   }

   public int count(@Nullable Object var1) {
      AtomicInteger var2 = (AtomicInteger)Maps.safeGet(this.countMap, var1);
      return var2 == null ? 0 : var2.get();
   }

   public int size() {
      long var1 = 0L;

      AtomicInteger var4;
      for(Iterator var3 = this.countMap.values().iterator(); var3.hasNext(); var1 += (long)var4.get()) {
         var4 = (AtomicInteger)var3.next();
      }

      return Ints.saturatedCast(var1);
   }

   public Object[] toArray() {
      return this.snapshot().toArray();
   }

   public <T> T[] toArray(T[] var1) {
      return this.snapshot().toArray(var1);
   }

   private List<E> snapshot() {
      ArrayList var1 = Lists.newArrayListWithExpectedSize(this.size());
      Iterator var2 = this.entrySet().iterator();

      while(var2.hasNext()) {
         Multiset.Entry var3 = (Multiset.Entry)var2.next();
         Object var4 = var3.getElement();

         for(int var5 = var3.getCount(); var5 > 0; --var5) {
            var1.add(var4);
         }
      }

      return var1;
   }

   @CanIgnoreReturnValue
   public int add(E var1, int var2) {
      Preconditions.checkNotNull(var1);
      if (var2 == 0) {
         return this.count(var1);
      } else {
         CollectPreconditions.checkPositive(var2, "occurences");

         AtomicInteger var3;
         AtomicInteger var7;
         do {
            var3 = (AtomicInteger)Maps.safeGet(this.countMap, var1);
            if (var3 == null) {
               var3 = (AtomicInteger)this.countMap.putIfAbsent(var1, new AtomicInteger(var2));
               if (var3 == null) {
                  return 0;
               }
            }

            while(true) {
               int var4 = var3.get();
               if (var4 == 0) {
                  var7 = new AtomicInteger(var2);
                  break;
               }

               try {
                  int var5 = IntMath.checkedAdd(var4, var2);
                  if (var3.compareAndSet(var4, var5)) {
                     return var4;
                  }
               } catch (ArithmeticException var6) {
                  throw new IllegalArgumentException("Overflow adding " + var2 + " occurrences to a count of " + var4);
               }
            }
         } while(this.countMap.putIfAbsent(var1, var7) != null && !this.countMap.replace(var1, var3, var7));

         return 0;
      }
   }

   @CanIgnoreReturnValue
   public int remove(@Nullable Object var1, int var2) {
      if (var2 == 0) {
         return this.count(var1);
      } else {
         CollectPreconditions.checkPositive(var2, "occurences");
         AtomicInteger var3 = (AtomicInteger)Maps.safeGet(this.countMap, var1);
         if (var3 == null) {
            return 0;
         } else {
            int var4;
            int var5;
            do {
               var4 = var3.get();
               if (var4 == 0) {
                  return 0;
               }

               var5 = Math.max(0, var4 - var2);
            } while(!var3.compareAndSet(var4, var5));

            if (var5 == 0) {
               this.countMap.remove(var1, var3);
            }

            return var4;
         }
      }
   }

   @CanIgnoreReturnValue
   public boolean removeExactly(@Nullable Object var1, int var2) {
      if (var2 == 0) {
         return true;
      } else {
         CollectPreconditions.checkPositive(var2, "occurences");
         AtomicInteger var3 = (AtomicInteger)Maps.safeGet(this.countMap, var1);
         if (var3 == null) {
            return false;
         } else {
            int var4;
            int var5;
            do {
               var4 = var3.get();
               if (var4 < var2) {
                  return false;
               }

               var5 = var4 - var2;
            } while(!var3.compareAndSet(var4, var5));

            if (var5 == 0) {
               this.countMap.remove(var1, var3);
            }

            return true;
         }
      }
   }

   @CanIgnoreReturnValue
   public int setCount(E var1, int var2) {
      Preconditions.checkNotNull(var1);
      CollectPreconditions.checkNonnegative(var2, "count");

      AtomicInteger var3;
      AtomicInteger var5;
      label40:
      do {
         var3 = (AtomicInteger)Maps.safeGet(this.countMap, var1);
         if (var3 == null) {
            if (var2 == 0) {
               return 0;
            }

            var3 = (AtomicInteger)this.countMap.putIfAbsent(var1, new AtomicInteger(var2));
            if (var3 == null) {
               return 0;
            }
         }

         int var4;
         do {
            var4 = var3.get();
            if (var4 == 0) {
               if (var2 == 0) {
                  return 0;
               }

               var5 = new AtomicInteger(var2);
               continue label40;
            }
         } while(!var3.compareAndSet(var4, var2));

         if (var2 == 0) {
            this.countMap.remove(var1, var3);
         }

         return var4;
      } while(this.countMap.putIfAbsent(var1, var5) != null && !this.countMap.replace(var1, var3, var5));

      return 0;
   }

   @CanIgnoreReturnValue
   public boolean setCount(E var1, int var2, int var3) {
      Preconditions.checkNotNull(var1);
      CollectPreconditions.checkNonnegative(var2, "oldCount");
      CollectPreconditions.checkNonnegative(var3, "newCount");
      AtomicInteger var4 = (AtomicInteger)Maps.safeGet(this.countMap, var1);
      if (var4 == null) {
         if (var2 != 0) {
            return false;
         } else if (var3 == 0) {
            return true;
         } else {
            return this.countMap.putIfAbsent(var1, new AtomicInteger(var3)) == null;
         }
      } else {
         int var5 = var4.get();
         if (var5 == var2) {
            if (var5 == 0) {
               if (var3 == 0) {
                  this.countMap.remove(var1, var4);
                  return true;
               }

               AtomicInteger var6 = new AtomicInteger(var3);
               return this.countMap.putIfAbsent(var1, var6) == null || this.countMap.replace(var1, var4, var6);
            }

            if (var4.compareAndSet(var5, var3)) {
               if (var3 == 0) {
                  this.countMap.remove(var1, var4);
               }

               return true;
            }
         }

         return false;
      }
   }

   Set<E> createElementSet() {
      final Set var1 = this.countMap.keySet();
      return new ForwardingSet<E>() {
         protected Set<E> delegate() {
            return var1;
         }

         public boolean contains(@Nullable Object var1x) {
            return var1x != null && Collections2.safeContains(var1, var1x);
         }

         public boolean containsAll(Collection<?> var1x) {
            return this.standardContainsAll(var1x);
         }

         public boolean remove(Object var1x) {
            return var1x != null && Collections2.safeRemove(var1, var1x);
         }

         public boolean removeAll(Collection<?> var1x) {
            return this.standardRemoveAll(var1x);
         }
      };
   }

   public Set<Multiset.Entry<E>> createEntrySet() {
      return new ConcurrentHashMultiset.EntrySet();
   }

   int distinctElements() {
      return this.countMap.size();
   }

   public boolean isEmpty() {
      return this.countMap.isEmpty();
   }

   Iterator<Multiset.Entry<E>> entryIterator() {
      final AbstractIterator var1 = new AbstractIterator<Multiset.Entry<E>>() {
         private final Iterator<java.util.Map.Entry<E, AtomicInteger>> mapEntries;

         {
            this.mapEntries = ConcurrentHashMultiset.this.countMap.entrySet().iterator();
         }

         protected Multiset.Entry<E> computeNext() {
            java.util.Map.Entry var1;
            int var2;
            do {
               if (!this.mapEntries.hasNext()) {
                  return (Multiset.Entry)this.endOfData();
               }

               var1 = (java.util.Map.Entry)this.mapEntries.next();
               var2 = ((AtomicInteger)var1.getValue()).get();
            } while(var2 == 0);

            return Multisets.immutableEntry(var1.getKey(), var2);
         }
      };
      return new ForwardingIterator<Multiset.Entry<E>>() {
         private Multiset.Entry<E> last;

         protected Iterator<Multiset.Entry<E>> delegate() {
            return var1;
         }

         public Multiset.Entry<E> next() {
            this.last = (Multiset.Entry)super.next();
            return this.last;
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.last != null);
            ConcurrentHashMultiset.this.setCount(this.last.getElement(), 0);
            this.last = null;
         }
      };
   }

   public void clear() {
      this.countMap.clear();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.countMap);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      ConcurrentMap var2 = (ConcurrentMap)var1.readObject();
      ConcurrentHashMultiset.FieldSettersHolder.COUNT_MAP_FIELD_SETTER.set(this, var2);
   }

   private class EntrySet extends AbstractMultiset<E>.EntrySet {
      private EntrySet() {
         super();
      }

      ConcurrentHashMultiset<E> multiset() {
         return ConcurrentHashMultiset.this;
      }

      public Object[] toArray() {
         return this.snapshot().toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return this.snapshot().toArray(var1);
      }

      private List<Multiset.Entry<E>> snapshot() {
         ArrayList var1 = Lists.newArrayListWithExpectedSize(this.size());
         Iterators.addAll(var1, this.iterator());
         return var1;
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }

   private static class FieldSettersHolder {
      static final Serialization.FieldSetter<ConcurrentHashMultiset> COUNT_MAP_FIELD_SETTER = Serialization.getFieldSetter(ConcurrentHashMultiset.class, "countMap");

      private FieldSettersHolder() {
         super();
      }
   }
}
