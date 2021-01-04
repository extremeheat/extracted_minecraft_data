package net.minecraft.core;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapFiller {
   public static <K, V> Map<K, V> linkedHashMapFrom(Iterable<K> var0, Iterable<V> var1) {
      return from(var0, var1, Maps.newLinkedHashMap());
   }

   public static <K, V> Map<K, V> from(Iterable<K> var0, Iterable<V> var1, Map<K, V> var2) {
      Iterator var3 = var1.iterator();
      Iterator var4 = var0.iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         var2.put(var5, var3.next());
      }

      if (var3.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return var2;
      }
   }
}
