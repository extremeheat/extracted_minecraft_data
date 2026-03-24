package net.minecraft.world.item.crafting;

public abstract class CustomRecipe implements CraftingRecipe {
   public CustomRecipe() {
      super();
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean showNotification() {
      return false;
   }

   public String group() {
      return "";
   }

   public CraftingBookCategory category() {
      return CraftingBookCategory.MISC;
   }

   public PlacementInfo placementInfo() {
      return PlacementInfo.NOT_PLACEABLE;
   }

   public abstract RecipeSerializer<? extends CustomRecipe> getSerializer();
}
