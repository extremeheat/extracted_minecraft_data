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
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

public class ShapelessRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final int count;
   private final NonNullList<Ingredient> ingredients = NonNullList.create();
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
   @Nullable
   private String group;

   public ShapelessRecipeBuilder(RecipeCategory var1, ItemLike var2, int var3) {
      super();
      this.category = var1;
      this.result = var2.asItem();
      this.count = var3;
   }

   public static ShapelessRecipeBuilder shapeless(RecipeCategory var0, ItemLike var1) {
      return new ShapelessRecipeBuilder(var0, var1, 1);
   }

   public static ShapelessRecipeBuilder shapeless(RecipeCategory var0, ItemLike var1, int var2) {
      return new ShapelessRecipeBuilder(var0, var1, var2);
   }

   public ShapelessRecipeBuilder requires(TagKey<Item> var1) {
      return this.requires(Ingredient.of(var1));
   }

   public ShapelessRecipeBuilder requires(ItemLike var1) {
      return this.requires((ItemLike)var1, 1);
   }

   public ShapelessRecipeBuilder requires(ItemLike var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.requires(Ingredient.of(var1));
      }

      return this;
   }

   public ShapelessRecipeBuilder requires(Ingredient var1) {
      return this.requires((Ingredient)var1, 1);
   }

   public ShapelessRecipeBuilder requires(Ingredient var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.ingredients.add(var1);
      }

      return this;
   }

   public ShapelessRecipeBuilder unlockedBy(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public ShapelessRecipeBuilder group(@Nullable String var1) {
      this.group = var1;
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(RecipeOutput var1, ResourceLocation var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2)).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(AdvancementRequirements.Strategy.OR);
      Map var10000 = this.criteria;
      Objects.requireNonNull(var3);
      var10000.forEach(var3::addCriterion);
      ShapelessRecipe var4 = new ShapelessRecipe((String)Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), new ItemStack(this.result, this.count), this.ingredients);
      var1.accept(var2, var4, var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(var1));
      }
   }

   // $FF: synthetic method
   public RecipeBuilder group(@Nullable final String var1) {
      return this.group(var1);
   }

   // $FF: synthetic method
   public RecipeBuilder unlockedBy(final String var1, final Criterion var2) {
      return this.unlockedBy(var1, var2);
   }
}
