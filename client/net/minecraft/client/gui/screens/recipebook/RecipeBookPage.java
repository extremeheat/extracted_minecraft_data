package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeBookPage {
   public static final int ITEMS_PER_PAGE = 20;
   private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("recipe_book/page_forward"), ResourceLocation.withDefaultNamespace("recipe_book/page_forward_highlighted")
   );
   private static final WidgetSprites PAGE_BACKWARD_SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("recipe_book/page_backward"), ResourceLocation.withDefaultNamespace("recipe_book/page_backward_highlighted")
   );
   private final List<RecipeButton> buttons = Lists.newArrayListWithCapacity(20);
   @Nullable
   private RecipeButton hoveredButton;
   private final OverlayRecipeComponent overlay = new OverlayRecipeComponent();
   private Minecraft minecraft;
   private final List<RecipeShownListener> showListeners = Lists.newArrayList();
   private List<RecipeCollection> recipeCollections = ImmutableList.of();
   private StateSwitchingButton forwardButton;
   private StateSwitchingButton backButton;
   private int totalPages;
   private int currentPage;
   private RecipeBook recipeBook;
   @Nullable
   private RecipeHolder<?> lastClickedRecipe;
   @Nullable
   private RecipeCollection lastClickedRecipeCollection;

   public RecipeBookPage() {
      super();

      for (int var1 = 0; var1 < 20; var1++) {
         this.buttons.add(new RecipeButton());
      }
   }

   public void init(Minecraft var1, int var2, int var3) {
      this.minecraft = var1;
      this.recipeBook = var1.player.getRecipeBook();

      for (int var4 = 0; var4 < this.buttons.size(); var4++) {
         this.buttons.get(var4).setPosition(var2 + 11 + 25 * (var4 % 5), var3 + 31 + 25 * (var4 / 5));
      }

      this.forwardButton = new StateSwitchingButton(var2 + 93, var3 + 137, 12, 17, false);
      this.forwardButton.initTextureValues(PAGE_FORWARD_SPRITES);
      this.backButton = new StateSwitchingButton(var2 + 38, var3 + 137, 12, 17, true);
      this.backButton.initTextureValues(PAGE_BACKWARD_SPRITES);
   }

   public void addListener(RecipeBookComponent var1) {
      this.showListeners.remove(var1);
      this.showListeners.add(var1);
   }

   public void updateCollections(List<RecipeCollection> var1, boolean var2) {
      this.recipeCollections = var1;
      this.totalPages = (int)Math.ceil((double)var1.size() / 20.0);
      if (this.totalPages <= this.currentPage || var2) {
         this.currentPage = 0;
      }

      this.updateButtonsForPage();
   }

   private void updateButtonsForPage() {
      int var1 = 20 * this.currentPage;

      for (int var2 = 0; var2 < this.buttons.size(); var2++) {
         RecipeButton var3 = this.buttons.get(var2);
         if (var1 + var2 < this.recipeCollections.size()) {
            RecipeCollection var4 = this.recipeCollections.get(var1 + var2);
            var3.init(var4, this);
            var3.visible = true;
         } else {
            var3.visible = false;
         }
      }

      this.updateArrowButtons();
   }

   private void updateArrowButtons() {
      this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
      this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
   }

   public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, float var6) {
      if (this.totalPages > 1) {
         MutableComponent var7 = Component.translatable("gui.recipebook.page", this.currentPage + 1, this.totalPages);
         int var8 = this.minecraft.font.width(var7);
         var1.drawString(this.minecraft.font, var7, var2 - var8 / 2 + 73, var3 + 141, -1, false);
      }

      this.hoveredButton = null;

      for (RecipeButton var10 : this.buttons) {
         var10.render(var1, var4, var5, var6);
         if (var10.visible && var10.isHoveredOrFocused()) {
            this.hoveredButton = var10;
         }
      }

      this.backButton.render(var1, var4, var5, var6);
      this.forwardButton.render(var1, var4, var5, var6);
      this.overlay.render(var1, var4, var5, var6);
   }

   public void renderTooltip(GuiGraphics var1, int var2, int var3) {
      if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
         var1.renderComponentTooltip(this.minecraft.font, this.hoveredButton.getTooltipText(), var2, var3);
      }
   }

   @Nullable
   public RecipeHolder<?> getLastClickedRecipe() {
      return this.lastClickedRecipe;
   }

   @Nullable
   public RecipeCollection getLastClickedRecipeCollection() {
      return this.lastClickedRecipeCollection;
   }

   public void setInvisible() {
      this.overlay.setVisible(false);
   }

   public boolean mouseClicked(double var1, double var3, int var5, int var6, int var7, int var8, int var9) {
      this.lastClickedRecipe = null;
      this.lastClickedRecipeCollection = null;
      if (this.overlay.isVisible()) {
         if (this.overlay.mouseClicked(var1, var3, var5)) {
            this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
            this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
         } else {
            this.overlay.setVisible(false);
         }

         return true;
      } else if (this.forwardButton.mouseClicked(var1, var3, var5)) {
         this.currentPage++;
         this.updateButtonsForPage();
         return true;
      } else if (this.backButton.mouseClicked(var1, var3, var5)) {
         this.currentPage--;
         this.updateButtonsForPage();
         return true;
      } else {
         for (RecipeButton var11 : this.buttons) {
            if (var11.mouseClicked(var1, var3, var5)) {
               if (var5 == 0) {
                  this.lastClickedRecipe = var11.getRecipe();
                  this.lastClickedRecipeCollection = var11.getCollection();
               } else if (var5 == 1 && !this.overlay.isVisible() && !var11.isOnlyOption()) {
                  this.overlay
                     .init(this.minecraft, var11.getCollection(), var11.getX(), var11.getY(), var6 + var8 / 2, var7 + 13 + var9 / 2, (float)var11.getWidth());
               }

               return true;
            }
         }

         return false;
      }
   }

   public void recipesShown(List<RecipeHolder<?>> var1) {
      for (RecipeShownListener var3 : this.showListeners) {
         var3.recipesShown(var1);
      }
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public RecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   protected void listButtons(Consumer<AbstractWidget> var1) {
      var1.accept(this.forwardButton);
      var1.accept(this.backButton);
      this.buttons.forEach(var1);
   }
}
