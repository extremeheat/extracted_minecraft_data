package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;

public class SlotDisplays {
   public SlotDisplays() {
      super();
   }

   public static SlotDisplay.Type<?> bootstrap(Registry<SlotDisplay.Type<?>> var0) {
      Registry.register(var0, "empty", SlotDisplay.Empty.TYPE);
      Registry.register(var0, "any_fuel", SlotDisplay.AnyFuel.TYPE);
      Registry.register(var0, "item", SlotDisplay.ItemSlotDisplay.TYPE);
      Registry.register(var0, "item_stack", SlotDisplay.ItemStackSlotDisplay.TYPE);
      Registry.register(var0, "tag", SlotDisplay.TagSlotDisplay.TYPE);
      Registry.register(var0, "smithing_trim", SlotDisplay.SmithingTrimDemoSlotDisplay.TYPE);
      Registry.register(var0, "with_remainder", SlotDisplay.WithRemainder.TYPE);
      return Registry.register(var0, "composite", SlotDisplay.Composite.TYPE);
   }
}
