package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class MoreCollectors {
   private static final Collector<Object, ?, Optional<Object>> TO_OPTIONAL;
   private static final Object NULL_PLACEHOLDER;
   private static final Collector<Object, ?, Object> ONLY_ELEMENT;

   public static <T> Collector<T, ?, Optional<T>> toOptional() {
      return TO_OPTIONAL;
   }

   public static <T> Collector<T, ?, T> onlyElement() {
      return ONLY_ELEMENT;
   }

   private MoreCollectors() {
      super();
   }

   static {
      TO_OPTIONAL = Collector.of(MoreCollectors.ToOptionalState::new, MoreCollectors.ToOptionalState::add, MoreCollectors.ToOptionalState::combine, MoreCollectors.ToOptionalState::getOptional, Characteristics.UNORDERED);
      NULL_PLACEHOLDER = new Object();
      ONLY_ELEMENT = Collector.of(MoreCollectors.ToOptionalState::new, (var0, var1) -> {
         var0.add(var1 == null ? NULL_PLACEHOLDER : var1);
      }, MoreCollectors.ToOptionalState::combine, (var0) -> {
         Object var1 = var0.getElement();
         return var1 == NULL_PLACEHOLDER ? null : var1;
      }, Characteristics.UNORDERED);
   }

   private static final class ToOptionalState {
      static final int MAX_EXTRAS = 4;
      @Nullable
      Object element = null;
      @Nullable
      List<Object> extras = null;

      ToOptionalState() {
         super();
      }

      IllegalArgumentException multiples(boolean var1) {
         StringBuilder var2 = (new StringBuilder()).append("expected one element but was: <").append(this.element);
         Iterator var3 = this.extras.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            var2.append(", ").append(var4);
         }

         if (var1) {
            var2.append(", ...");
         }

         var2.append('>');
         throw new IllegalArgumentException(var2.toString());
      }

      void add(Object var1) {
         Preconditions.checkNotNull(var1);
         if (this.element == null) {
            this.element = var1;
         } else if (this.extras == null) {
            this.extras = new ArrayList(4);
            this.extras.add(var1);
         } else {
            if (this.extras.size() >= 4) {
               throw this.multiples(true);
            }

            this.extras.add(var1);
         }

      }

      MoreCollectors.ToOptionalState combine(MoreCollectors.ToOptionalState var1) {
         if (this.element == null) {
            return var1;
         } else if (var1.element == null) {
            return this;
         } else {
            if (this.extras == null) {
               this.extras = new ArrayList();
            }

            this.extras.add(var1.element);
            if (var1.extras != null) {
               this.extras.addAll(var1.extras);
            }

            if (this.extras.size() > 4) {
               this.extras.subList(4, this.extras.size()).clear();
               throw this.multiples(true);
            } else {
               return this;
            }
         }
      }

      Optional<Object> getOptional() {
         if (this.extras == null) {
            return Optional.ofNullable(this.element);
         } else {
            throw this.multiples(false);
         }
      }

      Object getElement() {
         if (this.element == null) {
            throw new NoSuchElementException();
         } else if (this.extras == null) {
            return this.element;
         } else {
            throw this.multiples(false);
         }
      }
   }
}
