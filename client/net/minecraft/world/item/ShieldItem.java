package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;

public class ShieldItem extends Item {
   public ShieldItem(Item.Properties var1) {
      super(var1);
   }

   public Component getName(ItemStack var1) {
      DyeColor var2 = (DyeColor)var1.get(DataComponents.BASE_COLOR);
      if (var2 != null) {
         String var10000 = this.descriptionId;
         return Component.translatable(var10000 + "." + var2.getName());
      } else {
         return super.getName(var1);
      }
   }
}
