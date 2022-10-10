package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeItemHelper;

public class RecipeList {
   private final List<IRecipe> field_192713_b = Lists.newArrayList();
   private final Set<IRecipe> field_194215_b = Sets.newHashSet();
   private final Set<IRecipe> field_194216_c = Sets.newHashSet();
   private final Set<IRecipe> field_194217_d = Sets.newHashSet();
   private boolean field_194218_e = true;

   public RecipeList() {
      super();
   }

   public boolean func_194209_a() {
      return !this.field_194217_d.isEmpty();
   }

   public void func_194214_a(RecipeBook var1) {
      Iterator var2 = this.field_192713_b.iterator();

      while(var2.hasNext()) {
         IRecipe var3 = (IRecipe)var2.next();
         if (var1.func_193830_f(var3)) {
            this.field_194217_d.add(var3);
         }
      }

   }

   public void func_194210_a(RecipeItemHelper var1, int var2, int var3, RecipeBook var4) {
      for(int var5 = 0; var5 < this.field_192713_b.size(); ++var5) {
         IRecipe var6 = (IRecipe)this.field_192713_b.get(var5);
         boolean var7 = var6.func_194133_a(var2, var3) && var4.func_193830_f(var6);
         if (var7) {
            this.field_194216_c.add(var6);
         } else {
            this.field_194216_c.remove(var6);
         }

         if (var7 && var1.func_194116_a(var6, (IntList)null)) {
            this.field_194215_b.add(var6);
         } else {
            this.field_194215_b.remove(var6);
         }
      }

   }

   public boolean func_194213_a(IRecipe var1) {
      return this.field_194215_b.contains(var1);
   }

   public boolean func_192708_c() {
      return !this.field_194215_b.isEmpty();
   }

   public boolean func_194212_c() {
      return !this.field_194216_c.isEmpty();
   }

   public List<IRecipe> func_192711_b() {
      return this.field_192713_b;
   }

   public List<IRecipe> func_194208_a(boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      Set var3 = var1 ? this.field_194215_b : this.field_194216_c;
      Iterator var4 = this.field_192713_b.iterator();

      while(var4.hasNext()) {
         IRecipe var5 = (IRecipe)var4.next();
         if (var3.contains(var5)) {
            var2.add(var5);
         }
      }

      return var2;
   }

   public List<IRecipe> func_194207_b(boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.field_192713_b.iterator();

      while(var3.hasNext()) {
         IRecipe var4 = (IRecipe)var3.next();
         if (this.field_194216_c.contains(var4) && this.field_194215_b.contains(var4) == var1) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public void func_192709_a(IRecipe var1) {
      this.field_192713_b.add(var1);
      if (this.field_194218_e) {
         ItemStack var2 = ((IRecipe)this.field_192713_b.get(0)).func_77571_b();
         ItemStack var3 = var1.func_77571_b();
         this.field_194218_e = ItemStack.func_179545_c(var2, var3) && ItemStack.func_77970_a(var2, var3);
      }

   }

   public boolean func_194211_e() {
      return this.field_194218_e;
   }
}
