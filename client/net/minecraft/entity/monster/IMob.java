package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMob extends IAnimals {
   Predicate<Entity> field_82192_a = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof IMob;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   };
   Predicate<Entity> field_175450_e = new Predicate<Entity>() {
      public boolean apply(Entity var1) {
         return var1 instanceof IMob && !var1.func_82150_aj();
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((Entity)var1);
      }
   };
}
