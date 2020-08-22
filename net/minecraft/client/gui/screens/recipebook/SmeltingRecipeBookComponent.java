package net.minecraft.client.gui.screens.recipebook;

import java.util.Set;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class SmeltingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
   protected boolean getFilteringCraftable() {
      return this.book.isFurnaceFilteringCraftable();
   }

   protected void setFilteringCraftable(boolean var1) {
      this.book.setFurnaceFilteringCraftable(var1);
   }

   protected boolean isGuiOpen() {
      return this.book.isFurnaceGuiOpen();
   }

   protected void setGuiOpen(boolean var1) {
      this.book.setFurnaceGuiOpen(var1);
   }

   protected String getRecipeFilterName() {
      return "gui.recipebook.toggleRecipes.smeltable";
   }

   protected Set getFuelItems() {
      return AbstractFurnaceBlockEntity.getFuel().keySet();
   }
}
