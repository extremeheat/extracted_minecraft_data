package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.GroupNarration;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jspecify.annotations.Nullable;

public class RecipeBookPage extends AbstractContainerWidget {
   public static final int ITEMS_PER_PAGE = 20;
   private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/page_forward"), Identifier.withDefaultNamespace("recipe_book/page_forward_highlighted"));
   private static final WidgetSprites PAGE_BACKWARD_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/page_backward"), Identifier.withDefaultNamespace("recipe_book/page_backward_highlighted"));
   private static final Component NEXT_PAGE_TEXT = Component.translatable("gui.recipebook.next_page");
   private static final Component PREVIOUS_PAGE_TEXT = Component.translatable("gui.recipebook.previous_page");
   private static final int TURN_PAGE_SPRITE_WIDTH = 12;
   private static final int TURN_PAGE_SPRITE_HEIGHT = 17;
   private static final int RECIPE_COLUMNS = 5;
   private static final int RECIPE_ROWS = 4;
   private static final int PAGE_CONTROLS_HEIGHT = 26;
   private static final int LEFT_PADDING = 11;
   private static final int TOP_PADDING = 31;
   private final List<RecipeButton> buttons = new ArrayList(20);
   private Minecraft minecraft;
   private final RecipeBookComponent<?> parent;
   private List<RecipeCollection> recipeCollections = ImmutableList.of();
   private @Nullable ImageButton forwardButton;
   private @Nullable ImageButton backButton;
   private int totalPages;
   private int currentPage;
   private ClientRecipeBook recipeBook;
   private boolean isFiltering;
   private final List<AbstractWidget> children = new ArrayList();
   private @Nullable AbstractWidget recipeGridWidget;

   public RecipeBookPage(final RecipeBookComponent<?> parent, final SlotSelectTime slotSelectTime, final RecipeBookComponent.RecipePlacer recipePlacer, final Predicate<RecipeButton> tryOpenOverlay) {
      super(0, 0, 125, 126, CommonComponents.EMPTY);
      this.parent = parent;

      for(int i = 0; i < 20; ++i) {
         this.buttons.add(new RecipeButton(slotSelectTime, recipePlacer, tryOpenOverlay));
      }

   }

   public void init(final Minecraft minecraft) {
      this.children.clear();
      this.minecraft = minecraft;
      this.recipeBook = minecraft.player.getRecipeBook();
      this.setPosition(this.parent.getX() + 11, this.parent.getY() + 31);
      GridLayout grid = new GridLayout();
      GridLayout.RowHelper rowHelper = grid.createRowHelper(5);
      List var10000 = this.buttons;
      Objects.requireNonNull(rowHelper);
      var10000.forEach(rowHelper::addChild);
      ScrollableLayout recipeGrid = new ScrollableLayout(minecraft, grid, 100, AbstractScrollArea.ScrollbarSettings.NO_SCROLL, 0, ScrollableLayout.ReserveStrategy.RIGHT, GroupNarration.grid(5, "narration.group_navigation.usage.recipe"));
      recipeGrid.setPosition(this.getX(), this.getY());
      recipeGrid.arrangeElements();
      recipeGrid.allowTabNavigationInside(false);
      recipeGrid.visitWidgets((widget) -> {
         this.recipeGridWidget = widget;
         this.children.add(widget);
      });
      this.backButton = new TurnPageButton(this.getX() + 27, this.getY() + 106, PAGE_BACKWARD_SPRITES, (button) -> {
         --this.currentPage;
         this.updateButtonsForPage();
      }, PREVIOUS_PAGE_TEXT);
      this.backButton.setTooltip(Tooltip.create(PREVIOUS_PAGE_TEXT, (Component)null));
      this.children.add(this.backButton);
      this.forwardButton = new TurnPageButton(this.getX() + 82, this.getY() + 106, PAGE_FORWARD_SPRITES, (button) -> {
         ++this.currentPage;
         this.updateButtonsForPage();
      }, NEXT_PAGE_TEXT);
      this.forwardButton.setTooltip(Tooltip.create(NEXT_PAGE_TEXT, (Component)null));
      this.children.add(this.forwardButton);
   }

   public void updateCollections(final List<RecipeCollection> recipeCollections, final boolean resetPage, final boolean isFiltering) {
      this.recipeCollections = recipeCollections;
      this.isFiltering = isFiltering;
      this.totalPages = (int)Math.ceil((double)recipeCollections.size() / 20.0);
      if (this.totalPages <= this.currentPage || resetPage) {
         this.currentPage = 0;
      }

      this.updateButtonsForPage();
   }

   private void updateButtonsForPage() {
      int startOffset = 20 * this.currentPage;
      ContextMap context = SlotDisplayContext.fromLevel(this.minecraft.level);

      for(int i = 0; i < this.buttons.size(); ++i) {
         RecipeButton button = (RecipeButton)this.buttons.get(i);
         if (startOffset + i < this.recipeCollections.size()) {
            RecipeCollection recipeCollection = (RecipeCollection)this.recipeCollections.get(startOffset + i);
            button.update(recipeCollection, this.isFiltering, this, context);
            button.setVisible(true);
         } else {
            button.setVisible(false);
         }
      }

      this.updateArrowButtons();
   }

   private void updateArrowButtons() {
      if (this.forwardButton != null && this.backButton != null) {
         boolean forwardVisible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
         boolean backVisible = this.totalPages > 1 && this.currentPage > 0;
         this.forwardButton.setVisible(forwardVisible);
         this.backButton.setVisible(backVisible);
         if (this.minecraft.getLastInputType().isKeyboard()) {
            if (this.forwardButton.isFocused() && !forwardVisible) {
               this.setFocused(this.backButton);
            } else if (this.backButton.isFocused() && !backVisible) {
               this.setFocused(this.forwardButton);
            }
         }

      }
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics) {
      if (this.totalPages > 1) {
         Component pageNumbers = Component.translatable("gui.recipebook.page", this.currentPage + 1, this.totalPages);
         int pWidth = this.minecraft.font.width((FormattedText)pageNumbers);
         graphics.text(this.minecraft.font, (Component)pageNumbers, this.getX() + (this.getWidth() - pWidth) / 2, this.getY() + 110, -1);
      }

      for(AbstractWidget child : this.children) {
         child.extractRenderState(graphics);
      }

   }

   protected int contentHeight() {
      return this.getHeight();
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
   }

   public Collection<? extends NarratableEntry> getNarratables() {
      return this.children;
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   public void setRecipeGridActive(final boolean active) {
      if (this.recipeGridWidget != null) {
         this.recipeGridWidget.active = active;
      }

   }

   public void focusRecipeButton(final RecipeButton button) {
      AbstractWidget var3 = this.recipeGridWidget;
      if (var3 instanceof ContainerEventHandler grid) {
         ComponentPath.path((GuiEventListener)button, (ContainerEventHandler[])(grid, this)).applyFocus(true);
      }

   }

   public void recipeShown(final RecipeDisplayId recipe) {
      this.parent.recipeShown(recipe);
   }

   public ClientRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   private class TurnPageButton extends ImageButton {
      private TurnPageButton(final int x, final int y, final WidgetSprites sprites, final Button.OnPress onPress, final Component message) {
         Objects.requireNonNull(RecipeBookPage.this);
         super(x, y, 12, 17, sprites, onPress, message);
      }

      protected MutableComponent createNarrationMessage() {
         return CommonComponents.joinForNarration(super.createNarrationMessage(), Component.translatable("narration.recipe_book.current_page", RecipeBookPage.this.currentPage + 1, RecipeBookPage.this.totalPages));
      }
   }
}
