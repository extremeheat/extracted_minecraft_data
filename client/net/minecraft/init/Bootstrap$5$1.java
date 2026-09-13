package net.minecraft.init;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class Bootstrap$5$1 extends BehaviorProjectileDispense {
   Bootstrap$5$1(Bootstrap$5 var1, ItemStack var2) {
      super();
      this.field_150837_c = var1;
      this.field_150836_b = var2;
   }

   @Override
   protected IProjectile func_82499_a(World var1, IPosition var2) {
      return new EntityPotion(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c(), this.field_150836_b.func_77946_l());
   }

   @Override
   protected float func_82498_a() {
      return super.func_82498_a() * 0.5F;
   }

   @Override
   protected float func_82500_b() {
      return super.func_82500_b() * 1.25F;
   }
}
