package net.minecraft.world.item.crafting;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public interface RecipeType<T extends Recipe<?>> {
   RecipeType<CraftingRecipe> CRAFTING = register("crafting");
   RecipeType<SmeltingRecipe> SMELTING = register("smelting");
   RecipeType<BlastingRecipe> BLASTING = register("blasting");
   RecipeType<SmokingRecipe> SMOKING = register("smoking");
   RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
   RecipeType<StonecutterRecipe> STONECUTTING = register("stonecutting");
   RecipeType<SmithingRecipe> SMITHING = register("smithing");

   static <T extends Recipe<?>> RecipeType<T> register(final String var0) {
      return (RecipeType)Registry.register(BuiltInRegistries.RECIPE_TYPE, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), new RecipeType<T>() {
         public String toString() {
            return var0;
         }
      });
   }
}
