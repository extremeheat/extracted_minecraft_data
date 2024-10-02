package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public interface RecipeBuilder {
   ResourceLocation ROOT_RECIPE_ADVANCEMENT = ResourceLocation.withDefaultNamespace("recipes/root");

   RecipeBuilder unlockedBy(String var1, Criterion<?> var2);

   RecipeBuilder group(@Nullable String var1);

   Item getResult();

   void save(RecipeOutput var1, ResourceKey<Recipe<?>> var2);

   default void save(RecipeOutput var1) {
      this.save(var1, ResourceKey.create(Registries.RECIPE, getDefaultRecipeId(this.getResult())));
   }

   default void save(RecipeOutput var1, String var2) {
      ResourceLocation var3 = getDefaultRecipeId(this.getResult());
      ResourceLocation var4 = ResourceLocation.parse(var2);
      if (var4.equals(var3)) {
         throw new IllegalStateException("Recipe " + var2 + " should remove its 'save' argument as it is equal to default one");
      } else {
         this.save(var1, ResourceKey.create(Registries.RECIPE, var4));
      }
   }

   static ResourceLocation getDefaultRecipeId(ItemLike var0) {
      return BuiltInRegistries.ITEM.getKey(var0.asItem());
   }

   static CraftingBookCategory determineBookCategory(RecipeCategory var0) {
      return switch (var0) {
         case BUILDING_BLOCKS -> CraftingBookCategory.BUILDING;
         case TOOLS, COMBAT -> CraftingBookCategory.EQUIPMENT;
         case REDSTONE -> CraftingBookCategory.REDSTONE;
         default -> CraftingBookCategory.MISC;
      };
   }
}
