package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class ItemMapBase extends Item {
   public ItemMapBase(Item.Properties var1) {
      super(var1);
   }

   public boolean func_77643_m_() {
      return true;
   }

   @Nullable
   public Packet<?> func_150911_c(ItemStack var1, World var2, EntityPlayer var3) {
      return null;
   }
}
