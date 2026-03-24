package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class AirItem extends Item {
   public AirItem(final Block block, final Item.Properties properties) {
      super(properties);
   }

   public Component getName(final ItemStack itemStack) {
      return (Component)itemStack.typeHolder().components().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
   }
}
