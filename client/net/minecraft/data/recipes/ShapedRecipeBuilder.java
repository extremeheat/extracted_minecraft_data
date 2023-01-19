package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class ShapedRecipeBuilder implements RecipeBuilder {
   private final Item result;
   private final int count;
   private final List<String> rows = Lists.newArrayList();
   private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   @Nullable
   private String group;

   public ShapedRecipeBuilder(ItemLike var1, int var2) {
      super();
      this.result = var1.asItem();
      this.count = var2;
   }

   public static ShapedRecipeBuilder shaped(ItemLike var0) {
      return shaped(var0, 1);
   }

   public static ShapedRecipeBuilder shaped(ItemLike var0, int var1) {
      return new ShapedRecipeBuilder(var0, var1);
   }

   public ShapedRecipeBuilder define(Character var1, TagKey<Item> var2) {
      return this.define(var1, Ingredient.of(var2));
   }

   public ShapedRecipeBuilder define(Character var1, ItemLike var2) {
      return this.define(var1, Ingredient.of(var2));
   }

   public ShapedRecipeBuilder define(Character var1, Ingredient var2) {
      if (this.key.containsKey(var1)) {
         throw new IllegalArgumentException("Symbol '" + var1 + "' is already defined!");
      } else if (var1 == ' ') {
         throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
      } else {
         this.key.put(var1, var2);
         return this;
      }
   }

   public ShapedRecipeBuilder pattern(String var1) {
      if (!this.rows.isEmpty() && var1.length() != this.rows.get(0).length()) {
         throw new IllegalArgumentException("Pattern must be the same width on every line!");
      } else {
         this.rows.add(var1);
         return this;
      }
   }

   public ShapedRecipeBuilder unlockedBy(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public ShapedRecipeBuilder group(@Nullable String var1) {
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
         new ShapedRecipeBuilder.Result(
            var2,
            this.result,
            this.count,
            this.group == null ? "" : this.group,
            this.rows,
            this.key,
            this.advancement,
            new ResourceLocation(var2.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + var2.getPath())
         )
      );
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.rows.isEmpty()) {
         throw new IllegalStateException("No pattern is defined for shaped recipe " + var1 + "!");
      } else {
         HashSet var2 = Sets.newHashSet(this.key.keySet());
         var2.remove(' ');

         for(String var4 : this.rows) {
            for(int var5 = 0; var5 < var4.length(); ++var5) {
               char var6 = var4.charAt(var5);
               if (!this.key.containsKey(var6) && var6 != ' ') {
                  throw new IllegalStateException("Pattern in recipe " + var1 + " uses undefined symbol '" + var6 + "'");
               }

               var2.remove(var6);
            }
         }

         if (!var2.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + var1);
         } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + var1 + " only takes in a single item - should it be a shapeless recipe instead?");
         } else if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + var1);
         }
      }
   }

   static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<String> pattern;
      private final Map<Character, Ingredient> key;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(
         ResourceLocation var1,
         Item var2,
         int var3,
         String var4,
         List<String> var5,
         Map<Character, Ingredient> var6,
         Advancement.Builder var7,
         ResourceLocation var8
      ) {
         super();
         this.id = var1;
         this.result = var2;
         this.count = var3;
         this.group = var4;
         this.pattern = var5;
         this.key = var6;
         this.advancement = var7;
         this.advancementId = var8;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         JsonArray var2 = new JsonArray();

         for(String var4 : this.pattern) {
            var2.add(var4);
         }

         var1.add("pattern", var2);
         JsonObject var6 = new JsonObject();

         for(Entry var5 : this.key.entrySet()) {
            var6.add(String.valueOf(var5.getKey()), ((Ingredient)var5.getValue()).toJson());
         }

         var1.add("key", var6);
         JsonObject var8 = new JsonObject();
         var8.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            var8.addProperty("count", this.count);
         }

         var1.add("result", var8);
      }

      @Override
      public RecipeSerializer<?> getType() {
         return RecipeSerializer.SHAPED_RECIPE;
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