package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeButton extends AbstractWidget {
   private static final ResourceLocation SLOT_MANY_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_craftable");
   private static final ResourceLocation SLOT_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_craftable");
   private static final ResourceLocation SLOT_MANY_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_uncraftable");
   private static final ResourceLocation SLOT_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_uncraftable");
   private static final float ANIMATION_TIME = 15.0F;
   private static final int BACKGROUND_SIZE = 25;
   private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
   private RecipeCollection collection;
   private List<RecipeHolder<?>> recipes = List.of();
   private final SlotSelectTime slotSelectTime;
   private float animationTime;

   public RecipeButton(SlotSelectTime var1) {
      super(0, 0, 25, 25, CommonComponents.EMPTY);
      this.slotSelectTime = var1;
   }

   public void init(RecipeCollection var1, boolean var2, RecipeBookPage var3) {
      this.collection = var1;
      this.recipes = var1.getFittingRecipes(var2 ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY);

      for (RecipeHolder var5 : this.recipes) {
         if (var3.getRecipeBook().willHighlight(var5)) {
            var3.recipesShown(this.recipes);
            this.animationTime = 15.0F;
            break;
         }
      }
   }

   public RecipeCollection getCollection() {
      return this.collection;
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      ResourceLocation var5;
      if (this.collection.hasCraftable()) {
         if (this.hasMultipleRecipes()) {
            var5 = SLOT_MANY_CRAFTABLE_SPRITE;
         } else {
            var5 = SLOT_CRAFTABLE_SPRITE;
         }
      } else if (this.hasMultipleRecipes()) {
         var5 = SLOT_MANY_UNCRAFTABLE_SPRITE;
      } else {
         var5 = SLOT_UNCRAFTABLE_SPRITE;
      }

      boolean var6 = this.animationTime > 0.0F;
      if (var6) {
         float var7 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         var1.pose().pushPose();
         var1.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
         var1.pose().scale(var7, var7, 1.0F);
         var1.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
         this.animationTime -= var4;
      }

      var1.blitSprite(RenderType::guiTextured, var5, this.getX(), this.getY(), this.width, this.height);
      ItemStack var9 = this.getRecipe().value().getResultItem(this.collection.registryAccess());
      int var8 = 4;
      if (this.collection.hasSingleResultItem() && this.hasMultipleRecipes()) {
         var1.renderItem(var9, this.getX() + var8 + 1, this.getY() + var8 + 1, 0, 10);
         var8--;
      }

      var1.renderFakeItem(var9, this.getX() + var8, this.getY() + var8);
      if (var6) {
         var1.pose().popPose();
      }
   }

   private boolean hasMultipleRecipes() {
      return this.recipes.size() > 1;
   }

   public boolean isOnlyOption() {
      return this.recipes.size() == 1;
   }

   public RecipeHolder<?> getRecipe() {
      int var1 = this.slotSelectTime.currentIndex() % this.recipes.size();
      return this.recipes.get(var1);
   }

   public List<Component> getTooltipText() {
      ItemStack var1 = this.getRecipe().value().getResultItem(this.collection.registryAccess());
      ArrayList var2 = Lists.newArrayList(Screen.getTooltipFromItem(Minecraft.getInstance(), var1));
      if (this.hasMultipleRecipes()) {
         var2.add(MORE_RECIPES_TOOLTIP);
      }

      return var2;
   }

   @Override
   public void updateWidgetNarration(NarrationElementOutput var1) {
      ItemStack var2 = this.getRecipe().value().getResultItem(this.collection.registryAccess());
      var1.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", var2.getHoverName()));
      if (this.hasMultipleRecipes()) {
         var1.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more"));
      } else {
         var1.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
      }
   }

   @Override
   public int getWidth() {
      return 25;
   }

   @Override
   protected boolean isValidClickButton(int var1) {
      return var1 == 0 || var1 == 1;
   }
}
