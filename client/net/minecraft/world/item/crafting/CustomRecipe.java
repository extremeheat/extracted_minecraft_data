package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class CustomRecipe implements CraftingRecipe {
   // $FF: renamed from: id net.minecraft.resources.ResourceLocation
   private final ResourceLocation field_52;

   public CustomRecipe(ResourceLocation var1) {
      super();
      this.field_52 = var1;
   }

   public ResourceLocation getId() {
      return this.field_52;
   }

   public boolean isSpecial() {
      return true;
   }

   public ItemStack getResultItem() {
      return ItemStack.EMPTY;
   }
}
