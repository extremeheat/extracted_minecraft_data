package net.minecraft.entity;

import java.util.concurrent.Callable;

class Entity$1 implements Callable {
   Entity$1(Entity var1) {
      super();
      this.field_85155_a = var1;
   }

   public String call() {
      return EntityList.func_75621_b(this.field_85155_a) + " (" + this.field_85155_a.getClass().getCanonicalName() + ")";
   }
}
