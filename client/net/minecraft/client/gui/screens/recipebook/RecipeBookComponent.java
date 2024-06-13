package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeBookComponent implements PlaceRecipe<Ingredient>, Renderable, GuiEventListener, NarratableEntry, RecipeShownListener {
   public static final WidgetSprites RECIPE_BUTTON_SPRITES = new WidgetSprites(
      new ResourceLocation("recipe_book/button"), new ResourceLocation("recipe_book/button_highlighted")
   );
   private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(
      new ResourceLocation("recipe_book/filter_enabled"),
      new ResourceLocation("recipe_book/filter_disabled"),
      new ResourceLocation("recipe_book/filter_enabled_highlighted"),
      new ResourceLocation("recipe_book/filter_disabled_highlighted")
   );
   protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private static final Component SEARCH_HINT = Component.translatable("gui.recipebook.search_hint")
      .withStyle(ChatFormatting.ITALIC)
      .withStyle(ChatFormatting.GRAY);
   public static final int IMAGE_WIDTH = 147;
   public static final int IMAGE_HEIGHT = 166;
   private static final int OFFSET_X_POSITION = 86;
   private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
   private static final Component ALL_RECIPES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.all");
   private int xOffset;
   private int width;
   private int height;
   protected final GhostRecipe ghostRecipe = new GhostRecipe();
   private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
   @Nullable
   private RecipeBookTabButton selectedTab;
   protected StateSwitchingButton filterButton;
   protected RecipeBookMenu<?> menu;
   protected Minecraft minecraft;
   @Nullable
   private EditBox searchBox;
   private String lastSearch = "";
   private ClientRecipeBook book;
   private final RecipeBookPage recipeBookPage = new RecipeBookPage();
   private final StackedContents stackedContents = new StackedContents();
   private int timesInventoryChanged;
   private boolean ignoreTextInput;
   private boolean visible;
   private boolean widthTooNarrow;

   public RecipeBookComponent() {
      super();
   }

   public void init(int var1, int var2, Minecraft var3, boolean var4, RecipeBookMenu<?> var5) {
      this.minecraft = var3;
      this.width = var1;
      this.height = var2;
      this.menu = var5;
      this.widthTooNarrow = var4;
      var3.player.containerMenu = var5;
      this.book = var3.player.getRecipeBook();
      this.timesInventoryChanged = var3.player.getInventory().getTimesChanged();
      this.visible = this.isVisibleAccordingToBookData();
      if (this.visible) {
         this.initVisuals();
      }
   }

   public void initVisuals() {
      this.xOffset = this.widthTooNarrow ? 0 : 86;
      int var1 = (this.width - 147) / 2 - this.xOffset;
      int var2 = (this.height - 166) / 2;
      this.stackedContents.clear();
      this.minecraft.player.getInventory().fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      String var3 = this.searchBox != null ? this.searchBox.getValue() : "";
      this.searchBox = new EditBox(this.minecraft.font, var1 + 25, var2 + 13, 81, 9 + 5, Component.translatable("itemGroup.search"));
      this.searchBox.setMaxLength(50);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(var3);
      this.searchBox.setHint(SEARCH_HINT);
      this.recipeBookPage.init(this.minecraft, var1, var2);
      this.recipeBookPage.addListener(this);
      this.filterButton = new StateSwitchingButton(var1 + 110, var2 + 12, 26, 16, this.book.isFiltering(this.menu));
      this.updateFilterButtonTooltip();
      this.initFilterButtonTextures();
      this.tabButtons.clear();

      for (RecipeBookCategories var5 : RecipeBookCategories.getCategories(this.menu.getRecipeBookType())) {
         this.tabButtons.add(new RecipeBookTabButton(var5));
      }

      if (this.selectedTab != null) {
         this.selectedTab = this.tabButtons.stream().filter(var1x -> var1x.getCategory().equals(this.selectedTab.getCategory())).findFirst().orElse(null);
      }

      if (this.selectedTab == null) {
         this.selectedTab = this.tabButtons.get(0);
      }

      this.selectedTab.setStateTriggered(true);
      this.updateCollections(false);
      this.updateTabs();
   }

   private void updateFilterButtonTooltip() {
      this.filterButton.setTooltip(this.filterButton.isStateTriggered() ? Tooltip.create(this.getRecipeFilterName()) : Tooltip.create(ALL_RECIPES_TOOLTIP));
   }

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_BUTTON_SPRITES);
   }

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

   public void slotClicked(@Nullable Slot var1) {
      if (var1 != null && var1.index < this.menu.getSize()) {
         this.ghostRecipe.clear();
         if (this.isVisible()) {
            this.updateStackedContents();
         }
      }
   }

   private void updateCollections(boolean var1) {
      List var2 = this.book.getCollection(this.selectedTab.getCategory());
      var2.forEach(var1x -> var1x.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book));
      ArrayList var3 = Lists.newArrayList(var2);
      var3.removeIf(var0 -> !var0.hasKnownRecipes());
      var3.removeIf(var0 -> !var0.hasFitting());
      String var4 = this.searchBox.getValue();
      if (!var4.isEmpty()) {
         ObjectLinkedOpenHashSet var5 = new ObjectLinkedOpenHashSet(
            this.minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(var4.toLowerCase(Locale.ROOT))
         );
         var3.removeIf(var1x -> !var5.contains(var1x));
      }

      if (this.book.isFiltering(this.menu)) {
         var3.removeIf(var0 -> !var0.hasCraftable());
      }

      this.recipeBookPage.updateCollections(var3, var1);
   }

   private void updateTabs() {
      int var1 = (this.width - 147) / 2 - this.xOffset - 30;
      int var2 = (this.height - 166) / 2 + 3;
      byte var3 = 27;
      int var4 = 0;

      for (RecipeBookTabButton var6 : this.tabButtons) {
         RecipeBookCategories var7 = var6.getCategory();
         if (var7 == RecipeBookCategories.CRAFTING_SEARCH || var7 == RecipeBookCategories.FURNACE_SEARCH) {
            var6.visible = true;
            var6.setPosition(var1, var2 + 27 * var4++);
         } else if (var6.updateVisibility(this.book)) {
            var6.setPosition(var1, var2 + 27 * var4++);
            var6.startAnimation(this.minecraft);
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
      this.updateCollections(false);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.isVisible()) {
         var1.pose().pushPose();
         var1.pose().translate(0.0F, 0.0F, 100.0F);
         int var5 = (this.width - 147) / 2 - this.xOffset;
         int var6 = (this.height - 166) / 2;
         var1.blit(RECIPE_BOOK_LOCATION, var5, var6, 1, 1, 147, 166);
         this.searchBox.render(var1, var2, var3, var4);

         for (RecipeBookTabButton var8 : this.tabButtons) {
            var8.render(var1, var2, var3, var4);
         }

         this.filterButton.render(var1, var2, var3, var4);
         this.recipeBookPage.render(var1, var5, var6, var2, var3, var4);
         var1.pose().popPose();
      }
   }

   public void renderTooltip(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      if (this.isVisible()) {
         this.recipeBookPage.renderTooltip(var1, var4, var5);
         this.renderGhostRecipeTooltip(var1, var2, var3, var4, var5);
      }
   }

   protected Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   private void renderGhostRecipeTooltip(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      ItemStack var6 = null;

      for (int var7 = 0; var7 < this.ghostRecipe.size(); var7++) {
         GhostRecipe.GhostIngredient var8 = this.ghostRecipe.get(var7);
         int var9 = var8.getX() + var2;
         int var10 = var8.getY() + var3;
         if (var4 >= var9 && var5 >= var10 && var4 < var9 + 16 && var5 < var10 + 16) {
            var6 = var8.getItem();
         }
      }

      if (var6 != null && this.minecraft.screen != null) {
         var1.renderComponentTooltip(this.minecraft.font, Screen.getTooltipFromItem(this.minecraft, var6), var4, var5);
      }
   }

   public void renderGhostRecipe(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
      this.ghostRecipe.render(var1, this.minecraft, var2, var3, var4, var5);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.recipeBookPage.mouseClicked(var1, var3, var5, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            RecipeHolder var9 = this.recipeBookPage.getLastClickedRecipe();
            RecipeCollection var10 = this.recipeBookPage.getLastClickedRecipeCollection();
            if (var9 != null && var10 != null) {
               if (!var10.isCraftable(var9) && this.ghostRecipe.getRecipe() == var9) {
                  return false;
               }

               this.ghostRecipe.clear();
               this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, var9, Screen.hasShiftDown());
               if (!this.isOffsetNextToMainGUI()) {
                  this.setVisible(false);
               }
            }

            return true;
         } else if (this.searchBox.mouseClicked(var1, var3, var5)) {
            this.searchBox.setFocused(true);
            return true;
         } else {
            this.searchBox.setFocused(false);
            if (this.filterButton.mouseClicked(var1, var3, var5)) {
               boolean var8 = this.toggleFiltering();
               this.filterButton.setStateTriggered(var8);
               this.updateFilterButtonTooltip();
               this.sendUpdateSettings();
               this.updateCollections(false);
               return true;
            } else {
               for (RecipeBookTabButton var7 : this.tabButtons) {
                  if (var7.mouseClicked(var1, var3, var5)) {
                     if (this.selectedTab != var7) {
                        if (this.selectedTab != null) {
                           this.selectedTab.setStateTriggered(false);
                        }

                        this.selectedTab = var7;
                        this.selectedTab.setStateTriggered(true);
                        this.updateCollections(true);
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

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      if (!this.isVisible() || this.minecraft.player.isSpectator()) {
         return false;
      } else if (var1 == 256 && !this.isOffsetNextToMainGUI()) {
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
      } else {
         return false;
      }
   }

   @Override
   public boolean keyReleased(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      return GuiEventListener.super.keyReleased(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (this.ignoreTextInput) {
         return false;
      } else if (!this.isVisible() || this.minecraft.player.isSpectator()) {
         return false;
      } else if (this.searchBox.charTyped(var1, var2)) {
         this.checkSearchStringUpdate();
         return true;
      } else {
         return GuiEventListener.super.charTyped(var1, var2);
      }
   }

   @Override
   public boolean isMouseOver(double var1, double var3) {
      return false;
   }

   @Override
   public void setFocused(boolean var1) {
   }

   @Override
   public boolean isFocused() {
      return false;
   }

   private void checkSearchStringUpdate() {
      String var1 = this.searchBox.getValue().toLowerCase(Locale.ROOT);
      this.pirateSpeechForThePeople(var1);
      if (!var1.equals(this.lastSearch)) {
         this.updateCollections(false);
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
      this.updateTabs();
      if (this.isVisible()) {
         this.updateCollections(false);
      }
   }

   @Override
   public void recipesShown(List<RecipeHolder<?>> var1) {
      for (RecipeHolder var3 : var1) {
         this.minecraft.player.removeRecipeHighlight(var3);
      }
   }

   public void setupGhostRecipe(RecipeHolder<?> var1, List<Slot> var2) {
      ItemStack var3 = var1.value().getResultItem(this.minecraft.level.registryAccess());
      this.ghostRecipe.setRecipe(var1);
      this.ghostRecipe.addIngredient(Ingredient.of(var3), ((Slot)var2.get(0)).x, ((Slot)var2.get(0)).y);
      this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), var1, var1.value().getIngredients().iterator(), 0);
   }

   @Override
   public void addItemToSlot(Iterator<Ingredient> var1, int var2, int var3, int var4, int var5) {
      Ingredient var6 = (Ingredient)var1.next();
      if (!var6.isEmpty()) {
         Slot var7 = this.menu.slots.get(var2);
         this.ghostRecipe.addIngredient(var6, var7.x, var7.y);
      }
   }

   protected void sendUpdateSettings() {
      if (this.minecraft.getConnection() != null) {
         RecipeBookType var1 = this.menu.getRecipeBookType();
         boolean var2 = this.book.getBookSettings().isOpen(var1);
         boolean var3 = this.book.getBookSettings().isFiltering(var1);
         this.minecraft.getConnection().send(new ServerboundRecipeBookChangeSettingsPacket(var1, var2, var3));
      }
   }

   @Override
   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.visible ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
   }

   @Override
   public void updateNarration(NarrationElementOutput var1) {
      ArrayList var2 = Lists.newArrayList();
      this.recipeBookPage.listButtons(var1x -> {
         if (var1x.isActive()) {
            var2.add(var1x);
         }
      });
      var2.add(this.searchBox);
      var2.add(this.filterButton);
      var2.addAll(this.tabButtons);
      Screen.NarratableSearchResult var3 = Screen.findNarratableWidget(var2, null);
      if (var3 != null) {
         var3.entry.updateNarration(var1.nest());
      }
   }
}
