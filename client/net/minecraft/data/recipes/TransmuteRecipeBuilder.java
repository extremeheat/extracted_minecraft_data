package net.minecraft.data.recipes;

import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import org.jspecify.annotations.Nullable;

public class TransmuteRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final TransmuteResult result;
   private final Ingredient input;
   private final Ingredient material;
   private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
   private @Nullable String group;
   private MinMaxBounds.Ints materialCount;
   private boolean addMaterialCountToOutput;

   private TransmuteRecipeBuilder(final RecipeCategory category, final TransmuteResult result, final Ingredient input, final Ingredient material) {
      super();
      this.materialCount = TransmuteRecipe.DEFAULT_MATERIAL_COUNT;
      this.category = category;
      this.result = result;
      this.input = input;
      this.material = material;
   }

   public static TransmuteRecipeBuilder transmute(final RecipeCategory category, final Ingredient input, final Ingredient material, final Item result) {
      return transmute(category, input, material, new TransmuteResult(result));
   }

   public static TransmuteRecipeBuilder transmute(final RecipeCategory category, final Ingredient input, final Ingredient material, final ItemStackTemplate result) {
      return transmute(category, input, material, TransmuteResult.fromTemplate(result));
   }

   public static TransmuteRecipeBuilder transmute(final RecipeCategory category, final Ingredient input, final Ingredient material, final TransmuteResult result) {
      return new TransmuteRecipeBuilder(category, result, input, material);
   }

   public TransmuteRecipeBuilder unlockedBy(final String name, final Criterion<?> criterion) {
      this.advancementBuilder.unlockedBy(name, criterion);
      return this;
   }

   public TransmuteRecipeBuilder group(final @Nullable String group) {
      this.group = group;
      return this;
   }

   public TransmuteRecipeBuilder addMaterialCountToOutput() {
      this.addMaterialCountToOutput = true;
      return this;
   }

   public TransmuteRecipeBuilder setMaterialCount(final MinMaxBounds.Ints materialCount) {
      this.materialCount = materialCount;
      return this;
   }

   public @Nullable ResourceKey<Recipe<?>> defaultId() {
      return (ResourceKey)this.result.item().map((item) -> RecipeBuilder.getDefaultRecipeId(new ItemStackTemplate(item, this.result.count(), this.result.components()))).orElse((Object)null);
   }

   public void save(final RecipeOutput output, final ResourceKey<Recipe<?>> id) {
      TransmuteRecipe recipe = new TransmuteRecipe(RecipeBuilder.createCraftingCommonInfo(true), RecipeBuilder.createCraftingBookInfo(this.category, this.group), this.input, this.material, this.materialCount, this.result, this.addMaterialCountToOutput);
      output.accept(id, recipe, this.advancementBuilder.build(output, id, this.category));
   }
}
