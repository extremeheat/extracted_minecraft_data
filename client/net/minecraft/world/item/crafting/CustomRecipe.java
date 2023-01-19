package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class CustomRecipe implements CraftingRecipe {
   private final ResourceLocation id;

   public CustomRecipe(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public boolean isSpecial() {
      return true;
   }

   @Override
   public ItemStack getResultItem() {
      return ItemStack.EMPTY;
   }
}
