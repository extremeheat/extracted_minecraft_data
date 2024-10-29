package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;

public class SlotDisplays {
   public SlotDisplays() {
      super();
   }

   public static SlotDisplay.Type<?> bootstrap(Registry<SlotDisplay.Type<?>> var0) {
      Registry.register(var0, (String)"empty", SlotDisplay.Empty.TYPE);
      Registry.register(var0, (String)"any_fuel", SlotDisplay.AnyFuel.TYPE);
      Registry.register(var0, (String)"item", SlotDisplay.ItemSlotDisplay.TYPE);
      Registry.register(var0, (String)"item_stack", SlotDisplay.ItemStackSlotDisplay.TYPE);
      Registry.register(var0, (String)"tag", SlotDisplay.TagSlotDisplay.TYPE);
      Registry.register(var0, (String)"smithing_trim", SlotDisplay.SmithingTrimDemoSlotDisplay.TYPE);
      Registry.register(var0, (String)"with_remainder", SlotDisplay.WithRemainder.TYPE);
      return (SlotDisplay.Type)Registry.register(var0, (String)"composite", SlotDisplay.Composite.TYPE);
   }
}
