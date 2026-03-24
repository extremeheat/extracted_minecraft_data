package net.minecraft.data.recipes;

import java.util.Objects;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.Nullable;

public interface RecipeBuilder {
   Identifier ROOT_RECIPE_ADVANCEMENT = Identifier.withDefaultNamespace("recipes/root");

   RecipeBuilder unlockedBy(String name, Criterion<?> criterion);

   RecipeBuilder group(@Nullable String group);

   ResourceKey<Recipe<?>> defaultId();

   void save(RecipeOutput output, ResourceKey<Recipe<?>> location);

   default void save(final RecipeOutput output) {
      this.save(output, this.defaultId());
   }

   default void save(final RecipeOutput output, final String id) {
      ResourceKey<Recipe<?>> defaultKey = this.defaultId();
      ResourceKey<Recipe<?>> overriddenKey = ResourceKey.create(Registries.RECIPE, Identifier.parse(id));
      if (overriddenKey == defaultKey) {
         throw new IllegalStateException("Recipe " + id + " should remove its 'save' argument as it is equal to default one");
      } else {
         this.save(output, overriddenKey);
      }
   }

   static CraftingBookCategory determineCraftingBookCategory(final RecipeCategory category) {
      CraftingBookCategory var10000;
      switch (category) {
         case BUILDING_BLOCKS:
            var10000 = CraftingBookCategory.BUILDING;
            break;
         case TOOLS:
         case COMBAT:
            var10000 = CraftingBookCategory.EQUIPMENT;
            break;
         case REDSTONE:
            var10000 = CraftingBookCategory.REDSTONE;
            break;
         default:
            var10000 = CraftingBookCategory.MISC;
      }

      return var10000;
   }

   static Recipe.CommonInfo createCraftingCommonInfo(final boolean showNotification) {
      return new Recipe.CommonInfo(showNotification);
   }

   static CraftingRecipe.CraftingBookInfo createCraftingBookInfo(final RecipeCategory category, final @Nullable String group) {
      return new CraftingRecipe.CraftingBookInfo(determineCraftingBookCategory(category), (String)Objects.requireNonNullElse(group, ""));
   }

   static ResourceKey<Recipe<?>> getDefaultRecipeId(final ItemInstance result) {
      return ResourceKey.create(Registries.RECIPE, ((ResourceKey)result.typeHolder().unwrapKey().orElseThrow()).identifier());
   }
}
