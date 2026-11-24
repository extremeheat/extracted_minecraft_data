package net.minecraft.client.gui.screens.recipebook;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ItemTabButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Updatable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public abstract class RecipeBookComponent<T extends RecipeBookMenu> extends AbstractContainerWidget implements Updatable {
   public static final WidgetSprites RECIPE_BUTTON_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/button"), Identifier.withDefaultNamespace("recipe_book/button_highlighted"));
   protected static final Identifier RECIPE_BOOK_LOCATION = Identifier.withDefaultNamespace("textures/gui/recipe_book.png");
   private static final int BACKGROUND_TEXTURE_WIDTH = 256;
   private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final Component SEARCH_BOX_NAME = Component.translatable("gui.recipebook.search_box");
   private static final Component SEARCH_HINT;
   public static final int IMAGE_WIDTH = 147;
   public static final int IMAGE_HEIGHT = 166;
   private static final int OFFSET_X_POSITION = 86;
   private static final int MIN_WIDTH = 379;
   private static final int TAB_WIDTH = 35;
   private static final int TAB_HEIGHT = 27;
   private static final int TAB_ICON_SPACING = 11;
   private static final int TAB_TWO_ICONS_X_OFFSET = -1;
   private static final int TAB_X_OFFSET_WHEN_SELECTED = -2;
   private static final WidgetSprites TAB_SPRITES;
   private static final int BORDER_WIDTH = 8;
   private static final Component ALL_RECIPES_TOOLTIP;
   private static final Component FILTER_BUTTON_NAME;
   private static final int TICKS_TO_SWAP_SLOT = 30;
   private int xOffset;
   private float time;
   private @Nullable RecipeDisplayId lastPlacedRecipe;
   private final GhostSlots ghostSlots;
   protected CycleButton<Boolean> filterButton;
   protected final T menu;
   protected Minecraft minecraft;
   private @Nullable EditBox searchBox;
   private @Nullable ScreenRectangle magnifierIconPlacement;
   private String lastSearch = "";
   private final List<TabInfo> tabInfos;
   private ClientRecipeBook book;
   private final RecipeBookPage recipeBookPage;
   private @Nullable RecipeDisplayId lastRecipe;
   private @Nullable RecipeCollection lastRecipeCollection;
   private final StackedItemContents stackedContents = new StackedItemContents();
   private int timesInventoryChanged;
   private boolean ignoreTextInput;
   private boolean widthTooNarrow;
   private int screenWidth;
   private int screenHeight;
   private final List<AbstractWidget> children = new ArrayList();
   private final OverlayRecipeComponent overlay;
   private final TabManager tabManager = new TabManager((var0) -> {
   }, (var0) -> {
   }, (var1) -> this.updateCollections(true, this.isFiltering()), (var0) -> {
   });
   private TabNavigationBar tabBar;

   public RecipeBookComponent(final T menu, final List<TabInfo> tabInfos) {
      super(0, 0, 147, 166, CommonComponents.EMPTY);
      this.menu = menu;
      this.tabInfos = tabInfos;
      SlotSelectTime slotSelectTime = () -> Mth.floor(this.time / 30.0F);
      this.ghostSlots = new GhostSlots(slotSelectTime);
      this.overlay = new OverlayRecipeComponent(slotSelectTime, menu instanceof AbstractFurnaceMenu, this::tryPlaceRecipe);
      this.recipeBookPage = new RecipeBookPage(this, slotSelectTime, this::tryPlaceRecipe, this::tryOpenOverlay);
   }

   public void init(final int screenWidth, final int screenHeight, final Minecraft minecraft) {
      this.minecraft = minecraft;
      this.screenWidth = screenWidth;
      this.screenHeight = screenHeight;
      this.widthTooNarrow = screenWidth < 379;
      this.book = minecraft.player.getRecipeBook();
      this.timesInventoryChanged = minecraft.player.getInventory().getTimesChanged();
      this.visible = this.isVisibleAccordingToBookData();
      if (this.isVisible()) {
         this.initVisuals();
      }

   }

   private void initVisuals() {
      boolean isFiltering = this.isFiltering();
      this.xOffset = this.widthTooNarrow ? 0 : 86;
      int recipeBookX0 = (this.screenWidth - 147) / 2;
      this.setPosition(recipeBookX0 - this.xOffset, (this.screenHeight - 166) / 2);
      this.children.clear();
      this.stackedContents.clear();
      this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      String oldEdit = this.searchBox != null ? this.searchBox.getValue() : "";
      Font var10003 = this.minecraft.font;
      int var10004 = this.getX() + 25;
      int var10005 = this.getY() + 13;
      Objects.requireNonNull(this.minecraft.font);
      this.searchBox = new EditBox(var10003, var10004, var10005, 81, 9 + 5, SEARCH_BOX_NAME);
      this.searchBox.setMaxLength(50);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(-1);
      this.searchBox.setValue(oldEdit);
      this.searchBox.setResponder(this::checkSearchStringUpdate);
      this.searchBox.setHint(SEARCH_HINT);
      this.children.add(this.searchBox);
      this.magnifierIconPlacement = ScreenRectangle.of(ScreenAxis.HORIZONTAL, this.getX() + 8, this.searchBox.getY(), this.searchBox.getX() - this.getX() - 8, this.searchBox.getHeight());
      this.filterButton = ((CycleButton.Builder)CycleButton.booleanBuilder(this.getRecipeFilterName(), ALL_RECIPES_TOOLTIP, isFiltering).withTooltip((filtering) -> filtering ? Tooltip.create(this.getRecipeFilterName(), (Component)null) : Tooltip.create(ALL_RECIPES_TOOLTIP, (Component)null))).withSprite((cycleButton, filtering) -> this.getFilterButtonTextures().get(filtering, cycleButton.isHighlighted())).displayState(CycleButton.DisplayState.HIDE).create(this.getX() + 110, this.getY() + 12, 26, 16, FILTER_BUTTON_NAME, (button, value) -> {
         this.toggleFiltering();
         this.sendUpdateSettings();
         this.updateCollections(false, value);
      });
      this.children.add(this.filterButton);
      this.recipeBookPage.init(this.minecraft);
      this.recipeBookPage.setRecipeGridActive(!this.overlay.isVisible());
      this.children.add(this.recipeBookPage);
      this.selectMatchingRecipes();
      this.updateTabs(isFiltering);
      this.updateCollections(false, isFiltering);
   }

   protected int contentHeight() {
      return this.height;
   }

   public List<? extends GuiEventListener> children() {
      return this.getActiveChildren();
   }

   private List<AbstractWidget> getActiveChildren() {
      if (!this.overlay.isVisible()) {
         return this.children;
      } else {
         List<AbstractWidget> activeChildren = new ArrayList(this.children);
         activeChildren.add(activeChildren.indexOf(this.recipeBookPage), this.overlay);
         return activeChildren;
      }
   }

   public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
      boolean leavingOverlay = navigationEvent instanceof FocusNavigationEvent.TabNavigation && this.overlay.isVisible() && this.getFocused() == this.overlay;
      ComponentPath focusPath = super.nextFocusPath(navigationEvent);
      if (leavingOverlay) {
         this.hideOverlay();
      }

      return focusPath;
   }

   protected abstract WidgetSprites getFilterButtonTextures();

   public boolean isWidthTooNarrow() {
      return this.widthTooNarrow;
   }

   public boolean isActive() {
      return this.isVisible();
   }

   public void toggleVisibility() {
      this.setVisible(!this.isVisible());
   }

   private boolean isVisibleAccordingToBookData() {
      return this.book.isOpen(this.menu.getRecipeBookType());
   }

   public void setVisible(final boolean visible) {
      super.setVisible(visible);
      if (visible) {
         this.initVisuals();
      }

      this.book.setOpen(this.menu.getRecipeBookType(), visible);
      if (!visible) {
         this.closeOverlay();
      }

      this.sendUpdateSettings();
   }

   public boolean closeOverlay() {
      if (!this.overlay.isVisible()) {
         return false;
      } else {
         this.hideOverlay();
         RecipeButton opener = this.overlay.getOpener();
         if (opener != null) {
            this.setFocused(this.recipeBookPage);
            this.recipeBookPage.focusRecipeButton(opener);
         }

         return true;
      }
   }

   private void hideOverlay() {
      this.overlay.setVisible(false);
      this.recipeBookPage.setRecipeGridActive(true);
   }

   private void focusOverlay() {
      this.setFocused(this.overlay);
      this.overlay.focusFirstButton();
   }

   protected abstract boolean isCraftingSlot(Slot slot);

   public void slotClicked(final @Nullable SlotWidget slotWidget) {
      if (slotWidget != null && this.isCraftingSlot(slotWidget.slot())) {
         this.lastPlacedRecipe = null;
         this.ghostSlots.clear();
         if (this.isVisible()) {
            this.updateStackedContents();
         }
      }

   }

   private void selectMatchingRecipes() {
      for(TabInfo tabInfo : this.tabInfos) {
         for(RecipeCollection recipeCollection : this.book.getCollection(tabInfo.category())) {
            this.selectMatchingRecipes(recipeCollection, this.stackedContents);
         }
      }

   }

   protected abstract void selectMatchingRecipes(RecipeCollection collection, StackedItemContents stackedContents);

   private void updateCollections(final boolean resetPage, final boolean isFiltering) {
      Tab var4 = this.tabManager.getCurrentTab();
      if (var4 instanceof TabInfo tabInfo) {
         List var9 = this.book.getCollection(tabInfo.category());
         List<RecipeCollection> collection = new ArrayList(var9);
         collection.removeIf((c) -> !c.hasAnySelected());
         String searchTarget = this.searchBox.getValue();
         if (!searchTarget.isEmpty()) {
            ClientPacketListener connection = this.minecraft.getConnection();
            if (connection != null) {
               ObjectSet<RecipeCollection> set = new ObjectLinkedOpenHashSet(connection.searchTrees().recipes().search(searchTarget.toLowerCase(Locale.ROOT)));
               collection.removeIf((e) -> !set.contains(e));
            }
         }

         if (isFiltering) {
            collection.removeIf((c) -> !c.hasCraftable());
         }

         this.recipeBookPage.updateCollections(collection, resetPage, isFiltering);
      }
   }

   private void updateTabs(final boolean isFiltering) {
      Tab selectedTab = this.tabManager.getCurrentTab();
      boolean tabBarFocused = this.tabBar != null && this.getFocused() == this.tabBar;
      this.children.remove(this.tabBar);
      TabNavigationBar.Builder tabBarBuilder = TabNavigationBar.builder(this.tabManager, this.getX() - 30, this.getY() + 3, 35, 27 * this.tabInfos.size(), LinearLayout.Orientation.VERTICAL);

      for(TabInfo tabInfo : this.tabInfos) {
         if (tabInfo.category() instanceof SearchRecipeBookCategory || this.hasSelectedRecipes(tabInfo.category())) {
            ItemTabButton.Builder buttonBuilder = ItemTabButton.builder(this.tabManager, tabInfo, TAB_SPRITES).size(35, 27).message(tabInfo.name()).xOffsetWhenSelected(-2);
            tabInfo.secondaryIcon().ifPresentOrElse((secondaryIcon) -> buttonBuilder.icons(tabInfo.primaryIcon(), secondaryIcon, 11).iconXOffset(-1), () -> buttonBuilder.icon(tabInfo.primaryIcon()));
            tabBarBuilder.addTab(buttonBuilder.build(), tabInfo);
         }
      }

      this.tabBar = tabBarBuilder.build();
      int selectedIndex = Math.max(this.tabBar.getTabs().indexOf(selectedTab), 0);
      this.tabBar.selectTab(selectedIndex, false);
      this.children.add(0, this.tabBar);
      if (tabBarFocused) {
         this.setFocused(this.tabBar);
         this.tabBar.setFocused((GuiEventListener)this.tabBar.children().get(selectedIndex));
      }

      for(GuiEventListener child : this.tabBar.children()) {
         if (child instanceof ItemTabButton tabButton) {
            Tab var10 = tabButton.tab();
            if (var10 instanceof TabInfo tabInfo) {
               if (!(tabInfo.category() instanceof SearchRecipeBookCategory) && this.hasNewRecipes(tabInfo.category(), isFiltering)) {
                  tabButton.startAnimation();
               }
            }
         }
      }

   }

   private boolean hasNewRecipes(final ExtendedRecipeBookCategory category, final boolean isFiltering) {
      RecipeCollection.CraftableStatus recipesToShow = isFiltering ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY;

      for(RecipeCollection collection : this.book.getCollection(category)) {
         for(RecipeDisplayEntry recipe : collection.getSelectedRecipes(recipesToShow)) {
            if (this.book.willHighlight(recipe.id())) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean hasSelectedRecipes(final ExtendedRecipeBookCategory category) {
      for(RecipeCollection collection : this.book.getCollection(category)) {
         if (collection.hasAnySelected()) {
            return true;
         }
      }

      return false;
   }

   public void tick() {
      boolean shouldBeVisible = this.isVisibleAccordingToBookData();
      if (this.isVisible() != shouldBeVisible) {
         this.setVisible(shouldBeVisible);
      }

      if (this.isVisible()) {
         if (this.timesInventoryChanged != this.minecraft.player.getInventory().getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.getInventory().getTimesChanged();
         }

      }
   }

   private void updateStackedContents() {
      this.stackedContents.clear();
      this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      this.selectMatchingRecipes();
      this.updateCollections(false, this.isFiltering());
   }

   private boolean isFiltering() {
      return this.book.isFiltering(this.menu.getRecipeBookType());
   }

   public void update(final DeltaTracker deltaTracker) {
      if (this.isVisible()) {
         if (!this.minecraft.hasControlDown()) {
            this.time += deltaTracker.getGameTimeDeltaTicks();
         }

      }
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics) {
      graphics.blit(GuiPipeline.TEXTURED, RECIPE_BOOK_LOCATION, this.getX(), this.getY(), 1.0F, 1.0F, 147, 166, 256, 256);
      if (this.overlay.isVisible()) {
         graphics.extractWithoutMouse(() -> this.children.forEach((child) -> child.extractRenderState(graphics)));
      } else {
         this.children.forEach((child) -> child.extractRenderState(graphics));
      }

      graphics.nextStratum();
      this.overlay.extractRenderState(graphics);
   }

   public @Nullable Tooltip getGhostSlotTooltip(final Slot slot) {
      return this.ghostSlots.getTooltip(this.minecraft, slot);
   }

   protected abstract Component getRecipeFilterName();

   public void extractGhostRecipe(final GuiGraphicsExtractor graphics, final int xOffset, final int yOffset, final boolean isResultSlotBig) {
      this.ghostSlots.extractRenderState(graphics, xOffset, yOffset, this.minecraft, isResultSlotBig);
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.overlay.isVisible()) {
            if (!this.overlay.mouseClicked(event, doubleClick)) {
               this.closeOverlay();
            }

            return true;
         } else if (event.isLeftMouse() && this.searchBox != null && this.magnifierIconPlacement != null && this.magnifierIconPlacement.containsPoint(Mth.floor(event.x()), Mth.floor(event.y()))) {
            this.setFocused(this.searchBox);
            return true;
         } else {
            boolean clicked = super.mouseClicked(event, doubleClick);
            if (this.overlay.isVisible()) {
               this.focusOverlay();
            }

            return clicked;
         }
      } else {
         return false;
      }
   }

   public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
      return this.searchBox != null && this.searchBox.isFocused() ? this.searchBox.mouseDragged(event, dx, dy) : false;
   }

   private void tryPlaceRecipe(final RecipeCollection recipeCollection, final RecipeDisplayId recipe, final boolean useMaxItems) {
      if (recipeCollection.isCraftable(recipe) || !recipe.equals(this.lastPlacedRecipe)) {
         this.lastPlacedRecipe = recipe;
         this.lastRecipeCollection = recipeCollection;
         this.lastRecipe = recipe;
         this.ghostSlots.clear();
         this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, recipe, useMaxItems);
         if (!this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
         }

      }
   }

   public boolean repeatLastRecipe(final boolean useMaxItems) {
      if (this.lastRecipeCollection != null && this.lastRecipe != null) {
         AbstractWidget.playButtonClickSound(this.minecraft.getSoundManager());
         this.tryPlaceRecipe(this.lastRecipeCollection, this.lastRecipe, useMaxItems);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryOpenOverlay(final RecipeButton button) {
      if (!this.overlay.isVisible() && !button.isOnlyOption()) {
         this.overlay.init(button, SlotDisplayContext.fromLevel(this.minecraft.level), this.isFiltering(), this.recipeBookPage.getX() + this.recipeBookPage.getWidth() / 2, this.recipeBookPage.getY() + this.recipeBookPage.getHeight() / 2);
         this.recipeBookPage.setRecipeGridActive(false);
         this.focusOverlay();
         return true;
      } else {
         return false;
      }
   }

   private void toggleFiltering() {
      RecipeBookType type = this.menu.getRecipeBookType();
      boolean newSetting = !this.book.isFiltering(type);
      this.book.setFiltering(type, newSetting);
   }

   public boolean hasClickedOutside(final double mx, final double my, final int leftPos, final int topPos, final int imageWidth, final int imageHeight) {
      if (!this.isVisible()) {
         return true;
      } else {
         boolean clickedOutside = mx < (double)leftPos || my < (double)topPos || mx >= (double)(leftPos + imageWidth) || my >= (double)(topPos + imageHeight);
         boolean clickedOnRecipeBook = (double)(leftPos - 147) < mx && mx < (double)leftPos && (double)topPos < my && my < (double)(topPos + imageHeight);
         boolean clickedOnTab = this.tabBar.isMouseOver(mx, my);
         return clickedOutside && !clickedOnRecipeBook && !clickedOnTab;
      }
   }

   public boolean focusSearchBox() {
      if (this.searchBox != null && !this.searchBox.isFocused() && !this.overlay.isVisible()) {
         this.ignoreTextInput = true;
         this.setFocused(this.searchBox);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(final KeyEvent event) {
      this.ignoreTextInput = false;
      return this.isVisible() && !this.minecraft.player.isSpectator() ? super.keyPressed(event) : false;
   }

   public boolean keyReleased(final KeyEvent event) {
      this.ignoreTextInput = false;
      return super.keyReleased(event);
   }

   public boolean charTyped(final CharacterEvent event) {
      if (this.ignoreTextInput) {
         return false;
      } else {
         return this.isVisible() && !this.minecraft.player.isSpectator() ? super.charTyped(event) : false;
      }
   }

   public boolean preeditUpdated(final @Nullable PreeditEvent event) {
      if (this.ignoreTextInput) {
         return false;
      } else {
         return this.isVisible() && !this.minecraft.player.isSpectator() ? super.preeditUpdated(event) : false;
      }
   }

   public boolean capturesInput() {
      return this.searchBox != null && this.searchBox.capturesInput();
   }

   private void checkSearchStringUpdate(final String string) {
      String searchText = string.toLowerCase(Locale.ROOT);
      this.pirateSpeechForThePeople(searchText);
      if (!searchText.equals(this.lastSearch)) {
         this.updateCollections(false, this.isFiltering());
         this.lastSearch = searchText;
      }

   }

   private void pirateSpeechForThePeople(final String searchTarget) {
      if ("excitedze".equals(searchTarget)) {
         LanguageManager languageManager = this.minecraft.getLanguageManager();
         String arrrrCode = "en_pt";
         LanguageInfo language = languageManager.getLanguage("en_pt");
         if (language == null || languageManager.getSelected().equals("en_pt")) {
            return;
         }

         languageManager.setSelected("en_pt");
         this.minecraft.options.languageCode = "en_pt";
         this.minecraft.reloadResourcePacks();
         this.minecraft.options.save();
      }

   }

   private boolean isOffsetNextToMainGUI() {
      return this.xOffset == 86;
   }

   public void recipesUpdated() {
      this.selectMatchingRecipes();
      if (this.isVisible()) {
         this.updateTabs(this.isFiltering());
         this.updateCollections(false, this.isFiltering());
      }

   }

   public void recipeShown(final RecipeDisplayId recipe) {
      this.minecraft.player.removeRecipeHighlight(recipe);
   }

   public void fillGhostRecipe(final RecipeDisplay recipe) {
      this.ghostSlots.clear();
      ContextMap context = SlotDisplayContext.fromLevel((Level)Objects.requireNonNull(this.minecraft.level));
      this.fillGhostRecipe(this.ghostSlots, recipe, context);
   }

   protected abstract void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipe, ContextMap context);

   protected void sendUpdateSettings() {
      if (this.minecraft.getConnection() != null) {
         RecipeBookType type = this.menu.getRecipeBookType();
         boolean open = this.book.getBookSettings().isOpen(type);
         boolean filtering = this.book.getBookSettings().isFiltering(type);
         this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(type, open, filtering));
      }

   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
   }

   public Collection<? extends NarratableEntry> getNarratables() {
      return !this.isActive() ? List.of() : this.getActiveChildren().stream().flatMap((child) -> child.getNarratables().stream()).toList();
   }

   static {
      SEARCH_HINT = Component.translatable("gui.recipebook.search_hint").withStyle(EditBox.SEARCH_HINT_STYLE);
      TAB_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/tab"), Identifier.withDefaultNamespace("recipe_book/tab_selected"));
      ALL_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.all");
      FILTER_BUTTON_NAME = Component.translatable("gui.recipebook.filter");
   }

   public static record TabInfo(ItemStack primaryIcon, Optional<ItemStack> secondaryIcon, ExtendedRecipeBookCategory category, Component name) implements Tab {
      public TabInfo(final SearchRecipeBookCategory category) {
         this(new ItemStack(Items.COMPASS), Optional.empty(), category, Component.translatable("gui.recipebook.tab.all"));
      }

      public TabInfo(final Item icon, final RecipeBookCategory category) {
         this(new ItemStack(icon), Optional.empty(), category, categoryName(category));
      }

      public TabInfo(final Item primaryIcon, final Item secondaryIcon, final RecipeBookCategory category) {
         this(new ItemStack(primaryIcon), Optional.of(new ItemStack(secondaryIcon)), category, categoryName(category));
      }

      public TabInfo {
         super();
      }

      private static Component categoryName(final RecipeBookCategory category) {
         return Component.translatable(Util.makeDescriptionId("recipe_book_category", BuiltInRegistries.RECIPE_BOOK_CATEGORY.getKey(category)));
      }

      public void visitChildren(final Consumer<AbstractWidget> childrenConsumer) {
      }

      public void doLayout(final ScreenRectangle screenRectangle) {
      }

      public Layout getLayout() {
         return null;
      }
   }

   @FunctionalInterface
   public interface RecipePlacer {
      void placeRecipe(RecipeCollection collection, RecipeDisplayId recipe, boolean useMaxItems);
   }
}
