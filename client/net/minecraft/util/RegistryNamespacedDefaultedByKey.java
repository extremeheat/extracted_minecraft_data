package net.minecraft.util;

public class RegistryNamespacedDefaultedByKey extends RegistryNamespaced {
   private final String field_148760_d;
   private Object field_148761_e;

   public RegistryNamespacedDefaultedByKey(String var1) {
      super();
      this.field_148760_d = var1;
   }

   @Override
   public void func_148756_a(int var1, String var2, Object var3) {
      if (this.field_148760_d.equals(var2)) {
         this.field_148761_e = var3;
      }

      super.func_148756_a(var1, var2, var3);
   }

   @Override
   public Object func_82594_a(String var1) {
      Object var2 = super.func_82594_a(var1);
      return var2 == null ? this.field_148761_e : var2;
   }

   @Override
   public Object func_148754_a(int var1) {
      Object var2 = super.func_148754_a(var1);
      return var2 == null ? this.field_148761_e : var2;
   }
}
