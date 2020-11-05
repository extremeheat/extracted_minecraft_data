package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookComponent extends GuiComponent implements Widget, GuiEventListener, RecipeShownListener, PlaceRecipe<Ingredient> {
   protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private static final Component SEARCH_HINT;
   private static final Component ONLY_CRAFTABLES_TOOLTIP;
   private static final Component ALL_RECIPES_TOOLTIP;
   private int xOffset;
   private int width;
   private int height;
   protected final GhostRecipe ghostRecipe = new GhostRecipe();
   private final List<RecipeBookTabButton> tabButtons = Lists.newArrayList();
   private RecipeBookTabButton selectedTab;
   protected StateSwitchingButton filterButton;
   protected RecipeBookMenu<?> menu;
   protected Minecraft minecraft;
   private EditBox searchBox;
   private String lastSearch = "";
   private ClientRecipeBook book;
   private final RecipeBookPage recipeBookPage = new RecipeBookPage();
   private final StackedContents stackedContents = new StackedContents();
   private int timesInventoryChanged;
   private boolean ignoreTextInput;

   public RecipeBookComponent() {
      super();
   }

   public void init(int var1, int var2, Minecraft var3, boolean var4, RecipeBookMenu<?> var5) {
      this.minecraft = var3;
      this.width = var1;
      this.height = var2;
      this.menu = var5;
      var3.player.containerMenu = var5;
      this.book = var3.player.getRecipeBook();
      this.timesInventoryChanged = var3.player.inventory.getTimesChanged();
      if (this.isVisible()) {
         this.initVisuals(var4);
      }

      var3.keyboardHandler.setSendRepeatsToGui(true);
   }

   public void initVisuals(boolean var1) {
      this.xOffset = var1 ? 0 : 86;
      int var2 = (this.width - 147) / 2 - this.xOffset;
      int var3 = (this.height - 166) / 2;
      this.stackedContents.clear();
      this.minecraft.player.inventory.fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      String var4 = this.searchBox != null ? this.searchBox.getValue() : "";
      Font var10003 = this.minecraft.font;
      int var10004 = var2 + 25;
      int var10005 = var3 + 14;
      this.minecraft.font.getClass();
      this.searchBox = new EditBox(var10003, var10004, var10005, 80, 9 + 5, new TranslatableComponent("itemGroup.search"));
      this.searchBox.setMaxLength(50);
      this.searchBox.setBordered(false);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(var4);
      this.recipeBookPage.init(this.minecraft, var2, var3);
      this.recipeBookPage.addListener(this);
      this.filterButton = new StateSwitchingButton(var2 + 110, var3 + 12, 26, 16, this.book.isFiltering(this.menu));
      this.initFilterButtonTextures();
      this.tabButtons.clear();
      Iterator var5 = RecipeBookCategories.getCategories(this.menu.getRecipeBookType()).iterator();

      while(var5.hasNext()) {
         RecipeBookCategories var6 = (RecipeBookCategories)var5.next();
         this.tabButtons.add(new RecipeBookTabButton(var6));
      }

      if (this.selectedTab != null) {
         this.selectedTab = (RecipeBookTabButton)this.tabButtons.stream().filter((var1x) -> {
            return var1x.getCategory().equals(this.selectedTab.getCategory());
         }).findFirst().orElse((Object)null);
      }

      if (this.selectedTab == null) {
         this.selectedTab = (RecipeBookTabButton)this.tabButtons.get(0);
      }

      this.selectedTab.setStateTriggered(true);
      this.updateCollections(false);
      this.updateTabs();
   }

   public boolean changeFocus(boolean var1) {
      return false;
   }

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 41, 28, 18, RECIPE_BOOK_LOCATION);
   }

   public void removed() {
      this.searchBox = null;
      this.selectedTab = null;
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public int updateScreenPosition(boolean var1, int var2, int var3) {
      int var4;
      if (this.isVisible() && !var1) {
         var4 = 177 + (var2 - var3 - 200) / 2;
      } else {
         var4 = (var2 - var3) / 2;
      }

      return var4;
   }

   public void toggleVisibility() {
      this.setVisible(!this.isVisible());
   }

   public boolean isVisible() {
      return this.book.isOpen(this.menu.getRecipeBookType());
   }

   protected void setVisible(boolean var1) {
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
      var2.forEach((var1x) -> {
         var1x.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book);
      });
      ArrayList var3 = Lists.newArrayList(var2);
      var3.removeIf((var0) -> {
         return !var0.hasKnownRecipes();
      });
      var3.removeIf((var0) -> {
         return !var0.hasFitting();
      });
      String var4 = this.searchBox.getValue();
      if (!var4.isEmpty()) {
         ObjectLinkedOpenHashSet var5 = new ObjectLinkedOpenHashSet(this.minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(var4.toLowerCase(Locale.ROOT)));
         var3.removeIf((var1x) -> {
            return !var5.contains(var1x);
         });
      }

      if (this.book.isFiltering(this.menu)) {
         var3.removeIf((var0) -> {
            return !var0.hasCraftable();
         });
      }

      this.recipeBookPage.updateCollections(var3, var1);
   }

   private void updateTabs() {
      int var1 = (this.width - 147) / 2 - this.xOffset - 30;
      int var2 = (this.height - 166) / 2 + 3;
      boolean var3 = true;
      int var4 = 0;
      Iterator var5 = this.tabButtons.iterator();

      while(true) {
         while(var5.hasNext()) {
            RecipeBookTabButton var6 = (RecipeBookTabButton)var5.next();
            RecipeBookCategories var7 = var6.getCategory();
            if (var7 != RecipeBookCategories.CRAFTING_SEARCH && var7 != RecipeBookCategories.FURNACE_SEARCH) {
               if (var6.updateVisibility(this.book)) {
                  var6.setPosition(var1, var2 + 27 * var4++);
                  var6.startAnimation(this.minecraft);
               }
            } else {
               var6.visible = true;
               var6.setPosition(var1, var2 + 27 * var4++);
            }
         }

         return;
      }
   }

   public void tick() {
      if (this.isVisible()) {
         if (this.timesInventoryChanged != this.minecraft.player.inventory.getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.inventory.getTimesChanged();
         }

         this.searchBox.tick();
      }
   }

   private void updateStackedContents() {
      this.stackedContents.clear();
      this.minecraft.player.inventory.fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      this.updateCollections(false);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.isVisible()) {
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 100.0F);
         this.minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var5 = (this.width - 147) / 2 - this.xOffset;
         int var6 = (this.height - 166) / 2;
         this.blit(var1, var5, var6, 1, 1, 147, 166);
         if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
            drawString(var1, this.minecraft.font, SEARCH_HINT, var5 + 25, var6 + 14, -1);
         } else {
            this.searchBox.render(var1, var2, var3, var4);
         }

         Iterator var7 = this.tabButtons.iterator();

         while(var7.hasNext()) {
            RecipeBookTabButton var8 = (RecipeBookTabButton)var7.next();
            var8.render(var1, var2, var3, var4);
         }

         this.filterButton.render(var1, var2, var3, var4);
         this.recipeBookPage.render(var1, var5, var6, var2, var3, var4);
         RenderSystem.popMatrix();
      }
   }

   public void renderTooltip(PoseStack var1, int var2, int var3, int var4, int var5) {
      if (this.isVisible()) {
         this.recipeBookPage.renderTooltip(var1, var4, var5);
         if (this.filterButton.isHovered()) {
            Component var6 = this.getFilterButtonTooltip();
            if (this.minecraft.screen != null) {
               this.minecraft.screen.renderTooltip(var1, var6, var4, var5);
            }
         }

         this.renderGhostRecipeTooltip(var1, var2, var3, var4, var5);
      }
   }

   private Component getFilterButtonTooltip() {
      return this.filterButton.isStateTriggered() ? this.getRecipeFilterName() : ALL_RECIPES_TOOLTIP;
   }

   protected Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   private void renderGhostRecipeTooltip(PoseStack var1, int var2, int var3, int var4, int var5) {
      ItemStack var6 = null;

      for(int var7 = 0; var7 < this.ghostRecipe.size(); ++var7) {
         GhostRecipe.GhostIngredient var8 = this.ghostRecipe.get(var7);
         int var9 = var8.getX() + var2;
         int var10 = var8.getY() + var3;
         if (var4 >= var9 && var5 >= var10 && var4 < var9 + 16 && var5 < var10 + 16) {
            var6 = var8.getItem();
         }
      }

      if (var6 != null && this.minecraft.screen != null) {
         this.minecraft.screen.renderComponentTooltip(var1, this.minecraft.screen.getTooltipFromItem(var6), var4, var5);
      }

   }

   public void renderGhostRecipe(PoseStack var1, int var2, int var3, boolean var4, float var5) {
      this.ghostRecipe.render(var1, this.minecraft, var2, var3, var4, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.recipeBookPage.mouseClicked(var1, var3, var5, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            Recipe var9 = this.recipeBookPage.getLastClickedRecipe();
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
            return true;
         } else if (this.filterButton.mouseClicked(var1, var3, var5)) {
            boolean var8 = this.toggleFiltering();
            this.filterButton.setStateTriggered(var8);
            this.sendUpdateSettings();
            this.updateCollections(false);
            return true;
         } else {
            Iterator var6 = this.tabButtons.iterator();

            RecipeBookTabButton var7;
            do {
               if (!var6.hasNext()) {
                  return false;
               }

               var7 = (RecipeBookTabButton)var6.next();
            } while(!var7.mouseClicked(var1, var3, var5));

            if (this.selectedTab != var7) {
               this.selectedTab.setStateTriggered(false);
               this.selectedTab = var7;
               this.selectedTab.setStateTriggered(true);
               this.updateCollections(true);
            }

            return true;
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
         return var10 && !var11 && !this.selectedTab.isHovered();
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
            this.searchBox.setFocus(true);
            return true;
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
         LanguageInfo var3 = var2.getLanguage("en_pt");
         if (var2.getSelected().compareTo(var3) == 0) {
            return;
         }

         var2.setSelected(var3);
         this.minecraft.options.languageCode = var3.getCode();
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

   public void recipesShown(List<Recipe<?>> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Recipe var3 = (Recipe)var2.next();
         this.minecraft.player.removeRecipeHighlight(var3);
      }

   }

   public void setupGhostRecipe(Recipe<?> var1, List<Slot> var2) {
      ItemStack var3 = var1.getResultItem();
      this.ghostRecipe.setRecipe(var1);
      this.ghostRecipe.addIngredient(Ingredient.of(var3), ((Slot)var2.get(0)).x, ((Slot)var2.get(0)).y);
      this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), var1, var1.getIngredients().iterator(), 0);
   }

   public void addItemToSlot(Iterator<Ingredient> var1, int var2, int var3, int var4, int var5) {
      Ingredient var6 = (Ingredient)var1.next();
      if (!var6.isEmpty()) {
         Slot var7 = (Slot)this.menu.slots.get(var2);
         this.ghostRecipe.addIngredient(var6, var7.x, var7.y);
      }

   }

   protected void sendUpdateSettings() {
      if (this.minecraft.getConnection() != null) {
         RecipeBookType var1 = this.menu.getRecipeBookType();
         boolean var2 = this.book.getBookSettings().isOpen(var1);
         boolean var3 = this.book.getBookSettings().isFiltering(var1);
         this.minecraft.getConnection().send((Packet)(new ServerboundRecipeBookChangeSettingsPacket(var1, var2, var3)));
      }

   }

   static {
      SEARCH_HINT = (new TranslatableComponent("gui.recipebook.search_hint")).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
      ONLY_CRAFTABLES_TOOLTIP = new TranslatableComponent("gui.recipebook.toggleRecipes.craftable");
      ALL_RECIPES_TOOLTIP = new TranslatableComponent("gui.recipebook.toggleRecipes.all");
   }
}
