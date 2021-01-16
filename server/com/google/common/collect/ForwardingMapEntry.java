package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMapEntry<K, V> extends ForwardingObject implements Entry<K, V> {
   protected ForwardingMapEntry() {
      super();
   }

   protected abstract Entry<K, V> delegate();

   public K getKey() {
      return this.delegate().getKey();
   }

   public V getValue() {
      return this.delegate().getValue();
   }

   public V setValue(V var1) {
      return this.delegate().setValue(var1);
   }

   public boolean equals(@Nullable Object var1) {
      return this.delegate().equals(var1);
   }

   public int hashCode() {
      return this.delegate().hashCode();
   }

   protected boolean standardEquals(@Nullable Object var1) {
      if (!(var1 instanceof Entry)) {
         return false;
      } else {
         Entry var2 = (Entry)var1;
         return Objects.equal(this.getKey(), var2.getKey()) && Objects.equal(this.getValue(), var2.getValue());
      }
   }

   protected int standardHashCode() {
      Object var1 = this.getKey();
      Object var2 = this.getValue();
      return (var1 == null ? 0 : var1.hashCode()) ^ (var2 == null ? 0 : var2.hashCode());
   }

   @Beta
   protected String standardToString() {
      return this.getKey() + "=" + this.getValue();
   }
}
