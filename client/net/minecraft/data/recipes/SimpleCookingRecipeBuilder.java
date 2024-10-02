package net.minecraft.data.recipes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final CookingBookCategory bookCategory;
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   @Nullable
   private String group;
   private final AbstractCookingRecipe.Factory<?> factory;

   private SimpleCookingRecipeBuilder(
      RecipeCategory var1, CookingBookCategory var2, ItemLike var3, Ingredient var4, float var5, int var6, AbstractCookingRecipe.Factory<?> var7
   ) {
      super();
      this.category = var1;
      this.bookCategory = var2;
      this.result = var3.asItem();
      this.ingredient = var4;
      this.experience = var5;
      this.cookingTime = var6;
      this.factory = var7;
   }

   public static <T extends AbstractCookingRecipe> SimpleCookingRecipeBuilder generic(
      Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4, RecipeSerializer<T> var5, AbstractCookingRecipe.Factory<T> var6
   ) {
      return new SimpleCookingRecipeBuilder(var1, determineRecipeCategory(var5, var2), var2, var0, var3, var4, var6);
   }

   public static SimpleCookingRecipeBuilder campfireCooking(Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4) {
      return new SimpleCookingRecipeBuilder(var1, CookingBookCategory.FOOD, var2, var0, var3, var4, CampfireCookingRecipe::new);
   }

   public static SimpleCookingRecipeBuilder blasting(Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4) {
      return new SimpleCookingRecipeBuilder(var1, determineBlastingRecipeCategory(var2), var2, var0, var3, var4, BlastingRecipe::new);
   }

   public static SimpleCookingRecipeBuilder smelting(Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4) {
      return new SimpleCookingRecipeBuilder(var1, determineSmeltingRecipeCategory(var2), var2, var0, var3, var4, SmeltingRecipe::new);
   }

   public static SimpleCookingRecipeBuilder smoking(Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4) {
      return new SimpleCookingRecipeBuilder(var1, CookingBookCategory.FOOD, var2, var0, var3, var4, SmokingRecipe::new);
   }

   public SimpleCookingRecipeBuilder unlockedBy(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public SimpleCookingRecipeBuilder group(@Nullable String var1) {
      this.group = var1;
      return this;
   }

   @Override
   public Item getResult() {
      return this.result;
   }

   @Override
   public void save(RecipeOutput var1, ResourceKey<Recipe<?>> var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(AdvancementRequirements.Strategy.OR);
      this.criteria.forEach(var3::addCriterion);
      AbstractCookingRecipe var4 = this.factory
         .create(Objects.requireNonNullElse(this.group, ""), this.bookCategory, this.ingredient, new ItemStack(this.result), this.experience, this.cookingTime);
      var1.accept(var2, var4, var3.build(var2.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike var0) {
      if (var0.asItem().components().has(DataComponents.FOOD)) {
         return CookingBookCategory.FOOD;
      } else {
         return var0.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
      }
   }

   private static CookingBookCategory determineBlastingRecipeCategory(ItemLike var0) {
      return var0.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
   }

   private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> var0, ItemLike var1) {
      if (var0 == RecipeSerializer.SMELTING_RECIPE) {
         return determineSmeltingRecipeCategory(var1);
      } else if (var0 == RecipeSerializer.BLASTING_RECIPE) {
         return determineBlastingRecipeCategory(var1);
      } else if (var0 != RecipeSerializer.SMOKING_RECIPE && var0 != RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
         throw new IllegalStateException("Unknown cooking recipe type");
      } else {
         return CookingBookCategory.FOOD;
      }
   }

   private void ensureValid(ResourceKey<Recipe<?>> var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1.location());
      }
   }
}
