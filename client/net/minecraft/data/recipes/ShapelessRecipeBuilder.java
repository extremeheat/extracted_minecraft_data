package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.List;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class ShapelessRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final int count;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   @Nullable
   private String group;

   public ShapelessRecipeBuilder(RecipeCategory var1, ItemLike var2, int var3) {
      super();
      this.category = var1;
      this.result = var2.asItem();
      this.count = var3;
   }

   public static ShapelessRecipeBuilder shapeless(RecipeCategory var0, ItemLike var1) {
      return new ShapelessRecipeBuilder(var0, var1, 1);
   }

   public static ShapelessRecipeBuilder shapeless(RecipeCategory var0, ItemLike var1, int var2) {
      return new ShapelessRecipeBuilder(var0, var1, var2);
   }

   public ShapelessRecipeBuilder requires(TagKey<Item> var1) {
      return this.requires(Ingredient.of(var1));
   }

   public ShapelessRecipeBuilder requires(ItemLike var1) {
      return this.requires(var1, 1);
   }

   public ShapelessRecipeBuilder requires(ItemLike var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.requires(Ingredient.of(var1));
      }

      return this;
   }

   public ShapelessRecipeBuilder requires(Ingredient var1) {
      return this.requires(var1, 1);
   }

   public ShapelessRecipeBuilder requires(Ingredient var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.ingredients.add(var1);
      }

      return this;
   }

   public ShapelessRecipeBuilder unlockedBy(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public ShapelessRecipeBuilder group(@Nullable String var1) {
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
         new ShapelessRecipeBuilder.Result(
            var2,
            this.result,
            this.count,
            this.group == null ? "" : this.group,
            determineBookCategory(this.category),
            this.ingredients,
            var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/"))
         )
      );
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static class Result extends CraftingRecipeBuilder.CraftingResult {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final AdvancementHolder advancement;

      public Result(ResourceLocation var1, Item var2, int var3, String var4, CraftingBookCategory var5, List<Ingredient> var6, AdvancementHolder var7) {
         super(var5);
         this.id = var1;
         this.result = var2;
         this.count = var3;
         this.group = var4;
         this.ingredients = var6;
         this.advancement = var7;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         super.serializeRecipeData(var1);
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         JsonArray var2 = new JsonArray();

         for(Ingredient var4 : this.ingredients) {
            var2.add(var4.toJson(false));
         }

         var1.add("ingredients", var2);
         JsonObject var5 = new JsonObject();
         var5.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            var5.addProperty("count", this.count);
         }

         var1.add("result", var5);
      }

      @Override
      public RecipeSerializer<?> type() {
         return RecipeSerializer.SHAPELESS_RECIPE;
      }

      @Override
      public ResourceLocation id() {
         return this.id;
      }

      @Override
      public AdvancementHolder advancement() {
         return this.advancement;
      }
   }
}
