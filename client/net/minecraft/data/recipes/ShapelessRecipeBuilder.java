package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
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

public class ShapelessRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private String group;

   public ShapelessRecipeBuilder(ItemLike var1, int var2) {
      super();
      this.result = var1.asItem();
      this.count = var2;
   }

   public static ShapelessRecipeBuilder shapeless(ItemLike var0) {
      return new ShapelessRecipeBuilder(var0, 1);
   }

   public static ShapelessRecipeBuilder shapeless(ItemLike var0, int var1) {
      return new ShapelessRecipeBuilder(var0, var1);
   }

   public ShapelessRecipeBuilder requires(Tag<Item> var1) {
      return this.requires(Ingredient.of(var1));
   }

   public ShapelessRecipeBuilder requires(ItemLike var1) {
      return this.requires((ItemLike)var1, 1);
   }

   public ShapelessRecipeBuilder requires(ItemLike var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.requires(Ingredient.of(var1));
      }

      return this;
   }

   public ShapelessRecipeBuilder requires(Ingredient var1) {
      return this.requires((Ingredient)var1, 1);
   }

   public ShapelessRecipeBuilder requires(Ingredient var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.ingredients.add(var1);
      }

      return this;
   }

   public ShapelessRecipeBuilder unlocks(String var1, CriterionTriggerInstance var2) {
      this.advancement.addCriterion(var1, var2);
      return this;
   }

   public ShapelessRecipeBuilder group(String var1) {
      this.group = var1;
      return this;
   }

   public void save(Consumer<FinishedRecipe> var1) {
      this.save(var1, Registry.ITEM.getKey(this.result));
   }

   public void save(Consumer<FinishedRecipe> var1, String var2) {
      ResourceLocation var3 = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(var2)).equals(var3)) {
         throw new IllegalStateException("Shapeless Recipe " + var2 + " should remove its 'save' argument");
      } else {
         this.save(var1, new ResourceLocation(var2));
      }
   }

   public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2) {
      this.ensureValid(var2);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)(new RecipeUnlockedTrigger.TriggerInstance(var2))).rewards(AdvancementRewards.Builder.recipe(var2)).requirements(RequirementsStrategy.OR);
      var1.accept(new ShapelessRecipeBuilder.Result(var2, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(var2.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + var2.getPath())));
   }

   private void ensureValid(ResourceLocation var1) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation var1, Item var2, int var3, String var4, List<Ingredient> var5, Advancement.Builder var6, ResourceLocation var7) {
         super();
         this.id = var1;
         this.result = var2;
         this.count = var3;
         this.group = var4;
         this.ingredients = var5;
         this.advancement = var6;
         this.advancementId = var7;
      }

      public void serializeRecipeData(JsonObject var1) {
         if (!this.group.isEmpty()) {
            var1.addProperty("group", this.group);
         }

         JsonArray var2 = new JsonArray();
         Iterator var3 = this.ingredients.iterator();

         while(var3.hasNext()) {
            Ingredient var4 = (Ingredient)var3.next();
            var2.add(var4.toJson());
         }

         var1.add("ingredients", var2);
         JsonObject var5 = new JsonObject();
         var5.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            var5.addProperty("count", this.count);
         }

         var1.add("result", var5);
      }

      public RecipeSerializer<?> getType() {
         return RecipeSerializer.SHAPELESS_RECIPE;
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
