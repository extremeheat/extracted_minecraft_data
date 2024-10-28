package net.minecraft.world.item.crafting.display;

import net.minecraft.core.Registry;

public class RecipeDisplays {
   public RecipeDisplays() {
      super();
   }

   public static RecipeDisplay.Type<?> bootstrap(Registry<RecipeDisplay.Type<?>> var0) {
      Registry.register(var0, (String)"crafting_shapeless", ShapelessCraftingRecipeDisplay.TYPE);
      Registry.register(var0, (String)"crafting_shaped", ShapedCraftingRecipeDisplay.TYPE);
      Registry.register(var0, (String)"furnace", FurnaceRecipeDisplay.TYPE);
      Registry.register(var0, (String)"stonecutter", StonecutterRecipeDisplay.TYPE);
      return (RecipeDisplay.Type)Registry.register(var0, (String)"smithing", SmithingRecipeDisplay.TYPE);
   }
}
