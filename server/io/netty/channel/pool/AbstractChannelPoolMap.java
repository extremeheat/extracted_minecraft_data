package io.netty.channel.pool;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractChannelPoolMap<K, P extends ChannelPool> implements ChannelPoolMap<K, P>, Iterable<Entry<K, P>>, Closeable {
   private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();

   public AbstractChannelPoolMap() {
      super();
   }

   public final P get(K var1) {
      ChannelPool var2 = (ChannelPool)this.map.get(ObjectUtil.checkNotNull(var1, "key"));
      if (var2 == null) {
         var2 = this.newPool(var1);
         ChannelPool var3 = (ChannelPool)this.map.putIfAbsent(var1, var2);
         if (var3 != null) {
            var2.close();
            var2 = var3;
         }
      }

      return var2;
   }

   public final boolean remove(K var1) {
      ChannelPool var2 = (ChannelPool)this.map.remove(ObjectUtil.checkNotNull(var1, "key"));
      if (var2 != null) {
         var2.close();
         return true;
      } else {
         return false;
      }
   }

   public final Iterator<Entry<K, P>> iterator() {
      return new ReadOnlyIterator(this.map.entrySet().iterator());
   }

   public final int size() {
      return this.map.size();
   }

   public final boolean isEmpty() {
      return this.map.isEmpty();
   }

   public final boolean contains(K var1) {
      return this.map.containsKey(ObjectUtil.checkNotNull(var1, "key"));
   }

   protected abstract P newPool(K var1);

   public final void close() {
      Iterator var1 = this.map.keySet().iterator();

      while(var1.hasNext()) {
         Object var2 = var1.next();
         this.remove(var2);
      }

   }
}
