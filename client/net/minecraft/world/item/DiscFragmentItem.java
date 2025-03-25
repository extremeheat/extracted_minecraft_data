package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.component.TooltipDisplay;

public class DiscFragmentItem extends Item {
   public DiscFragmentItem(Item.Properties var1) {
      super(var1);
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, TooltipDisplay var3, Consumer<Component> var4, TooltipFlag var5) {
      var4.accept(this.getDisplayName().withStyle(ChatFormatting.GRAY));
   }

   public MutableComponent getDisplayName() {
      return Component.translatable(this.descriptionId + ".desc");
   }
}
