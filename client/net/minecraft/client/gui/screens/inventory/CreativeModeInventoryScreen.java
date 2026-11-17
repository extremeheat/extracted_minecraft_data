package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ItemTabButton;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.GroupNarration;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.social.EntityPortraitWidget;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Prediction;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class CreativeModeInventoryScreen extends AbstractContainerScreen<ItemPickerMenu> {
   private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
   private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");
   private static final int NUM_ROWS = 5;
   private static final int NUM_COLS = 9;
   private static final int SCROLL_MARGIN_ROWS = 2;
   private static final int TAB_WIDTH = 26;
   private static final int TAB_SPACING = 1;
   private static final int TAB_HEIGHT = 32;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private static final SimpleContainer CONTAINER = new SimpleContainer(45);
   private static final Component TITLE = Component.translatable("container.creative_inventory");
   private static final Component DESTROY_ITEM_SLOT_NAME = Component.translatable("inventory.binSlot");
   private static final Component TOP_TAB_BAR_NAME = Component.translatable("narration.tab_navigation.top");
   private static final Component BOTTOM_TAB_BAR_NAME = Component.translatable("narration.tab_navigation.bottom");
   private static CreativeModeTab selectedTab = CreativeModeTabs.getDefaultTab();
   private float scrollOffs;
   private boolean scrolling;
   private EditBox searchBox;
   private @Nullable List<Slot> originalSlots;
   private @Nullable Slot destroyItemSlot;
   private CreativeInventoryListener listener;
   private boolean ignoreTextInput;
   private boolean hasClickedOutside;
   private final Set<TagKey<Item>> visibleTags = new HashSet();
   private final boolean displayOperatorCreativeTab;
   private final EffectsInInventory effects;
   private TabNavigationBar tabBarTop;
   private TabNavigationBar tabBarBottom;
   private final TabManager tabManager = new TabManager((var0) -> {
   }, (var0) -> {
   }, (tab) -> {
      if (tab instanceof CreativeModeTabAdapter tabAdapter) {
         this.showTab(tabAdapter.tab());
      }

   }, (tab) -> {
      if (tab instanceof CreativeModeTabAdapter tabAdapter) {
         if (tabAdapter.tab().getType() == CreativeModeTab.Type.INVENTORY) {
            (this.menu).slots.clear();
            (this.menu).slots.addAll(this.originalSlots);
            this.originalSlots = null;
         }
      }

   });
   private final Function<Slot, Tooltip> tooltipSupplier = (slot) -> {
      ItemStack item = slot.getItem();
      boolean isCreativeSlot = slot instanceof CustomCreativeSlot;
      boolean isSingleCategoryTab = selectedTab.getType() == CreativeModeTab.Type.CATEGORY;
      boolean isSearchTab = selectedTab.getType() == CreativeModeTab.Type.SEARCH;
      TooltipFlag.Default originalTooltipStyle = this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
      TooltipFlag tooltipStyle = isCreativeSlot ? originalTooltipStyle.asCreative() : originalTooltipStyle;
      List<Component> originalLines = item.getTooltipLines(Item.TooltipContext.of((Level)this.minecraft.level), this.minecraft.player, tooltipStyle);
      if (!originalLines.isEmpty() && (!isSingleCategoryTab || !isCreativeSlot)) {
         List<Component> linesToDisplay = new ArrayList(originalLines);
         if (isSearchTab && isCreativeSlot) {
            this.visibleTags.forEach((tag) -> {
               if (item.is(tag)) {
                  linesToDisplay.add(1, Component.literal("#" + String.valueOf(tag.location())).withStyle(ChatFormatting.DARK_PURPLE));
               }

            });
         }

         int i = 1;

         for(CreativeModeTab tab : CreativeModeTabs.tabs()) {
            if (tab.getType() != CreativeModeTab.Type.SEARCH && tab.contains(item)) {
               linesToDisplay.add(i++, tab.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
            }
         }

         return Tooltip.createForItem(linesToDisplay, item);
      } else {
         return Tooltip.createForItem(originalLines, item);
      }
   };

   public CreativeModeInventoryScreen(final LocalPlayer player, final FeatureFlagSet enabledFeatures, final boolean displayOperatorCreativeTab) {
      super(new ItemPickerMenu(player), player.getInventory(), TITLE, 195, 136);
      player.containerMenu = this.menu;
      this.displayOperatorCreativeTab = displayOperatorCreativeTab;
      this.tryRebuildTabContents(player.connection.searchTrees(), enabledFeatures, this.hasPermissions(player), player.level().registryAccess());
      this.effects = new EffectsInInventory(this);
   }

   protected void init() {
      if (!this.minecraft.player.hasInfiniteMaterials()) {
         this.minecraft.gui.setScreen(new InventoryScreen(this.minecraft.player));
      } else {
         super.init();
         TabNavigationBar.Builder tabBarTopBuilder = TabNavigationBar.builder(this.tabManager, this.leftPos, this.topPos - 32 + 4, this.imageWidth, 32, LinearLayout.Orientation.HORIZONTAL).spacing(1).name(TOP_TAB_BAR_NAME);
         TabNavigationBar.Builder tabBarBottomBuilder = TabNavigationBar.builder(this.tabManager, this.leftPos, this.topPos + this.imageHeight - 4, this.imageWidth, 32, LinearLayout.Orientation.HORIZONTAL).spacing(1).name(BOTTOM_TAB_BAR_NAME);

         for(CreativeModeTab tab : CreativeModeTabs.tabs()) {
            boolean isTop = tab.row() == CreativeModeTab.Row.TOP;
            int index = Math.clamp((long)tab.column(), 0, 6) + 1;
            String row = isTop ? "top" : "bottom";
            WidgetSprites tabSprite = new WidgetSprites(Identifier.withDefaultNamespace("container/creative_inventory/tab_" + row + "_unselected_" + index), Identifier.withDefaultNamespace("container/creative_inventory/tab_" + row + "_selected_" + index));
            CreativeModeTabAdapter tabAdapter = new CreativeModeTabAdapter(tab);
            ItemTabButton tabButton = ItemTabButton.builder(this.tabManager, tabAdapter, tabSprite).size(26, 32).icon(tab.getIconItem()).iconYOffset(isTop ? 1 : -1).message(tab.getDisplayName()).build();
            TabNavigationBar.Builder tabBarBuilder = isTop ? tabBarTopBuilder : tabBarBottomBuilder;
            TabNavigationBar.Align align = tab.isAlignedRight() ? TabNavigationBar.Align.RIGHT_OR_BOTTOM : TabNavigationBar.Align.LEFT_OR_TOP;
            tabBarBuilder.addTabToGroup(align, tabButton, tabAdapter);
         }

         this.tabBarTop = (TabNavigationBar)this.addWidget(tabBarTopBuilder.build());
         this.tabBarBottom = (TabNavigationBar)this.addWidget(tabBarBottomBuilder.build());
         Font var10003 = this.font;
         int var10004 = this.leftPos + 82;
         int var10005 = this.topPos + 6;
         Objects.requireNonNull(this.font);
         this.searchBox = new EditBox(var10003, var10004, var10005, 80, 9, Component.translatable("itemGroup.search"));
         this.searchBox.setMaxLength(50);
         this.searchBox.setBordered(false);
         this.searchBox.setVisible(false);
         this.searchBox.setTextColor(-1);
         this.searchBox.setInvertHighlightedTextColor(false);
         this.searchBox.setResponder((value) -> {
            if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
               this.refreshSearchResults();
            }

         });
         this.addRenderableWidget(this.searchBox);
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
         this.listener = new CreativeInventoryListener(this.minecraft);
         this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
         if (!selectedTab.shouldDisplay()) {
            selectedTab = CreativeModeTabs.getDefaultTab();
         }

         this.selectTab(selectedTab);
      }
   }

   private boolean hasPermissions(final Player player) {
      return player.canUseGameMasterBlocks() && this.displayOperatorCreativeTab;
   }

   private void tryRefreshInvalidatedTabs(final FeatureFlagSet enabledFeatures, final boolean hasPermissions, final HolderLookup.Provider holders) {
      ClientPacketListener connection = this.minecraft.getConnection();
      if (this.tryRebuildTabContents(connection != null ? connection.searchTrees() : null, enabledFeatures, hasPermissions, holders)) {
         for(CreativeModeTab tab : CreativeModeTabs.allTabs()) {
            Collection<ItemStack> displayList = tab.getDisplayItems();
            if (tab == selectedTab) {
               if (tab.getType() == CreativeModeTab.Type.CATEGORY && displayList.isEmpty()) {
                  this.selectTab(CreativeModeTabs.getDefaultTab());
               } else {
                  this.refreshCurrentTabContents(displayList);
               }
            }
         }
      }

   }

   private boolean tryRebuildTabContents(final @Nullable SessionSearchTrees searchTrees, final FeatureFlagSet enabledFeatures, final boolean hasPermissions, final HolderLookup.Provider holders) {
      if (!CreativeModeTabs.tryRebuildTabContents(enabledFeatures, hasPermissions, holders)) {
         return false;
      } else {
         if (searchTrees != null) {
            List<ItemStack> creativeSearchItems = List.copyOf(CreativeModeTabs.searchTab().getDisplayItems());
            searchTrees.updateCreativeTooltips(holders, creativeSearchItems);
            searchTrees.updateCreativeTags(creativeSearchItems);
         }

         return true;
      }
   }

   private void refreshCurrentTabContents(final Collection<ItemStack> displayList) {
      int oldRowIndex = ((ItemPickerMenu)this.menu).getRowIndexForScroll(this.scrollOffs);
      (this.menu).items.clear();
      if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
         this.refreshSearchResults();
      } else {
         (this.menu).items.addAll(displayList);
      }

      this.scrollOffs = ((ItemPickerMenu)this.menu).getScrollForRowIndex(oldRowIndex);
      ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
   }

   public void containerTick() {
      super.containerTick();
      LocalPlayer player = this.minecraft.player;
      if (player != null) {
         this.tryRefreshInvalidatedTabs(player.connection.enabledFeatures(), this.hasPermissions(player), player.level().registryAccess());
         if (!player.hasInfiniteMaterials()) {
            this.minecraft.gui.setScreen(new InventoryScreen(player));
         }
      }

   }

   protected void slotClicked(final @Nullable SlotWidget slotWidget, final int slotId, final int buttonNum, ContainerInput containerInput) {
      if (this.isCreativeSlot(slotWidget) && !this.minecraft.getLastInputType().isKeyboard()) {
         this.searchBox.moveCursorToEnd(false);
         this.searchBox.setHighlightPos(0);
      }

      Slot slot = slotWidget != null ? slotWidget.slot() : null;
      boolean quickKey = containerInput == ContainerInput.QUICK_MOVE;
      if (quickKey) {
         this.updateLastQuickMoved(slot);
      }

      containerInput = slotId == -999 && containerInput == ContainerInput.PICKUP ? ContainerInput.THROW : containerInput;
      if (containerInput != ContainerInput.THROW || this.minecraft.player.canDropItems()) {
         if (slotWidget != null) {
            slotWidget.clickSlot(containerInput);
         }

         if (slot == null && selectedTab.getType() != CreativeModeTab.Type.INVENTORY && containerInput != ContainerInput.QUICK_CRAFT) {
            if (!((ItemPickerMenu)this.menu).getCarried().isEmpty() && this.hasClickedOutside) {
               if (!this.minecraft.player.canDropItems()) {
                  return;
               }

               if (buttonNum == 0) {
                  this.minecraft.player.drop(((ItemPickerMenu)this.menu).getCarried(), true, Prediction.PREDICTED);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(((ItemPickerMenu)this.menu).getCarried());
                  ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
               }

               if (buttonNum == 1) {
                  ItemStack removedItem = ((ItemPickerMenu)this.menu).getCarried().split(1);
                  this.minecraft.player.drop(removedItem, true, Prediction.PREDICTED);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(removedItem);
               }
            }
         } else {
            if (slot != null && !slot.mayPickup(this.minecraft.player)) {
               return;
            }

            if (slot == this.destroyItemSlot && quickKey) {
               for(int i = 0; i < this.minecraft.player.inventoryMenu.getItems().size(); ++i) {
                  this.minecraft.player.inventoryMenu.getSlot(i).set(ItemStack.EMPTY);
                  this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, i);
               }
            } else if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
               if (slot == this.destroyItemSlot) {
                  ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
               } else if (containerInput == ContainerInput.THROW && slot != null && slot.hasItem()) {
                  ItemStack toDrop = slot.remove(buttonNum == 0 ? 1 : slot.getItem().getMaxStackSize());
                  ItemStack afterDrop = slot.getItem();
                  this.minecraft.player.drop(toDrop, true, Prediction.PREDICTED);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(toDrop);
                  this.minecraft.gameMode.handleCreativeModeItemAdd(afterDrop, ((SlotWrapper)slot).target.index);
               } else if (containerInput == ContainerInput.THROW && slotId == -999 && !((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                  this.minecraft.player.drop(((ItemPickerMenu)this.menu).getCarried(), true, Prediction.PREDICTED);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(((ItemPickerMenu)this.menu).getCarried());
                  ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
               } else {
                  this.minecraft.player.inventoryMenu.clicked(slot == null ? slotId : ((SlotWrapper)slot).target.index, buttonNum, containerInput, this.minecraft.player);
                  this.minecraft.player.inventoryMenu.broadcastChanges();
               }
            } else if (containerInput != ContainerInput.QUICK_CRAFT && slot != null && slot.container == CONTAINER) {
               ItemStack carried = ((ItemPickerMenu)this.menu).getCarried();
               ItemStack clicked = slot.getItem();
               if (containerInput == ContainerInput.SWAP) {
                  if (!clicked.isEmpty()) {
                     this.minecraft.player.getInventory().setItem(buttonNum, clicked.copyWithCount(clicked.getMaxStackSize()));
                     this.minecraft.player.inventoryMenu.broadcastChanges();
                  }

                  return;
               }

               if (containerInput == ContainerInput.CLONE) {
                  if (((ItemPickerMenu)this.menu).getCarried().isEmpty() && slot.hasItem()) {
                     ItemStack item = slot.getItem();
                     ((ItemPickerMenu)this.menu).setCarried(item.copyWithCount(item.getMaxStackSize()));
                  }

                  return;
               }

               if (containerInput == ContainerInput.THROW) {
                  if (!clicked.isEmpty()) {
                     ItemStack toDrop = clicked.copyWithCount(buttonNum == 0 ? 1 : clicked.getMaxStackSize());
                     this.minecraft.player.drop(toDrop, true, Prediction.PREDICTED);
                     this.minecraft.gameMode.handleCreativeModeItemDrop(toDrop);
                  }

                  return;
               }

               if (!carried.isEmpty() && !clicked.isEmpty() && ItemStack.isSameItemSameComponents(carried, clicked)) {
                  if (buttonNum == 0) {
                     if (quickKey) {
                        carried.setCount(carried.getMaxStackSize());
                     } else if (carried.getCount() < carried.getMaxStackSize()) {
                        carried.grow(1);
                     }
                  } else {
                     carried.shrink(1);
                  }
               } else if (!clicked.isEmpty() && carried.isEmpty()) {
                  int count = quickKey ? clicked.getMaxStackSize() : clicked.getCount();
                  ((ItemPickerMenu)this.menu).setCarried(clicked.copyWithCount(count));
               } else if (buttonNum == 0) {
                  ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
               } else if (!((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                  ((ItemPickerMenu)this.menu).getCarried().shrink(1);
               }
            } else if (this.menu != null) {
               ItemStack oldItemStack = slot == null ? ItemStack.EMPTY : ((ItemPickerMenu)this.menu).getSlot(slot.index).getItem();
               ((ItemPickerMenu)this.menu).clicked(slot == null ? slotId : slot.index, buttonNum, containerInput, this.minecraft.player);
               if (AbstractContainerMenu.getQuickcraftHeader(buttonNum) == 2) {
                  this.minecraft.player.inventoryMenu.broadcastChanges();
               } else if (slot != null && Inventory.isHotbarSlot(slot.getContainerSlot()) && selectedTab.getType() != CreativeModeTab.Type.INVENTORY) {
                  if (containerInput == ContainerInput.THROW && !oldItemStack.isEmpty() && !((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                     int numToDrop = buttonNum == 0 ? 1 : oldItemStack.getCount();
                     ItemStack toDrop = oldItemStack.copyWithCount(numToDrop);
                     oldItemStack.shrink(numToDrop);
                     this.minecraft.player.drop(toDrop, true, Prediction.PREDICTED);
                     this.minecraft.gameMode.handleCreativeModeItemDrop(toDrop);
                  }

                  this.minecraft.player.inventoryMenu.broadcastChanges();
               }
            }
         }

      }
   }

   private boolean isCreativeSlot(final @Nullable SlotWidget slotWidget) {
      return slotWidget != null && slotWidget.slot().container == CONTAINER;
   }

   public void resize(final int width, final int height) {
      int oldRowIndex = ((ItemPickerMenu)this.menu).getRowIndexForScroll(this.scrollOffs);
      String oldEdit = this.searchBox.getValue();
      this.init(width, height);
      if (!oldEdit.isEmpty()) {
         this.searchBox.setValue(oldEdit);
      }

      this.scrollOffs = ((ItemPickerMenu)this.menu).getScrollForRowIndex(oldRowIndex);
      ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
   }

   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.getInventory() != null) {
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
      }

   }

   public boolean charTyped(final CharacterEvent event) {
      if (this.ignoreTextInput) {
         return false;
      } else {
         return selectedTab.getType() != CreativeModeTab.Type.SEARCH ? false : super.charTyped(event);
      }
   }

   public boolean preeditUpdated(final @Nullable PreeditEvent event) {
      if (this.ignoreTextInput) {
         return false;
      } else {
         return selectedTab.getType() != CreativeModeTab.Type.SEARCH ? false : super.preeditUpdated(event);
      }
   }

   public boolean keyPressed(final KeyEvent event) {
      this.ignoreTextInput = false;
      if (this.tryScrollGrid(event)) {
         return true;
      } else if (selectedTab.getType() != CreativeModeTab.Type.SEARCH) {
         if (this.minecraft.options.keyChat.matches(event)) {
            this.ignoreTextInput = true;
            this.selectTab(CreativeModeTabs.searchTab());
            this.setFocused(this.searchBox);
            return true;
         } else {
            return super.keyPressed(event);
         }
      } else {
         SlotWidget hoveredSlotWidget = this.getHoveredSlot();
         boolean doQuickSwap = !this.isCreativeSlot(hoveredSlotWidget) || hoveredSlotWidget.slot().hasItem();
         boolean pressingNumber = InputConstants.getKey(event).getNumericKeyValue().isPresent();
         if (doQuickSwap && pressingNumber && this.checkHotbarKeyPressed(event)) {
            this.ignoreTextInput = true;
            return true;
         } else {
            return super.keyPressed(event);
         }
      }
   }

   private boolean tryScrollGrid(final KeyEvent event) {
      SlotWidget focusedSlotWidget = this.getFocusedSlotWidget();
      if (this.canScroll() && focusedSlotWidget != null) {
         Slot var4 = focusedSlotWidget.slot();
         if (var4 instanceof CustomCreativeSlot) {
            CustomCreativeSlot creativeSlot = (CustomCreativeSlot)var4;
            int var6 = creativeSlot.row();
            float oldScrollOffs = this.scrollOffs;
            if (event.isDown() && var6 >= 3) {
               this.scrollOffs = ((ItemPickerMenu)this.menu).subtractInputFromScroll(this.scrollOffs, -1.0);
               ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
            }

            if (event.isUp() && var6 < 2) {
               this.scrollOffs = ((ItemPickerMenu)this.menu).subtractInputFromScroll(this.scrollOffs, 1.0);
               ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
            }

            return oldScrollOffs != this.scrollOffs;
         }
      }

      return false;
   }

   public boolean keyReleased(final KeyEvent event) {
      this.ignoreTextInput = false;
      return super.keyReleased(event);
   }

   public boolean isInputCaptured() {
      return super.isInputCaptured() || this.searchBox.capturesInput();
   }

   private void refreshSearchResults() {
      (this.menu).items.clear();
      this.visibleTags.clear();
      String searchTerm = this.searchBox.getValue();
      if (searchTerm.isEmpty()) {
         (this.menu).items.addAll(selectedTab.getDisplayItems());
      } else {
         ClientPacketListener connection = this.minecraft.getConnection();
         if (connection != null) {
            SessionSearchTrees searchTrees = connection.searchTrees();
            SearchTree<ItemStack> tree;
            if (searchTerm.startsWith("#")) {
               searchTerm = searchTerm.substring(1);
               tree = searchTrees.creativeTagSearch();
               this.updateVisibleTags(searchTerm);
            } else {
               tree = searchTrees.creativeNameSearch();
            }

            (this.menu).items.addAll(tree.search(searchTerm.toLowerCase(Locale.ROOT)));
         }
      }

      this.scrollOffs = 0.0F;
      ((ItemPickerMenu)this.menu).scrollTo(0.0F);
   }

   private void updateVisibleTags(final String searchTerm) {
      int colonIndex = searchTerm.indexOf(58);
      Predicate<Identifier> matcher;
      if (colonIndex == -1) {
         matcher = (id) -> id.getPath().contains(searchTerm);
      } else {
         String nsMatcher = searchTerm.substring(0, colonIndex).trim();
         String pathMatcher = searchTerm.substring(colonIndex + 1).trim();
         matcher = (id) -> id.getNamespace().contains(nsMatcher) && id.getPath().contains(pathMatcher);
      }

      Stream var10000 = BuiltInRegistries.ITEM.getTags().map(HolderSet.Named::key).filter((tag) -> matcher.test(tag.location()));
      Set var10001 = this.visibleTags;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
   }

   protected void extractLabels(final GuiGraphicsExtractor graphics) {
      if (selectedTab.showTitle()) {
         graphics.text(this.font, (Component)selectedTab.getDisplayName(), 8, 6, -12566464, false);
      }

   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (event.isLeftMouse() && selectedTab.getType() != CreativeModeTab.Type.INVENTORY && this.insideScrollbar(event.x(), event.y())) {
         this.scrolling = this.canScroll();
         return true;
      } else {
         return super.mouseClicked(event, doubleClick);
      }
   }

   public boolean mouseReleased(final MouseButtonEvent event) {
      if (event.isLeftMouse()) {
         this.scrolling = false;
         if (!this.hasClickedOutside(event.x(), event.y()) && this.searchBox.isVisible()) {
            this.setFocused(this.searchBox);
         }
      }

      return super.mouseReleased(event);
   }

   private boolean canScroll() {
      return selectedTab.canScroll() && ((ItemPickerMenu)this.menu).canScroll();
   }

   private void showTab(final CreativeModeTab tab) {
      selectedTab = tab;
      this.quickCraftSlots.clear();
      (this.menu).items.clear();
      if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR) {
         HotbarManager manager = this.minecraft.getHotbarManager();

         for(int hotbarIndex = 0; hotbarIndex < 9; ++hotbarIndex) {
            Hotbar hotbar = manager.get(hotbarIndex);
            if (hotbar.isEmpty()) {
               for(int i = 0; i < 9; ++i) {
                  if (i == hotbarIndex) {
                     ItemStack placeholder = new ItemStack(Items.PAPER);
                     placeholder.set(DataComponents.CREATIVE_SLOT_LOCK, Unit.INSTANCE);
                     Component translatedKeyMessage = this.minecraft.options.keyHotbarSlots[hotbarIndex].getTranslatedKeyMessage();
                     Component activatorKeyMessage = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                     placeholder.set(DataComponents.ITEM_NAME, Component.translatable("inventory.hotbarInfo", activatorKeyMessage, translatedKeyMessage));
                     (this.menu).items.add(placeholder);
                  } else {
                     (this.menu).items.add(ItemStack.EMPTY);
                  }
               }
            } else {
               (this.menu).items.addAll(hotbar.load(this.minecraft.level.registryAccess()));
            }
         }
      } else if (selectedTab.getType() == CreativeModeTab.Type.CATEGORY) {
         (this.menu).items.addAll(selectedTab.getDisplayItems());
      }

      if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
         AbstractContainerMenu invMenu = this.minecraft.player.inventoryMenu;
         if (this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf((this.menu).slots);
         }

         (this.menu).slots.clear();

         for(int i = 5; i < invMenu.slots.size(); ++i) {
            int x;
            int y;
            if (i < 9) {
               int pos = i - 5;
               int col = pos / 2;
               int row = pos % 2;
               x = 54 + col * 54;
               y = 6 + row * 27;
            } else if (i == 45) {
               x = 35;
               y = 20;
            } else {
               int pos = i - 9;
               int col = pos % 9;
               int row = pos / 9;
               x = 9 + col * 18;
               if (i >= 36) {
                  y = 112;
               } else {
                  y = 54 + row * 18;
               }
            }

            Slot slot = new SlotWrapper(invMenu.slots.get(i), i, x, y);
            (this.menu).slots.add(slot);
         }

         this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112, Slot.Section.DESTROY_ITEM);
         (this.menu).slots.add(this.destroyItemSlot);
      }

      if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
         this.searchBox.setVisible(true);
         this.searchBox.setValue("");
      } else {
         this.searchBox.setVisible(false);
         this.searchBox.setValue("");
      }

      this.updateSlotWidgets();
      this.scrollOffs = 0.0F;
      ((ItemPickerMenu)this.menu).scrollTo(0.0F);
   }

   private void selectTab(final CreativeModeTab tab) {
      for(int index = 0; index < this.tabBarTop.getTabs().size(); ++index) {
         Object var4 = this.tabBarTop.getTabs().get(index);
         if (var4 instanceof CreativeModeTabAdapter tabAdapter) {
            if (tabAdapter.tab().equals(tab)) {
               this.tabBarTop.selectTab(index, false);
               return;
            }
         }
      }

      for(int index = 0; index < this.tabBarBottom.getTabs().size(); ++index) {
         Object var7 = this.tabBarBottom.getTabs().get(index);
         if (var7 instanceof CreativeModeTabAdapter tabAdapter) {
            if (tabAdapter.tab().equals(tab)) {
               this.tabBarBottom.selectTab(index, false);
               return;
            }
         }
      }

   }

   protected void updateSlotWidgets() {
      List<SlotWidget> widgets = new ArrayList();

      for(Slot slot : (this.menu).slots) {
         if (slot == this.destroyItemSlot) {
            widgets.add(new DestroyItemSlotWidget(slot, this.minecraft, this.imageWidth, this.leftPos, this.topPos));
         } else if (slot instanceof CustomCreativeSlot) {
            widgets.add(new CreativeItemSlotWidget(slot));
         } else {
            widgets.add(new SlotWidget(slot, this.minecraft, this.imageWidth, this.leftPos, this.topPos, this.tooltipSupplier, () -> null));
         }
      }

      this.setSlotWidgets(widgets);
   }

   protected void setInitialFocus() {
      if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
         this.setInitialFocus(this.searchBox);
      } else {
         super.setInitialFocus();
      }

   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      if (super.mouseScrolled(x, y, scrollX, scrollY)) {
         return true;
      } else if (!this.canScroll()) {
         return false;
      } else {
         this.scrollOffs = ((ItemPickerMenu)this.menu).subtractInputFromScroll(this.scrollOffs, scrollY);
         ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
         return true;
      }
   }

   protected boolean hasClickedOutside(final double mx, final double my) {
      this.hasClickedOutside = super.hasClickedOutside(mx, my) && !this.tabBarTop.isMouseOver(mx, my) && !this.tabBarBottom.isMouseOver(mx, my);
      return this.hasClickedOutside;
   }

   protected boolean insideScrollbar(final double xm, final double ym) {
      int xo = this.leftPos;
      int yo = this.topPos;
      int xscr = xo + 175;
      int yscr = yo + 18;
      int xscr2 = xscr + 14;
      int yscr2 = yscr + 112;
      return xm >= (double)xscr && ym >= (double)yscr && xm < (double)xscr2 && ym < (double)yscr2 && selectedTab.canScroll();
   }

   public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
      if (this.scrolling) {
         int yscr = this.topPos + 18;
         int yscr2 = yscr + 112;
         this.scrollOffs = ((float)event.y() - (float)yscr - 7.5F) / ((float)(yscr2 - yscr) - 15.0F);
         this.scrollOffs = Math.clamp(this.scrollOffs, 0.0F, 1.0F);
         ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
         return true;
      } else {
         return super.mouseDragged(event, dx, dy);
      }
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics) {
      this.effects.extractRenderState(graphics);
      super.extractRenderState(graphics);
   }

   public boolean showsActiveEffects() {
      return this.effects.canSeeEffects();
   }

   public void extractBackground(final GuiGraphicsExtractor graphics) {
      super.extractBackground(graphics);
      this.extractUnselectedTabButtons(graphics);
      graphics.blit(GuiPipeline.TEXTURED, selectedTab.getBackgroundTexture(), this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      if (this.insideScrollbar((double)graphics.mouseX(), (double)graphics.mouseY())) {
         if (this.canScroll()) {
            graphics.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
         } else {
            graphics.requestCursor(CursorTypes.NOT_ALLOWED);
         }
      }

      int xscr = this.leftPos + 175;
      int yscr = this.topPos + 18;
      int yscr2 = yscr + 112;
      if (selectedTab.canScroll()) {
         Identifier sprite = this.canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
         graphics.blitSprite(GuiPipeline.TEXTURED, (Identifier)sprite, xscr, yscr + (int)((float)(yscr2 - yscr - 17) * this.scrollOffs), 12, 15);
      }

      TabButton selectedTabButton = (selectedTab.row() == CreativeModeTab.Row.TOP ? this.tabBarTop : this.tabBarBottom).currentTabButton();
      if (selectedTabButton != null) {
         selectedTabButton.extractRenderState(graphics);
      }

      if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
         EntityPortraitWidget.extractEntityInInventoryFollowsMouse(graphics, this.leftPos + 73, this.topPos + 6, this.leftPos + 105, this.topPos + 49, 20.0F, 0.0625F, this.minecraft.player);
      }

   }

   private void extractUnselectedTabButtons(final GuiGraphicsExtractor graphics) {
      for(TabNavigationBar tabBar : List.of(this.tabBarTop, this.tabBarBottom)) {
         for(GuiEventListener child : tabBar.children()) {
            if (child instanceof TabButton tabButton) {
               if (!tabButton.isSelected()) {
                  tabButton.extractRenderState(graphics);
               }
            }
         }
      }

   }

   public boolean isInventoryOpen() {
      return selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
   }

   public static void handleHotbarLoadOrSave(final Minecraft minecraft, final int index, final boolean isLoadPressed, final boolean isSavePressed) {
      LocalPlayer player = minecraft.player;
      HolderGetter.Provider registries = player.level().registryAccess();
      HotbarManager manager = minecraft.getHotbarManager();
      Hotbar hotbar = manager.get(index);
      if (isLoadPressed) {
         List<ItemStack> originalItems = hotbar.load(registries);

         for(int i = 0; i < Inventory.getSelectionSize(); ++i) {
            ItemStack itemStack = (ItemStack)originalItems.get(i);
            player.getInventory().setItem(i, itemStack);
            minecraft.gameMode.handleCreativeModeItemAdd(itemStack, 36 + i);
         }

         player.inventoryMenu.broadcastChanges();
      } else if (isSavePressed) {
         hotbar.storeFrom(player.getInventory(), registries);
         Component translatedKeyMessage = minecraft.options.keyHotbarSlots[index].getTranslatedKeyMessage();
         Component activatorKeyMessage = minecraft.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
         Component message = Component.translatable("inventory.hotbarSaved", activatorKeyMessage, translatedKeyMessage);
         minecraft.gui.hud.setOverlayMessage(message, false);
         minecraft.getNarrator().saySystemNow(message);
         manager.save();
      }

   }

   private static class SlotWrapper extends Slot {
      private final Slot target;

      public SlotWrapper(final Slot target, final int index, final int x, final int y) {
         super(target.container, index, x, y, target.getSection());
         this.target = target;
      }

      public void onTake(final Player player, final ItemStack carried) {
         this.target.onTake(player, carried);
      }

      public boolean mayPlace(final ItemStack itemStack) {
         return this.target.mayPlace(itemStack);
      }

      public ItemStack getItem() {
         return this.target.getItem();
      }

      public boolean hasItem() {
         return this.target.hasItem();
      }

      public void setByPlayer(final ItemStack itemStack, final ItemStack previous) {
         this.target.setByPlayer(itemStack, previous);
      }

      public void set(final ItemStack itemStack) {
         this.target.set(itemStack);
      }

      public void setChanged() {
         this.target.setChanged();
      }

      public int getMaxStackSize() {
         return this.target.getMaxStackSize();
      }

      public int getMaxStackSize(final ItemStack itemStack) {
         return this.target.getMaxStackSize(itemStack);
      }

      public @Nullable Identifier getNoItemIcon() {
         return this.target.getNoItemIcon();
      }

      public @Nullable Component getNarrationName() {
         return this.target.getNarrationName();
      }

      public ItemStack remove(final int amount) {
         return this.target.remove(amount);
      }

      public boolean isActive() {
         return this.target.isActive();
      }

      public boolean mayPickup(final Player player) {
         return this.target.mayPickup(player);
      }
   }

   private static class CustomCreativeSlot extends Slot {
      public CustomCreativeSlot(final Container container, final int slot, final int x, final int y) {
         super(container, slot, x, y, Slot.Section.CONTAINER);
      }

      public int row() {
         return this.getContainerSlot() / 9;
      }

      public boolean mayPickup(final Player player) {
         ItemStack item = this.getItem();
         if (super.mayPickup(player) && !item.isEmpty()) {
            return item.isItemEnabled(player.level().enabledFeatures()) && !item.has(DataComponents.CREATIVE_SLOT_LOCK);
         } else {
            return item.isEmpty();
         }
      }
   }

   private class CreativeItemSlotWidget extends SlotWidget {
      private static final GroupNarration GRID_NARRATION = GroupNarration.grid(9, "narration.group_navigation.usage.slot");

      public CreativeItemSlotWidget(final Slot slot) {
         Objects.requireNonNull(CreativeModeInventoryScreen.this);
         super(slot, CreativeModeInventoryScreen.this.minecraft, CreativeModeInventoryScreen.this.imageWidth, CreativeModeInventoryScreen.this.leftPos, CreativeModeInventoryScreen.this.topPos, CreativeModeInventoryScreen.this.tooltipSupplier, () -> null);
      }

      protected Component createNarrationTitle(final @Nullable Component groupName, final @Nullable Component location) {
         Component contents = this.createNarrationContents();
         Component tabName = CreativeModeInventoryScreen.selectedTab.getDisplayName();
         int index = ((ItemPickerMenu)CreativeModeInventoryScreen.this.menu).getRowIndexForScroll(CreativeModeInventoryScreen.this.scrollOffs) * 9 + this.slot().getContainerSlot();
         Component position = index < (CreativeModeInventoryScreen.this.menu).items.size() ? GRID_NARRATION.position(index, (CreativeModeInventoryScreen.this.menu).items.size()) : null;
         return position == null ? Component.translatable("narration.creative_item", contents, tabName) : Component.translatable("narration.creative_item.located", contents, tabName, position);
      }

      protected @Nullable Component createNarrationUsage() {
         ItemStack carried = ((ItemPickerMenu)CreativeModeInventoryScreen.this.menu).getCarried();
         if (carried.isEmpty()) {
            return super.createNarrationUsage();
         } else {
            String usageKey = "narration.slot.usage." + (this.isFocused() ? "focused." : "hovered.");
            if (ItemStack.isSameItemSameComponents(carried, this.slot().getItem())) {
               return carried.getCount() < carried.getMaxStackSize() ? Component.translatable(usageKey + "pick_up") : null;
            } else {
               return Component.translatable(usageKey + "destroy", getItemNarration(carried));
            }
         }
      }
   }

   private static class DestroyItemSlotWidget extends SlotWidget {
      private static final Tooltip TOOLTIP;

      public DestroyItemSlotWidget(final Slot slot, final Minecraft minecraft, final int containerWidth, final int leftPos, final int topPos) {
         super(slot, minecraft, containerWidth, leftPos, topPos, () -> TOOLTIP);
      }

      protected Component createNarrationTitle(final @Nullable Component groupName, final @Nullable Component location) {
         return CreativeModeInventoryScreen.DESTROY_ITEM_SLOT_NAME;
      }

      protected @Nullable Component createNarrationUsage() {
         ItemStack carried = this.minecraft.player.containerMenu.getCarried();
         return carried.isEmpty() ? null : Component.translatable("narration.slot.usage." + (this.isFocused() ? "focused." : "hovered.") + "destroy", getItemNarration(carried));
      }

      protected void updateTooltipNarration(final NarrationElementOutput output) {
      }

      static {
         TOOLTIP = Tooltip.create(CreativeModeInventoryScreen.DESTROY_ITEM_SLOT_NAME);
      }
   }

   public static class ItemPickerMenu extends AbstractContainerMenu {
      public final NonNullList<ItemStack> items = NonNullList.<ItemStack>create();
      private final AbstractContainerMenu inventoryMenu;

      public ItemPickerMenu(final Player player) {
         super((MenuType)null, 0);
         this.inventoryMenu = player.inventoryMenu;
         Inventory inventory = player.getInventory();

         for(int y = 0; y < 5; ++y) {
            for(int x = 0; x < 9; ++x) {
               this.addSlot(new CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, y * 9 + x, 9 + x * 18, 18 + y * 18));
            }
         }

         this.addInventoryHotbarSlots(inventory, 9, 112);
         this.scrollTo(0.0F);
      }

      public boolean stillValid(final Player player) {
         return true;
      }

      protected int calculateRowCount() {
         return Mth.positiveCeilDiv(this.items.size(), 9) - 5;
      }

      protected int getRowIndexForScroll(final float scrollOffs) {
         return Math.max((int)((double)(scrollOffs * (float)this.calculateRowCount()) + 0.5), 0);
      }

      protected float getScrollForRowIndex(final int rowIndex) {
         return Math.clamp((float)rowIndex / (float)this.calculateRowCount(), 0.0F, 1.0F);
      }

      protected float subtractInputFromScroll(final float scrollOffs, final double input) {
         return Math.clamp(scrollOffs - (float)(input / (double)this.calculateRowCount()), 0.0F, 1.0F);
      }

      public void scrollTo(final float scrollOffs) {
         int rowToScrollTo = this.getRowIndexForScroll(scrollOffs);

         for(int y = 0; y < 5; ++y) {
            for(int x = 0; x < 9; ++x) {
               int slot = x + (y + rowToScrollTo) * 9;
               if (slot >= 0 && slot < this.items.size()) {
                  CreativeModeInventoryScreen.CONTAINER.setItem(x + y * 9, this.items.get(slot));
               } else {
                  CreativeModeInventoryScreen.CONTAINER.setItem(x + y * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.items.size() > 45;
      }

      public ItemStack quickMoveStack(final Player player, final int slotIndex) {
         if (slotIndex >= this.slots.size() - 9 && slotIndex < this.slots.size()) {
            Slot slot = this.slots.get(slotIndex);
            if (slot != null && slot.hasItem()) {
               slot.setByPlayer(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      public boolean canTakeItemForPickAll(final ItemStack carried, final Slot target) {
         return target.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public boolean canDragTo(final Slot slot) {
         return slot.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public ItemStack getCarried() {
         return this.inventoryMenu.getCarried();
      }

      public void setCarried(final ItemStack carried) {
         this.inventoryMenu.setCarried(carried);
      }
   }

   private static class CreativeModeTabAdapter implements Tab {
      private final CreativeModeTab tab;

      private CreativeModeTabAdapter(final CreativeModeTab tab) {
         super();
         this.tab = tab;
      }

      public CreativeModeTab tab() {
         return this.tab;
      }

      public void visitChildren(final Consumer<AbstractWidget> childrenConsumer) {
      }

      public void doLayout(final ScreenRectangle screenRectangle) {
      }

      public Layout getLayout() {
         return null;
      }
   }
}
