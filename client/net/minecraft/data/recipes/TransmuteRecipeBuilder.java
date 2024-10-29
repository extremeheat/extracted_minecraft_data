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
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.TransmuteRecipe;

public class TransmuteRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Holder<Item> result;
   private final Ingredient input;
   private final Ingredient material;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
   @Nullable
   private String group;

   private TransmuteRecipeBuilder(RecipeCategory var1, Holder<Item> var2, Ingredient var3, Ingredient var4) {
      super();
      this.category = var1;
      this.result = var2;
      this.input = var3;
      this.material = var4;
   }

   public static TransmuteRecipeBuilder transmute(RecipeCategory var0, Ingredient var1, Ingredient var2, Item var3) {
      return new TransmuteRecipeBuilder(var0, var3.builtInRegistryHolder(), var1, var2);
   }

   public TransmuteRecipeBuilder unlockedBy(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public TransmuteRecipeBuilder group(@Nullable String var1) {
      this.group = var1;
      return this;
   }

   public Item getResult() {
      return (Item)this.result.value();
   }

   public void save(RecipeOutput var1, ResourceKey<Recipe<?>> var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2)).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(AdvancementRequirements.Strategy.OR);
      Map var10000 = this.criteria;
      Objects.requireNonNull(var3);
      var10000.forEach(var3::addCriterion);
      TransmuteRecipe var4 = new TransmuteRecipe((String)Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), this.input, this.material, this.result);
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
