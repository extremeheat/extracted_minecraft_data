package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

public abstract class RecipeBookComponent<T extends RecipeBookMenu> implements Renderable, GuiEventListener, NarratableEntry {
   public static final WidgetSprites RECIPE_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/button"), ResourceLocation.withDefaultNamespace("recipe_book/button_highlighted"));
   protected static final ResourceLocation RECIPE_BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png");
   private static final int BACKGROUND_TEXTURE_WIDTH = 256;
   private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final Component SEARCH_HINT;
   public static final int IMAGE_WIDTH = 147;
   public static final int IMAGE_HEIGHT = 166;
   private static final int OFFSET_X_POSITION = 86;
   private static final int BORDER_WIDTH = 8;
   private static final Component ALL_RECIPES_TOOLTIP;
   private static final int TICKS_TO_SWAP_SLOT = 30;
   private int xOffset;
   private int width;
   private int height;
   private float time;
   @Nullable
   private RecipeDisplayId lastPlacedRecipe;
   private final GhostSlots ghostSlots;
   private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
   @Nullable
   private RecipeBookTabButton selectedTab;
   protected StateSwitchingButton filterButton;
   protected final T menu;
   protected Minecraft minecraft;
   @Nullable
   private EditBox searchBox;
   private String lastSearch = "";
   private final List<TabInfo> tabInfos;
   private ClientRecipeBook book;
   private final RecipeBookPage recipeBookPage;
   @Nullable
   private RecipeDisplayId lastRecipe;
   @Nullable
   private RecipeCollection lastRecipeCollection;
   private final StackedItemContents stackedContents = new StackedItemContents();
   private int timesInventoryChanged;
   private boolean ignoreTextInput;
   private boolean visible;
   private boolean widthTooNarrow;
   @Nullable
   private ScreenRectangle magnifierIconPlacement;

   public RecipeBookComponent(T var1, List<TabInfo> var2) {
      super();
      this.menu = var1;
      this.tabInfos = var2;
      SlotSelectTime var3 = () -> Mth.floor(this.time / 30.0F);
      this.ghostSlots = new GhostSlots(var3);
      this.recipeBookPage = new RecipeBookPage(this, var3, var1 instanceof AbstractFurnaceMenu);
   }

   public void init(int var1, int var2, Minecraft var3, boolean var4) {
      this.minecraft = var3;
      this.width = var1;
      this.height = var2;
      this.widthTooNarrow = var4;
      this.book = var3.player.getRecipeBook();
      this.timesInventoryChanged = var3.player.getInventory().getTimesChanged();
      this.visible = this.isVisibleAccordingToBookData();
      if (this.visible) {
         this.initVisuals();
      }

   }

   private void initVisuals() {
      boolean var1 = this.isFiltering();
      this.xOffset = this.widthTooNarrow ? 0 : 86;
      int var2 = this.getXOrigin();
      int var3 = this.getYOrigin();
      this.stackedContents.clear();
      this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      String var4 = this.searchBox != null ? this.searchBox.getValue() : "";
      Font var10003 = this.minecraft.font;
      int var10004 = var2 + 25;
      int var10005 = var3 + 13;
      Objects.requireNonNull(this.minecraft.font);
      this.searchBox = new EditBox(var10003, var10004, var10005, 81, 9 + 5, Component.translatable("itemGroup.search"));
      this.searchBox.setMaxLength(50);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(var4);
      this.searchBox.setHint(SEARCH_HINT);
      this.magnifierIconPlacement = ScreenRectangle.of(ScreenAxis.HORIZONTAL, var2 + 8, this.searchBox.getY(), this.searchBox.getX() - this.getXOrigin(), this.searchBox.getHeight());
      this.recipeBookPage.init(this.minecraft, var2, var3);
      this.filterButton = new StateSwitchingButton(var2 + 110, var3 + 12, 26, 16, var1);
      this.updateFilterButtonTooltip();
      this.initFilterButtonTextures();
      this.tabButtons.clear();

      for(TabInfo var6 : this.tabInfos) {
         this.tabButtons.add(new RecipeBookTabButton(var6));
      }

      if (this.selectedTab != null) {
         this.selectedTab = (RecipeBookTabButton)this.tabButtons.stream().filter((var1x) -> var1x.getCategory().equals(this.selectedTab.getCategory())).findFirst().orElse((Object)null);
      }

      if (this.selectedTab == null) {
         this.selectedTab = (RecipeBookTabButton)this.tabButtons.get(0);
      }

      this.selectedTab.setStateTriggered(true);
      this.selectMatchingRecipes();
      this.updateTabs(var1);
      this.updateCollections(false, var1);
   }

   private int getYOrigin() {
      return (this.height - 166) / 2;
   }

   private int getXOrigin() {
      return (this.width - 147) / 2 - this.xOffset;
   }

   private void updateFilterButtonTooltip() {
      this.filterButton.setTooltip(this.filterButton.isStateTriggered() ? Tooltip.create(this.getRecipeFilterName()) : Tooltip.create(ALL_RECIPES_TOOLTIP));
   }

   protected abstract void initFilterButtonTextures();

   public int updateScreenPosition(int var1, int var2) {
      int var3;
      if (this.isVisible() && !this.widthTooNarrow) {
         var3 = 177 + (var1 - var2 - 200) / 2;
      } else {
         var3 = (var1 - var2) / 2;
      }

      return var3;
   }

   public void toggleVisibility() {
      this.setVisible(!this.isVisible());
   }

   public boolean isVisible() {
      return this.visible;
   }

   private boolean isVisibleAccordingToBookData() {
      return this.book.isOpen(this.menu.getRecipeBookType());
   }

   protected void setVisible(boolean var1) {
      if (var1) {
         this.initVisuals();
      }

      this.visible = var1;
      this.book.setOpen(this.menu.getRecipeBookType(), var1);
      if (!var1) {
         this.recipeBookPage.setInvisible();
      }

      this.sendUpdateSettings();
   }

   protected abstract boolean isCraftingSlot(Slot var1);

   public void slotClicked(@Nullable Slot var1) {
      if (var1 != null && this.isCraftingSlot(var1)) {
         this.lastPlacedRecipe = null;
         this.ghostSlots.clear();
         if (this.isVisible()) {
            this.updateStackedContents();
         }
      }

   }

   private void selectMatchingRecipes() {
      for(TabInfo var2 : this.tabInfos) {
         for(RecipeCollection var4 : this.book.getCollection(var2.category())) {
            this.selectMatchingRecipes(var4, this.stackedContents);
         }
      }

   }

   protected abstract void selectMatchingRecipes(RecipeCollection var1, StackedItemContents var2);

   private void updateCollections(boolean var1, boolean var2) {
      List var3 = this.book.getCollection(this.selectedTab.getCategory());
      ArrayList var4 = Lists.newArrayList(var3);
      var4.removeIf((var0) -> !var0.hasAnySelected());
      String var5 = this.searchBox.getValue();
      if (!var5.isEmpty()) {
         ClientPacketListener var6 = this.minecraft.getConnection();
         if (var6 != null) {
            ObjectLinkedOpenHashSet var7 = new ObjectLinkedOpenHashSet(var6.searchTrees().recipes().search(var5.toLowerCase(Locale.ROOT)));
            var4.removeIf((var1x) -> !var7.contains(var1x));
         }
      }

      if (var2) {
         var4.removeIf((var0) -> !var0.hasCraftable());
      }

      this.recipeBookPage.updateCollections(var4, var1, var2);
   }

   private void updateTabs(boolean var1) {
      int var2 = (this.width - 147) / 2 - this.xOffset - 30;
      int var3 = (this.height - 166) / 2 + 3;
      boolean var4 = true;
      int var5 = 0;

      for(RecipeBookTabButton var7 : this.tabButtons) {
         ExtendedRecipeBookCategory var8 = var7.getCategory();
         if (var8 instanceof SearchRecipeBookCategory) {
            var7.visible = true;
            var7.setPosition(var2, var3 + 27 * var5++);
         } else if (var7.updateVisibility(this.book)) {
            var7.setPosition(var2, var3 + 27 * var5++);
            var7.startAnimation(this.book, var1);
         }
      }

   }

   public void tick() {
      boolean var1 = this.isVisibleAccordingToBookData();
      if (this.isVisible() != var1) {
         this.setVisible(var1);
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

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.isVisible()) {
         if (!Screen.hasControlDown()) {
            this.time += var4;
         }

         var1.pose().pushPose();
         var1.pose().translate(0.0F, 0.0F, 100.0F);
         int var5 = this.getXOrigin();
         int var6 = this.getYOrigin();
         var1.blit(RenderType::guiTextured, RECIPE_BOOK_LOCATION, var5, var6, 1.0F, 1.0F, 147, 166, 256, 256);
         this.searchBox.render(var1, var2, var3, var4);

         for(RecipeBookTabButton var8 : this.tabButtons) {
            var8.render(var1, var2, var3, var4);
         }

         this.filterButton.render(var1, var2, var3, var4);
         this.recipeBookPage.render(var1, var5, var6, var2, var3, var4);
         var1.pose().popPose();
      }
   }

   public void renderTooltip(GuiGraphics var1, int var2, int var3, @Nullable Slot var4) {
      if (this.isVisible()) {
         this.recipeBookPage.renderTooltip(var1, var2, var3);
         this.ghostSlots.renderTooltip(var1, this.minecraft, var2, var3, var4);
      }
   }

   protected abstract Component getRecipeFilterName();

   public void renderGhostRecipe(GuiGraphics var1, boolean var2) {
      this.ghostSlots.render(var1, this.minecraft, var2);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.recipeBookPage.mouseClicked(var1, var3, var5, this.getXOrigin(), this.getYOrigin(), 147, 166)) {
            RecipeDisplayId var10 = this.recipeBookPage.getLastClickedRecipe();
            RecipeCollection var11 = this.recipeBookPage.getLastClickedRecipeCollection();
            if (var10 != null && var11 != null) {
               if (!this.tryPlaceRecipe(var11, var10)) {
                  return false;
               }

               this.lastRecipeCollection = var11;
               this.lastRecipe = var10;
               if (!this.isOffsetNextToMainGUI()) {
                  this.setVisible(false);
               }
            }

            return true;
         } else {
            if (this.searchBox != null) {
               boolean var6 = this.magnifierIconPlacement != null && this.magnifierIconPlacement.containsPoint(Mth.floor(var1), Mth.floor(var3));
               if (var6 || this.searchBox.mouseClicked(var1, var3, var5)) {
                  this.searchBox.setFocused(true);
                  return true;
               }

               this.searchBox.setFocused(false);
            }

            if (this.filterButton.mouseClicked(var1, var3, var5)) {
               boolean var9 = this.toggleFiltering();
               this.filterButton.setStateTriggered(var9);
               this.updateFilterButtonTooltip();
               this.sendUpdateSettings();
               this.updateCollections(false, var9);
               return true;
            } else {
               for(RecipeBookTabButton var7 : this.tabButtons) {
                  if (var7.mouseClicked(var1, var3, var5)) {
                     if (this.selectedTab != var7) {
                        if (this.selectedTab != null) {
                           this.selectedTab.setStateTriggered(false);
                        }

                        this.selectedTab = var7;
                        this.selectedTab.setStateTriggered(true);
                        this.updateCollections(true, this.isFiltering());
                     }

                     return true;
                  }
               }

               return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean tryPlaceRecipe(RecipeCollection var1, RecipeDisplayId var2) {
      if (!var1.isCraftable(var2) && var2.equals(this.lastPlacedRecipe)) {
         return false;
      } else {
         this.lastPlacedRecipe = var2;
         this.ghostSlots.clear();
         this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, var2, Screen.hasShiftDown());
         return true;
      }
   }

   private boolean toggleFiltering() {
      RecipeBookType var1 = this.menu.getRecipeBookType();
      boolean var2 = !this.book.isFiltering(var1);
      this.book.setFiltering(var1, var2);
      return var2;
   }

   public boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7, int var8, int var9) {
      if (!this.isVisible()) {
         return true;
      } else {
         boolean var10 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + var7) || var3 >= (double)(var6 + var8);
         boolean var11 = (double)(var5 - 147) < var1 && var1 < (double)var5 && (double)var6 < var3 && var3 < (double)(var6 + var8);
         return var10 && !var11 && !this.selectedTab.isHoveredOrFocused();
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (var1 == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
         } else if (this.searchBox.keyPressed(var1, var2, var3)) {
            this.checkSearchStringUpdate();
            return true;
         } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && var1 != 256) {
            return true;
         } else if (this.minecraft.options.keyChat.matches(var1, var2) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
         } else if (CommonInputs.selected(var1) && this.lastRecipeCollection != null && this.lastRecipe != null) {
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
            return this.tryPlaceRecipe(this.lastRecipeCollection, this.lastRecipe);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean keyReleased(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      return GuiEventListener.super.keyReleased(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      if (this.ignoreTextInput) {
         return false;
      } else if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.searchBox.charTyped(var1, var2)) {
            this.checkSearchStringUpdate();
            return true;
         } else {
            return GuiEventListener.super.charTyped(var1, var2);
         }
      } else {
         return false;
      }
   }

   public boolean isMouseOver(double var1, double var3) {
      return false;
   }

   public void setFocused(boolean var1) {
   }

   public boolean isFocused() {
      return false;
   }

   private void checkSearchStringUpdate() {
      String var1 = this.searchBox.getValue().toLowerCase(Locale.ROOT);
      this.pirateSpeechForThePeople(var1);
      if (!var1.equals(this.lastSearch)) {
         this.updateCollections(false, this.isFiltering());
         this.lastSearch = var1;
      }

   }

   private void pirateSpeechForThePeople(String var1) {
      if ("excitedze".equals(var1)) {
         LanguageManager var2 = this.minecraft.getLanguageManager();
         String var3 = "en_pt";
         LanguageInfo var4 = var2.getLanguage("en_pt");
         if (var4 == null || var2.getSelected().equals("en_pt")) {
            return;
         }

         var2.setSelected("en_pt");
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
      this.updateTabs(this.isFiltering());
      if (this.isVisible()) {
         this.updateCollections(false, this.isFiltering());
      }

   }

   public void recipeShown(RecipeDisplayId var1) {
      this.minecraft.player.removeRecipeHighlight(var1);
   }

   public void fillGhostRecipe(RecipeDisplay var1) {
      this.ghostSlots.clear();
      ContextMap var2 = SlotDisplayContext.fromLevel((Level)Objects.requireNonNull(this.minecraft.level));
      this.fillGhostRecipe(this.ghostSlots, var1, var2);
   }

   protected abstract void fillGhostRecipe(GhostSlots var1, RecipeDisplay var2, ContextMap var3);

   protected void sendUpdateSettings() {
      if (this.minecraft.getConnection() != null) {
         RecipeBookType var1 = this.menu.getRecipeBookType();
         boolean var2 = this.book.getBookSettings().isOpen(var1);
         boolean var3 = this.book.getBookSettings().isFiltering(var1);
         this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(var1, var2, var3));
      }

   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.visible ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
   }

   public void updateNarration(NarrationElementOutput var1) {
      ArrayList var2 = Lists.newArrayList();
      this.recipeBookPage.listButtons((var1x) -> {
         if (var1x.isActive()) {
            var2.add(var1x);
         }

      });
      var2.add(this.searchBox);
      var2.add(this.filterButton);
      var2.addAll(this.tabButtons);
      Screen.NarratableSearchResult var3 = Screen.findNarratableWidget(var2, (NarratableEntry)null);
      if (var3 != null) {
         var3.entry.updateNarration(var1.nest());
      }

   }

   static {
      SEARCH_HINT = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
      ALL_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.all");
   }

   public static record TabInfo(ItemStack primaryIcon, Optional<ItemStack> secondaryIcon, ExtendedRecipeBookCategory category) {
      public TabInfo(SearchRecipeBookCategory var1) {
         this((ItemStack)(new ItemStack(Items.COMPASS)), (Optional)Optional.empty(), (ExtendedRecipeBookCategory)var1);
      }

      public TabInfo(Item var1, RecipeBookCategory var2) {
         this((ItemStack)(new ItemStack(var1)), (Optional)Optional.empty(), (ExtendedRecipeBookCategory)var2);
      }

      public TabInfo(Item var1, Item var2, RecipeBookCategory var3) {
         this((ItemStack)(new ItemStack(var1)), (Optional)Optional.of(new ItemStack(var2)), (ExtendedRecipeBookCategory)var3);
      }

      public TabInfo(ItemStack var1, Optional<ItemStack> var2, ExtendedRecipeBookCategory var3) {
         super();
         this.primaryIcon = var1;
         this.secondaryIcon = var2;
         this.category = var3;
      }
   }
}
