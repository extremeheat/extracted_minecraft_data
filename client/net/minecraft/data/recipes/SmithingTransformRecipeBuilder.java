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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class SmithingTransformRecipeBuilder {
   private final Ingredient template;
   private final Ingredient base;
   private final Ingredient addition;
   private final RecipeCategory category;
   private final Item result;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap();

   public SmithingTransformRecipeBuilder(Ingredient var1, Ingredient var2, Ingredient var3, RecipeCategory var4, Item var5) {
      super();
      this.category = var4;
      this.template = var1;
      this.base = var2;
      this.addition = var3;
      this.result = var5;
   }

   public static SmithingTransformRecipeBuilder smithing(Ingredient var0, Ingredient var1, Ingredient var2, RecipeCategory var3, Item var4) {
      return new SmithingTransformRecipeBuilder(var0, var1, var2, var3, var4);
   }

   public SmithingTransformRecipeBuilder unlocks(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public void save(RecipeOutput var1, String var2) {
      this.save(var1, ResourceLocation.parse(var2));
   }

   public void save(RecipeOutput var1, ResourceLocation var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2)).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(AdvancementRequirements.Strategy.OR);
      Map var10000 = this.criteria;
      Objects.requireNonNull(var3);
      var10000.forEach(var3::addCriterion);
      SmithingTransformRecipe var4 = new SmithingTransformRecipe(this.template, this.base, this.addition, new ItemStack(this.result));
      var1.accept(var2, var4, var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(var1));
      }
   }
}
