package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.UnmodifiableIterator;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

abstract class MultiEdgesConnecting<E> extends AbstractSet<E> {
   private final Map<E, ?> outEdgeToNode;
   private final Object targetNode;

   MultiEdgesConnecting(Map<E, ?> var1, Object var2) {
      super();
      this.outEdgeToNode = (Map)Preconditions.checkNotNull(var1);
      this.targetNode = Preconditions.checkNotNull(var2);
   }

   public UnmodifiableIterator<E> iterator() {
      final Iterator var1 = this.outEdgeToNode.entrySet().iterator();
      return new AbstractIterator<E>() {
         protected E computeNext() {
            while(true) {
               if (var1.hasNext()) {
                  Entry var1x = (Entry)var1.next();
                  if (!MultiEdgesConnecting.this.targetNode.equals(var1x.getValue())) {
                     continue;
                  }

                  return var1x.getKey();
               }

               return this.endOfData();
            }
         }
      };
   }

   public boolean contains(@Nullable Object var1) {
      return this.targetNode.equals(this.outEdgeToNode.get(var1));
   }
}
