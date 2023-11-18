package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

public class ShapedRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
   private final RecipeCategory category;
   private final Item result;
   private final int count;
   private final List<String> rows = Lists.newArrayList();
   private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
   @Nullable
   private String group;
   private boolean showNotification = true;

   public ShapedRecipeBuilder(RecipeCategory var1, ItemLike var2, int var3) {
      super();
      this.category = var1;
      this.result = var2.asItem();
      this.count = var3;
   }

   public static ShapedRecipeBuilder shaped(RecipeCategory var0, ItemLike var1) {
      return shaped(var0, var1, 1);
   }

   public static ShapedRecipeBuilder shaped(RecipeCategory var0, ItemLike var1, int var2) {
      return new ShapedRecipeBuilder(var0, var1, var2);
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

   public ShapedRecipeBuilder unlockedBy(String var1, Criterion<?> var2) {
      this.criteria.put(var1, var2);
      return this;
   }

   public ShapedRecipeBuilder group(@Nullable String var1) {
      this.group = var1;
      return this;
   }

   public ShapedRecipeBuilder showNotification(boolean var1) {
      this.showNotification = var1;
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
         new ShapedRecipeBuilder.Result(
            var2,
            this.result,
            this.count,
            this.group == null ? "" : this.group,
            determineBookCategory(this.category),
            this.rows,
            this.key,
            var3.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/")),
            this.showNotification
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
         } else if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + var1);
         }
      }
   }

   static class Result extends CraftingRecipeBuilder.CraftingResult {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<String> pattern;
      private final Map<Character, Ingredient> key;
      private final AdvancementHolder advancement;
      private final boolean showNotification;

      public Result(
         ResourceLocation var1,
         Item var2,
         int var3,
         String var4,
         CraftingBookCategory var5,
         List<String> var6,
         Map<Character, Ingredient> var7,
         AdvancementHolder var8,
         boolean var9
      ) {
         super(var5);
         this.id = var1;
         this.result = var2;
         this.count = var3;
         this.group = var4;
         this.pattern = var6;
         this.key = var7;
         this.advancement = var8;
         this.showNotification = var9;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         super.serializeRecipeData(var1);
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
            var6.add(String.valueOf(var5.getKey()), ((Ingredient)var5.getValue()).toJson(false));
         }

         var1.add("key", var6);
         JsonObject var8 = new JsonObject();
         var8.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            var8.addProperty("count", this.count);
         }

         var1.add("result", var8);
         var1.addProperty("show_notification", this.showNotification);
      }

      @Override
      public RecipeSerializer<?> type() {
         return RecipeSerializer.SHAPED_RECIPE;
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
