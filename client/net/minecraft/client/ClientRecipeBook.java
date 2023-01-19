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
import net.minecraft.core.Registry;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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

   public void setupCollections(Iterable<Recipe<?>> var1) {
      Map var2 = categorizeAndGroupRecipes(var1);
      HashMap var3 = Maps.newHashMap();
      Builder var4 = ImmutableList.builder();
      var2.forEach((var2x, var3x) -> var3.put(var2x, (List)var3x.stream().map(RecipeCollection::new).peek(var4::add).collect(ImmutableList.toImmutableList())));
      RecipeBookCategories.AGGREGATE_CATEGORIES
         .forEach(
            (var1x, var2x) -> var3.put(
                  var1x,
                  (List)var2x.stream()
                     .flatMap(var1xx -> ((List)var3.getOrDefault(var1xx, ImmutableList.of())).stream())
                     .collect(ImmutableList.toImmutableList())
               )
         );
      this.collectionsByTab = ImmutableMap.copyOf(var3);
      this.allCollections = var4.build();
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

   private static RecipeBookCategories getCategory(Recipe<?> var0) {
      RecipeType var1 = var0.getType();
      if (var1 == RecipeType.CRAFTING) {
         ItemStack var2 = var0.getResultItem();
         CreativeModeTab var3 = var2.getItem().getItemCategory();
         if (var3 == CreativeModeTab.TAB_BUILDING_BLOCKS) {
            return RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
         } else if (var3 == CreativeModeTab.TAB_TOOLS || var3 == CreativeModeTab.TAB_COMBAT) {
            return RecipeBookCategories.CRAFTING_EQUIPMENT;
         } else {
            return var3 == CreativeModeTab.TAB_REDSTONE ? RecipeBookCategories.CRAFTING_REDSTONE : RecipeBookCategories.CRAFTING_MISC;
         }
      } else if (var1 == RecipeType.SMELTING) {
         if (var0.getResultItem().getItem().isEdible()) {
            return RecipeBookCategories.FURNACE_FOOD;
         } else {
            return var0.getResultItem().getItem() instanceof BlockItem ? RecipeBookCategories.FURNACE_BLOCKS : RecipeBookCategories.FURNACE_MISC;
         }
      } else if (var1 == RecipeType.BLASTING) {
         return var0.getResultItem().getItem() instanceof BlockItem ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
      } else if (var1 == RecipeType.SMOKING) {
         return RecipeBookCategories.SMOKER_FOOD;
      } else if (var1 == RecipeType.STONECUTTING) {
         return RecipeBookCategories.STONECUTTER;
      } else if (var1 == RecipeType.CAMPFIRE_COOKING) {
         return RecipeBookCategories.CAMPFIRE;
      } else if (var1 == RecipeType.SMITHING) {
         return RecipeBookCategories.SMITHING;
      } else {
         LOGGER.warn("Unknown recipe category: {}/{}", LogUtils.defer(() -> Registry.RECIPE_TYPE.getKey(var0.getType())), LogUtils.defer(var0::getId));
         return RecipeBookCategories.UNKNOWN;
      }
   }

   public List<RecipeCollection> getCollections() {
      return this.allCollections;
   }

   public List<RecipeCollection> getCollection(RecipeBookCategories var1) {
      return this.collectionsByTab.getOrDefault(var1, Collections.emptyList());
   }
}
