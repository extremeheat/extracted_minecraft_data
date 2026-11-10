package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public class RecipeDisplays {
   public RecipeDisplays() {
      super();
   }

   public static void bootstrap(final WritableRegistry<RecipeDisplay.Type<?>> registry) {
      Registry.register(registry, (String)"crafting_shapeless", ShapelessCraftingRecipeDisplay.TYPE);
      Registry.register(registry, (String)"crafting_shaped", ShapedCraftingRecipeDisplay.TYPE);
      Registry.register(registry, (String)"furnace", FurnaceRecipeDisplay.TYPE);
      Registry.register(registry, (String)"stonecutter", StonecutterRecipeDisplay.TYPE);
      Registry.register(registry, (String)"smithing", SmithingRecipeDisplay.TYPE);
   }
}
