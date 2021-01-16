package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingObject {
   protected ForwardingObject() {
      super();
   }

   protected abstract Object delegate();

   public String toString() {
      return this.delegate().toString();
   }
}
