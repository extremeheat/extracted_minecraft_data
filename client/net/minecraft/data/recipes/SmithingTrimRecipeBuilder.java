package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SmithingTrimRecipeBuilder {
   private final RecipeCategory category;
   private final Ingredient template;
   private final Ingredient base;
   private final Ingredient addition;
   private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
   private final RecipeSerializer<?> type;

   public SmithingTrimRecipeBuilder(RecipeSerializer<?> var1, RecipeCategory var2, Ingredient var3, Ingredient var4, Ingredient var5) {
      super();
      this.category = var2;
      this.type = var1;
      this.template = var3;
      this.base = var4;
      this.addition = var5;
   }

   public static SmithingTrimRecipeBuilder smithingTrim(Ingredient var0, Ingredient var1, Ingredient var2, RecipeCategory var3) {
      return new SmithingTrimRecipeBuilder(RecipeSerializer.SMITHING_TRIM, var3, var0, var1, var2);
   }

   public SmithingTrimRecipeBuilder unlocks(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement
         .parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT)
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(RequirementsStrategy.OR);
      var1.accept(
         new SmithingTrimRecipeBuilder.Result(
            var2, this.type, this.template, this.base, this.addition, this.advancement, var2.withPrefix("recipes/" + this.category.getFolderName() + "/")
         )
      );
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static record Result(ResourceLocation a, RecipeSerializer<?> b, Ingredient c, Ingredient d, Ingredient e, Advancement.Builder f, ResourceLocation g)
      implements FinishedRecipe {
      private final ResourceLocation id;
      private final RecipeSerializer<?> type;
      private final Ingredient template;
      private final Ingredient base;
      private final Ingredient addition;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(
         ResourceLocation var1, RecipeSerializer<?> var2, Ingredient var3, Ingredient var4, Ingredient var5, Advancement.Builder var6, ResourceLocation var7
      ) {
         super();
         this.id = var1;
         this.type = var2;
         this.template = var3;
         this.base = var4;
         this.addition = var5;
         this.advancement = var6;
         this.advancementId = var7;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         var1.add("template", this.template.toJson());
         var1.add("base", this.base.toJson());
         var1.add("addition", this.addition.toJson());
      }

      @Override
      public ResourceLocation getId() {
         return this.id;
      }

      @Override
      public RecipeSerializer<?> getType() {
         return this.type;
      }

      @Nullable
      @Override
      public JsonObject serializeAdvancement() {
         return this.advancement.serializeToJson();
      }

      @Nullable
      @Override
      public ResourceLocation getAdvancementId() {
         return this.advancementId;
      }
   }
}
