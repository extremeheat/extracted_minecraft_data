package net.minecraft.util.context;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;

public class ContextKeySet {
   private final Set<ContextKey<?>> required;
   private final Set<ContextKey<?>> allowed;

   ContextKeySet(Set<ContextKey<?>> var1, Set<ContextKey<?>> var2) {
      super();
      this.required = Set.copyOf(var1);
      this.allowed = Set.copyOf(Sets.union(var1, var2));
   }

   public Set<ContextKey<?>> required() {
      return this.required;
   }

   public Set<ContextKey<?>> allowed() {
      return this.allowed;
   }

   public String toString() {
      Joiner var10000 = Joiner.on(", ");
      Iterator var10001 = this.allowed.stream().map((var1) -> {
         String var10000 = this.required.contains(var1) ? "!" : "";
         return var10000 + String.valueOf(var1.name());
      }).iterator();
      return "[" + var10000.join(var10001) + "]";
   }

   public static class Builder {
      private final Set<ContextKey<?>> required = Sets.newIdentityHashSet();
      private final Set<ContextKey<?>> optional = Sets.newIdentityHashSet();

      public Builder() {
         super();
      }

      public Builder required(ContextKey<?> var1) {
         if (this.optional.contains(var1)) {
            throw new IllegalArgumentException("Parameter " + String.valueOf(var1.name()) + " is already optional");
         } else {
            this.required.add(var1);
            return this;
         }
      }

      public Builder optional(ContextKey<?> var1) {
         if (this.required.contains(var1)) {
            throw new IllegalArgumentException("Parameter " + String.valueOf(var1.name()) + " is already required");
         } else {
            this.optional.add(var1);
            return this;
         }
      }

      public ContextKeySet build() {
         return new ContextKeySet(this.required, this.optional);
      }
   }
}
