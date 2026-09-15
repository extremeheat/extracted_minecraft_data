package net.minecraft.util.context;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public final class ContextMap {
   public static final ContextMap EMPTY = new ContextMap(Map.of());
   private final Map<ContextKey<?>, Object> params;

   private ContextMap(final Map<ContextKey<?>, Object> params) {
      super();
      this.params = params;
   }

   public static Builder builder() {
      return new Builder();
   }

   public boolean has(final ContextKey<?> key) {
      return this.params.containsKey(key);
   }

   public <T> T getOrThrow(final ContextKey<T> key) {
      T value = (T)this.get(key);
      if (value == null) {
         throw new NoSuchElementException(key.name().toString());
      } else {
         return value;
      }
   }

   public <T> @Nullable T get(final ContextKey<T> key) {
      return (T)this.params.get(key);
   }

   @Contract("_,!null->!null; _,_->_")
   public <T> @Nullable T getOrDefault(final ContextKey<T> param, final @Nullable T _default) {
      return (T)this.params.getOrDefault(param, _default);
   }

   public static class Builder {
      private final Map<ContextKey<?>, Object> params = new Reference2ObjectOpenHashMap();

      private Builder() {
         super();
      }

      public <T> Builder set(final ContextKey<T> param, final @Nullable T value) {
         if (value == null) {
            this.params.remove(param);
         } else {
            this.params.put(param, value);
         }

         return this;
      }

      public <T> @Nullable T get(final ContextKey<T> param) {
         return (T)this.params.get(param);
      }

      public ContextMap build() {
         return this.params.isEmpty() ? ContextMap.EMPTY : new ContextMap(new Reference2ObjectOpenHashMap(this.params));
      }

      public ContextMap buildAndValidate(final ContextKeySet paramSet) {
         Set<ContextKey<?>> notAllowed = Sets.difference(this.params.keySet(), paramSet.allowed());
         if (!notAllowed.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + String.valueOf(notAllowed));
         } else {
            Set<ContextKey<?>> missingRequired = Sets.difference(paramSet.required(), this.params.keySet());
            if (!missingRequired.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + String.valueOf(missingRequired));
            } else {
               return this.build();
            }
         }
      }
   }
}
