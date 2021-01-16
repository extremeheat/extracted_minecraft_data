package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
public final class ElementOrder<T> {
   private final ElementOrder.Type type;
   @Nullable
   private final Comparator<T> comparator;

   private ElementOrder(ElementOrder.Type var1, @Nullable Comparator<T> var2) {
      super();
      this.type = (ElementOrder.Type)Preconditions.checkNotNull(var1);
      this.comparator = var2;
      Preconditions.checkState(var1 == ElementOrder.Type.SORTED == (var2 != null));
   }

   public static <S> ElementOrder<S> unordered() {
      return new ElementOrder(ElementOrder.Type.UNORDERED, (Comparator)null);
   }

   public static <S> ElementOrder<S> insertion() {
      return new ElementOrder(ElementOrder.Type.INSERTION, (Comparator)null);
   }

   public static <S extends Comparable<? super S>> ElementOrder<S> natural() {
      return new ElementOrder(ElementOrder.Type.SORTED, Ordering.natural());
   }

   public static <S> ElementOrder<S> sorted(Comparator<S> var0) {
      return new ElementOrder(ElementOrder.Type.SORTED, var0);
   }

   public ElementOrder.Type type() {
      return this.type;
   }

   public Comparator<T> comparator() {
      if (this.comparator != null) {
         return this.comparator;
      } else {
         throw new UnsupportedOperationException("This ordering does not define a comparator.");
      }
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ElementOrder)) {
         return false;
      } else {
         ElementOrder var2 = (ElementOrder)var1;
         return this.type == var2.type && Objects.equal(this.comparator, var2.comparator);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.type, this.comparator);
   }

   public String toString() {
      MoreObjects.ToStringHelper var1 = MoreObjects.toStringHelper((Object)this).add("type", this.type);
      if (this.comparator != null) {
         var1.add("comparator", this.comparator);
      }

      return var1.toString();
   }

   <K extends T, V> Map<K, V> createMap(int var1) {
      switch(this.type) {
      case UNORDERED:
         return Maps.newHashMapWithExpectedSize(var1);
      case INSERTION:
         return Maps.newLinkedHashMapWithExpectedSize(var1);
      case SORTED:
         return Maps.newTreeMap(this.comparator());
      default:
         throw new AssertionError();
      }
   }

   <T1 extends T> ElementOrder<T1> cast() {
      return this;
   }

   public static enum Type {
      UNORDERED,
      INSERTION,
      SORTED;

      private Type() {
      }
   }
}
