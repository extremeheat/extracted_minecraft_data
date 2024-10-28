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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap();
   @Nullable
   private String group;
   private final SingleItemRecipe.Factory<?> factory;

   public SingleItemRecipeBuilder(RecipeCategory var1, SingleItemRecipe.Factory<?> var2, Ingredient var3, ItemLike var4, int var5) {
      super();
      this.category = var1;
      this.factory = var2;
      this.result = var4.asItem();
      this.ingredient = var3;
      this.count = var5;
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient var0, RecipeCategory var1, ItemLike var2) {
      return new SingleItemRecipeBuilder(var1, StonecutterRecipe::new, var0, var2, 1);
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient var0, RecipeCategory var1, ItemLike var2, int var3) {
      return new SingleItemRecipeBuilder(var1, StonecutterRecipe::new, var0, var2, var3);
   }

   public SingleItemRecipeBuilder unlockedBy(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public SingleItemRecipeBuilder group(@Nullable String var1) {
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
      SingleItemRecipe var4 = this.factory.create((String)Objects.requireNonNullElse(this.group, ""), this.ingredient, new ItemStack(this.result, this.count));
      var1.accept(var2, var4, var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(var1));
      }
   }

   // $FF: synthetic method
   public RecipeBuilder group(@Nullable String var1) {
      return this.group(var1);
   }

   // $FF: synthetic method
   public RecipeBuilder unlockedBy(String var1, Criterion var2) {
      return this.unlockedBy(var1, var2);
   }
}
