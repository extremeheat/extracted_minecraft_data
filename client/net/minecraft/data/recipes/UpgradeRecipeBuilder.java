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

public class UpgradeRecipeBuilder {
   private final Ingredient base;
   private final Ingredient addition;
   private final Item result;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private final RecipeSerializer<?> type;

   public UpgradeRecipeBuilder(RecipeSerializer<?> var1, Ingredient var2, Ingredient var3, Item var4) {
      super();
      this.type = var1;
      this.base = var2;
      this.addition = var3;
      this.result = var4;
   }

   public static UpgradeRecipeBuilder smithing(Ingredient var0, Ingredient var1, Item var2) {
      return new UpgradeRecipeBuilder(RecipeSerializer.SMITHING, var0, var1, var2);
   }

   public UpgradeRecipeBuilder unlocks(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public void save(Consumer<FinishedRecipe> var1, String var2) {
      this.save(var1, new ResourceLocation(var2));
   }

   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)RecipeUnlockedTrigger.unlocked(var2)).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(RequirementsStrategy.field_0);
      RecipeSerializer var10004 = this.type;
      Ingredient var10005 = this.base;
      Ingredient var10006 = this.addition;
      Item var10007 = this.result;
      Advancement.Builder var10008 = this.advancement;
      String var10011 = var2.getNamespace();
      String var10012 = this.result.getItemCategory().getRecipeFolderName();
      var1.accept(new UpgradeRecipeBuilder.Result(var2, var10004, var10005, var10006, var10007, var10008, new ResourceLocation(var10011, "recipes/" + var10012 + "/" + var2.getPath())));
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static class Result implements FinishedRecipe {
      // $FF: renamed from: id net.minecraft.resources.ResourceLocation
      private final ResourceLocation field_226;
      private final Ingredient base;
      private final Ingredient addition;
      private final Item result;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final RecipeSerializer<?> type;

      public Result(ResourceLocation var1, RecipeSerializer<?> var2, Ingredient var3, Ingredient var4, Item var5, Advancement.Builder var6, ResourceLocation var7) {
         super();
         this.field_226 = var1;
         this.type = var2;
         this.base = var3;
         this.addition = var4;
         this.result = var5;
         this.advancement = var6;
         this.advancementId = var7;
      }

      public void serializeRecipeData(JsonObject var1) {
         var1.add("base", this.base.toJson());
         var1.add("addition", this.addition.toJson());
         JsonObject var2 = new JsonObject();
         var2.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         var1.add("result", var2);
      }

      public ResourceLocation getId() {
         return this.field_226;
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
