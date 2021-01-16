package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class ArrayListMultimap<K, V> extends AbstractListMultimap<K, V> {
   private static final int DEFAULT_VALUES_PER_KEY = 3;
   @VisibleForTesting
   transient int expectedValuesPerKey;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <K, V> ArrayListMultimap<K, V> create() {
      return new ArrayListMultimap();
   }

   public static <K, V> ArrayListMultimap<K, V> create(int var0, int var1) {
      return new ArrayListMultimap(var0, var1);
   }

   public static <K, V> ArrayListMultimap<K, V> create(Multimap<? extends K, ? extends V> var0) {
      return new ArrayListMultimap(var0);
   }

   private ArrayListMultimap() {
      super(new HashMap());
      this.expectedValuesPerKey = 3;
   }

   private ArrayListMultimap(int var1, int var2) {
      super(Maps.newHashMapWithExpectedSize(var1));
      CollectPreconditions.checkNonnegative(var2, "expectedValuesPerKey");
      this.expectedValuesPerKey = var2;
   }

   private ArrayListMultimap(Multimap<? extends K, ? extends V> var1) {
      this(var1.keySet().size(), var1 instanceof ArrayListMultimap ? ((ArrayListMultimap)var1).expectedValuesPerKey : 3);
      this.putAll(var1);
   }

   List<V> createCollection() {
      return new ArrayList(this.expectedValuesPerKey);
   }

   /** @deprecated */
   @Deprecated
   public void trimToSize() {
      Iterator var1 = this.backingMap().values().iterator();

      while(var1.hasNext()) {
         Collection var2 = (Collection)var1.next();
         ArrayList var3 = (ArrayList)var2;
         var3.trimToSize();
      }

   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Serialization.writeMultimap(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.expectedValuesPerKey = 3;
      int var2 = Serialization.readCount(var1);
      HashMap var3 = Maps.newHashMap();
      this.setMap(var3);
      Serialization.populateMultimap(this, var1, var2);
   }
}
