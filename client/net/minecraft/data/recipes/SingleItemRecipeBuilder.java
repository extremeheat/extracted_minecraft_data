package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
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
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
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

   public SingleItemRecipeBuilder unlockedBy(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
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
   public void save(RecipeOutput var1, ResourceLocation var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(AdvancementRequirements.Strategy.OR);
      this.criteria.forEach(var3::addCriterion);
      var1.accept(
         new SingleItemRecipeBuilder.Result(
            var2,
            this.type,
            this.group == null ? "" : this.group,
            this.ingredient,
            this.result,
            this.count,
            var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/"))
         )
      );
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static record Result(ResourceLocation a, RecipeSerializer<?> b, String c, Ingredient d, Item e, int f, AdvancementHolder g)
      implements FinishedRecipe {
      private final ResourceLocation id;
      private final RecipeSerializer<?> type;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final int count;
      private final AdvancementHolder advancement;

      public Result(ResourceLocation var1, RecipeSerializer<?> var2, String var3, Ingredient var4, Item var5, int var6, AdvancementHolder var7) {
         super();
         this.id = var1;
         this.type = var2;
         this.group = var3;
         this.ingredient = var4;
         this.result = var5;
         this.count = var6;
         this.advancement = var7;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         var1.add("ingredient", this.ingredient.toJson(false));
         var1.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
         var1.addProperty("count", this.count);
      }
   }
}
