package net.minecraft.item;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemItemFrame extends ItemHangingEntity {
   public ItemItemFrame(Item.Properties var1) {
      super(EntityItemFrame.class, var1);
   }

   protected boolean func_200127_a(EntityPlayer var1, EnumFacing var2, ItemStack var3, BlockPos var4) {
      return !World.func_189509_E(var4) && var1.func_175151_a(var4, var2, var3);
   }
}
