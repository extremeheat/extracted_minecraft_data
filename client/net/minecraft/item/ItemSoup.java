package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemSoup extends ItemFood {
   public ItemSoup(int var1) {
      super(var1, false);
      this.func_77625_d(1);
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityPlayer var3) {
      super.func_77654_b(var1, var2, var3);
      return new ItemStack(Items.field_151054_z);
   }
}
