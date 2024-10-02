package net.minecraft.stats;

import net.minecraft.world.inventory.RecipeBookType;

public class RecipeBook {
   protected final RecipeBookSettings bookSettings = new RecipeBookSettings();

   public RecipeBook() {
      super();
   }

   public boolean isOpen(RecipeBookType var1) {
      return this.bookSettings.isOpen(var1);
   }

   public void setOpen(RecipeBookType var1, boolean var2) {
      this.bookSettings.setOpen(var1, var2);
   }

   public boolean isFiltering(RecipeBookType var1) {
      return this.bookSettings.isFiltering(var1);
   }

   public void setFiltering(RecipeBookType var1, boolean var2) {
      this.bookSettings.setFiltering(var1, var2);
   }

   public void setBookSettings(RecipeBookSettings var1) {
      this.bookSettings.replaceFrom(var1);
   }

   public RecipeBookSettings getBookSettings() {
      return this.bookSettings.copy();
   }

   public void setBookSetting(RecipeBookType var1, boolean var2, boolean var3) {
      this.bookSettings.setOpen(var1, var2);
      this.bookSettings.setFiltering(var1, var3);
   }
}
