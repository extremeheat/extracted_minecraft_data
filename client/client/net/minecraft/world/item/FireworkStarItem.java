package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.FireworkExplosion;

public class FireworkStarItem extends Item {
   public FireworkStarItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      FireworkExplosion var5 = var1.get(DataComponents.FIREWORK_EXPLOSION);
      if (var5 != null) {
         var5.addToTooltip(var2, var3::add, var4);
      }
   }
}
