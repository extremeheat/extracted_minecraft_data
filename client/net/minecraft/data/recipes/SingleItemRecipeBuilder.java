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

public class SingleItemRecipeBuilder implements RecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   @Nullable
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

   public SingleItemRecipeBuilder unlockedBy(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public SingleItemRecipeBuilder group(@Nullable String var1) {
      this.group = var1;
      return this;
   }

   public Item getResult() {
      return this.result;
   }

   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", (CriterionTriggerInstance)RecipeUnlockedTrigger.unlocked(var2)).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(RequirementsStrategy.OR);
      RecipeSerializer var10004 = this.type;
      String var10005 = this.group == null ? "" : this.group;
      Ingredient var10006 = this.ingredient;
      Item var10007 = this.result;
      int var10008 = this.count;
      Advancement.Builder var10009 = this.advancement;
      String var10012 = var2.getNamespace();
      String var10013 = this.result.getItemCategory().getRecipeFolderName();
      var1.accept(new Result(var2, var10004, var10005, var10006, var10007, var10008, var10009, new ResourceLocation(var10012, "recipes/" + var10013 + "/" + var2.getPath())));
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   // $FF: synthetic method
   public RecipeBuilder group(@Nullable String var1) {
      return this.group(var1);
   }

   // $FF: synthetic method
   public RecipeBuilder unlockedBy(String var1, CriterionTriggerInstance var2) {
      return this.unlockedBy(var1, var2);
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
