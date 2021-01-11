package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {
   public ItemFishingRod() {
      super();
      this.func_77656_e(64);
      this.func_77625_d(1);
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   public boolean func_77662_d() {
      return true;
   }

   public boolean func_77629_n_() {
      return true;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (var3.field_71104_cf != null) {
         int var4 = var3.field_71104_cf.func_146034_e();
         var1.func_77972_a(var4, var3);
         var3.func_71038_i();
      } else {
         var2.func_72956_a(var3, "random.bow", 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
         if (!var2.field_72995_K) {
            var2.func_72838_d(new EntityFishHook(var2, var3));
         }

         var3.func_71038_i();
         var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
      }

      return var1;
   }

   public boolean func_77616_k(ItemStack var1) {
      return super.func_77616_k(var1);
   }

   public int func_77619_b() {
      return 1;
   }
}
