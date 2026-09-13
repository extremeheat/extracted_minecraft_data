package net.minecraft.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {
   private IIcon field_94598_a;

   public ItemFishingRod() {
      super();
      this.func_77656_e(64);
      this.func_77625_d(1);
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   @Override
   public boolean func_77662_d() {
      return true;
   }

   @Override
   public boolean func_77629_n_() {
      return true;
   }

   @Override
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
      }

      return var1;
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      this.field_77791_bV = var1.func_94245_a(this.func_111208_A() + "_uncast");
      this.field_94598_a = var1.func_94245_a(this.func_111208_A() + "_cast");
   }

   public IIcon func_94597_g() {
      return this.field_94598_a;
   }

   @Override
   public boolean func_77616_k(ItemStack var1) {
      return super.func_77616_k(var1);
   }

   @Override
   public int func_77619_b() {
      return 1;
   }
}
