package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public class SlotDisplays {
   public SlotDisplays() {
      super();
   }

   public static void bootstrap(final WritableRegistry<SlotDisplay.Type<?>> registry) {
      Registry.register(registry, (String)"empty", SlotDisplay.Empty.TYPE);
      Registry.register(registry, (String)"any_fuel", SlotDisplay.AnyFuel.TYPE);
      Registry.register(registry, (String)"with_any_potion", SlotDisplay.WithAnyPotion.TYPE);
      Registry.register(registry, (String)"only_with_component", SlotDisplay.OnlyWithComponent.TYPE);
      Registry.register(registry, (String)"item", SlotDisplay.ItemSlotDisplay.TYPE);
      Registry.register(registry, (String)"item_stack", SlotDisplay.ItemStackSlotDisplay.TYPE);
      Registry.register(registry, (String)"tag", SlotDisplay.TagSlotDisplay.TYPE);
      Registry.register(registry, (String)"dyed", SlotDisplay.DyedSlotDemo.TYPE);
      Registry.register(registry, (String)"smithing_trim", SlotDisplay.SmithingTrimDemoSlotDisplay.TYPE);
      Registry.register(registry, (String)"with_remainder", SlotDisplay.WithRemainder.TYPE);
      Registry.register(registry, (String)"composite", SlotDisplay.Composite.TYPE);
   }
}
