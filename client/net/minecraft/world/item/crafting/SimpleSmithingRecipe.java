package net.minecraft.world.item.crafting;

import org.jspecify.annotations.Nullable;

public abstract class SimpleSmithingRecipe implements SmithingRecipe {
   protected final Recipe.CommonInfo commonInfo;
   private @Nullable PlacementInfo placementInfo;

   protected SimpleSmithingRecipe(final Recipe.CommonInfo commonInfo) {
      super();
      this.commonInfo = commonInfo;
   }

   public abstract RecipeSerializer<? extends SimpleSmithingRecipe> getSerializer();

   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = this.createPlacementInfo();
      }

      return this.placementInfo;
   }

   protected abstract PlacementInfo createPlacementInfo();

   public String group() {
      return "";
   }

   public final boolean showNotification() {
      return this.commonInfo.showNotification();
   }
}
