package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SmithingTransformRecipeBuilder {
   private final Ingredient template;
   private final Ingredient base;
   private final Ingredient addition;
   private final RecipeCategory category;
   private final Item result;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   private final RecipeSerializer<?> type;

   public SmithingTransformRecipeBuilder(RecipeSerializer<?> var1, Ingredient var2, Ingredient var3, Ingredient var4, RecipeCategory var5, Item var6) {
      super();
      this.category = var5;
      this.type = var1;
      this.template = var2;
      this.base = var3;
      this.addition = var4;
      this.result = var6;
   }

   public static SmithingTransformRecipeBuilder smithing(Ingredient var0, Ingredient var1, Ingredient var2, RecipeCategory var3, Item var4) {
      return new SmithingTransformRecipeBuilder(RecipeSerializer.SMITHING_TRANSFORM, var0, var1, var2, var3, var4);
   }

   public SmithingTransformRecipeBuilder unlocks(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public void save(RecipeOutput var1, String var2) {
      this.save(var1, new ResourceLocation(var2));
   }

   public void save(RecipeOutput var1, ResourceLocation var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(AdvancementRequirements.Strategy.OR);
      this.criteria.forEach(var3::addCriterion);
      var1.accept(
         new SmithingTransformRecipeBuilder.Result(
            var2,
            this.type,
            this.template,
            this.base,
            this.addition,
            this.result,
            var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/"))
         )
      );
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static record Result(ResourceLocation a, RecipeSerializer<?> b, Ingredient c, Ingredient d, Ingredient e, Item f, AdvancementHolder g)
      implements FinishedRecipe {
      private final ResourceLocation id;
      private final RecipeSerializer<?> type;
      private final Ingredient template;
      private final Ingredient base;
      private final Ingredient addition;
      private final Item result;
      private final AdvancementHolder advancement;

      public Result(ResourceLocation var1, RecipeSerializer<?> var2, Ingredient var3, Ingredient var4, Ingredient var5, Item var6, AdvancementHolder var7) {
         super();
         this.id = var1;
         this.type = var2;
         this.template = var3;
         this.base = var4;
         this.addition = var5;
         this.result = var6;
         this.advancement = var7;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         var1.add("template", this.template.toJson(true));
         var1.add("base", this.base.toJson(true));
         var1.add("addition", this.addition.toJson(true));
         JsonObject var2 = new JsonObject();
         var2.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
         var1.add("result", var2);
      }
   }
}
