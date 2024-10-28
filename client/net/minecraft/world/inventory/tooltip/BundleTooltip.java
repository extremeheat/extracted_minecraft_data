package net.minecraft.world.inventory.tooltip;

import net.minecraft.world.item.component.BundleContents;

public record BundleTooltip(BundleContents contents) implements TooltipComponent {
   public BundleTooltip(BundleContents contents) {
      super();
      this.contents = contents;
   }

   public BundleContents contents() {
      return this.contents;
   }
}
