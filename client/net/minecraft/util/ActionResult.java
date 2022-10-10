package net.minecraft.util;

public class ActionResult<T> {
   private final EnumActionResult field_188399_a;
   private final T field_188400_b;

   public ActionResult(EnumActionResult var1, T var2) {
      super();
      this.field_188399_a = var1;
      this.field_188400_b = var2;
   }

   public EnumActionResult func_188397_a() {
      return this.field_188399_a;
   }

   public T func_188398_b() {
      return this.field_188400_b;
   }
}
