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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private String group;
   private final RecipeSerializer<?> type;

   public SingleItemRecipeBuilder(RecipeSerializer<?> var1, Ingredient var2, ItemLike var3, int var4) {
      super();
      this.type = var1;
      this.result = var3.asItem();
      this.ingredient = var2;
      this.count = var4;
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient var0, ItemLike var1) {
      return new SingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, var0, var1, 1);
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient var0, ItemLike var1, int var2) {
      return new SingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, var0, var1, var2);
   }

   public SingleItemRecipeBuilder unlocks(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public void save(Consumer<FinishedRecipe> var1, String var2) {
      ResourceLocation var3 = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(var2)).equals(var3)) {
         throw new IllegalStateException("Single Item Recipe " + var2 + " should remove its 'save' argument");
      } else {
         this.save(var1, new ResourceLocation(var2));
      }
   }

   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)RecipeUnlockedTrigger.unlocked(var2)).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(RequirementsStrategy.OR);
      var1.accept(new SingleItemRecipeBuilder.Result(var2, this.type, this.group == null ? "" : this.group, this.ingredient, this.result, this.count, this.advancement, new ResourceLocation(var2.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + var2.getPath())));
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

      public Result(ResourceLocation var1, RecipeSerializer<?> var2, String var3, Ingredient var4, Item var5, int var6, Advancement.Builder var7, ResourceLocation var8) {
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

      public void serializeRecipeData(JsonObject var1) {
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         var1.add("ingredient", this.ingredient.toJson());
         var1.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         var1.addProperty("count", this.count);
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public RecipeSerializer<?> getType() {
         return this.type;
      }

      @Nullable
      public JsonObject serializeAdvancement() {
         return this.advancement.serializeToJson();
      }

      @Nullable
      public ResourceLocation getAdvancementId() {
         return this.advancementId;
      }
   }
}
