package net.minecraft.server.permissions;

import java.util.function.Predicate;

public record PermissionProviderCheck<T extends PermissionSetSupplier>(PermissionCheck test) implements Predicate<T> {
   public static final PermissionProviderCheck<?> ALWAYS_PASS;

   public PermissionProviderCheck {
      super();
   }

   public boolean test(final PermissionSetSupplier t) {
      return this.test.check(t.permissions());
   }

   public <S extends PermissionSetSupplier> PermissionProviderCheck<S> cast() {
      return this;
   }

   static {
      ALWAYS_PASS = new PermissionProviderCheck(PermissionCheck.AlwaysPass.INSTANCE);
   }
}
