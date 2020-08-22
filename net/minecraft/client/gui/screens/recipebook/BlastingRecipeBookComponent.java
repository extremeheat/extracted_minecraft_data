package net.minecraft.client.gui.screens.recipebook;

import java.util.Set;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class BlastingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
   protected boolean getFilteringCraftable() {
      return this.book.isBlastingFurnaceFilteringCraftable();
   }

   protected void setFilteringCraftable(boolean var1) {
      this.book.setBlastingFurnaceFilteringCraftable(var1);
   }

   protected boolean isGuiOpen() {
      return this.book.isBlastingFurnaceGuiOpen();
   }

   protected void setGuiOpen(boolean var1) {
      this.book.setBlastingFurnaceGuiOpen(var1);
   }

   protected String getRecipeFilterName() {
      return "gui.recipebook.toggleRecipes.blastable";
   }

   protected Set getFuelItems() {
      return AbstractFurnaceBlockEntity.getFuel().keySet();
   }
}
