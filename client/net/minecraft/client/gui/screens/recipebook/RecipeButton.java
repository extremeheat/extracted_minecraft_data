package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeButton extends AbstractWidget {
   private static final ResourceLocation SLOT_MANY_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_craftable");
   private static final ResourceLocation SLOT_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_craftable");
   private static final ResourceLocation SLOT_MANY_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_many_uncraftable");
   private static final ResourceLocation SLOT_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_uncraftable");
   private static final float ANIMATION_TIME = 15.0F;
   private static final int BACKGROUND_SIZE = 25;
   public static final int TICKS_TO_SWAP = 30;
   private static final Component MORE_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.moreRecipes");
   private RecipeBookMenu<?, ?> menu;
   private RecipeBook book;
   private RecipeCollection collection;
   private float time;
   private float animationTime;
   private int currentIndex;

   public RecipeButton() {
      super(0, 0, 25, 25, CommonComponents.EMPTY);
   }

   public void init(RecipeCollection var1, RecipeBookPage var2) {
      this.collection = var1;
      this.menu = (RecipeBookMenu)var2.getMinecraft().player.containerMenu;
      this.book = var2.getRecipeBook();
      List var3 = var1.getRecipes(this.book.isFiltering(this.menu));
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         RecipeHolder var5 = (RecipeHolder)var4.next();
         if (this.book.willHighlight(var5)) {
            var2.recipesShown(var3);
            this.animationTime = 15.0F;
            break;
         }
      }

   }

   public RecipeCollection getCollection() {
      return this.collection;
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (!Screen.hasControlDown()) {
         this.time += var4;
      }

      ResourceLocation var5;
      if (this.collection.hasCraftable()) {
         if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            var5 = SLOT_MANY_CRAFTABLE_SPRITE;
         } else {
            var5 = SLOT_CRAFTABLE_SPRITE;
         }
      } else if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
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

      var1.blitSprite(var5, this.getX(), this.getY(), this.width, this.height);
      List var10 = this.getOrderedRecipes();
      this.currentIndex = Mth.floor(this.time / 30.0F) % var10.size();
      ItemStack var8 = ((RecipeHolder)var10.get(this.currentIndex)).value().getResultItem(this.collection.registryAccess());
      int var9 = 4;
      if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
         var1.renderItem(var8, this.getX() + var9 + 1, this.getY() + var9 + 1, 0, 10);
         --var9;
      }

      var1.renderFakeItem(var8, this.getX() + var9, this.getY() + var9);
      if (var6) {
         var1.pose().popPose();
      }

   }

   private List<RecipeHolder<?>> getOrderedRecipes() {
      List var1 = this.collection.getDisplayRecipes(true);
      if (!this.book.isFiltering(this.menu)) {
         var1.addAll(this.collection.getDisplayRecipes(false));
      }

      return var1;
   }

   public boolean isOnlyOption() {
      return this.getOrderedRecipes().size() == 1;
   }

   public RecipeHolder<?> getRecipe() {
      List var1 = this.getOrderedRecipes();
      return (RecipeHolder)var1.get(this.currentIndex);
   }

   public List<Component> getTooltipText() {
      ItemStack var1 = ((RecipeHolder)this.getOrderedRecipes().get(this.currentIndex)).value().getResultItem(this.collection.registryAccess());
      ArrayList var2 = Lists.newArrayList(Screen.getTooltipFromItem(Minecraft.getInstance(), var1));
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         var2.add(MORE_RECIPES_TOOLTIP);
      }

      return var2;
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      ItemStack var2 = ((RecipeHolder)this.getOrderedRecipes().get(this.currentIndex)).value().getResultItem(this.collection.registryAccess());
      var1.add(NarratedElementType.TITLE, (Component)Component.translatable("narration.recipe", var2.getHoverName()));
      if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
         var1.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"), Component.translatable("narration.recipe.usage.more"));
      } else {
         var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
      }

   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int var1) {
      return var1 == 0 || var1 == 1;
   }
}
