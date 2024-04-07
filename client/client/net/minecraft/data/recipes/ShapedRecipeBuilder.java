package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

public class ShapedRecipeBuilder implements RecipeBuilder {
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
      ShapedRecipePattern var3 = this.ensureValid(var2);
      Advancement.Builder var4 = var1.advancement()
         .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(var2))
         .rewards(AdvancementRewards.Builder.recipe(var2))
         .requirements(AdvancementRequirements.Strategy.OR);
      this.criteria.forEach(var4::addCriterion);
      ShapedRecipe var5 = new ShapedRecipe(
         Objects.requireNonNullElse(this.group, ""),
         RecipeBuilder.determineBookCategory(this.category),
         var3,
         new ItemStack(this.result, this.count),
         this.showNotification
      );
      var1.accept(var2, var5, var4.build(var2.withPrefix("recipes/" + this.category.getFolderName() + "/")));
   }

   private ShapedRecipePattern ensureValid(ResourceLocation var1) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      } else {
         return ShapedRecipePattern.of(this.key, this.rows);
      }
   }
}
