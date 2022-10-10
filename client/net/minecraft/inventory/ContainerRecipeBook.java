package net.minecraft.inventory;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;

public abstract class ContainerRecipeBook extends Container {
   public ContainerRecipeBook() {
      super();
   }

   public abstract void func_201771_a(RecipeItemHelper var1);

   public abstract void func_201768_e();

   public abstract boolean func_201769_a(IRecipe var1);

   public abstract int func_201767_f();

   public abstract int func_201770_g();

   public abstract int func_201772_h();

   public abstract int func_203721_h();
}
