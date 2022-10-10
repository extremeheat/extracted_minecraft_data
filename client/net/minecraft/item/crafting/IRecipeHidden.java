package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class IRecipeHidden implements IRecipe {
   private final ResourceLocation field_199563_a;

   public IRecipeHidden(ResourceLocation var1) {
      super();
      this.field_199563_a = var1;
   }

   public ResourceLocation func_199560_c() {
      return this.field_199563_a;
   }

   public boolean func_192399_d() {
      return true;
   }

   public ItemStack func_77571_b() {
      return ItemStack.field_190927_a;
   }
}
