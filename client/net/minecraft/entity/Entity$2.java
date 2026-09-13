package net.minecraft.entity;

import java.util.concurrent.Callable;

class Entity$2 implements Callable {
   Entity$2(Entity var1) {
      super();
      this.field_96564_a = var1;
   }

   public String call() {
      return this.field_96564_a.func_70005_c_();
   }
}
