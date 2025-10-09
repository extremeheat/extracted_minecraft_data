package net.minecraft.server.permissions;

import java.util.function.Predicate;

public record PermissionProviderCheck<T extends PermissionSetSupplier>(PermissionCheck test) implements Predicate<T> {
   public PermissionProviderCheck(PermissionCheck var1) {
      super();
      this.test = var1;
   }

   public boolean test(T var1) {
      return this.test.check(var1.permissions());
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((PermissionSetSupplier)var1);
   }
}
