package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
   public BehaviorProjectileDispense() {
      super();
   }

   public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      World var3 = var1.func_82618_k();
      IPosition var4 = BlockDispenser.func_149939_a(var1);
      EnumFacing var5 = BlockDispenser.func_149937_b(var1.func_82620_h());
      IProjectile var6 = this.func_82499_a(var3, var4);
      var6.func_70186_c((double)var5.func_82601_c(), (double)((float)var5.func_96559_d() + 0.1F), (double)var5.func_82599_e(), this.func_82500_b(), this.func_82498_a());
      var3.func_72838_d((Entity)var6);
      var2.func_77979_a(1);
      return var2;
   }

   protected void func_82485_a(IBlockSource var1) {
      var1.func_82618_k().func_175718_b(1002, var1.func_180699_d(), 0);
   }

   protected abstract IProjectile func_82499_a(World var1, IPosition var2);

   protected float func_82498_a() {
      return 6.0F;
   }

   protected float func_82500_b() {
      return 1.1F;
   }
}
