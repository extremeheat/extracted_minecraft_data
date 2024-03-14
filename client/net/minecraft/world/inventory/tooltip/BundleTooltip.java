package net.minecraft.world.inventory.tooltip;

import net.minecraft.world.item.component.BundleContents;

public record BundleTooltip(BundleContents a) implements TooltipComponent {
   private final BundleContents contents;

   public BundleTooltip(BundleContents var1) {
      super();
      this.contents = var1;
   }
}
