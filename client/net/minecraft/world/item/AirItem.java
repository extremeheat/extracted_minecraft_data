package net.minecraft.world.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class AirItem extends Item {
   private final Block block;

   public AirItem(Block var1, Item.Properties var2) {
      super(var2);
      this.block = var1;
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      this.block.appendHoverText(var1, var2, var3, var4);
   }

   @Override
   public Component getName(ItemStack var1) {
      return this.getName();
   }
}