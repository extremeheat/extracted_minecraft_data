package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   @Nullable
   private String group;
   private final RecipeSerializer<?> type;

   public SingleItemRecipeBuilder(RecipeCategory var1, RecipeSerializer<?> var2, Ingredient var3, ItemLike var4, int var5) {
      super();
      this.category = var1;
      this.type = var2;
      this.result = var4.asItem();
      this.ingredient = var3;
      this.count = var5;
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient var0, RecipeCategory var1, ItemLike var2) {
      return new SingleItemRecipeBuilder(var1, RecipeSerializer.STONECUTTER, var0, var2, 1);
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient var0, RecipeCategory var1, ItemLike var2, int var3) {
      return new SingleItemRecipeBuilder(var1, RecipeSerializer.STONECUTTER, var0, var2, var3);
   }

   public SingleItemRecipeBuilder unlockedBy(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public SingleItemRecipeBuilder group(@Nullable String var1) {
      this.group = var1;
      return this;
   }

   @Override
   public Item getResult() {
      return this.result;
   }

   @Override
   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement
         .parent(ROOT_RECIPE_ADVANCEMENT)
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(RequirementsStrategy.OR);
      var1.accept(
         new SingleItemRecipeBuilder.Result(
            var2,
            this.type,
            this.group == null ? "" : this.group,
            this.ingredient,
            this.result,
            this.count,
            this.advancement,
            var2.withPrefix("recipes/" + this.category.getFolderName() + "/")
         )
      );
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final int count;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final RecipeSerializer<?> type;

      public Result(
         ResourceLocation var1, RecipeSerializer<?> var2, String var3, Ingredient var4, Item var5, int var6, Advancement.Builder var7, ResourceLocation var8
      ) {
         super();
         this.id = var1;
         this.type = var2;
         this.group = var3;
         this.ingredient = var4;
         this.result = var5;
         this.count = var6;
         this.advancement = var7;
         this.advancementId = var8;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         var1.add("ingredient", this.ingredient.toJson());
         var1.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
         var1.addProperty("count", this.count);
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
