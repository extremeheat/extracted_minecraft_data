package net.minecraft.data.recipes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

public class ShapelessRecipeBuilder implements RecipeBuilder {
   private final HolderGetter<Item> items;
   private final RecipeCategory category;
   private final ItemStack result;
   private final List<Ingredient> ingredients = new ArrayList();
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
   @Nullable
   private String group;

   private ShapelessRecipeBuilder(HolderGetter<Item> var1, RecipeCategory var2, ItemStack var3) {
      super();
      this.items = var1;
      this.category = var2;
      this.result = var3;
   }

   public static ShapelessRecipeBuilder shapeless(HolderGetter<Item> var0, RecipeCategory var1, ItemStack var2) {
      return new ShapelessRecipeBuilder(var0, var1, var2);
   }

   public static ShapelessRecipeBuilder shapeless(HolderGetter<Item> var0, RecipeCategory var1, ItemLike var2) {
      return shapeless(var0, var1, var2, 1);
   }

   public static ShapelessRecipeBuilder shapeless(HolderGetter<Item> var0, RecipeCategory var1, ItemLike var2, int var3) {
      return new ShapelessRecipeBuilder(var0, var1, var2.asItem().getDefaultInstance().copyWithCount(var3));
   }

   public ShapelessRecipeBuilder requires(TagKey<Item> var1) {
      return this.requires(Ingredient.of((HolderSet)this.items.getOrThrow(var1)));
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
      return this.result.getItem();
   }

   public void save(RecipeOutput var1, ResourceKey<Recipe<?>> var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2)).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(AdvancementRequirements.Strategy.OR);
      Map var10000 = this.criteria;
      Objects.requireNonNull(var3);
      var10000.forEach(var3::addCriterion);
      ShapelessRecipe var4 = new ShapelessRecipe((String)Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), this.result, this.ingredients);
      var1.accept(var2, var4, var3.build(var2.location().withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private void ensureValid(ResourceKey<Recipe<?>> var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(var1.location()));
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
