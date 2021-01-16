package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
public interface SetMultimap<K, V> extends Multimap<K, V> {
   Set<V> get(@Nullable K var1);

   @CanIgnoreReturnValue
   Set<V> removeAll(@Nullable Object var1);

   @CanIgnoreReturnValue
   Set<V> replaceValues(K var1, Iterable<? extends V> var2);

   Set<Entry<K, V>> entries();

   Map<K, Collection<V>> asMap();

   boolean equals(@Nullable Object var1);
}
