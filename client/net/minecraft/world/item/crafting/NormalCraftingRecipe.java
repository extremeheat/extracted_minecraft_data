package net.minecraft.world.item.crafting;

import org.jspecify.annotations.Nullable;

public abstract class NormalCraftingRecipe implements CraftingRecipe {
   protected final Recipe.CommonInfo commonInfo;
   protected final CraftingRecipe.CraftingBookInfo bookInfo;
   private @Nullable PlacementInfo placementInfo;

   protected NormalCraftingRecipe(final Recipe.CommonInfo commonInfo, final CraftingRecipe.CraftingBookInfo bookInfo) {
      super();
      this.commonInfo = commonInfo;
      this.bookInfo = bookInfo;
   }

   public abstract RecipeSerializer<? extends NormalCraftingRecipe> getSerializer();

   public final String group() {
      return this.bookInfo.group();
   }

   public final CraftingBookCategory category() {
      return this.bookInfo.category();
   }

   public final boolean showNotification() {
      return this.commonInfo.showNotification();
   }

   protected abstract PlacementInfo createPlacementInfo();

   public final PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = this.createPlacementInfo();
      }

      return this.placementInfo;
   }
}
