package net.minecraft.client.gui.screens.recipebook;

import java.util.Set;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class SmokingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
   protected boolean getFilteringCraftable() {
      return this.book.isSmokerFilteringCraftable();
   }

   protected void setFilteringCraftable(boolean var1) {
      this.book.setSmokerFilteringCraftable(var1);
   }

   protected boolean isGuiOpen() {
      return this.book.isSmokerGuiOpen();
   }

   protected void setGuiOpen(boolean var1) {
      this.book.setSmokerGuiOpen(var1);
   }

   protected String getRecipeFilterName() {
      return "gui.recipebook.toggleRecipes.smokable";
   }

   protected Set getFuelItems() {
      return AbstractFurnaceBlockEntity.getFuel().keySet();
   }
}
