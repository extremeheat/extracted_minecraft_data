package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@GwtCompatible
public abstract class UnmodifiableListIterator<E> extends UnmodifiableIterator<E> implements ListIterator<E> {
   protected UnmodifiableListIterator() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public final void add(E var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void set(E var1) {
      throw new UnsupportedOperationException();
   }
}
