package net.minecraft.entity.ai;

import java.util.Comparator;
import net.minecraft.entity.Entity;

public class EntityAINearestAttackableTarget$Sorter implements Comparator {
   private final Entity field_75459_b;

   public EntityAINearestAttackableTarget$Sorter(Entity var1) {
      super();
      this.field_75459_b = var1;
   }

   public int compare(Entity var1, Entity var2) {
      double var3 = this.field_75459_b.func_70068_e(var1);
      double var5 = this.field_75459_b.func_70068_e(var2);
      if (var3 < var5) {
         return -1;
      } else {
         return var3 > var5 ? 1 : 0;
      }
   }
}
