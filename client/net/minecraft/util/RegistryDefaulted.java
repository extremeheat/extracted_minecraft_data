package net.minecraft.util;

public class RegistryDefaulted<K, V> extends RegistrySimple<K, V> {
   private final V field_82597_b;

   public RegistryDefaulted(V var1) {
      super();
      this.field_82597_b = var1;
   }

   public V func_82594_a(K var1) {
      Object var2 = super.func_82594_a(var1);
      return var2 == null ? this.field_82597_b : var2;
   }
}
