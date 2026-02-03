package net.minecraft.server.permissions;

import java.util.function.Predicate;

public record PermissionProviderCheck<T extends PermissionSetSupplier>(PermissionCheck test) implements Predicate<T> {
   public PermissionProviderCheck {
      super();
   }

   public boolean test(final T t) {
      return this.test.check(t.permissions());
   }
}
