package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V> extends RegistryNamespaced<K, V> {
   private final K field_148760_d;
   private V field_148761_e;

   public RegistryNamespacedDefaultedByKey(K var1) {
      super();
      this.field_148760_d = var1;
   }

   public void func_177775_a(int var1, K var2, V var3) {
      if (this.field_148760_d.equals(var2)) {
         this.field_148761_e = var3;
      }

      super.func_177775_a(var1, var2, var3);
   }

   public void func_177776_a() {
      Validate.notNull(this.field_148760_d);
   }

   public V func_82594_a(K var1) {
      Object var2 = super.func_82594_a(var1);
      return var2 == null ? this.field_148761_e : var2;
   }

   public V func_148754_a(int var1) {
      Object var2 = super.func_148754_a(var1);
      return var2 == null ? this.field_148761_e : var2;
   }
}
