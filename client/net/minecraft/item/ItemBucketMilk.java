package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemBucketMilk extends Item {
   public ItemBucketMilk() {
      super();
      this.func_77625_d(1);
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   @Override
   public ItemStack func_77654_b(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var3.field_71075_bZ.field_75098_d) {
         --var1.field_77994_a;
      }

      if (!var2.field_72995_K) {
         var3.func_70674_bp();
      }

      return var1.field_77994_a <= 0 ? new ItemStack(Items.field_151133_ar) : var1;
   }

   @Override
   public int func_77626_a(ItemStack var1) {
      return 32;
   }

   @Override
   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.drink;
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      var3.func_71008_a(var1, this.func_77626_a(var1));
      return var1;
   }
}
