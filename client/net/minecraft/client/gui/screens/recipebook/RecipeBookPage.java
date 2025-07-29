package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class RecipeBookPage {
   public static final int ITEMS_PER_PAGE = 20;
   private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/page_forward"), ResourceLocation.withDefaultNamespace("recipe_book/page_forward_highlighted"));
   private static final WidgetSprites PAGE_BACKWARD_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/page_backward"), ResourceLocation.withDefaultNamespace("recipe_book/page_backward_highlighted"));
   private final List<RecipeButton> buttons = Lists.newArrayListWithCapacity(20);
   @Nullable
   private RecipeButton hoveredButton;
   private final OverlayRecipeComponent overlay;
   private Minecraft minecraft;
   private final RecipeBookComponent<?> parent;
   private List<RecipeCollection> recipeCollections = ImmutableList.of();
   private StateSwitchingButton forwardButton;
   private StateSwitchingButton backButton;
   private int totalPages;
   private int currentPage;
   private ClientRecipeBook recipeBook;
   @Nullable
   private RecipeDisplayId lastClickedRecipe;
   @Nullable
   private RecipeCollection lastClickedRecipeCollection;
   private boolean isFiltering;

   public RecipeBookPage(RecipeBookComponent<?> var1, SlotSelectTime var2, boolean var3) {
      super();
      this.parent = var1;
      this.overlay = new OverlayRecipeComponent(var2, var3);

      for(int var4 = 0; var4 < 20; ++var4) {
         this.buttons.add(new RecipeButton(var2));
      }

   }

   public void init(Minecraft var1, int var2, int var3) {
      this.minecraft = var1;
      this.recipeBook = var1.player.getRecipeBook();

      for(int var4 = 0; var4 < this.buttons.size(); ++var4) {
         ((RecipeButton)this.buttons.get(var4)).setPosition(var2 + 11 + 25 * (var4 % 5), var3 + 31 + 25 * (var4 / 5));
      }

      this.forwardButton = new StateSwitchingButton(var2 + 93, var3 + 137, 12, 17, false);
      this.forwardButton.initTextureValues(PAGE_FORWARD_SPRITES);
      this.backButton = new StateSwitchingButton(var2 + 38, var3 + 137, 12, 17, true);
      this.backButton.initTextureValues(PAGE_BACKWARD_SPRITES);
   }

   public void updateCollections(List<RecipeCollection> var1, boolean var2, boolean var3) {
      this.recipeCollections = var1;
      this.isFiltering = var3;
      this.totalPages = (int)Math.ceil((double)var1.size() / 20.0);
      if (this.totalPages <= this.currentPage || var2) {
         this.currentPage = 0;
      }

      this.updateButtonsForPage();
   }

   private void updateButtonsForPage() {
      int var1 = 20 * this.currentPage;
      ContextMap var2 = SlotDisplayContext.fromLevel(this.minecraft.level);

      for(int var3 = 0; var3 < this.buttons.size(); ++var3) {
         RecipeButton var4 = (RecipeButton)this.buttons.get(var3);
         if (var1 + var3 < this.recipeCollections.size()) {
            RecipeCollection var5 = (RecipeCollection)this.recipeCollections.get(var1 + var3);
            var4.init(var5, this.isFiltering, this, var2);
            var4.visible = true;
         } else {
            var4.visible = false;
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
         int var8 = this.minecraft.font.width((FormattedText)var7);
         var1.drawString(this.minecraft.font, (Component)var7, var2 - var8 / 2 + 73, var3 + 141, -1);
      }

      this.hoveredButton = null;

      for(RecipeButton var10 : this.buttons) {
         var10.render(var1, var4, var5, var6);
         if (var10.visible && var10.isHoveredOrFocused()) {
            this.hoveredButton = var10;
         }
      }

      this.backButton.render(var1, var4, var5, var6);
      this.forwardButton.render(var1, var4, var5, var6);
      var1.nextStratum();
      this.overlay.render(var1, var4, var5, var6);
   }

   public void renderTooltip(GuiGraphics var1, int var2, int var3) {
      if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
         ItemStack var4 = this.hoveredButton.getDisplayStack();
         ResourceLocation var5 = (ResourceLocation)var4.get(DataComponents.TOOLTIP_STYLE);
         var1.setComponentTooltipForNextFrame(this.minecraft.font, this.hoveredButton.getTooltipText(var4), var2, var3, var5);
      }

   }

   @Nullable
   public RecipeDisplayId getLastClickedRecipe() {
      return this.lastClickedRecipe;
   }

   @Nullable
   public RecipeCollection getLastClickedRecipeCollection() {
      return this.lastClickedRecipeCollection;
   }

   public void setInvisible() {
      this.overlay.setVisible(false);
   }

   public boolean mouseClicked(double var1, double var3, int var5, int var6, int var7, int var8, int var9, boolean var10) {
      this.lastClickedRecipe = null;
      this.lastClickedRecipeCollection = null;
      if (this.overlay.isVisible()) {
         if (this.overlay.mouseClicked(var1, var3, var5, var10)) {
            this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
            this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
         } else {
            this.overlay.setVisible(false);
         }

         return true;
      } else if (this.forwardButton.mouseClicked(var1, var3, var5, var10)) {
         ++this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else if (this.backButton.mouseClicked(var1, var3, var5, var10)) {
         --this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else {
         ContextMap var11 = SlotDisplayContext.fromLevel(this.minecraft.level);

         for(RecipeButton var13 : this.buttons) {
            if (var13.mouseClicked(var1, var3, var5, var10)) {
               if (var5 == 0) {
                  this.lastClickedRecipe = var13.getCurrentRecipe();
                  this.lastClickedRecipeCollection = var13.getCollection();
               } else if (var5 == 1 && !this.overlay.isVisible() && !var13.isOnlyOption()) {
                  this.overlay.init(var13.getCollection(), var11, this.isFiltering, var13.getX(), var13.getY(), var6 + var8 / 2, var7 + 13 + var9 / 2, (float)var13.getWidth());
               }

               return true;
            }
         }

         return false;
      }
   }

   public void recipeShown(RecipeDisplayId var1) {
      this.parent.recipeShown(var1);
   }

   public ClientRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   protected void listButtons(Consumer<AbstractWidget> var1) {
      var1.accept(this.forwardButton);
      var1.accept(this.backButton);
      this.buttons.forEach(var1);
   }
}
