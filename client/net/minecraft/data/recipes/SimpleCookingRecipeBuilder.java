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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final CookingBookCategory bookCategory;
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   @Nullable
   private String group;
   private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

   private SimpleCookingRecipeBuilder(
      RecipeCategory var1,
      CookingBookCategory var2,
      ItemLike var3,
      Ingredient var4,
      float var5,
      int var6,
      RecipeSerializer<? extends AbstractCookingRecipe> var7
   ) {
      super();
      this.category = var1;
      this.bookCategory = var2;
      this.result = var3.asItem();
      this.ingredient = var4;
      this.experience = var5;
      this.cookingTime = var6;
      this.serializer = var7;
   }

   public static SimpleCookingRecipeBuilder generic(
      Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4, RecipeSerializer<? extends AbstractCookingRecipe> var5
   ) {
      return new SimpleCookingRecipeBuilder(var1, determineRecipeCategory(var5, var2), var2, var0, var3, var4, var5);
   }

   public static SimpleCookingRecipeBuilder campfireCooking(Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4) {
      return new SimpleCookingRecipeBuilder(var1, CookingBookCategory.FOOD, var2, var0, var3, var4, RecipeSerializer.CAMPFIRE_COOKING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder blasting(Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4) {
      return new SimpleCookingRecipeBuilder(var1, determineBlastingRecipeCategory(var2), var2, var0, var3, var4, RecipeSerializer.BLASTING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder smelting(Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4) {
      return new SimpleCookingRecipeBuilder(var1, determineSmeltingRecipeCategory(var2), var2, var0, var3, var4, RecipeSerializer.SMELTING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder smoking(Ingredient var0, RecipeCategory var1, ItemLike var2, float var3, int var4) {
      return new SimpleCookingRecipeBuilder(var1, CookingBookCategory.FOOD, var2, var0, var3, var4, RecipeSerializer.SMOKING_RECIPE);
   }

   public SimpleCookingRecipeBuilder unlockedBy(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
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
   public void save(RecipeOutput var1, ResourceLocation var2) {
      this.ensureValid(var2);
      Advancement.Builder var3 = var1.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(AdvancementRequirements.Strategy.OR);
      this.criteria.forEach(var3::addCriterion);
      var1.accept(
         new SimpleCookingRecipeBuilder.Result(
            var2,
            this.group == null ? "" : this.group,
            this.bookCategory,
            this.ingredient,
            this.result,
            this.experience,
            this.cookingTime,
            var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/")),
            this.serializer
         )
      );
   }

   private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike var0) {
      if (var0.asItem().isEdible()) {
         return CookingBookCategory.FOOD;
      } else {
         return var0.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
      }
   }

   private static CookingBookCategory determineBlastingRecipeCategory(ItemLike var0) {
      return var0.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
   }

   private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> var0, ItemLike var1) {
      if (var0 == RecipeSerializer.SMELTING_RECIPE) {
         return determineSmeltingRecipeCategory(var1);
      } else if (var0 == RecipeSerializer.BLASTING_RECIPE) {
         return determineBlastingRecipeCategory(var1);
      } else if (var0 != RecipeSerializer.SMOKING_RECIPE && var0 != RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
         throw new IllegalStateException("Unknown cooking recipe type");
      } else {
         return CookingBookCategory.FOOD;
      }
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   static record Result(
      ResourceLocation a,
      String b,
      CookingBookCategory c,
      Ingredient d,
      Item e,
      float f,
      int g,
      AdvancementHolder h,
      RecipeSerializer<? extends AbstractCookingRecipe> i
   ) implements FinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final CookingBookCategory category;
      private final Ingredient ingredient;
      private final Item result;
      private final float experience;
      private final int cookingTime;
      private final AdvancementHolder advancement;
      private final RecipeSerializer<? extends AbstractCookingRecipe> type;

      Result(
         ResourceLocation var1,
         String var2,
         CookingBookCategory var3,
         Ingredient var4,
         Item var5,
         float var6,
         int var7,
         AdvancementHolder var8,
         RecipeSerializer<? extends AbstractCookingRecipe> var9
      ) {
         super();
         this.id = var1;
         this.group = var2;
         this.category = var3;
         this.ingredient = var4;
         this.result = var5;
         this.experience = var6;
         this.cookingTime = var7;
         this.advancement = var8;
         this.type = var9;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         var1.addProperty("category", this.category.getSerializedName());
         var1.add("ingredient", this.ingredient.toJson(false));
         var1.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
         var1.addProperty("experience", this.experience);
         var1.addProperty("cookingtime", this.cookingTime);
      }
   }
}
