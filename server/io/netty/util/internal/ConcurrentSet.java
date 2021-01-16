package io.netty.util.internal;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

public final class ConcurrentSet<E> extends AbstractSet<E> implements Serializable {
   private static final long serialVersionUID = -6761513279741915432L;
   private final ConcurrentMap<E, Boolean> map = PlatformDependent.newConcurrentHashMap();

   public ConcurrentSet() {
      super();
   }

   public int size() {
      return this.map.size();
   }

   public boolean contains(Object var1) {
      return this.map.containsKey(var1);
   }

   public boolean add(E var1) {
      return this.map.putIfAbsent(var1, Boolean.TRUE) == null;
   }

   public boolean remove(Object var1) {
      return this.map.remove(var1) != null;
   }

   public void clear() {
      this.map.clear();
   }

   public Iterator<E> iterator() {
      return this.map.keySet().iterator();
   }
}
