package net.minecraft.data.recipes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

public class SmithingTrimRecipeBuilder {
   private final RecipeCategory category;
   private final Ingredient template;
   private final Ingredient base;
   private final Ingredient addition;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap();

   public SmithingTrimRecipeBuilder(RecipeCategory var1, Ingredient var2, Ingredient var3, Ingredient var4) {
      super();
      this.category = var1;
      this.template = var2;
      this.base = var3;
      this.addition = var4;
   }

   public static SmithingTrimRecipeBuilder smithingTrim(Ingredient var0, Ingredient var1, Ingredient var2, RecipeCategory var3) {
      return new SmithingTrimRecipeBuilder(var3, var0, var1, var2);
   }

   public SmithingTrimRecipeBuilder unlocks(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public void save(RecipeOutput var1, ResourceLocation var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2)).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(AdvancementRequirements.Strategy.OR);
      Map var10000 = this.criteria;
      Objects.requireNonNull(var3);
      var10000.forEach(var3::addCriterion);
      SmithingTrimRecipe var4 = new SmithingTrimRecipe(this.template, this.base, this.addition);
      var1.accept(var2, var4, var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(var1));
      }
   }
}
