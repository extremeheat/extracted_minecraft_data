package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Block;

public class PlayerHeadItem extends StandingAndWallBlockItem {
   public PlayerHeadItem(final Block block, final Block wallBlock, final Item.Properties properties) {
      super(block, wallBlock, Direction.DOWN, properties);
   }

   public Component getName(final ItemStack itemStack) {
      ResolvableProfile profile = (ResolvableProfile)itemStack.get(DataComponents.PROFILE);
      return (Component)(profile != null && profile.name().isPresent() ? Component.translatable(this.descriptionId + ".named", profile.name().get()) : super.getName(itemStack));
   }
}
