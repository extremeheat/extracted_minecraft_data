package net.minecraft.util.context;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;

public class ContextKeySet {
   private final Set<ContextKey<?>> required;
   private final Set<ContextKey<?>> allowed;

   private ContextKeySet(final Set<ContextKey<?>> required, final Set<ContextKey<?>> optional) {
      super();
      this.required = Set.copyOf(required);
      this.allowed = Set.copyOf(Sets.union(required, optional));
   }

   public Set<ContextKey<?>> required() {
      return this.required;
   }

   public Set<ContextKey<?>> allowed() {
      return this.allowed;
   }

   public String toString() {
      Joiner var10000 = Joiner.on(", ");
      Iterator var10001 = this.allowed.stream().map((k) -> {
         String var10000 = this.required.contains(k) ? "!" : "";
         return var10000 + String.valueOf(k.name());
      }).iterator();
      return "[" + var10000.join(var10001) + "]";
   }

   public static class Builder {
      private final Set<ContextKey<?>> required = Sets.newIdentityHashSet();
      private final Set<ContextKey<?>> optional = Sets.newIdentityHashSet();

      public Builder() {
         super();
      }

      public Builder required(final ContextKey<?> param) {
         if (this.optional.contains(param)) {
            throw new IllegalArgumentException("Parameter " + String.valueOf(param.name()) + " is already optional");
         } else {
            this.required.add(param);
            return this;
         }
      }

      public Builder optional(final ContextKey<?> param) {
         if (this.required.contains(param)) {
            throw new IllegalArgumentException("Parameter " + String.valueOf(param.name()) + " is already required");
         } else {
            this.optional.add(param);
            return this;
         }
      }

      public ContextKeySet build() {
         return new ContextKeySet(this.required, this.optional);
      }
   }
}
