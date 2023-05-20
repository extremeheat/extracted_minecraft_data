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

@Deprecated(
   forRemoval = true
)
public class LegacyUpgradeRecipeBuilder {
   private final Ingredient base;
   private final Ingredient addition;
   private final RecipeCategory category;
   private final Item result;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private final RecipeSerializer<?> type;

   public LegacyUpgradeRecipeBuilder(RecipeSerializer<?> var1, Ingredient var2, Ingredient var3, RecipeCategory var4, Item var5) {
      super();
      this.category = var4;
      this.type = var1;
      this.base = var2;
      this.addition = var3;
      this.result = var5;
   }

   public static LegacyUpgradeRecipeBuilder smithing(Ingredient var0, Ingredient var1, RecipeCategory var2, Item var3) {
      return new LegacyUpgradeRecipeBuilder(RecipeSerializer.SMITHING, var0, var1, var2, var3);
   }

   public LegacyUpgradeRecipeBuilder unlocks(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public void save(Consumer<FinishedRecipe> var1, String var2) {
      this.save(var1, new ResourceLocation(var2));
   }

   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement
         .parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT)
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(RequirementsStrategy.OR);
      var1.accept(
         new LegacyUpgradeRecipeBuilder.Result(
            var2, this.type, this.base, this.addition, this.result, this.advancement, var2.withPrefix("recipes/" + this.category.getFolderName() + "/")
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
      private final Ingredient base;
      private final Ingredient addition;
      private final Item result;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final RecipeSerializer<?> type;

      public Result(
         ResourceLocation var1, RecipeSerializer<?> var2, Ingredient var3, Ingredient var4, Item var5, Advancement.Builder var6, ResourceLocation var7
      ) {
         super();
         this.id = var1;
         this.type = var2;
         this.base = var3;
         this.addition = var4;
         this.result = var5;
         this.advancement = var6;
         this.advancementId = var7;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         var1.add("base", this.base.toJson());
         var1.add("addition", this.addition.toJson());
         JsonObject var2 = new JsonObject();
         var2.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
         var1.add("result", var2);
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
