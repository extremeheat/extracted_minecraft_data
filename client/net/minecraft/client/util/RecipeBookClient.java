package net.minecraft.client.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeManager;

public class RecipeBookClient extends RecipeBook {
   private final RecipeManager field_199645_e;
   private final Map<RecipeBookCategories, List<RecipeList>> field_197931_e = Maps.newHashMap();
   private final List<RecipeList> field_197932_f = Lists.newArrayList();

   public RecipeBookClient(RecipeManager var1) {
      super();
      this.field_199645_e = var1;
   }

   public void func_199644_c() {
      this.field_197932_f.clear();
      this.field_197931_e.clear();
      HashBasedTable var1 = HashBasedTable.create();
      Iterator var2 = this.field_199645_e.func_199510_b().iterator();

      while(var2.hasNext()) {
         IRecipe var3 = (IRecipe)var2.next();
         if (!var3.func_192399_d()) {
            RecipeBookCategories var4 = func_202887_g(var3);
            String var5 = var3.func_193358_e();
            RecipeList var6;
            if (var5.isEmpty()) {
               var6 = this.func_202889_b(var4);
            } else {
               var6 = (RecipeList)var1.get(var4, var5);
               if (var6 == null) {
                  var6 = this.func_202889_b(var4);
                  var1.put(var4, var5, var6);
               }
            }

            var6.func_192709_a(var3);
         }
      }

   }

   private RecipeList func_202889_b(RecipeBookCategories var1) {
      RecipeList var2 = new RecipeList();
      this.field_197932_f.add(var2);
      ((List)this.field_197931_e.computeIfAbsent(var1, (var0) -> {
         return Lists.newArrayList();
      })).add(var2);
      if (var1 != RecipeBookCategories.FURNACE_BLOCKS && var1 != RecipeBookCategories.FURNACE_FOOD && var1 != RecipeBookCategories.FURNACE_MISC) {
         ((List)this.field_197931_e.computeIfAbsent(RecipeBookCategories.SEARCH, (var0) -> {
            return Lists.newArrayList();
         })).add(var2);
      } else {
         ((List)this.field_197931_e.computeIfAbsent(RecipeBookCategories.FURNACE_SEARCH, (var0) -> {
            return Lists.newArrayList();
         })).add(var2);
      }

      return var2;
   }

   private static RecipeBookCategories func_202887_g(IRecipe var0) {
      if (var0 instanceof FurnaceRecipe) {
         if (var0.func_77571_b().func_77973_b() instanceof ItemFood) {
            return RecipeBookCategories.FURNACE_FOOD;
         } else {
            return var0.func_77571_b().func_77973_b() instanceof ItemBlock ? RecipeBookCategories.FURNACE_BLOCKS : RecipeBookCategories.FURNACE_MISC;
         }
      } else {
         ItemStack var1 = var0.func_77571_b();
         ItemGroup var2 = var1.func_77973_b().func_77640_w();
         if (var2 == ItemGroup.field_78030_b) {
            return RecipeBookCategories.BUILDING_BLOCKS;
         } else if (var2 != ItemGroup.field_78040_i && var2 != ItemGroup.field_78037_j) {
            return var2 == ItemGroup.field_78028_d ? RecipeBookCategories.REDSTONE : RecipeBookCategories.MISC;
         } else {
            return RecipeBookCategories.EQUIPMENT;
         }
      }
   }

   public static List<RecipeBookCategories> func_202888_a(Container var0) {
      if (!(var0 instanceof ContainerWorkbench) && !(var0 instanceof ContainerPlayer)) {
         return var0 instanceof ContainerFurnace ? Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC}) : Lists.newArrayList();
      } else {
         return Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.SEARCH, RecipeBookCategories.EQUIPMENT, RecipeBookCategories.BUILDING_BLOCKS, RecipeBookCategories.MISC, RecipeBookCategories.REDSTONE});
      }
   }

   public List<RecipeList> func_199642_d() {
      return this.field_197932_f;
   }

   public List<RecipeList> func_202891_a(RecipeBookCategories var1) {
      return (List)this.field_197931_e.getOrDefault(var1, Collections.emptyList());
   }
}
