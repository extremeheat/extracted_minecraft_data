package net.minecraft.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;

public class ClientRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogUtils.getLogger();
   private Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab = ImmutableMap.of();
   private List<RecipeCollection> allCollections = ImmutableList.of();

   public ClientRecipeBook() {
      super();
   }

   public void setupCollections(Iterable<Recipe<?>> var1, RegistryAccess var2) {
      Map var3 = categorizeAndGroupRecipes(var1);
      HashMap var4 = Maps.newHashMap();
      Builder var5 = ImmutableList.builder();
      var3.forEach(
         (var3x, var4x) -> var4.put(
               var3x, (List)var4x.stream().map(var1xx -> new RecipeCollection(var2, var1xx)).peek(var5::add).collect(ImmutableList.toImmutableList())
            )
      );
      RecipeBookCategories.AGGREGATE_CATEGORIES
         .forEach(
            (var1x, var2x) -> var4.put(
                  var1x,
                  (List)var2x.stream()
                     .flatMap(var1xx -> ((List)var4.getOrDefault(var1xx, ImmutableList.of())).stream())
                     .collect(ImmutableList.toImmutableList())
               )
         );
      this.collectionsByTab = ImmutableMap.copyOf(var4);
      this.allCollections = var5.build();
   }

   private static Map<RecipeBookCategories, List<List<Recipe<?>>>> categorizeAndGroupRecipes(Iterable<Recipe<?>> var0) {
      HashMap var1 = Maps.newHashMap();
      HashBasedTable var2 = HashBasedTable.create();

      for(Recipe var4 : var0) {
         if (!var4.isSpecial() && !var4.isIncomplete()) {
            RecipeBookCategories var5 = getCategory(var4);
            String var6 = var4.getGroup();
            if (var6.isEmpty()) {
               var1.computeIfAbsent(var5, var0x -> Lists.newArrayList()).add(ImmutableList.of(var4));
            } else {
               Object var7 = (List)var2.get(var5, var6);
               if (var7 == null) {
                  var7 = Lists.newArrayList();
                  var2.put(var5, var6, var7);
                  var1.computeIfAbsent(var5, var0x -> Lists.newArrayList()).add(var7);
               }

               var7.add(var4);
            }
         }
      }

      return var1;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static RecipeBookCategories getCategory(Recipe<?> var0) {
      if (var0 instanceof CraftingRecipe var4) {
         return switch(var4.category()) {
            case BUILDING -> RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            case EQUIPMENT -> RecipeBookCategories.CRAFTING_EQUIPMENT;
            case REDSTONE -> RecipeBookCategories.CRAFTING_REDSTONE;
            case MISC -> RecipeBookCategories.CRAFTING_MISC;
         };
      } else {
         RecipeType var1 = var0.getType();
         if (var0 instanceof AbstractCookingRecipe var2) {
            CookingBookCategory var3 = var2.category();
            if (var1 == RecipeType.SMELTING) {
               return switch(var3) {
                  case BLOCKS -> RecipeBookCategories.FURNACE_BLOCKS;
                  case FOOD -> RecipeBookCategories.FURNACE_FOOD;
                  case MISC -> RecipeBookCategories.FURNACE_MISC;
               };
            }

            if (var1 == RecipeType.BLASTING) {
               return var3 == CookingBookCategory.BLOCKS ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
            }

            if (var1 == RecipeType.SMOKING) {
               return RecipeBookCategories.SMOKER_FOOD;
            }

            if (var1 == RecipeType.CAMPFIRE_COOKING) {
               return RecipeBookCategories.CAMPFIRE;
            }
         }

         if (var1 == RecipeType.STONECUTTING) {
            return RecipeBookCategories.STONECUTTER;
         } else if (var1 == RecipeType.SMITHING) {
            return RecipeBookCategories.SMITHING;
         } else {
            LOGGER.warn(
               "Unknown recipe category: {}/{}", LogUtils.defer(() -> BuiltInRegistries.RECIPE_TYPE.getKey(var0.getType())), LogUtils.defer(var0::getId)
            );
            return RecipeBookCategories.UNKNOWN;
         }
      }
   }

   public List<RecipeCollection> getCollections() {
      return this.allCollections;
   }

   public List<RecipeCollection> getCollection(RecipeBookCategories var1) {
      return this.collectionsByTab.getOrDefault(var1, Collections.emptyList());
   }
}
