package net.minecraft.world.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class AirItem extends Item {
   public AirItem(Block var1, Item.Properties var2) {
      super(var2);
   }

   public Component getName(ItemStack var1) {
      return this.getName();
   }
}
