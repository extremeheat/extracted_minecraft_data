package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookPage {
   public static final int ITEMS_PER_PAGE = 20;
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
   private Recipe<?> lastClickedRecipe;
   @Nullable
   private RecipeCollection lastClickedRecipeCollection;

   public RecipeBookPage() {
      super();

      for(int var1 = 0; var1 < 20; ++var1) {
         this.buttons.add(new RecipeButton());
      }

   }

   public void init(Minecraft var1, int var2, int var3) {
      this.minecraft = var1;
      this.recipeBook = var1.player.getRecipeBook();

      for(int var4 = 0; var4 < this.buttons.size(); ++var4) {
         ((RecipeButton)this.buttons.get(var4)).setPosition(var2 + 11 + 25 * (var4 % 5), var3 + 31 + 25 * (var4 / 5));
      }

      this.forwardButton = new StateSwitchingButton(var2 + 93, var3 + 137, 12, 17, false);
      this.forwardButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
      this.backButton = new StateSwitchingButton(var2 + 38, var3 + 137, 12, 17, true);
      this.backButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
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

      for(int var2 = 0; var2 < this.buttons.size(); ++var2) {
         RecipeButton var3 = (RecipeButton)this.buttons.get(var2);
         if (var1 + var2 < this.recipeCollections.size()) {
            RecipeCollection var4 = (RecipeCollection)this.recipeCollections.get(var1 + var2);
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

   public void render(PoseStack var1, int var2, int var3, int var4, int var5, float var6) {
      if (this.totalPages > 1) {
         int var10000 = this.currentPage + 1;
         String var7 = "" + var10000 + "/" + this.totalPages;
         int var8 = this.minecraft.font.width(var7);
         this.minecraft.font.draw(var1, (String)var7, (float)(var2 - var8 / 2 + 73), (float)(var3 + 141), -1);
      }

      this.hoveredButton = null;
      Iterator var9 = this.buttons.iterator();

      while(var9.hasNext()) {
         RecipeButton var10 = (RecipeButton)var9.next();
         var10.render(var1, var4, var5, var6);
         if (var10.visible && var10.isHoveredOrFocused()) {
            this.hoveredButton = var10;
         }
      }

      this.backButton.render(var1, var4, var5, var6);
      this.forwardButton.render(var1, var4, var5, var6);
      this.overlay.render(var1, var4, var5, var6);
   }

   public void renderTooltip(PoseStack var1, int var2, int var3) {
      if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
         this.minecraft.screen.renderComponentTooltip(var1, this.hoveredButton.getTooltipText(this.minecraft.screen), var2, var3);
      }

   }

   @Nullable
   public Recipe<?> getLastClickedRecipe() {
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
         ++this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else if (this.backButton.mouseClicked(var1, var3, var5)) {
         --this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else {
         Iterator var10 = this.buttons.iterator();

         RecipeButton var11;
         do {
            if (!var10.hasNext()) {
               return false;
            }

            var11 = (RecipeButton)var10.next();
         } while(!var11.mouseClicked(var1, var3, var5));

         if (var5 == 0) {
            this.lastClickedRecipe = var11.getRecipe();
            this.lastClickedRecipeCollection = var11.getCollection();
         } else if (var5 == 1 && !this.overlay.isVisible() && !var11.isOnlyOption()) {
            this.overlay.init(this.minecraft, var11.getCollection(), var11.x, var11.y, var6 + var8 / 2, var7 + 13 + var9 / 2, (float)var11.getWidth());
         }

         return true;
      }
   }

   public void recipesShown(List<Recipe<?>> var1) {
      Iterator var2 = this.showListeners.iterator();

      while(var2.hasNext()) {
         RecipeShownListener var3 = (RecipeShownListener)var2.next();
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
