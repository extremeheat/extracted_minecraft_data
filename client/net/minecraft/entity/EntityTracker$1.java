package net.minecraft.entity;

import java.util.concurrent.Callable;

class EntityTracker$1 implements Callable {
   EntityTracker$1(EntityTracker var1, int var2) {
      super();
      this.field_96569_b = var1;
      this.field_96570_a = var2;
   }

   public String call() {
      String var1 = "Once per " + this.field_96570_a + " ticks";
      if (this.field_96570_a == 2147483647) {
         var1 = "Maximum (" + var1 + ")";
      }

      return var1;
   }
}
