package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;

public class RecipeDisplays {
   public RecipeDisplays() {
      super();
   }

   public static RecipeDisplay.Type<?> bootstrap(Registry<RecipeDisplay.Type<?>> var0) {
      Registry.register(var0, "crafting_shapeless", ShapelessCraftingRecipeDisplay.TYPE);
      Registry.register(var0, "crafting_shaped", ShapedCraftingRecipeDisplay.TYPE);
      Registry.register(var0, "furnace", FurnaceRecipeDisplay.TYPE);
      Registry.register(var0, "stonecutter", StonecutterRecipeDisplay.TYPE);
      return Registry.register(var0, "smithing", SmithingRecipeDisplay.TYPE);
   }
}
