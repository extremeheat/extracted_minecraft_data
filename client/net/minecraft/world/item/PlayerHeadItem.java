package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;

public class PlayerHeadItem extends StandingAndWallBlockItem {
   public PlayerHeadItem(Block var1, Block var2, Item.Properties var3) {
      super(var1, var2, Direction.DOWN, var3);
   }

   public Component getName(ItemStack var1) {
      ResolvableProfile var2 = (ResolvableProfile)var1.get(DataComponents.PROFILE);
      return (Component)(var2 != null && var2.name().isPresent() ? Component.translatable(this.descriptionId + ".named", var2.name().get()) : super.getName(var1));
   }
}
