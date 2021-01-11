package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class ItemMapBase extends Item {
   protected ItemMapBase() {
      super();
   }

   public boolean func_77643_m_() {
      return true;
   }

   public Packet func_150911_c(ItemStack var1, World var2, EntityPlayer var3) {
      return null;
   }
}
