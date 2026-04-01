package net.minecraft.world.inventory.tooltip;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public record RecipeTooltip(List<ItemStack> ingredients) implements TooltipComponent {
   public RecipeTooltip {
      super();
   }
}
