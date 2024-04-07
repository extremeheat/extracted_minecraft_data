package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;

public class BlastFurnaceMenu extends AbstractFurnaceMenu {
   public BlastFurnaceMenu(int var1, Inventory var2) {
      super(MenuType.BLAST_FURNACE, RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, var1, var2);
   }

   public BlastFurnaceMenu(int var1, Inventory var2, Container var3, ContainerData var4) {
      super(MenuType.BLAST_FURNACE, RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE, var1, var2, var3, var4);
   }
}
