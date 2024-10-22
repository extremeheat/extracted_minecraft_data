package net.minecraft.util.context;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import org.jetbrains.annotations.Contract;

public class ContextMap {
   private final Map<ContextKey<?>, Object> params;

   ContextMap(Map<ContextKey<?>, Object> var1) {
      super();
      this.params = var1;
   }

   public boolean has(ContextKey<?> var1) {
      return this.params.containsKey(var1);
   }

   public <T> T getOrThrow(ContextKey<T> var1) {
      Object var2 = this.params.get(var1);
      if (var2 == null) {
         throw new NoSuchElementException(var1.name().toString());
      } else {
         return (T)var2;
      }
   }

   @Nullable
   public <T> T getOptional(ContextKey<T> var1) {
      return (T)this.params.get(var1);
   }

   @Nullable
   @Contract("_,!null->!null; _,_->_")
   public <T> T getOrDefault(ContextKey<T> var1, @Nullable T var2) {
      return (T)this.params.getOrDefault(var1, var2);
   }

   public static class Builder {
      private final Map<ContextKey<?>, Object> params = new IdentityHashMap<>();

      public Builder() {
         super();
      }

      public <T> ContextMap.Builder withParameter(ContextKey<T> var1, T var2) {
         this.params.put(var1, var2);
         return this;
      }

      public <T> ContextMap.Builder withOptionalParameter(ContextKey<T> var1, @Nullable T var2) {
         if (var2 == null) {
            this.params.remove(var1);
         } else {
            this.params.put(var1, var2);
         }

         return this;
      }

      public <T> T getParameter(ContextKey<T> var1) {
         Object var2 = this.params.get(var1);
         if (var2 == null) {
            throw new NoSuchElementException(var1.name().toString());
         } else {
            return (T)var2;
         }
      }

      @Nullable
      public <T> T getOptionalParameter(ContextKey<T> var1) {
         return (T)this.params.get(var1);
      }

      public ContextMap create(ContextKeySet var1) {
         SetView var2 = Sets.difference(this.params.keySet(), var1.allowed());
         if (!var2.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + var2);
         } else {
            SetView var3 = Sets.difference(var1.required(), this.params.keySet());
            if (!var3.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + var3);
            } else {
               return new ContextMap(this.params);
            }
         }
      }
   }
}
