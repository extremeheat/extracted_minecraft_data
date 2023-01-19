package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingRecipeBuilder implements RecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   @Nullable
   private String group;
   private final SimpleCookingSerializer<?> serializer;

   private SimpleCookingRecipeBuilder(ItemLike var1, Ingredient var2, float var3, int var4, SimpleCookingSerializer<?> var5) {
      super();
      this.result = var1.asItem();
      this.ingredient = var2;
      this.experience = var3;
      this.cookingTime = var4;
      this.serializer = var5;
   }

   public static SimpleCookingRecipeBuilder cooking(Ingredient var0, ItemLike var1, float var2, int var3, SimpleCookingSerializer<?> var4) {
      return new SimpleCookingRecipeBuilder(var1, var0, var2, var3, var4);
   }

   public static SimpleCookingRecipeBuilder campfireCooking(Ingredient var0, ItemLike var1, float var2, int var3) {
      return cooking(var0, var1, var2, var3, RecipeSerializer.CAMPFIRE_COOKING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder blasting(Ingredient var0, ItemLike var1, float var2, int var3) {
      return cooking(var0, var1, var2, var3, RecipeSerializer.BLASTING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder smelting(Ingredient var0, ItemLike var1, float var2, int var3) {
      return cooking(var0, var1, var2, var3, RecipeSerializer.SMELTING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder smoking(Ingredient var0, ItemLike var1, float var2, int var3) {
      return cooking(var0, var1, var2, var3, RecipeSerializer.SMOKING_RECIPE);
   }

   public SimpleCookingRecipeBuilder unlockedBy(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
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
   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement
         .parent(ROOT_RECIPE_ADVANCEMENT)
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(RequirementsStrategy.OR);
      var1.accept(
         new SimpleCookingRecipeBuilder.Result(
            var2,
            this.group == null ? "" : this.group,
            this.ingredient,
            this.result,
            this.experience,
            this.cookingTime,
            this.advancement,
            new ResourceLocation(var2.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + var2.getPath()),
            this.serializer
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
      private final float experience;
      private final int cookingTime;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

      public Result(
         ResourceLocation var1,
         String var2,
         Ingredient var3,
         Item var4,
         float var5,
         int var6,
         Advancement.Builder var7,
         ResourceLocation var8,
         RecipeSerializer<? extends AbstractCookingRecipe> var9
      ) {
         super();
         this.id = var1;
         this.group = var2;
         this.ingredient = var3;
         this.result = var4;
         this.experience = var5;
         this.cookingTime = var6;
         this.advancement = var7;
         this.advancementId = var8;
         this.serializer = var9;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         var1.add("ingredient", this.ingredient.toJson());
         var1.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         var1.addProperty("experience", this.experience);
         var1.addProperty("cookingtime", this.cookingTime);
      }

      @Override
      public RecipeSerializer<?> getType() {
         return this.serializer;
      }

      @Override
      public ResourceLocation getId() {
         return this.id;
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
