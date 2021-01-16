package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Map.Entry;

@GwtIncompatible
public final class MutableClassToInstanceMap<B> extends ForwardingMap<Class<? extends B>, B> implements ClassToInstanceMap<B>, Serializable {
   private final Map<Class<? extends B>, B> delegate;

   public static <B> MutableClassToInstanceMap<B> create() {
      return new MutableClassToInstanceMap(new HashMap());
   }

   public static <B> MutableClassToInstanceMap<B> create(Map<Class<? extends B>, B> var0) {
      return new MutableClassToInstanceMap(var0);
   }

   private MutableClassToInstanceMap(Map<Class<? extends B>, B> var1) {
      super();
      this.delegate = (Map)Preconditions.checkNotNull(var1);
   }

   protected Map<Class<? extends B>, B> delegate() {
      return this.delegate;
   }

   private static <B> Entry<Class<? extends B>, B> checkedEntry(final Entry<Class<? extends B>, B> var0) {
      return new ForwardingMapEntry<Class<? extends B>, B>() {
         protected Entry<Class<? extends B>, B> delegate() {
            return var0;
         }

         public B setValue(B var1) {
            return super.setValue(MutableClassToInstanceMap.cast((Class)this.getKey(), var1));
         }
      };
   }

   public Set<Entry<Class<? extends B>, B>> entrySet() {
      return new ForwardingSet<Entry<Class<? extends B>, B>>() {
         protected Set<Entry<Class<? extends B>, B>> delegate() {
            return MutableClassToInstanceMap.this.delegate().entrySet();
         }

         public Spliterator<Entry<Class<? extends B>, B>> spliterator() {
            return CollectSpliterators.map(this.delegate().spliterator(), (var0) -> {
               return MutableClassToInstanceMap.checkedEntry(var0);
            });
         }

         public Iterator<Entry<Class<? extends B>, B>> iterator() {
            return new TransformedIterator<Entry<Class<? extends B>, B>, Entry<Class<? extends B>, B>>(this.delegate().iterator()) {
               Entry<Class<? extends B>, B> transform(Entry<Class<? extends B>, B> var1) {
                  return MutableClassToInstanceMap.checkedEntry(var1);
               }
            };
         }

         public Object[] toArray() {
            return this.standardToArray();
         }

         public <T> T[] toArray(T[] var1) {
            return this.standardToArray(var1);
         }
      };
   }

   @CanIgnoreReturnValue
   public B put(Class<? extends B> var1, B var2) {
      return super.put(var1, cast(var1, var2));
   }

   public void putAll(Map<? extends Class<? extends B>, ? extends B> var1) {
      LinkedHashMap var2 = new LinkedHashMap(var1);
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         cast((Class)var4.getKey(), var4.getValue());
      }

      super.putAll(var2);
   }

   @CanIgnoreReturnValue
   public <T extends B> T putInstance(Class<T> var1, T var2) {
      return cast(var1, this.put(var1, var2));
   }

   public <T extends B> T getInstance(Class<T> var1) {
      return cast(var1, this.get(var1));
   }

   @CanIgnoreReturnValue
   private static <B, T extends B> T cast(Class<T> var0, B var1) {
      return Primitives.wrap(var0).cast(var1);
   }

   private Object writeReplace() {
      return new MutableClassToInstanceMap.SerializedForm(this.delegate());
   }

   private static final class SerializedForm<B> implements Serializable {
      private final Map<Class<? extends B>, B> backingMap;
      private static final long serialVersionUID = 0L;

      SerializedForm(Map<Class<? extends B>, B> var1) {
         super();
         this.backingMap = var1;
      }

      Object readResolve() {
         return MutableClassToInstanceMap.create(this.backingMap);
      }
   }
}
