package net.minecraft.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;

public class ClientRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogUtils.getLogger();
   private Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab = ImmutableMap.of();
   private List<RecipeCollection> allCollections = ImmutableList.of();

   public ClientRecipeBook() {
      super();
   }

   public void setupCollections(Iterable<RecipeHolder<?>> var1, RegistryAccess var2) {
      Map var3 = categorizeAndGroupRecipes(var1);
      HashMap var4 = Maps.newHashMap();
      ImmutableList.Builder var5 = ImmutableList.builder();
      var3.forEach((var3x, var4x) -> {
         Stream var10002 = var4x.stream().map((var1) -> {
            return new RecipeCollection(var2, var1);
         });
         Objects.requireNonNull(var5);
         var4.put(var3x, (List)var10002.peek(var5::add).collect(ImmutableList.toImmutableList()));
      });
      RecipeBookCategories.AGGREGATE_CATEGORIES.forEach((var1x, var2x) -> {
         var4.put(var1x, (List)var2x.stream().flatMap((var1) -> {
            return ((List)var4.getOrDefault(var1, ImmutableList.of())).stream();
         }).collect(ImmutableList.toImmutableList()));
      });
      this.collectionsByTab = ImmutableMap.copyOf(var4);
      this.allCollections = var5.build();
   }

   private static Map<RecipeBookCategories, List<List<RecipeHolder<?>>>> categorizeAndGroupRecipes(Iterable<RecipeHolder<?>> var0) {
      HashMap var1 = Maps.newHashMap();
      HashBasedTable var2 = HashBasedTable.create();
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         RecipeHolder var4 = (RecipeHolder)var3.next();
         Recipe var5 = var4.value();
         if (!var5.isSpecial() && !var5.isIncomplete()) {
            RecipeBookCategories var6 = getCategory(var4);
            String var7 = var5.getGroup();
            if (var7.isEmpty()) {
               ((List)var1.computeIfAbsent(var6, (var0x) -> {
                  return Lists.newArrayList();
               })).add(ImmutableList.of(var4));
            } else {
               Object var8 = (List)var2.get(var6, var7);
               if (var8 == null) {
                  var8 = Lists.newArrayList();
                  var2.put(var6, var7, var8);
                  ((List)var1.computeIfAbsent(var6, (var0x) -> {
                     return Lists.newArrayList();
                  })).add(var8);
               }

               ((List)var8).add(var4);
            }
         }
      }

      return var1;
   }

   private static RecipeBookCategories getCategory(RecipeHolder<?> var0) {
      Recipe var1 = var0.value();
      RecipeBookCategories var5;
      if (var1 instanceof CraftingRecipe) {
         CraftingRecipe var6 = (CraftingRecipe)var1;
         switch (var6.category()) {
            case BUILDING -> var5 = RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            case EQUIPMENT -> var5 = RecipeBookCategories.CRAFTING_EQUIPMENT;
            case REDSTONE -> var5 = RecipeBookCategories.CRAFTING_REDSTONE;
            case MISC -> var5 = RecipeBookCategories.CRAFTING_MISC;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var5;
      } else {
         RecipeType var2 = var1.getType();
         if (var1 instanceof AbstractCookingRecipe) {
            AbstractCookingRecipe var3 = (AbstractCookingRecipe)var1;
            CookingBookCategory var4 = var3.category();
            if (var2 == RecipeType.SMELTING) {
               switch (var4) {
                  case BLOCKS -> var5 = RecipeBookCategories.FURNACE_BLOCKS;
                  case FOOD -> var5 = RecipeBookCategories.FURNACE_FOOD;
                  case MISC -> var5 = RecipeBookCategories.FURNACE_MISC;
                  default -> throw new MatchException((String)null, (Throwable)null);
               }

               return var5;
            }

            if (var2 == RecipeType.BLASTING) {
               return var4 == CookingBookCategory.BLOCKS ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
            }

            if (var2 == RecipeType.SMOKING) {
               return RecipeBookCategories.SMOKER_FOOD;
            }

            if (var2 == RecipeType.CAMPFIRE_COOKING) {
               return RecipeBookCategories.CAMPFIRE;
            }
         }

         if (var2 == RecipeType.STONECUTTING) {
            return RecipeBookCategories.STONECUTTER;
         } else if (var2 == RecipeType.SMITHING) {
            return RecipeBookCategories.SMITHING;
         } else {
            Logger var10000 = LOGGER;
            Object var10002 = LogUtils.defer(() -> {
               return BuiltInRegistries.RECIPE_TYPE.getKey(var1.getType());
            });
            Objects.requireNonNull(var0);
            var10000.warn("Unknown recipe category: {}/{}", var10002, LogUtils.defer(var0::id));
            return RecipeBookCategories.UNKNOWN;
         }
      }
   }

   public List<RecipeCollection> getCollections() {
      return this.allCollections;
   }

   public List<RecipeCollection> getCollection(RecipeBookCategories var1) {
      return (List)this.collectionsByTab.getOrDefault(var1, Collections.emptyList());
   }
}
