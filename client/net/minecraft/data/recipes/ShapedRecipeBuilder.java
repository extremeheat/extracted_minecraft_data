package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.Iterator;
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
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapedRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List<String> rows = Lists.newArrayList();
   private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
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

   public ShapedRecipeBuilder define(Character var1, Tag<Item> var2) {
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
      if (!this.rows.isEmpty() && var1.length() != ((String)this.rows.get(0)).length()) {
         throw new IllegalArgumentException("Pattern must be the same width on every line!");
      } else {
         this.rows.add(var1);
         return this;
      }
   }

   public ShapedRecipeBuilder unlocks(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public ShapedRecipeBuilder group(String var1) {
      this.group = var1;
      return this;
   }

   public void save(Consumer<FinishedRecipe> var1) {
      this.save(var1, Registry.ITEM.getKey(this.result));
   }

   public void save(Consumer<FinishedRecipe> var1, String var2) {
      ResourceLocation var3 = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(var2)).equals(var3)) {
         throw new IllegalStateException("Shaped Recipe " + var2 + " should remove its 'save' argument");
      } else {
         this.save(var1, new ResourceLocation(var2));
      }
   }

   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)(new RecipeUnlockedTrigger.TriggerInstance(var2))).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(RequirementsStrategy.OR);
      var1.accept(new ShapedRecipeBuilder.Result(var2, this.result, this.count, this.group == null ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(var2.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + var2.getPath())));
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.rows.isEmpty()) {
         throw new IllegalStateException("No pattern is defined for shaped recipe " + var1 + "!");
      } else {
         HashSet var2 = Sets.newHashSet(this.key.keySet());
         var2.remove(' ');
         Iterator var3 = this.rows.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();

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
         } else if (this.rows.size() == 1 && ((String)this.rows.get(0)).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + var1 + " only takes in a single item - should it be a shapeless recipe instead?");
         } else if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + var1);
         }
      }
   }

   class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<String> pattern;
      private final Map<Character, Ingredient> key;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation var2, Item var3, int var4, String var5, List<String> var6, Map<Character, Ingredient> var7, Advancement.Builder var8, ResourceLocation var9) {
         super();
         this.id = var2;
         this.result = var3;
         this.count = var4;
         this.group = var5;
         this.pattern = var6;
         this.key = var7;
         this.advancement = var8;
         this.advancementId = var9;
      }

      public void serializeRecipeData(JsonObject var1) {
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         JsonArray var2 = new JsonArray();
         Iterator var3 = this.pattern.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var2.add(var4);
         }

         var1.add("pattern", var2);
         JsonObject var6 = new JsonObject();
         Iterator var7 = this.key.entrySet().iterator();

         while(var7.hasNext()) {
            Entry var5 = (Entry)var7.next();
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

      public RecipeSerializer<?> getType() {
         return RecipeSerializer.SHAPED_RECIPE;
      }

      public ResourceLocation getId() {
         return this.id;
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
