package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemSoup extends ItemFood {
   public ItemSoup(int var1, Item.Properties var2) {
      super(var1, 0.6F, false, var2);
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityLivingBase var3) {
      super.func_77654_b(var1, var2, var3);
      return new ItemStack(Items.field_151054_z);
   }
}
