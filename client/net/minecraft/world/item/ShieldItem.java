package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;

public class ShieldItem extends Item {
   public ShieldItem(final Item.Properties properties) {
      super(properties);
   }

   public Component getName(final ItemStack itemStack) {
      DyeColor baseColor = (DyeColor)itemStack.get(DataComponents.BASE_COLOR);
      if (baseColor != null) {
         String var10000 = this.descriptionId;
         return Component.translatable(var10000 + "." + baseColor.getName());
      } else {
         return super.getName(itemStack);
      }
   }
}
