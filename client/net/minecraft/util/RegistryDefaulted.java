package net.minecraft.util;

public class RegistryDefaulted extends RegistrySimple {
   private final Object field_82597_b;

   public RegistryDefaulted(Object var1) {
      super();
      this.field_82597_b = var1;
   }

   @Override
   public Object func_82594_a(Object var1) {
      Object var2 = super.func_82594_a(var1);
      return var2 == null ? this.field_82597_b : var2;
   }
}
