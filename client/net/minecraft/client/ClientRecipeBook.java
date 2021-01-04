package net.minecraft.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public class ClientRecipeBook extends RecipeBook {
   private final RecipeManager recipes;
   private final Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab = Maps.newHashMap();
   private final List<RecipeCollection> collections = Lists.newArrayList();

   public ClientRecipeBook(RecipeManager var1) {
      super();
      this.recipes = var1;
   }

   public void setupCollections() {
      this.collections.clear();
      this.collectionsByTab.clear();
      HashBasedTable var1 = HashBasedTable.create();
      Iterator var2 = this.recipes.getRecipes().iterator();

      while(var2.hasNext()) {
         Recipe var3 = (Recipe)var2.next();
         if (!var3.isSpecial()) {
            RecipeBookCategories var4 = getCategory(var3);
            String var5 = var3.getGroup();
            RecipeCollection var6;
            if (var5.isEmpty()) {
               var6 = this.createCollection(var4);
            } else {
               var6 = (RecipeCollection)var1.get(var4, var5);
               if (var6 == null) {
                  var6 = this.createCollection(var4);
                  var1.put(var4, var5, var6);
               }
            }

            var6.add(var3);
         }
      }

   }

   private RecipeCollection createCollection(RecipeBookCategories var1) {
      RecipeCollection var2 = new RecipeCollection();
      this.collections.add(var2);
      ((List)this.collectionsByTab.computeIfAbsent(var1, (var0) -> {
         return Lists.newArrayList();
      })).add(var2);
      if (var1 != RecipeBookCategories.FURNACE_BLOCKS && var1 != RecipeBookCategories.FURNACE_FOOD && var1 != RecipeBookCategories.FURNACE_MISC) {
         if (var1 != RecipeBookCategories.BLAST_FURNACE_BLOCKS && var1 != RecipeBookCategories.BLAST_FURNACE_MISC) {
            if (var1 == RecipeBookCategories.SMOKER_FOOD) {
               this.addToCollection(RecipeBookCategories.SMOKER_SEARCH, var2);
            } else if (var1 == RecipeBookCategories.STONECUTTER) {
               this.addToCollection(RecipeBookCategories.STONECUTTER, var2);
            } else if (var1 == RecipeBookCategories.CAMPFIRE) {
               this.addToCollection(RecipeBookCategories.CAMPFIRE, var2);
            } else {
               this.addToCollection(RecipeBookCategories.SEARCH, var2);
            }
         } else {
            this.addToCollection(RecipeBookCategories.BLAST_FURNACE_SEARCH, var2);
         }
      } else {
         this.addToCollection(RecipeBookCategories.FURNACE_SEARCH, var2);
      }

      return var2;
   }

   private void addToCollection(RecipeBookCategories var1, RecipeCollection var2) {
      ((List)this.collectionsByTab.computeIfAbsent(var1, (var0) -> {
         return Lists.newArrayList();
      })).add(var2);
   }

   private static RecipeBookCategories getCategory(Recipe<?> var0) {
      RecipeType var1 = var0.getType();
      if (var1 == RecipeType.SMELTING) {
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
      } else {
         ItemStack var2 = var0.getResultItem();
         CreativeModeTab var3 = var2.getItem().getItemCategory();
         if (var3 == CreativeModeTab.TAB_BUILDING_BLOCKS) {
            return RecipeBookCategories.BUILDING_BLOCKS;
         } else if (var3 != CreativeModeTab.TAB_TOOLS && var3 != CreativeModeTab.TAB_COMBAT) {
            return var3 == CreativeModeTab.TAB_REDSTONE ? RecipeBookCategories.REDSTONE : RecipeBookCategories.MISC;
         } else {
            return RecipeBookCategories.EQUIPMENT;
         }
      }
   }

   public static List<RecipeBookCategories> getCategories(RecipeBookMenu<?> var0) {
      if (!(var0 instanceof CraftingMenu) && !(var0 instanceof InventoryMenu)) {
         if (var0 instanceof FurnaceMenu) {
            return Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC});
         } else if (var0 instanceof BlastFurnaceMenu) {
            return Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC});
         } else {
            return var0 instanceof SmokerMenu ? Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD}) : Lists.newArrayList();
         }
      } else {
         return Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.SEARCH, RecipeBookCategories.EQUIPMENT, RecipeBookCategories.BUILDING_BLOCKS, RecipeBookCategories.MISC, RecipeBookCategories.REDSTONE});
      }
   }

   public List<RecipeCollection> getCollections() {
      return this.collections;
   }

   public List<RecipeCollection> getCollection(RecipeBookCategories var1) {
      return (List)this.collectionsByTab.getOrDefault(var1, Collections.emptyList());
   }
}
